package com.cogent.util;


import com.cogent.DataBase.DBUser.Contact;
import com.cogent.contactsfragment.SortCursor.SortEntry;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ContactUtils {
	public static Context m_context;
	//所有联系人的数据list
	public static ArrayList<SortEntry> mPersons = new ArrayList<SortEntry>();
	//SQLiteDatabase db;
	public static final String[] CONTACT_COLS = {Contact.CONTACTNAME, Contact.CONTACTNO};
	
	//初始化传入主Activity的上下文
	public static void init(Context context)
	{
		m_context = context;
	}

	//往数据库中新增联系人
	public static void AddContact(String name, String account)
	{
		ContentValues values = new ContentValues(); 

        //往data表插入姓名数据 
        values.put(Contact.CONTACTNAME, name);
        values.put(Contact.CONTACTNO, account); 
        m_context.getContentResolver().insert(ContactProvider.CONTENT_URI, values);
	}
	
	// 查询数据库中联系人
	public static Cursor QueryContact() {
		Cursor cursor = m_context.getContentResolver().query(ContactProvider.CONTENT_URI , null, null, null, null);
		return cursor;
	}

	//更改数据库中联系人
	public static void ChangeContact(String name, String number, String ContactId)
	{
		ContentValues values = new ContentValues();

        values.put(Contact.CONTACTNAME, name);
        values.put(Contact.CONTACTNO, number);
        m_context.getContentResolver().update(ContactProvider.CONTENT_URI,
                        values,
                        "_ID=?",
                        new String[] { ContactId });
		
	}
	
	//删除联系人
	public static void DeleteContact(String ContactId)
	{
      Uri uri = Uri.withAppendedPath(ContactProvider.CONTENT_URI, ContactId);   
		m_context.getContentResolver().delete(uri, null, null);
	}
}

