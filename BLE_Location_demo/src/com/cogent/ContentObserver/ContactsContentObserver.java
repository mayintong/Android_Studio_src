package com.cogent.ContentObserver;


import com.cogent.util.ContactProvider;
import com.cogent.util.ContactUtils;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class ContactsContentObserver extends ContentObserver{

	public ContactsContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Log.i("iBeacon contact", "联系人数据库发生了变化");
	//	ContactUtils.getContacts();
		//refreshData();
	}
	
/*private void refreshData() {
		Cursor mCursor = ContactUtils.QueryContact();
	
		if (mCursor.isClosed()) {
			System.out.println("check if cursor is closed");
		return;
		}
		//更新
		mCursor.requery();
	}*/

}
