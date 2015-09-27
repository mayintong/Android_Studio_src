package com.cogent.contactsfragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.cogent.ContentObserver.ContactsContentObserver;
import com.cogent.DataBase.DBUser.Contact;
import com.cogent.QQ.BaseActivity;
import com.cogent.QQ.LocationActivity;
import com.cogent.QQ.R;
import com.cogent.contactsfragment.AlphabetScrollBar.OnTouchingLetterChangedListener;
import com.cogent.contactsfragment.SortCursor.SortEntry;
import com.cogent.util.ContactProvider;
import com.cogent.util.ContactUtils;
import java.util.Map;
import java.util.HashMap;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.VolleyError;
import java.text.SimpleDateFormat;

import com.cogent.Communications.RequestManager;
import com.cogent.util.HttpUtil;
import com.cogent.Communications.BLNotifier;
import com.cogent.DataBase.BLConstants;
import com.cogent.Communications.Communications;

//package com.example.adr_client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;


public class LoadContactList extends BaseActivity {
	private Activity mContext;
		//字母列视图View
		private AlphabetScrollBar m_asb;
		//显示选中的字母
		private TextView m_letterNotice;
		//联系人的列表
		private ListView m_contactslist;
		//联系人列表的适配器
		private ContactsCursorAdapter m_contactsAdapter;
		//筛选后的适配器
		private FilterAdapter m_FAdapter;
		//筛选查询后的数据list
		private ArrayList<SortEntry> mFilterList = new ArrayList<SortEntry>();
		//加载器监听器
		private ContactsLoaderListener m_ContactsCallback = new ContactsLoaderListener();
		//搜索过滤联系人EditText
		private EditText m_FilterEditText;
		//没有匹配联系人时显示的TextView
		private TextView m_listEmptyText;
		//新增联系人按钮
		private ImageButton m_AddContactBtn;
		//最上面的layout
		private FrameLayout m_topcontactslayout;
		//联系人内容观察者
		private ContactsContentObserver ContactsCO;  
		//选中的联系人名字
		private String ChooseContactName;
		//选中的联系人号码
		private String ChooseContactNumber;
		//选中的联系人ID
		private String ChooseContactID;
		//加载对话框
		ProgressDialog m_dialogLoading;
		//删除搜索文本图标
		ImageView deleteText;
	public LoadContactList(Activity context) {
		mComm = new Communications(this);
		mComm.setOnResponseListener(this);
		mComm.setOnErrorResponseListener(this);
		mContext = context;
	}

	@Override
	public void refreshUI(){}
	@Override
	public void onImageResponse(String tag, Bitmap response) {}
	@Override
	public void onSuccess(String tag, String response) {
		if (tag.equals(Communications.TAG_SINGLE_TRACK)) {

			BLNotifier.notifyUi(BLNotifier.TYPE_AUTO_UPDATE_LOCATION, response);
		}

	}
	@Override
	public void onResponse(String tag, String response) {
		//Log.d(DEBUG_TAG, "TAG:" + tag + "---Response:" + response);


        String result = HttpUtil.parseJson(response, "friend");
		String[] pos = result.split(";");
		Log.e("llllllllllllllll",pos.length-1+"");
		for(int i=0;i<=pos.length-1;i++) {
			String[] temp_info = pos[i].split(",");
			Log.e("friend",temp_info[0]+","+temp_info[1]+','+temp_info[2]);
			//LocationActivity.PeopleToShow.put(temp_info[0], 800 + "," + temp_info[2] + "0" + "," + temp_info[0]);
			LocationActivity.PeopleToShow.put(temp_info[0], temp_info[1] + "," + temp_info[2] + "," + temp_info[0]);
		}
//        Boolean parse_result = result.equals(BLConstants.MSG_PASS);
//
//        if (parse_result)
//            onSuccess(tag, response);
//        else
//            onFail(tag, response);

	}

