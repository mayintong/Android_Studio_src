package com.cogent.QQ;

import com.cogent.Communications.Communications;
import com.cogent.DataBase.BLConstants;
import com.cogent.util.ClassPathResource;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**注册界面activity*/
public class RegisterActivity extends BaseActivity implements android.view.View.OnClickListener{
	public static final int REGION_SELECT = 1;
	private TextView tv_top_title;
	private Button btn_title_left, btn_title_right, btn_reg, btn_get_checkcode;
	private EditText name_edit, et_accountNo, email_edit, pwd_edit, pwdConfirm_edit, checkCode_edit, phoneNum_edit;
	private RadioGroup rgroup;
	private RadioButton genderRb;
	private GestureDetector mGestureDetector;

	private String nameVal;
	private String accountVal;
	private String emailVal;
	private String pwdVal;
	private String pwdConfirmVal;
	private String phoneVal;
	private String genderVal;
	//private TimeCount time;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
	}

	private void initView(){
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		tv_top_title.setText(R.string.register_title);

		btn_title_right = (Button) findViewById(R.id.btn_title_right);
		btn_title_right.setVisibility(View.GONE);

		btn_title_left = (Button) findViewById(R.id.btn_title_left);
		btn_title_left.setOnClickListener(this);

		btn_reg = (Button) findViewById(R.id.btn_reg);
		btn_reg.setOnClickListener(this);

		btn_get_checkcode = (Button) findViewById(R.id.btn_getCode);
		btn_get_checkcode.setOnClickListener(this);
		name_edit = (EditText)findViewById(R.id.name_edit);	
		et_accountNo = (EditText)findViewById(R.id.accountNo_edit);
		checkCode_edit = (EditText) findViewById(R.id.checkCode_edit);
		email_edit = (EditText)findViewById(R.id.email_edit);
		phoneNum_edit = (EditText) findViewById(R.id.mobile_edit);
		pwd_edit = (EditText)findViewById(R.id.regPwd_edit);
		
		rgroup = (RadioGroup)findViewById(R.id.radioGroup);
		pwdConfirm_edit = (EditText)findViewById(R.id.pwdConfirm_edit);
		
		pwd_edit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				pwdVal = pwd_edit.getText().toString();
				if (!hasFocus) {
					if (pwdVal.length() <6 ||pwdVal.length() >15)
						Toast.makeText(getApplication(), R.string.passwd_length, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		pwdConfirm_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				pwdConfirmVal = pwdConfirm_edit.getText().toString();
				if (!hasFocus) {
					if (!pwdConfirmVal.equals(pwdVal))
						Toast.makeText(getApplication(), R.string.passwd_identical, Toast.LENGTH_SHORT).show();
				}
			}
		});
		phoneNum_edit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				phoneVal = phoneNum_edit.getText().toString();
				if (!hasFocus) {
					if (phoneVal.length() <11 || ClassPathResource.isMobileNO(phoneVal) == false)
						Toast.makeText(getApplication(), R.string.phone_length, Toast.LENGTH_SHORT).show();
				}
			}
		});

		email_edit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				emailVal = email_edit.getText().toString();
				if (!hasFocus) {
					if (emailVal.equals(""))
						Toast.makeText(getApplication(), R.string.email_tip, Toast.LENGTH_SHORT).show();
					else if (ClassPathResource.isEmail(emailVal) == false)
						Toast.makeText(getApplication(), R.string.email_format_error, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){

		case R.id.btn_title_left:
			RegisterActivity.this.finish();
			break;
		case R.id.btn_reg:
			nameVal = name_edit.getText().toString().trim();
			accountVal = et_accountNo.getText().toString().trim();
			emailVal = email_edit.getText().toString().trim();
			phoneVal = phoneNum_edit.getText().toString().trim();
			pwdVal = pwd_edit.getText().toString().trim();
			pwdConfirmVal = pwdConfirm_edit.getText().toString().trim();
			//genderVal = genderRb.getText().toString();
			if(((RadioButton)rgroup.getChildAt(0)).isChecked() == true)
				genderVal = "male";
			else
				genderVal = "female";
            
            Map<String, String> register_map = new HashMap<String, String>();
            register_map.put(BLConstants.ARG_USER_ID, accountVal);
            register_map.put(BLConstants.ARG_USER_PWD, pwdVal);
            register_map.put(BLConstants.ARG_USER_NAME, nameVal);
            register_map.put(BLConstants.ARG_USER_GENDER, genderVal);
            register_map.put(BLConstants.ARG_USER_PHONE, phoneVal);
            register_map.put(BLConstants.ARG_USER_EMAIL, emailVal);

            mComm.doVolleyPost(BLConstants.API_REGISTER, register_map, Communications.TAG_REGISTER);
	       
	        break;
		}
	}

    @Override
    public void onSuccess(String tag, String response) {
        if (tag.equals(Communications.TAG_REGISTER)) {
            showNotification(BLConstants.MSG_REG_OK);
        }
    }
}
