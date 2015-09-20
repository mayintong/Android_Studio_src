package com.cogent.ViewMenu;

import com.android.volley.VolleyError;
import com.cogent.Communications.Communications;
import com.cogent.DataBase.BLConstants;
import com.cogent.DataBase.DBHelper;

import com.cogent.QQ.LoginActivity;
import com.cogent.QQ.R;
import com.cogent.contactsfragment.LoadContactList;
import com.cogent.util.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class ViewTabber implements IViewFactory, OnClickListener,
        Communications.ResponseListener,
        Communications.ErrorResponseListener{
    private static final String DEBUG_TAG = "ViewTabber";

	/**
	 * 用于保存当前activity
	 */
	public Activity mContext;
    private Communications mComm;
	/**
	 * 当前页面焦点,即显示的页面索引
	 */
	int mCurrentFocus = -1;
	/**
	 * 切换页面时上一个view的索引
	 */
	int lastIndex = -1;
	/**
	 * 底部菜单栏初始化所有控件类的一个实例
	 */
	BottomViewItem item;
	EditText name;
	EditText mobile;
	EditText email;
	
	
	String name_val;
	String mobile_val;
	String email_val;
	String data;
	/**
	 * 存放view的布局容器
	 */
	public ViewContainer viewContainer;

	/**
	 * 首次创建页面的临时view
	 */
	public BaseView newBaseView;
	PopMenu popMenu;
	private LoadContactList loadContact;
	RadioButton genderRb;
	String gender_val;
	private int flag = 1;
	public ViewTabber(Activity context) {
		mContext = context;
        mComm = new Communications(this);
        mComm.setOnResponseListener(this);
        mComm.setOnErrorResponseListener(this);
		item = BottomViewItem.getInstance();
		initTab();
	}
	RadioGroup radgroup;
	/**
	 * 控件初始化    菜单项
	 */
	private void initTab() {
		loadContact = new LoadContactList(mContext);
		popMenu = new PopMenu(mContext);
		popMenu.addItems(new String[] {mContext.getString(R.string.popmenu_presoninfo), mContext.getString(R.string.changepwd), mContext.getString(R.string.popmenu_about), mContext.getString(R.string.logout)});

		for (int i = 0; i < item.viewNum; i++) {
			item.linears[i] = (LinearLayout) mContext.findViewById(item.linears_id[i]);
			item.linears[i].setOnClickListener(this);
			item.images[i] = (ImageView) mContext.findViewById(item.images_id[i]);
			item.texts[i] = (TextView) mContext.findViewById(item.texts_id[i]);
		}
		viewContainer = (ViewContainer) mContext.findViewById(R.id.main_view_container);
		viewContainer.setViewFactory(this);
		switchViewTab(0);
	}

	/**
	 * @param index
	 *            根据索引值切换view
	 */
	public void switchViewTab(int index) {
		if (index == mCurrentFocus)
			return;
		setViewTab(index);
		viewContainer.flipToView(index);
		//System.out.println("flipToView" + index);
	}

	/**
	 * @param index
	 *            根据索引值切换背景
	 */
	public void setViewTab(int index) {
		if (index == mCurrentFocus)
			return;
		lastIndex = mCurrentFocus;
		mCurrentFocus = index;
		for (int i = 0; i < item.viewNum; i++) {
			item.images[i].setBackgroundResource(i == index ? item.images_selected[i] : item.images_unselected[i]);
			item.texts[i].setTextColor(i == index ? mContext.getResources().getColor(R.color.blue) : mContext.getResources().getColor(R.color.bottom_text_unselected));
		}
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < item.viewNum; i++) {
			if (v.getId() == item.linears_id[i]) {
				if (v.getId() == item.linears_id[0]) {
					viewContainer.flipToView(0);
					setViewTab(0);
				}
				if (v.getId() == item.linears_id[1]) {
					viewContainer.flipToView(6);
					setViewTab(1);
					if (flag == 1)
					{
						loadContact.showContactList();
						flag = 0;
					}
				}
			}
				if (v.getId() == item.linears_id[2]) {//3项的index为2,4项index为3
					setViewTab(2);
					popMenu.showAsDropDown(v);
					popMenu.setOnItemClickListener(popmenuItemClickListener);
				}
		}
	}

	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			System.out.println("下拉菜单单击项" + position);
			if (position == 0) {
				//post server to get data 省略
				viewContainer.flipToView(3);
				getAndchangeinfo();
			} else if (position == 1){
				viewContainer.flipToView(4);
				changePwd();
			} else if (position == 2){
				viewContainer.flipToView(5);
			} else {
				
				DBHelper dbHelper;
				dbHelper = new DBHelper(mContext);
				String[] usernames = dbHelper.queryUserOrMapInfo("user_table");
				if (usernames.length > 0) {
					final String tempName = usernames[usernames.length - 1];
					dbHelper.insertOrUpdate(tempName, "", 0);
				}
				
				Intent logout = new Intent(mContext,LoginActivity.class);
				mContext.startActivity(logout);
				mContext.finish();
			}

			popMenu.dismiss();
		}
	};
	private void getAndchangeinfo() {

		
		Button btn_change = (Button)mContext.findViewById(R.id.btn_change);
		gender_val = "";

        Log.d(DEBUG_TAG, "Query user information ...");
        mComm.doVolleyPost(BLConstants.API_QUERY_INFO, null, Communications.TAG_QUERY_USER_INFO);

        

		//根据取得的性别设置radiobutton checked 状态
		/*for (int i=0; i<radgroup.getChildCount(); i++) {
			if (((RadioButton)radgroup.getChildAt(i)).getText().equals(gender_val))
				((RadioButton)radgroup.getChildAt(i)).setChecked(true);
		}
		*/

		

		btn_change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				radgroup = (RadioGroup)mContext.findViewById(R.id.radioGroup);
				// TODO Auto-generated method stub
				//修改、取值、 提交

					// TODO Auto-generated method stub;
				if(((RadioButton)radgroup.getChildAt(0)).isChecked() == true)
					gender_val = "male";
				else
					gender_val = "female";
				
				name_val = name.getText().toString().trim();
				mobile_val = mobile.getText().toString().trim();
				email_val = email.getText().toString().trim();
				//post data to server  省略

                Map<String, String> data_map = new HashMap<String, String>();
                data_map.put(BLConstants.ARG_USER_NAME, name_val);
                data_map.put(BLConstants.ARG_USER_GENDER, gender_val);
                data_map.put(BLConstants.ARG_USER_PHONE, mobile_val);
                data_map.put(BLConstants.ARG_USER_EMAIL, email_val);

                mComm.doVolleyPost(BLConstants.API_CHANGE_INFO, data_map, Communications.TAG_CHANGE_USER_INFO);
			}
			
		});
	}
	
	private void changePwd() {
		
		
		final EditText uid_edit = (EditText)mContext.findViewById(R.id.uid_edt);
		final EditText oldpwd_edit = (EditText)mContext.findViewById(R.id.oldpassword);
		final EditText newpwd_edit = (EditText)mContext.findViewById(R.id.newpassword);
		Button btn_submit = (Button)mContext.findViewById(R.id.btn_submit);


		btn_submit.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View arg0) {
				
				String uid_val = uid_edit.getText().toString().trim();
				String oldpwd_val = oldpwd_edit.getText().toString().trim();
				String newpwd_val = newpwd_edit.getText().toString().trim();

                Map<String, String> data_map = new HashMap<String, String>();
                data_map.put(BLConstants.ARG_USER_ID, uid_val);
                data_map.put(BLConstants.ARG_USER_PWD, oldpwd_val);
                data_map.put(BLConstants.ARG_USER_NEW_PWD, newpwd_val);

                mComm.doVolleyPost(BLConstants.API_CHANGE_PASSWORD, data_map, Communications.TAG_CHANGE_PASSWORD);
			}
		});
	}
	
    @Override
    public void onSuccess(String tag, String response) {
        if (tag.equals(Communications.TAG_QUERY_USER_INFO)) {
            radgroup = (RadioGroup)mContext.findViewById(R.id.radioGroup);
            name = (EditText)mContext.findViewById(R.id.name_edit);
            mobile = (EditText)mContext.findViewById(R.id.mobile_edit);
            email = (EditText)mContext.findViewById(R.id.email_edit);
            name.setText(HttpUtil.parseJsons(response, BLConstants.ARG_USER_INFO, BLConstants.ARG_USER_NAME));
            mobile.setText(HttpUtil.parseJsons(response, BLConstants.ARG_USER_INFO, BLConstants.ARG_USER_PHONE));
            email.setText(HttpUtil.parseJsons(response, BLConstants.ARG_USER_INFO, BLConstants.ARG_USER_EMAIL));
            if(HttpUtil.parseJsons(response, BLConstants.ARG_USER_INFO, BLConstants.ARG_USER_GENDER).equals("male"))
                ((RadioButton)radgroup.getChildAt(0)).setChecked(true);
            else
                ((RadioButton)radgroup.getChildAt(1)).setChecked(true);
        }

        if (tag.equals(Communications.TAG_CHANGE_USER_INFO)) {
            viewContainer.flipToView(0);
            setViewTab(0);
        }

        if (tag.equals(Communications.TAG_CHANGE_PASSWORD)) {
            DBHelper dbHelper;
            dbHelper = new DBHelper(mContext);
            String[] usernames = dbHelper.queryUserOrMapInfo("user_table");
            if (usernames.length > 0) {
                final String tempName = usernames[usernames.length - 1];
                dbHelper.insertOrUpdate(tempName, "", 0);
            }
            Toast.makeText(mContext,"Change success", Toast.LENGTH_SHORT).show();
            Intent logout = new Intent(mContext,LoginActivity.class);
            mContext.startActivity(logout);
            mContext.finish();
        }
    }

    @Override
    public void onFail(String tag, String response) {
        if (tag.equals(Communications.TAG_SINGLE_TRACK)) {
            int error_code = HttpUtil.parseJsonint(response, BLConstants.ARG_ERROR_CODE);
            String error_descrip = tag + BLConstants.MSG_FAIL_DESC + HttpUtil.parse_error(error_code);
            System.out.println(error_descrip);
        }
    }
    @Override
    public void refreshUI(){}
    @Override
    public void onImageResponse(String tag, Bitmap response) {}
    @Override
    public void onResponse(String tag, String response) {
        Log.d(DEBUG_TAG, "TAG:" + tag + "---Response:" + response);

        String result = HttpUtil.parseJson(response, BLConstants.ARG_REQ_RESULT);
        Boolean parse_result = result.equals(BLConstants.MSG_PASS);

        if (parse_result)
            onSuccess(tag, response);
        else
            onFail(tag, response);
    }

    @Override
    public void onErrorResponse(String tag, VolleyError volleyError) {
        Log.e(DEBUG_TAG, volleyError.getMessage(), volleyError);
    }
    
	@Override
	public View createView(int index) {
		newBaseView = new BaseView(mContext, item.layouts_id[index], this);
		return newBaseView.getView();
	}
}

