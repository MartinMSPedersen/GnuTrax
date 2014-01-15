package org.traxgame.droid;

import java.util.ArrayList;

import org.traxgame.main.Traxboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class BoardAdapter extends BaseAdapter {
	private Context context;
	private int[] tileToDrawable;
	private ArrayList<DroidTile> boardData;
	
	public BoardAdapter(Context context) {
		this.context = context;
		this.boardData = new ArrayList<DroidTile>();
		this.tileToDrawable = new int[8];
		this.tileToDrawable[Traxboard.EMPTY] = R.drawable.blank;
		this.tileToDrawable[Traxboard.NS] = R.drawable.ns;
		this.tileToDrawable[Traxboard.WE] = R.drawable.we;
		this.tileToDrawable[Traxboard.NW] = R.drawable.nw;
		this.tileToDrawable[Traxboard.NE] = R.drawable.ne;
		this.tileToDrawable[Traxboard.WS] = R.drawable.ws;
		this.tileToDrawable[Traxboard.SE] = R.drawable.se;
		this.tileToDrawable[Traxboard.INVALID] = R.drawable.invalid;
		for (int i = 0; i < 81; i++) {
			DroidTile tile = new DroidTile(context, i%9, i/9, Traxboard.EMPTY, false);
			boardData.add(tile);
		}
	}
	
	@Override
	public int getCount() {
		return boardData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.tileToDrawable[boardData.get(arg0).getTileType()];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0/8;
	}

	public DroidTile getTileAt(int x, int y) {
		int noOfCols = 9;
		return boardData.get(y*noOfCols+x);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DroidTile tile = this.boardData.get(position);
		tile.setImageResource(this.tileToDrawable[tile.getTileType()]);
		tile.setLayoutParams(new GridView.LayoutParams(80,80));
		return tile;
	}

}
