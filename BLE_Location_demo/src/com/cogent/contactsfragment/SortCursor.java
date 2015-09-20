package com.cogent.contactsfragment;
//Download by http://www.codefans.net
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.cogent.DataBase.DBUser.Contact;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;

public class SortCursor extends CursorWrapper{
	private ArrayList<SortEntry> mSortList;
	private ArrayList<SortEntry> mFilterList;//筛选后的数据list
	private Cursor mCursor;  
	private int mPos;
	
	public SortCursor(Cursor cursor) {
		super(cursor);
		
		mCursor = cursor;
		mSortList = new ArrayList<SortEntry>();
		for( cursor.moveToFirst(); ! cursor.isAfterLast();  cursor.moveToNext()) {
			SortEntry entry = new SortEntry();
			entry.mID =  cursor.getString(cursor.getColumnIndex(Contact._ID));
			entry.mName =  cursor.getString(cursor.getColumnIndex(Contact.CONTACTNAME));
			entry.mOrder = cursor.getPosition();
			entry.mPY = PinyinUtils.getPingYin(entry.mName);
			entry.mNum =  cursor.getString(cursor.getColumnIndex(Contact.CONTACTNO));
			entry.mFisrtSpell = PinyinUtils.getFirstSpell(entry.mName);
			entry.mchoose =false;
			mSortList.add(entry);
		}

		Collections.sort(mSortList, new ComparatorPY());

	}
	
    public static class SortEntry {  
    	public String mID;		  //在数据库中的ID号
		public String mName;  //姓名
		public String mPY;      //姓名拼音
		public String mNum;      //电话号码
		public String mFisrtSpell;      //中文名首字母 例:张雪冰:zxb
		public boolean mchoose;    //是否选中
		public int mOrder;      //在原Cursor中的位置
    }
    
	private class ComparatorPY implements Comparator<SortEntry>{

		@Override
		public int compare(SortEntry lhs, SortEntry rhs) {
			// TODO Auto-generated method stub
			String str1 = lhs.mPY;
			String str2 = rhs.mPY;
			return str1.compareToIgnoreCase(str2);
		}
		
	}

	@Override
	public boolean moveToPosition(int position) {
		mPos = position; 
		if(position < mSortList.size() && position >=0) 
		{
			return mCursor.moveToPosition(mSortList.get(position).mOrder);
		}

        if (position < 0) {  
            mPos = -1;  
        }  
        if (position >= mSortList.size()) {  
            mPos = mSortList.size();  
        }  
        return mCursor.moveToPosition(position);  
	}
	
	@Override
	public boolean moveToFirst() {
		return moveToPosition(0);
	}

	@Override
	public boolean moveToLast() {
		return moveToPosition(getCount() - 1); 
	}

	@Override
	public boolean moveToNext() {
		// TODO Auto-generated method stub
		return moveToPosition(mPos + 1);
	}

	@Override
	public boolean moveToPrevious() {
		// TODO Auto-generated method stub
		return moveToPosition(mPos - 1);
	}
	
	public int binarySearch(String letter) {
        for (int index = 0;   index < mSortList.size(); index++) {  
            if (mSortList.get(index).mPY.substring(0, 1).compareToIgnoreCase(letter) == 0) { 
            	return index;
            }  
        } 
        return -1;
	}
	
	public ArrayList<SortEntry> GetContactsArray() {
 
        	return mSortList;
	}
	
	public ArrayList<SortEntry> FilterSearch(String keyword) {
		mFilterList = new ArrayList<SortEntry>();
		mFilterList.clear();
        //遍历mArrayList  
        for (int i = 0; i < mSortList.size(); i++) {  
            //如果遍历到List包含所输入字符串  
            if (mSortList.get(i).mNum.indexOf(keyword) > 0
            		||isStrInString(mSortList.get(i).mPY,keyword)
            		|| mSortList.get(i).mName.contains(keyword)
            		||isStrInString(mSortList.get(i).mFisrtSpell,keyword)){
                //将遍历到的元素重新组成一个list  
            	
    			SortEntry entry = new SortEntry();
    			entry.mName = mSortList.get(i).mName;
    			entry.mID = mSortList.get(i).mID;
    			entry.mOrder = i;
    			entry.mPY = mSortList.get(i).mPY;
    			entry.mNum = mSortList.get(i).mNum;
    			mFilterList.add(entry);
            }  
        }  
        	return mFilterList;
	}
	
	public boolean isStrInString(String bigStr,String smallStr){
		  if(bigStr.toUpperCase().indexOf(smallStr.toUpperCase())>-1){
			  return true;
		  }else{
			  return false;
		  }
	}
	
	public String getName(int index){
		return mSortList.get(index).mName;
		
	}
	
	public String getNumber(int index){
		return mSortList.get(index).mNum;
		
	}
	
	public String getID(int index){
		return mSortList.get(index).mID;
		
	}

}
