package org.traxgame.droid;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TraxActivity extends Activity {

	//TODO Handle like the swing way of doing the board with extend.
	//TODO The EN and SW tiles seem to be switched in the gui. Or is it some other tiles???
	//TODO Better game logic s.t. redudant code is reduced
	
	private GnuTrax gnuTrax;
	private int[] tileToDrawable;

	private GridView boardPlace;

	private BoardAdapter boardAdapter;
	
	private int[] tiles; 
	private List<DroidTile> userMoves;
	private AlertDialog dialog;
	private Point latestPoint;
	
	private boolean userCanMove;
	
	// TODO: Update to use AsyncTask to improve UI

	private void showNewGameDialog(String winner) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
		dlgAlert.setMessage("Good game. The winner was: " + winner);
		dlgAlert.setTitle("Game over");
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				gnuTrax.userNew();
				userCanMove = true;
			}
		});
		dlgAlert.create().show();
	}

	private void updateBoard() {
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				boardAdapter.getTileAt(i, j).setTileType(this.gnuTrax.getTileAt(i, j));
			}
		}
		boardAdapter.notifyDataSetChanged();
	}
	
	public void userChoseMove(int userMove) {
		//Toast.makeText(this, "You chose "+userMove, Toast.LENGTH_LONG).show();
		DroidTile move = userMoves.get(userMove);
		String theMove = latestPoint.getPositionWithMove(move.getTileType());
		try {
			this.gnuTrax.gotAMove(theMove);
			if (!checkForWinner()) {
				Toast.makeText(this, "The AI is thinking", Toast.LENGTH_LONG).show();
				Toast.makeText(this, "Computer is thinking...",
						Toast.LENGTH_LONG).show();
				final DoMoveTask doMoveTask = new DoMoveTask(this);
				userCanMove = false;
				doMoveTask.execute("");
			} else {
				showNewGameDialog("White");
			}
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Illegal move "+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		updateBoard();
		dialog.cancel();
	}
	
	private void getPossibleMoves(LinearLayout ll, int x, int y) {
		List<Integer> result = gnuTrax.getPossibleMoves(x, y);
		userMoves = new ArrayList<DroidTile>(); 
		for (int i = 0; i < result.size(); i++) {
			DroidTile tile = new DroidTile(this, i, 0, result.get(i), true);
			tile.setImageResource(this.tileToDrawable[tile.getTileType()]);
			userMoves.add(tile);
			//tile.setLayoutParams(new GridView.LayoutParams(80,80));
		}
		for (DroidTile tile: userMoves) {
			ll.addView(tile);
		}
	}
	
	public void showPossibleMoves(int x, int y) {
		if (!userCanMove) {
			Toast.makeText(this, "The AI is not done yet", Toast.LENGTH_LONG).show();
			return;
		}
		latestPoint = new Point(x, y);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.chooseTileTitle);
		dialog = builder.create();
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		getPossibleMoves(ll, x, y);
		dialog.setView(ll);
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "Du trykkede på "+myX+","+myY, Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}

	protected void fillArrayWithTiles() {
		tiles = new int[8];
		tiles[Traxboard.EMPTY] = R.drawable.blank;
		tiles[Traxboard.NS] = R.drawable.ns;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		userCanMove = true;
		this.tileToDrawable = new int[8];
		this.tileToDrawable[Traxboard.EMPTY] = R.drawable.blank;
		this.tileToDrawable[Traxboard.NS] = R.drawable.ns;
		this.tileToDrawable[Traxboard.WE] = R.drawable.we;
		this.tileToDrawable[Traxboard.NW] = R.drawable.nw;
		this.tileToDrawable[Traxboard.NE] = R.drawable.ne;
		this.tileToDrawable[Traxboard.WS] = R.drawable.ws;
		this.tileToDrawable[Traxboard.SE] = R.drawable.se;
		this.tileToDrawable[Traxboard.INVALID] = R.drawable.invalid;
		
		boardPlace = (GridView) findViewById(R.id.boardplace);
		
		boardAdapter = new BoardAdapter(this);
		boardPlace.setAdapter(boardAdapter);

		if (gnuTrax == null)
			gnuTrax = new GnuTrax("simple"); //When done change this to uct

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

		public DoMoveTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPostExecute(String line) {
			try {
				gnuTrax.gotAMove(line);
			} catch (IllegalMoveException e) {
				Toast.makeText(context, "Wrong AI move " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			updateBoard();
			if (checkForWinner()) {
				showNewGameDialog("Black");
				return;
			}
			userCanMove = true;
		}

		@Override
		protected String doInBackground(String... move) {
			return gnuTrax.makeComputerMove();
		}
	}
}
