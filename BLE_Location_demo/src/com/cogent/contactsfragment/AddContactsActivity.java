package com.cogent.contactsfragment;

import java.util.Collections;

import com.cogent.QQ.LocationActivity;
import com.cogent.QQ.R;
import com.cogent.util.ContactUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddContactsActivity extends Activity{
	private Button m_SaveBtn;
	private EditText m_EditName;
	private EditText m_EditNum;
	private TextView m_TextTitle;
	private String m_ContactId;
	private int m_Type;
	
	ProgressDialog m_dialogLoading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.add_contacts);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("person");

		m_TextTitle = (TextView)findViewById(R.id.text_title);
		m_EditName = (EditText)findViewById(R.id.edit_name);
		m_EditNum = (EditText)findViewById(R.id.edit_num);

		m_Type = bundle.getInt("tpye");
		m_EditName.setText(bundle.getString("name"));
		m_EditNum.setText(bundle.getString("number"));
		//System.out.println("m_Type的值  " + m_Type);
		if(m_Type == 0)//新增联系人
		{
			m_TextTitle.setText(R.string.addContact);
		}
		else if(m_Type == 1)//编辑联系人
		{
			m_ContactId = bundle.getString("id");
			m_TextTitle.setText(R.string.editContact);
		}

		m_SaveBtn = (Button)findViewById(R.id.btn_save_contact);
		m_SaveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if("".equals(m_EditName.getText().toString()))
				{
					Toast.makeText(AddContactsActivity.this, R.string.inputName, Toast.LENGTH_SHORT).show();
				}
				else if("".equals(m_EditNum.getText().toString()))
				{
					Toast.makeText(AddContactsActivity.this, R.string.inputAccount, Toast.LENGTH_SHORT).show();
				}
				else if(m_Type == 0)
				{
					//新增联系人操作,放在线程中处理
					new SaveContactTask().execute();
				}
				else if(m_Type == 1)
				{
					//更新联系人操作,放在线程中处理
					new ChangeContactTask().execute();
				}
			}
		});
	}

	 class  SaveContactTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ContactUtils.AddContact(m_EditName.getText().toString(), m_EditNum.getText().toString());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(m_dialogLoading!= null)
			{
				m_dialogLoading.dismiss();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddContactsActivity.this);  
            m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
	        m_dialogLoading.setMessage(getString(R.string.saveContact));  
            m_dialogLoading.setCancelable(false);
            m_dialogLoading.show();  
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	 class  ChangeContactTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ContactUtils.ChangeContact(m_EditName.getText().toString(), m_EditNum.getText().toString(),m_ContactId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(m_dialogLoading!= null)
			{
				m_dialogLoading.dismiss();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddContactsActivity.this);  
	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
	        m_dialogLoading.setMessage(getString(R.string.saveContact));  
	        m_dialogLoading.setCancelable(false);
            m_dialogLoading.show();  
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
	}
}
