package com.example.ojg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

class MyDBHelper extends SQLiteOpenHelper implements Parcelable{
	static Context mContext;
	static int mVersion;
	public MyDBHelper(Context context, int version) {
		super(context, "TestDB.db", null, version);
		mContext=context;
		mVersion=version;
	}

	public MyDBHelper(Parcel source) {
		// TODO Auto-generated constructor stub
		super(mContext, "TestDB.db", null, mVersion);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE userinfo (PHONE VARCHAR(30),"
				+ "PASSWORD VARCHAR(30),"
				+ "NAME VARCHAR(20),"
				+ "LOVER VARCHAR(30),"
				+ "LAT DOUBLE,"
				+ "LNG DOUBLE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS userinfo");
		onCreate(db);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public MyDBHelper createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new MyDBHelper(source);
		}

		@Override
		public MyDBHelper[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MyDBHelper[size];
		}
	};
}