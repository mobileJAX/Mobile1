package com.example.ojg;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FirstTab extends Activity{
	String userPhone;
	Context mContext;
	String loverPhone;
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;
	boolean isLoverExist=false;
	TextView loverNumber;
	TextView congratulation;
	EditText PhoneNumber;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab1);

		mContext=this;

		final Intent intent=getIntent();
		Bundle myBundle=intent.getExtras();
		userPhone=myBundle.getString("userID");

		mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}		

		loverNumber=(TextView) findViewById(R.id.lovertext);
		cursor=db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			if(userPhone.equals(cursor.getString(0))){
				if(!cursor.getString(3).equals("null")){
					isLoverExist=true;
					//좋아하는사람 번호 세팅
					loverNumber.setText(cursor.getString(3)+" 입니다");
					loverPhone=cursor.getString(3);
				}
			}
		}
		cursor=db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			if(loverPhone!=null){
				if(loverPhone.equals(cursor.getString(0))){
					if(userPhone.equals(cursor.getString(3))){
						//내가 좋아하는사람이 나를 좋아하는지 검사					
						congratulation=(TextView) findViewById(R.id.congratext);
						congratulation.setText("축하합니다!당신이 좋아하는 사람이 당신을 좋아합니다!");
					}
				}
			}
		}

		Button regLoverBtn = (Button) findViewById(R.id.regloverbtn);
		regLoverBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(isLoverExist)
				{
					Toast.makeText(mContext,"이미 좋아하는 사람의 번호가 존재합니다!",Toast.LENGTH_SHORT).show();
				}
				else
				{
					LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View layout = inflater.inflate(R.layout.reg_lover, (ViewGroup) ((Activity) mContext).findViewById(R.id.popup));
					AlertDialog.Builder aDialog = new AlertDialog.Builder(mContext);//좋아하는 번호 등록창 팝업

					aDialog.setTitle("등록하기"); //타이틀바 제목
					aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅
					aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					aDialog.setNeutralButton("등록", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							PhoneNumber=(EditText) layout.findViewById(R.id.regloveredit);
							loverPhone=PhoneNumber.getText().toString();

							boolean isProperID=false;
							if(loverPhone.length()==11)
								isProperID=true;

							if(!isProperID)
								Toast.makeText(mContext,"번호 길이가 적합하지 않습니다.",Toast.LENGTH_SHORT).show();
							else
							{
								AlertDialog.Builder chkDialog = new AlertDialog.Builder(mContext);//한번더 확인 팝업
								chkDialog.setTitle(loverPhone);
								chkDialog.setMessage("한번 등록하면 변경할 수 없습니다.계속 하시겠습니까?");
								chkDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(mContext,"취소되었습니다.",Toast.LENGTH_SHORT).show();
									}
								});
								chkDialog.setNeutralButton("등록", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										if(!loverPhone.equals(userPhone))
										{
											try{												
												String myUrl="http://canwelove.cafe24.com/dbupdatelover.php?LOVER="+loverPhone+
														"&PHONE="+userPhone;						
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
											String query="UPDATE userinfo SET LOVER='"+loverPhone+"' WHERE PHONE='"+userPhone+"';";		
											db.execSQL(query);
											Toast.makeText(mContext,"등록되었습니다!",Toast.LENGTH_SHORT).show();
											isLoverExist=true;
											cursor=db.rawQuery("select * from userinfo;",null);
											while(cursor.moveToNext()) {
												if(userPhone.equals(cursor.getString(0))){
													if(cursor.getString(3)!=null){
														isLoverExist=true;
														loverNumber.setText(cursor.getString(3)+" 입니다.");													
													}
												}	
												if(loverPhone!=null){
													if(loverPhone.equals(cursor.getString(0))){
														if(userPhone.equals(cursor.getString(3))){
															//내가 좋아하는사람이 나를 좋아하는지 검사					
															congratulation=(TextView) findViewById(R.id.congratext);
															congratulation.setText("축하합니다!당신이 좋아하는 사람이 당신을 좋아합니다!");
														}
													}
												}
											}
										}
										else
											Toast.makeText(mContext,"자기자신과 사랑에 빠질 수는 없습니다!",Toast.LENGTH_SHORT).show();
									}
								});		
								AlertDialog Warning = chkDialog.create();
								Warning.show();
							}
						}
					});

					//팝업창 생성
					AlertDialog ad = aDialog.create();
					ad.show();//보여줌!
				}
			}
		});		
	}
}
