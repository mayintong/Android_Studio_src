package com.cogent.DataBase;

import android.provider.BaseColumns;

public final class DBUser {

	public static final class User implements BaseColumns {
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String ISSAVED = "issaved";
	}

	public static final class Map implements BaseColumns {
		public static final String MAPID = "map_id";
		public static final String SITEID = "site_id";
		public static final String MAPVERSION = "map_version";
		public static final String SCALESIZE = "scalesize";
		public static final String MAP = "map";
	}

	public static final class Position implements BaseColumns {
		public static final String MAPID = "map_id";
		public static final String MAPVERSION = "map_version";
		public static final String Mac = "device_mac";
		public static final String X_COORD = "X";
		public static final String Y_COORD = "Y";
	}
	
	public static final class Contact implements BaseColumns {
		public static final String CONTACTNAME = "contact_name";
		public static final String CONTACTNO = "contact_no";
	}
}

