package com.p3authentication.DBhandlerAndParsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler {
	public static final String KEY_ID = "id";
	public static final String KEY_XCOD = "xcod";
	public static final String KEY_YCOD = "ycod";
	public static final String KEY_IMG = "img";
	public static final String KEY_PATTERN = "pattern";
	public static final String KEY_PACKAGENAME = "packagename";
	private static final String DATABASE_NAME = "P3UserDb";
	private static final String DATABASE_USERTABLE = "LoginDetailsTable";
	private static final String DATABASE_PATTERNTABLE = "PatternDetailsTable";
	private static final String DATABASE_LOCKEDAPPSTABLE = "LockedAppsTable";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	public static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_USERTABLE + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_XCOD
					+ " INTEGER NOT NULL, " + KEY_YCOD + " INTEGER NOT NULL, "
					+ KEY_IMG + " TEXT NOT NULL);");
			db.execSQL("CREATE TABLE " + DATABASE_PATTERNTABLE + " ("
					+ KEY_PATTERN + " STRING NOT NULL);");
			db.execSQL("CREATE TABLE " + DATABASE_LOCKEDAPPSTABLE + " ("
					+ KEY_PACKAGENAME + " STRING);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_USERTABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PATTERNTABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LOCKEDAPPSTABLE);
			onCreate(db);
		}
	}

	public DataBaseHandler(Context c) {
		ourContext = c;
	}

	public DataBaseHandler open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public void deletetable() {
		ourDatabase.delete(DATABASE_USERTABLE, null, null);
	}

	public void deletePatterntable() {
		ourDatabase.delete(DATABASE_PATTERNTABLE, null, null);
	}

	public long createEntry(String xcod, String ycod, String img) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_XCOD, xcod);
		cv.put(KEY_YCOD, ycod);
		cv.put(KEY_IMG, img);
		return ourDatabase.insert(DATABASE_USERTABLE, null, cv);
	}

	public long createPattern(String pattern) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_PATTERN, pattern);

		return ourDatabase.insert(DATABASE_PATTERNTABLE, null, cv);
	}

	public String getData() {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ID, KEY_XCOD, KEY_YCOD, KEY_IMG };
		Cursor c = ourDatabase.query(DATABASE_USERTABLE, columns, null, null,
				null, null, null);
		String result = "";
		int iID = c.getColumnIndex(KEY_ID);
		int iXCOD = c.getColumnIndex(KEY_XCOD);
		int iYCOD = c.getColumnIndex(KEY_YCOD);
		int iIMG = c.getColumnIndex(KEY_IMG);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result = result + c.getString(iID) + " " + c.getString(iXCOD) + " "
					+ c.getString(iYCOD) + " " + c.getString(iIMG) + "\n";
		}
		return result;
	}

	public int[] getXcod() throws SQLException {
		// TODO Auto-generated method stub

		String[] columns = new String[] { KEY_ID, KEY_XCOD, KEY_YCOD, KEY_IMG };
		Cursor c = ourDatabase.query(DATABASE_USERTABLE, columns, null, null,
				null, null, null);
		int[] xcod = new int[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				xcod[i] = c.getInt(1);
				c.moveToNext();
			}
		}
		return xcod;

	}

	public int[] getYcod() throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ID, KEY_XCOD, KEY_YCOD, KEY_IMG };
		Cursor c = ourDatabase.query(DATABASE_USERTABLE, columns, null, null,
				null, null, null);
		int[] ycod = new int[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				ycod[i] = c.getInt(2);
				c.moveToNext();
			}
		}
		return ycod;

	}

	public String[] getImg() throws SQLException {
		// TODO Auto-generated method stub

		String[] columns = new String[] { KEY_ID, KEY_XCOD, KEY_YCOD, KEY_IMG };
		Cursor c = ourDatabase.query(DATABASE_USERTABLE, columns, null, null,
				null, null, null);
		String[] img = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				img[i] = c.getString(3);
				c.moveToNext();
			}
		}
		return img;
	}

	public String getPattern() throws SQLException {
		// TODO Auto-generated method stub

		String[] columns = new String[] { KEY_PATTERN };
		Cursor c = ourDatabase.query(DATABASE_PATTERNTABLE, columns, null,
				null, null, null, null);
		int patternID = c.getColumnIndex(KEY_PATTERN);
		String pattern = "";
		if (c.moveToFirst()) {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				pattern = c.getString(patternID);
			}
		}
		return pattern;

	}

	public long insertPackage(String packagename) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_PACKAGENAME, packagename);

		return ourDatabase.insert(DATABASE_LOCKEDAPPSTABLE, null, cv);
	}

	public void deletePackage(String packagename) {
		ourDatabase.delete(DATABASE_LOCKEDAPPSTABLE, KEY_PACKAGENAME + "="
				+ packagename, null);
	}

	public void deletePackageTable() {
		ourDatabase.delete(DATABASE_LOCKEDAPPSTABLE, null, null);
	}

	public String[] getPackages() throws SQLException {
		// TODO Auto-generated method stub

		String[] columns = new String[] { KEY_PACKAGENAME };
		Cursor c = ourDatabase.query(DATABASE_LOCKEDAPPSTABLE, columns, null,
				null, null, null, null);
		String[] packages = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				packages[i] = c.getString(0);
				c.moveToNext();
			}
		}
		return packages;
	}

	public int checkdata(String DataToCheck) throws SQLException {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		int value = 0;
		if (DataToCheck.equals("USER")) {
			String count = "SELECT count(*) FROM " + DATABASE_USERTABLE;
			Cursor mcursor = db.rawQuery(count, null);
			mcursor.moveToFirst();
			int icount = mcursor.getInt(0);
			if (icount > 0) {
				value = icount;
			}
		} else if (DataToCheck.equals("PATTERN")) {
			String count = "SELECT count(*) FROM " + DATABASE_PATTERNTABLE;
			Cursor mcursor = db.rawQuery(count, null);
			mcursor.moveToFirst();
			int icount = mcursor.getInt(0);
			if (icount > 0) {
				value = icount;
			}
		} else if (DataToCheck.equals("PACKAGE")) {
			String count = "SELECT count(*) FROM " + DATABASE_LOCKEDAPPSTABLE;
			Cursor mcursor = db.rawQuery(count, null);
			mcursor.moveToFirst();
			int icount = mcursor.getInt(0);
			if (icount > 0) {
				value = icount;
			}
		}
		return value;

	}

}
