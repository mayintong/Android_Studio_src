package com.cogent.contactsfragment;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SortCursorLoader extends CursorLoader{

public SortCursorLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = super.loadInBackground();
		return new SortCursor(cursor);
	}

}