	@Override
	public void onFail(String tag, String response) {
//		if (tag.equals(Communications.TAG_SINGLE_TRACK)) {
//			int error_code = HttpUtil.parseJsonint(response, BLConstants.ARG_ERROR_CODE);
//			String error_descrip = tag + BLConstants.MSG_FAIL_DESC + HttpUtil.parse_error(error_code);
//			System.out.println(error_descrip);
//		}
	}
public void showContactList()  {
		initView();
		ContactsCO = new ContactsContentObserver(new Handler());  
		mContext.getContentResolver().registerContentObserver(ContactProvider.CONTENT_URI, true, ContactsCO); 

		//得到字母列的对象,并设置触摸响应监听器
		m_asb.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//触摸字母列时,将联系人列表更新到首字母出现的位置
				SortCursor ContactsCursor = (SortCursor)m_contactsAdapter.getCursor();
				if(ContactsCursor != null) 
				{
					int idx = ContactsCursor.binarySearch(s);
					System.out.println("idx: " + idx);
					if(idx != -1)
					{
						m_contactslist.setSelection(idx);

					}
				}
			}
		});

		//联系人列表长按监听
		m_contactslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				Vibrator vib = (Vibrator)mContext.getSystemService(Service.VIBRATOR_SERVICE);
				vib.vibrate(50);
				
				if(m_topcontactslayout.getVisibility() == View.VISIBLE)
				{
					SortCursor ContactsCursor = (SortCursor)m_contactsAdapter.getCursor();
					ChooseContactName = ContactsCursor.getName(arg2);
					ChooseContactNumber = ContactsCursor.getNumber(arg2);
					ChooseContactID = ContactsCursor.getID(arg2);
				}
				else
				{
					ChooseContactName = mFilterList.get(arg2).mName;
					ChooseContactNumber = mFilterList.get(arg2).mNum;
					ChooseContactID = mFilterList.get(arg2).mID;
				}
				
				AlertDialog ListDialog = new AlertDialog.Builder(mContext).
						setTitle(ChooseContactName).
//		                setItems(new String[]{mContext.getString(R.string.dial), mContext.getString(R.string.deleteContact),
//								mContext.getString(R.string.editContact)}, new DialogInterface.OnClickListener() {
		setItems(new String[]{"dial", "deleteContact",
		"editContact", "ShowLocation","DisShowLocation","FindHim!"}, new DialogInterface.OnClickListener() {
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == 0) {
//									Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + ChooseContactNumber));
//									mContext.startActivity(intent);
			//parseLocation(1 + "," + 100 + "," +200 , 2);
		} else if (which == 1) {
			AlertDialog DeleteDialog = new AlertDialog.Builder(mContext).
					setTitle(R.string.deleteTip).
					setMessage(mContext.getString(R.string.deleteContact) + ChooseContactName + "?").
					setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除联系人操作,放在线程中处理
							new DeleteContactTask().execute();
						}
					}).
					setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).
					create();
			DeleteDialog.show();
		} else if (which == 2) {
			Bundle bundle = new Bundle();
			bundle.putInt("tpye", 1);
			bundle.putString("id", ChooseContactID);
			bundle.putString("name", ChooseContactName);
			bundle.putString("number", ChooseContactNumber);

			Intent intent = new Intent(mContext, AddContactsActivity.class);
			intent.putExtra("person", bundle);
			mContext.startActivity(intent);
		} else if (which == 3) {
			Map<String, String> track_map = new HashMap<String, String>();
			track_map.put("userid","admin");
			track_map.put("friendid",ChooseContactName);
			mComm.doVolleyPost(BLConstants.API_REQ_FRIEND, track_map, Communications.TAG_QUERY_POSITION);
			//LocationActivity.PeopleToShow.put(ChooseContactName,"400,400,"+ChooseContactName);
//			Intent it = new Intent("show_myt")；
//			sendBroadcast(it);
//			Intent intent = new Intent();
//			Bundle bundle = new Bundle();
////			bundle.putString("strSex", strSex);
////			bundle.putDouble("douHeight", douHeight);
//			bundle.putInt("LocX", 400);
//			bundle.putInt("LocY", 400);
//			intent.setClass(mContext, LocationActivity.class);
//			intent.putExtra("info", bundle);
		}
		else if (which == 4) {
			//LocationActivity.flag_draw_myt=0;
			LocationActivity.PeopleToShow.remove(ChooseContactName);
//			Intent it = new Intent("show_myt")；
//			sendBroadcast(it);
//			Intent intent = new Intent();
//			Bundle bundle = new Bundle();
////			bundle.putString("strSex", strSex);
////			bundle.putDouble("douHeight", douHeight);
//			bundle.putInt("LocX", 400);
//			bundle.putInt("LocY", 400);
//			intent.setClass(mContext, LocationActivity.class);
//			intent.putExtra("info", bundle);
		} else if (which == 5){




		}
	}
						}).
						create(); 
					ListDialog.show(); 
		        
				//return false;
			}
		});

    	//初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				if(!"".equals(s.toString().trim()))
				{  
					//根据编辑框值过滤联系人并更新联系列表
					SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
					mFilterList = data.FilterSearch(s.toString().trim());

					//如果没有过滤出联系人

					m_FAdapter = new FilterAdapter(mContext, mFilterList);
					m_contactslist.setAdapter(m_FAdapter);

					m_asb.setVisibility(View.GONE);
					m_topcontactslayout.setVisibility(View.GONE);
				}
				else
				{
					m_contactslist.setAdapter(m_contactsAdapter);
					m_topcontactslayout.setVisibility(View.VISIBLE);
					m_asb.setVisibility(View.VISIBLE);
				}
			}
