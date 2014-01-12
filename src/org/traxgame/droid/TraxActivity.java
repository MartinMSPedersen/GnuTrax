package org.traxgame.droid;

import org.traxgame.main.GnuTrax;
import org.traxgame.main.IllegalMoveException;
import org.traxgame.main.Traxboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TraxActivity extends Activity {

	private GnuTrax gnuTrax;

	private Button exeButton;
	private TextView boardPlace;
	private Spinner column;
	private Spinner row;
	private Spinner block;

	// TODO: Update to use AsyncTask to improve UI

	private void showNewGameDialog(String winner) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
		dlgAlert.setMessage("Good game. The winner was: " + winner);
		dlgAlert.setTitle("Game over");
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				gnuTrax.userNew();
				boardPlace.append("\n\nA new game had begun.\n\n"
						+ gnuTrax.welcome());
			}
		});
		dlgAlert.create().show();
		exeButton.setEnabled(true);
		column.setEnabled(true);
		row.setEnabled(true);
		block.setEnabled(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		boardPlace = (TextView) findViewById(R.id.boardplace);
		exeButton = (Button) findViewById(R.id.execute);
		column = (Spinner) findViewById(R.id.column);
		/*
		 * String[] columnNames = {"@","A","B","C","D","E","F","G","H"};
		 * ArrayAdapter<String> adp = new
		 * ArrayAdapter<String>(this,R.id.column,columnNames);
		 * column.setAdapter(adp);
		 */
		row = (Spinner) findViewById(R.id.row);
		block = (Spinner) findViewById(R.id.block);

		if (gnuTrax == null)
			gnuTrax = new GnuTrax("uct");

		boardPlace.setText(gnuTrax.welcome());

		// At click on button:
		// Disable button while computer is thinking. Add nice message to user
		// Run gotAMove
		// Clear board
		// Print result on board
		// Adjust textSize with: textSize on the textview
		// Run check for win.
		// command.setOnEditorActionListener(new
		// TextView.OnEditorActionListener() {
		
		final Context context = this;
		exeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exeButton.setEnabled(false);
				column.setEnabled(false);
				row.setEnabled(false);
				block.setEnabled(false);
				String move = "";
				move = column.getSelectedItem().toString()
						+ row.getSelectedItem().toString()
						+ block.getSelectedItem().toString();
				if (move.length() > 0) {
					try {
						gnuTrax.gotAMove(move);
					} catch (Exception e) {
						Toast.makeText(context, "Wrong move"+e.getMessage(), Toast.LENGTH_LONG).show();
					}
					String newBoard = gnuTrax.getLatestBoard();
					if (newBoard.startsWith("Wrong")) {
						boardPlace.append("\n\n" + newBoard);
						exeButton.setEnabled(true);
						column.setEnabled(true);
						row.setEnabled(true);
						block.setEnabled(true);
						return;
					} else {
						boardPlace.setText(newBoard);
						if (checkForWinner())
							return;
						boardPlace.append("\n\nComputer is thinking");
						final DoMoveTask doMoveTask = new DoMoveTask(
								TraxActivity.this);
						Toast.makeText(context, "Computer is thinking...", Toast.LENGTH_LONG).show();
						doMoveTask.execute(move);
					}

				}
			}
		});
	}

	private boolean checkForWinner() {
		if (gnuTrax.isGameOver() != Traxboard.NOPLAYER) {
			// Show message box with winner and if you want to
			// play again
			switch (gnuTrax.isGameOver()) {
			case Traxboard.BLACK:
				showNewGameDialog("black");
				break;
			case Traxboard.WHITE:
				showNewGameDialog("white");
				break;
			default:
				showNewGameDialog("everyone");
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class DoMoveTask extends AsyncTask<String, Integer, String> {

		private Context context;
		private String newBoard;

		public DoMoveTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPostExecute(String line) {
			try {
				gnuTrax.gotAMove(line);
			} catch (IllegalMoveException e) {
				Toast.makeText(context, "Wrong AI move "+e.getMessage(), Toast.LENGTH_LONG).show();
			}
			newBoard = gnuTrax.getLatestBoard();
			boardPlace.setText(newBoard);
			boardPlace.append("AI move: " + line);
			if (checkForWinner())
				return;
			exeButton.setEnabled(true);
			column.setEnabled(true);
			row.setEnabled(true);
			block.setEnabled(true);
		}

		@Override
		protected String doInBackground(String... move) {
			return gnuTrax.makeComputerMove();
		}
	}
}
