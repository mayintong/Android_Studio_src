package com.cogent.DataBase;

import java.sql.Blob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cogent.DataBase.DBUser.Contact;
import com.cogent.DataBase.DBUser.Map;
import com.cogent.DataBase.DBUser.Position;
import com.cogent.DataBase.DBUser.User;

/**
 * 操作数据库辅助类
 * 
 */
public class DBHelper {
	public static final int DB_VERSION = 3;
	public static final String DB_NAME = "account.db";//数据库名
	public static final String USER_TABLE_NAME = "user_table";//用户表名
	public static final String MAP_TABLE_NAME = "map_table";
	public static final String POS_TABLE_NAME = "position_table";
	public static final String CONTACT_TABLE_NAME = "contact_table";
	public static final String[] USER_COLS = { User.USERNAME, User.PASSWORD,
			User.ISSAVED };

	public static final String[] MAP_COLS = {Map.MAP };
	public static final String[] CONTACT_COLS = {Contact.CONTACTNAME, Contact.CONTACTNO};

	public static SQLiteDatabase db;
	DBOpenHelper dbOpenHelper;

	public DBHelper(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		establishDb();
	}

	/**
	 * 打开数据库
	 */
	private void establishDb() {
		if (this.db == null) {
			System.out.println("打开数据库");
			this.db = this.dbOpenHelper.getWritableDatabase();
		}
	}

	/**
	 * 插入一条记录
	 * 
	 * @param map
	 *            要插入的记录
	 * @param tableName
	 *            表名
	 * @return 插入记录的id -1表示插入不成功
	 */
	public long insertOrUpdate(String userName, String password, int isSaved) {
		boolean isUpdate = false;
		//String[] usernames = queryAllUserName();
		String[] usernames = queryUserOrMapInfo(USER_TABLE_NAME);
		for (int i = 0; i < usernames.length; i++) {
			if (userName.equals(usernames[i])) {
				isUpdate = true;
				break;
			}
		}
		long id = -1;
		if (isUpdate) {
			id = update(userName, password, isSaved);
		} else {
			if (db != null) {
				ContentValues values = new ContentValues();
				values.put(User.USERNAME, userName);
				values.put(User.PASSWORD, password);
				values.put(User.ISSAVED, isSaved);
				id = db.insert(USER_TABLE_NAME, null, values);
			}
		}
		return id;
	}

	/**
	 * 删除一条记录
	 * 
	 * @param userName
	 *            用户名
	 * @param tableName
	 *            表名
	 * @return 删除记录的id -1表示删除不成功
	 */
	public long delete(String userName) {
		long id = db.delete(USER_TABLE_NAME, User.USERNAME + " = '" + userName
				+ "'", null);
		return id;
	}

	/**
	 * 更新一条记录
	 * 
	 * @param
	 * 
	 * @param tableName
	 *            表名
	 * @return 更新过后记录的id -1表示更新不成功
	 */
	public long update(String username, String password, int isSaved) {
		ContentValues values = new ContentValues();
		values.put(User.USERNAME, username);
		values.put(User.PASSWORD, password);
		values.put(User.ISSAVED, isSaved);
		long id = db.update(USER_TABLE_NAME, values, User.USERNAME + " = '"
				+ username + "'", null);
		return id;
	}

	/**
	 * 根据用户名查询密码
	 * 
	 * @param username
	 *            用户名
	 * @param tableName
	 *            表名
	 * @return Hashmap 查询的记录
	 */
	public String queryPasswordByName(String username) {
		String sql = "select * from " + USER_TABLE_NAME + " where "
				+ User.USERNAME + " = '" + username + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String password = "";
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			password = cursor.getString(cursor.getColumnIndex(User.PASSWORD));
		}

