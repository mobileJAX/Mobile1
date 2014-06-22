package com.example.ojg;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ThirdTab extends Activity{
	ArrayList<String> list;
	ArrayAdapter<String> adapter;
	ListView lv;
	InputMethodManager imm;
	
	String userPhone;
	
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;
	
	Context mContext;
	Intent intent;
	Bundle myBundle;
	double dist=99999.0;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab3);
		
		mContext=this;
		
		intent=getIntent();
		
		myBundle=intent.getExtras();
		userPhone=myBundle.getString("userID");
		mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}	
		
		list = new ArrayList<String>();
		
		int i=0;
		cursor=db.rawQuery("select * from userinfo;",null);	
		while(cursor.moveToNext()) {
			if(userPhone.equals(cursor.getString(3))){
				i++;
				list.add(i+"¹ø¢¾");
			}
		}		
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, list);

		lv = (ListView) findViewById(R.id.loverlistview);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		lv.setAdapter(adapter);     
	}
}
