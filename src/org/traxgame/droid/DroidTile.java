package org.traxgame.droid;

import org.traxgame.main.Traxboard;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DroidTile extends ImageView {

	private int tileType;
	private int x, y;

	public DroidTile(Context context, int x, int y, int tileType,
			boolean possibleMoveShower) {
		super(context);
		this.x = x;
		this.y = y;
		this.tileType = tileType;
		final Context con = context;
		if (possibleMoveShower) {
			this.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
						((TraxActivity) con).userChoseMove(DroidTile.this.x);
				}
			});
		} else {
			this.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (DroidTile.this.tileType == Traxboard.EMPTY) {
						((TraxActivity) con).showPossibleMoves(
								DroidTile.this.x, DroidTile.this.y);
					} else {
						Toast.makeText(con, "This place is occupied",
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	public void setTileType(int tileType) {
		this.tileType = tileType;
	}
	
	public int getTileType() {
		return tileType;
	}
}
