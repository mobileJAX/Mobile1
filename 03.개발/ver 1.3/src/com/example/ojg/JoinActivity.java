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

public class JoinActivity extends Activity {
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {    //spinner item선택이 바꼈을때
			// TODO Auto-generated method stub

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	String query;
	Context mContext;
	boolean isDupl=false;
	String myNumber;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		
		Spinner spinner = (Spinner)findViewById(R.id.email_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mail_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		mContext=this;
		
		final Intent intent=getIntent();
		Bundle myBundle=intent.getExtras();
		
		mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}		
		
		EditText phoneNumber=(EditText) findViewById(R.id.phonenumber);
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myNumber = mTelephonyMgr.getLine1Number();	    		
		if(!myNumber.substring(0, 3).equals("010"))
			myNumber="0"+myNumber.substring(3);
		Log.e("myNumber", myNumber);
		
		phoneNumber.setText(myNumber);
		
		Button okBtn=(Button) findViewById(R.id.ok_btn);		
		okBtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	    	
	    		EditText password=(EditText) findViewById(R.id.input_password);
	    		EditText password2=(EditText) findViewById(R.id.input_password2);
	    		EditText name=(EditText) findViewById(R.id.input_name);	    		
	    		
	    		//회원가입시 Edit Text의 정보들
	    		final String ePassword=password.getText().toString();
	    		final String ePassword2=password2.getText().toString();
	    		String eName=name.getText().toString();
	    		
	    		boolean isPassDiff=true;
	    		if(ePassword.equals(ePassword2))
	    			isPassDiff=false;
	    		
	    		boolean isProperPass=false;
	    		if(password.length()>=4)
	    			isProperPass=true;
	    		
	    		boolean isProperName=false;
	    		if(!eName.isEmpty())
	    			isProperName=true;
	    		
	    		if(isDupl)
					Toast.makeText(mContext,"이미 가입된 전화번호입니다!",Toast.LENGTH_SHORT).show();
	    		else if(isPassDiff)
	    			Toast.makeText(mContext,"비밀번호가 다릅니다!",Toast.LENGTH_SHORT).show();
	    		else if(!isProperPass)
	    			Toast.makeText(mContext,"비밀 번호의 길이는 4자 이상이어야합니다.",Toast.LENGTH_SHORT).show();
	    		else if(!isProperName)
	    			Toast.makeText(mContext,"적합하지 않은 이름입니다.",Toast.LENGTH_SHORT).show();
				else
				{
					try{	
						eName=URLEncoder.encode(eName, "UTF-8");
						String myUrl="http://canwelove.cafe24.com/dbup.php?PHONE="+myNumber+
								"&PASSWORD="+ePassword+
								"&NAME="+eName+
								"&LOVER="+null+
								"&LAT="+0+
								"&LNG="+0;						
						URL text=new URL(myUrl);							
						HttpURLConnection conn = (HttpURLConnection)text.openConnection();
						if(conn != null){
							conn.setConnectTimeout(10000);
							conn.setUseCaches(false);
							// 연결되었음 코드가 리턴되면.
							if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
								Log.e("connection", "success");
							}							
							conn.connect();				
						}
					}catch(Exception ex){	
						ex.printStackTrace();
					}
					setResult(RESULT_OK);
					finish();
				}
	    	}
	    });
		Button duplBtn=(Button) findViewById(R.id.dupl_btn);
		duplBtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	    	    		
	    		Cursor cursor= db.rawQuery("select * from userinfo;",null);
	    		
	    		isDupl=false;
				while(cursor.moveToNext())
				{
					Log.e("중복체크", myNumber);	
					Log.e("aa", cursor.getString(0)+" "+cursor.getString(2)+" "+cursor.getString(3));
					if(myNumber.equals(cursor.getString(0)))
					{	
						isDupl=true;
						break;
					}
				}
				if(isDupl)
					Toast.makeText(mContext,"이미 가입된 전화번호입니다!",Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext,"사용 가능한 전화번호입니다!",Toast.LENGTH_SHORT).show();
	    	}
	    });
		Button cancelBtn=(Button) findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	    		    		
				finish();
	    	}
	    });
	}
}
