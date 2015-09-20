package com.cogent.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.cogent.DataBase.DBHelper;

public class ContactProvider extends ContentProvider{
	
	private static DBHelper dbHelper;
	public static final String AUTHORITY = "com.iBeacon.contact";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY + "/" + DBHelper.CONTACT_TABLE_NAME);
	
    public ContactProvider(){
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.db;
		String rowId = uri.getPathSegments().get(1);
		getContext().getContentResolver().notifyChange(uri, null); 
		return db.delete(dbHelper.CONTACT_TABLE_NAME,"_id = "+rowId, null);
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.db;
        long rowID = db.insert(dbHelper.CONTACT_TABLE_NAME, null, values);
        if (rowID > 0) {
			Uri url = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(url, null);
			return url;
		}
        return null;

	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,  
            String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.db;
		Cursor c = db.query(dbHelper.CONTACT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);  
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,  
            String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.db;   
		int count = 0;
		count = db.update(dbHelper.CONTACT_TABLE_NAME,values,selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count; 
	} 
	
}