		return password;
	}

	/**
	 * 记住密码选项框是否被选中
	 * 
	 * @param username
	 * @return
	 */
	public int queryIsSavedByName(String username) {
		String sql = "select * from " + USER_TABLE_NAME + " where "
				+ User.USERNAME + " = '" + username + "'";
		Cursor cursor = db.rawQuery(sql, null);
		int isSaved = 0;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			isSaved = cursor.getInt(cursor.getColumnIndex(User.ISSAVED));
		}
		return isSaved;
	}

	/**
	 * 查询所有用户名
	 * 
	 * @param tableName
	 *            表名
	 * @return 所有记录的list集合
	 */
	/*public String[] queryAllUserName() {
		if (db != null) {
			Cursor cursor = db.query(USER_TABLE_NAME, null, null, null, null,
					null, null);
			int count = cursor.getCount();
			String[] userNames = new String[count];
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					userNames[i] = cursor.getString(cursor
							.getColumnIndex(User.USERNAME));
					cursor.moveToNext();
				}
			}
			return userNames;
		} else {
			return new String[0];
		}

	}*/
	
	public String[] queryUserOrMapInfo(String table) {
		if (db != null) {
			Cursor cursor;
			if (table.equalsIgnoreCase("user_table")) 
				cursor = db.query(USER_TABLE_NAME, null, null, null, null,
					null, null);
			else if (table.equalsIgnoreCase("map_table")) 
				cursor = db.query(MAP_TABLE_NAME, null, null, null, null,
						null, null);
			else 				
				cursor = db.query(POS_TABLE_NAME, null, null, null, null,
					null, null);
			int count = cursor.getCount();
			String[] infos = new String[count];
			if (count > 0) {
				//第一次读取Cursor对象中的数据时，一定先移动游标，
				//否则游标的位置在第一条记录之前，引发异常
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					if (table.equalsIgnoreCase("user_table"))
						infos[i] = cursor.getString(cursor
								.getColumnIndex(User.USERNAME));
					else if (table.equalsIgnoreCase("map_table"))
						infos[i] = cursor.getString(cursor
								.getColumnIndex(Map.MAPVERSION));
					else 
						infos[i] = cursor.getString(cursor
								.getColumnIndex(Position.MAPVERSION));
					cursor.moveToNext();
				}
			}
			return infos;
		} else {
			return new String[0];
		}

	}

	/* 地图的数据表相关的方法*/
	
	public long insertOrUpdate(int mapId, int siteId, String mapVersion,float scalesize, byte[] map) {
		boolean isUpdate = false;
		String[] mapVersions = queryUserOrMapInfo(MAP_TABLE_NAME);
		for (int i = 0; i < mapVersions.length; i++) {
			if (mapVersion.equals(mapVersions[i])) {
				isUpdate = true;
				break;
			}
		}
		long id = -1;
		if (isUpdate) {
			id = update(mapId,siteId, mapVersion,scalesize, map);
		} else {
			if (db != null) {
				ContentValues values = new ContentValues();
				values.put(Map.MAPID, mapId);
				values.put(Map.SITEID, siteId);
				values.put(Map.MAPVERSION, mapVersion);
				values.put(Map.SCALESIZE, scalesize);
				values.put(Map.MAP, map);
				id = db.insert(MAP_TABLE_NAME, null, values);
			}
		}
		return id;
	}

	public long update(int mapId, int siteId, String mapVersion,float scalesize, byte[] map) {
		ContentValues values = new ContentValues();
		values.put(Map.MAPID, mapId);
		values.put(Map.SITEID, siteId);
		values.put(Map.MAPVERSION, mapVersion);
		values.put(Map.SCALESIZE, scalesize);
		values.put(Map.MAP, map);
		long id = db.update(MAP_TABLE_NAME, values, Map.MAPVERSION + " = '"
				+ mapVersion + "'", null);
		return id;
	}
	public float queryScalesize(int mapId){
		String sql = "select * from " + MAP_TABLE_NAME + " where " 
				+ Map.MAPID + " = '" + mapId + "'";;
		
		Cursor cursor = db.rawQuery(sql, null);
				float scalesize = 1;
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					scalesize = cursor.getFloat(cursor.getColumnIndex(Map.SCALESIZE));
				}

		return scalesize;
	}
	public Bitmap queryMap(int mapId) {
		String sql = "select * from " + MAP_TABLE_NAME + " where " 
				+ Map.MAPID + " = '" + mapId + "';";
		Cursor cursor = db.rawQuery(sql, null);
		byte[] data = null ;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			data = cursor.getBlob(cursor.getColumnIndex(Map.MAP));
		}
		else
			return null;
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	/*public String[] queryMapVersion() {
		if (db != null) {
			Cursor cursor = db.query(MAP_TABLE_NAME, null, null, null, null,
					null, null);
			int count = cursor.getCount();
			String[] mapVersions = new String[count];
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					mapVersions[i] = cursor.getString(cursor
							.getColumnIndex(Map.MAPVERSION));
					cursor.moveToNext();
				}
			}
			return mapVersions;
		} else {
			return new String[0];
		}
	}*/

	/*地图位置信息表相关方法*/
	public long insertOrUpdate( String mapId, String mapVersion, String mac, int x_coord, int y_coord) {
		boolean isUpdate = false;
		String[] mapVersions = queryUserOrMapInfo(POS_TABLE_NAME);
		for (int i = 0; i < mapVersions.length; i++) {
			if (mapVersion.equals(mapVersions[i])) {
				isUpdate = true;
				break;
			}
		}
		long id = -1;
		if (isUpdate) {
			id = update(mapId, mapVersion, mac, x_coord, y_coord);
		} else {
			if (db != null) {
				ContentValues values = new ContentValues();
				values.put(Position.MAPID, mapId);
				values.put(Position.MAPVERSION, mapVersion);
				values.put(Position.Mac, mac);
				values.put(Position.X_COORD, x_coord);
				values.put(Position.Y_COORD, y_coord);
				id = db.insert(POS_TABLE_NAME, null, values);
			}
		}
		return id;
	}

	public long update(String mapId, String mapVersion, String mac, int x_coord, int y_coord) {
		ContentValues values = new ContentValues();
		values.put(Position.MAPID, mapId);
		values.put(Position.MAPVERSION, mapVersion);
		values.put(Position.Mac, mac);
		values.put(Position.X_COORD, x_coord);
		values.put(Position.Y_COORD, y_coord);
		long id = db.update(POS_TABLE_NAME, values, Position.MAPVERSION + " = '"
				+ mapVersion + "'", null);
		return id;
	}

	public int[] queryCoords(String mapId, String mapVersion, String mac) {
		String sql = "select * from " + POS_TABLE_NAME + " where " 
				+ Position.Mac + " = '" + mac + "'";;
		Cursor cursor = db.rawQuery(sql, null);
		System.out.println(cursor);
		int[] data = new int[2];;
		System.out.println("11"+ cursor.getCount() );
		if (cursor.getCount() > 0) {
			System.out.println(cursor.getCount() );
			cursor.moveToFirst();
			data[0] = cursor.getInt(cursor.getColumnIndex(Position.X_COORD));
			data[1]= cursor.getInt(cursor.getColumnIndex(Position.Y_COORD));
			System.out.println(data[0]);
			System.out.println(data[1]);
		}
		return data;
	}

	
	//关闭数据库
	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	/**
	 * 数据库辅助类
	 */
	private static class DBOpenHelper extends SQLiteOpenHelper {

		DBOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		//回调函数，第一次创建时调用，通过SQL语句创建数据表
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + USER_TABLE_NAME + " (" + User._ID
					+ " integer primary key," + User.USERNAME + " text, "
					+ User.PASSWORD + " text, " + User.ISSAVED + " integer)");
			//创建地图信息表
			db.execSQL("create table " + MAP_TABLE_NAME + " (" + Map._ID 
					+ " integer primary key," + Map.MAPID + " integer," + Map.SITEID + " integer," 
					+ Map.MAPVERSION + " text," + Map.SCALESIZE + " float," + Map.MAP + " BLOB)");
			//创建地图中位置信息表
			db.execSQL("create table " + POS_TABLE_NAME + " (" + Position._ID 
					+ " integer primary key," + Position.MAPID + " text," 
					+ Position.MAPVERSION + " text," + Position.Mac + " text," 
					+ Position.X_COORD + " integer," + Position.Y_COORD + " integer)");
			//创建联系人信息表
			db.execSQL("create table " + CONTACT_TABLE_NAME + " (" + Contact._ID 
					+ " integer primary key," + Contact.CONTACTNAME + " text," 
					+ Contact.CONTACTNO + " text)" );
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + POS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME);
			onCreate(db);
		}

	}

}

