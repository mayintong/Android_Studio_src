package com.cogent.ViewMenu;



import com.cogent.QQ.R;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomViewItem {

	public static BottomViewItem instance;

	public static BottomViewItem getInstance() {
		if (instance == null) {
			instance = new BottomViewItem();
		}
		return instance;
	}
	// 底部菜单4项的情况
	/*public int viewNum = 4;
	public ImageView[] images = new ImageView[viewNum];
	public TextView[] texts = new TextView[viewNum];
	public LinearLayout[] linears = new LinearLayout[viewNum];
	public int[] images_id = new int[] { R.id.message_image, R.id.contacts_image, R.id.news_image, R.id.setting_image };
	public int[] texts_id = new int[] { R.id.message_text, R.id.contacts_text, R.id.news_text, R.id.setting_text };
	public int[] linears_id = new int[] { R.id.message_layout, R.id.contacts_layout, R.id.news_layout, R.id.setting_layout };
	public int[] images_selected = new int[] { R.drawable.message_selected, R.drawable.contacts_selected, R.drawable.news_selected, R.drawable.setting_selected };
	public int[] images_unselected = new int[] { R.drawable.message_unselected, R.drawable.contacts_unselected, R.drawable.news_unselected, R.drawable.setting_unselected };
	//ViewTabber.java 文件中createView方法调用
	public int[] layouts_id = new int[] { R.layout.message_view, R.layout.contacts_view, R.layout.change_userinfo, R.layout.change_password };
	*/
	//底部菜单3项的情况
	public int viewNum = 3;
	public ImageView[] images = new ImageView[viewNum];
	public TextView[] texts = new TextView[viewNum];
	public LinearLayout[] linears = new LinearLayout[viewNum];
	public int[] images_id = new int[] { R.id.message_image, R.id.contacts_image, R.id.setting_image };
	public int[] texts_id = new int[] { R.id.message_text, R.id.contacts_text, R.id.setting_text };
	public int[] linears_id = new int[] { R.id.message_layout, R.id.contacts_layout, R.id.setting_layout };
	public int[] images_selected = new int[] { R.drawable.menu_location_selected, R.drawable.menu_contacts_selected, R.drawable.menu_setting_selected };
	public int[] images_unselected = new int[] { R.drawable.menu_location_normal, R.drawable.menu_contacts_normal, R.drawable.menu_setting_normal };
	//ViewTabber.java 文件中createView方法调用
	public int[] layouts_id = new int[] { R.layout.activity_location, R.layout.activity_location, R.layout.activity_location, R.layout.change_userinfo, R.layout.change_password, R.layout.about_findme,R.layout.contactsfragment};
}
