package com.example.ojg;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SendMsgDlg extends Activity {
	String userPhone;

	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;

	Context mContext;
	Intent intent;
	Bundle myBundle;
	double dist = 99999.0;

	String message;
	EditText content;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_msg);

		mContext = this;

		final Intent intent=getIntent();
		Bundle myBundle=intent.getExtras();
		userPhone=myBundle.getString("userID");

		/*mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}
/*
		Button sendBtn = (Button) findViewById(R.id.sendmsg);
		sendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				content = (EditText) findViewById(R.id.writemsg);
				message = content.getText().toString();
				try {
					String myUrl = "http://canwelove.cafe24.com/message.php?CONTENT="
							+ message + "&SENDER=" + userPhone + "&RECEIVER=" + userPhone;
					URL text = new URL(myUrl);
					HttpURLConnection conn = (HttpURLConnection) text
							.openConnection();
					if (conn != null) {
						conn.setConnectTimeout(10000);
						conn.setUseCaches(false);
						// 연결되었음 코드가 리턴되면.
						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							Log.e("connection", "success");
						}
						conn.connect();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		});*/
	}
}
