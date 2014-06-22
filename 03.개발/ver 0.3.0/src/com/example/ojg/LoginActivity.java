package com.example.ojg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {	
	/** Called when the activity is first created. */
	Context mContext=this;
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;
	String myNumber;
	
	phpDown task;
	ArrayList<ListItem> listItem= new ArrayList<ListItem>();
	ListItem Item;
	
	long backKeyPressedTime = 0;
	Toast toast;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		task = new phpDown();
		task.execute("http://canwelove.cafe24.com/dbdown.php");

		mDBHelper = new MyDBHelper(this, 1);

		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}		
		//db.execSQL("delete from userinfo");
		Log.e("LoginActivity", "전화번호 이름 좋아하는사람");

		cursor= db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			Log.e("aa", cursor.getString(0)+
					" "+cursor.getString(2)+
					" "+cursor.getString(3)+
					" "+cursor.getInt(4)+
					" "+cursor.getInt(5));
		}
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myNumber = mTelephonyMgr.getLine1Number();	
		Log.e("myNumber", myNumber.substring(0, 3));
		if(!myNumber.substring(0, 3).equals("010"))
			myNumber="0"+myNumber.substring(3);
		Log.e("myNumber", myNumber);		
		EditText phoneNumber=(EditText) findViewById(R.id.userID);
		phoneNumber.setText(myNumber);
		
		Button logBtn = (Button) findViewById(R.id.login_btn);
		Button joinBtn = (Button) findViewById(R.id.join_btn);
		logBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				
				EditText password=(EditText) findViewById(R.id.password);
				String ePassword=password.getText().toString();

				cursor= db.rawQuery("select * from userinfo;",null);
				
				boolean isLoginConditon=false;
				while(cursor.moveToNext()) {
					if(myNumber.equals(cursor.getString(0)))
					{
						if(ePassword.equals(cursor.getString(1)))
						{
							isLoginConditon=true;
						}
					}						
				}
				
				if(isLoginConditon)
				{					
					Intent intent = new Intent(mContext, TabHostActivity.class);
					Bundle myData=new Bundle();
					myData.putParcelable("mdb", mDBHelper);//db를 넘겨줌
					myData.putString("userID", myNumber);//id값도 넘겨줌					
					intent.putExtras(myData);
					password.setText("");
					startActivity(intent);
				}
				else
					Toast.makeText(mContext,"아이디 또는 패스워드가 틀립니다!",Toast.LENGTH_SHORT).show();
			}
		});

		joinBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				EditText password=(EditText) findViewById(R.id.password);
				password.setText("");
				Intent intent = new Intent(mContext, JoinActivity.class);   
				Bundle myData=new Bundle();
				myData.putParcelable("mdb", mDBHelper);//db를 넘겨줌
				intent.putExtras(myData);
				startActivityForResult(intent, 1);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){ 
		super.onActivityResult(requestCode, resultCode, data); 		
		if(resultCode==RESULT_OK){
			if(requestCode==1){
				task = new phpDown();
				task.execute("http://canwelove.cafe24.com/dbdown.php");				
				Toast.makeText(mContext,"가입이 성공하였습니다!",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class phpDown extends AsyncTask<String, Integer,String>{
		@Override
		protected String doInBackground(String... urls) {
			StringBuilder jsonHtml = new StringBuilder();
			try{
				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				// 연결되었으면.
				if(conn != null){
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						for(;;){
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.  
							String line = br.readLine();
							if(line == null) break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
			return jsonHtml.toString();
		}

		protected void onPostExecute(String str){
			String lPhone;
			String lPassword;
			String lName;
			String lLover;
			double lat;
			double lng;
			try{
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results");
				db.execSQL("delete from userinfo");
				for(int i=0; i<ja.length(); i++){
					JSONObject jo = ja.getJSONObject(i);
					
					lPhone = jo.getString("PHONE");
					lPassword = jo.getString("PASSWORD");
					lName = jo.getString("NAME");
					lLover = jo.getString("LOVER");
					lat= Double.parseDouble(jo.getString("LAT"));
					lng= Double.parseDouble(jo.getString("LNG"));
					String query="INSERT INTO userinfo VALUES('"+lPhone+
							"','"+lPassword+
							"','"+lName+
							"','"+lLover+
							"',"+lat+
							","+lng+")";
					Log.e("lLover", lLover);
					db.execSQL(query);
					listItem.add(new ListItem(lPhone,lPassword,lName, lLover, lat, lng));
				}
			}catch(JSONException e){
				e.printStackTrace();
			}			
			cursor= db.rawQuery("select * from userinfo;",null);
			while(cursor.moveToNext()) {
				Log.e("aa", cursor.getString(0)+
						" "+cursor.getString(2)+
						" "+cursor.getString(3)+
						" "+cursor.getInt(4)+
						" "+cursor.getInt(5));
			}											
		}
	}
	public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();            
        }
    }
 
    public void showGuide() {
        toast = Toast.makeText(mContext,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