////////////////////////////




			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void initView() {
		m_asb = (AlphabetScrollBar)mContext.findViewById(R.id.alphabetscrollbar);

		m_letterNotice = (TextView)mContext.findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);
		
		mContext.getLoaderManager().initLoader(0,null,m_ContactsCallback);
		m_contactslist = (ListView)mContext.findViewById(R.id.pb_listvew);
		m_contactsAdapter = new ContactsCursorAdapter(mContext, null);
		m_contactslist.setAdapter(m_contactsAdapter);
		
		m_listEmptyText = (TextView)mContext.findViewById(R.id.nocontacts_notice);
		
		m_AddContactBtn = (ImageButton)mContext.findViewById(R.id.add_contacts);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		
		m_topcontactslayout = (FrameLayout)mContext.findViewById(R.id.top_contacts_layout);
		m_FilterEditText = (EditText)mContext.findViewById(R.id.pb_search_edit);
		
	}

	private class  DeleteContactTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ContactUtils.DeleteContact(ChooseContactID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(m_dialogLoading!= null)
			{
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(mContext);  
	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
	        m_dialogLoading.setMessage(mContext.getString(R.string.onDeleting));
	        m_dialogLoading.setCancelable(false);
	        m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			 Log.i("iBeacon", "onProgressUpdate"); 
		}
	}

	private class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v == m_AddContactBtn)
			{
				Bundle bundle = new Bundle();
				bundle.putInt("tpye", 0);
				bundle.putString("name", "");
				bundle.putString("number", "");
				
				Intent intent = new Intent(mContext, AddContactsActivity.class);
				intent.putExtra("person", bundle);
				mContext.startActivity(intent);
			}
		}
		
	}
	//加载器的监听器 
	private class ContactsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>{
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				return new SortCursorLoader(mContext,ContactProvider.CONTENT_URI, 
						null, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
					m_contactsAdapter.swapCursor(arg1);
				
				SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
				System.out.println("SortCursor : " + data);
				if(m_topcontactslayout.getVisibility() == View.VISIBLE)
				{
					ContactUtils.mPersons = data.GetContactsArray();
					//refresh listview
					mContext.getLoaderManager().restartLoader(0,null,m_ContactsCallback);
				}
				else
				{
					mFilterList = data.FilterSearch(m_FilterEditText.getText().toString().trim());
					m_FAdapter = new FilterAdapter(mContext, mFilterList);
					m_contactslist.setAdapter(m_FAdapter);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				m_contactsAdapter.swapCursor(null);
			}
		}
		//联系人列表项适配器
		private class ContactsCursorAdapter extends CursorAdapter{
			int ItemPos = -1;
			private ArrayList<SortCursor.SortEntry> list;
			private Context context;

			public ContactsCursorAdapter(Context context, Cursor c) {
				super(context, c);
				this.context = context;
				// TODO Auto-generated constructor stub
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ItemPos = position;
				return super.getView(position, convertView, parent);
			}

			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				if(cursor == null)
				{
					System.out.println("check bindView cursor is null");
					return;
				}

				System.out.println(" LoadContactList bindView contact name :" + cursor.getString(cursor.getColumnIndex(Contact.CONTACTNAME)));
	            TextView name = (TextView) view.findViewById(R.id.contacts_name);
	            name.setText(cursor.getString(cursor.getColumnIndex(Contact.CONTACTNAME)));
				//字母提示textview的显示 
				TextView letterTag = (TextView)view.findViewById(R.id.pb_item_LetterTag);
				//获得当前姓名的拼音首字母
				String firstLetter = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(Contact.CONTACTNAME))).substring(0,1).toUpperCase();

				//如果是第1个联系人 那么letterTag始终要显示
				if(ItemPos == 0)
				{
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
				else
				{
					//获得上一个姓名的拼音首字母
					cursor.moveToPrevious();
					String firstLetterPre = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(Contact.CONTACTNAME))).substring(0,1).toUpperCase();

					//比较一下两者是否相同
					if(firstLetter.equals(firstLetterPre))
					{
						letterTag.setVisibility(View.GONE);
					}
					else 
					{
						letterTag.setVisibility(View.VISIBLE);
						letterTag.setText(firstLetter);
					}
				}
			}

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
			}
		}

	    class FilterAdapter extends BaseAdapter{

	    	private LayoutInflater mInflater;
	    	private ArrayList<SortCursor.SortEntry> data;
	    	private Context context;

	        public FilterAdapter(Context context,
	        		ArrayList<SortCursor.SortEntry> data) {
	    	    this.mInflater = LayoutInflater.from(context);
	    	    this.data = data;
	    	    this.context = context;
	        }

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return data.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				arg1 = mInflater.inflate(R.layout.contacts_list_item, null);

				//姓名显示
				TextView nameCtrl = (TextView)arg1.findViewById(R.id.contacts_name);			
				String strName = data.get(arg0).mName;
				nameCtrl.setText(strName);
				return arg1;
			}
	    }
}
