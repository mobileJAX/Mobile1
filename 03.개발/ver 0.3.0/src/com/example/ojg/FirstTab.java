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
					//�����ϴ»�� ��ȣ ����
					loverNumber.setText(cursor.getString(3)+" �Դϴ�");
					loverPhone=cursor.getString(3);
				}
			}
		}
		cursor=db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			if(loverPhone!=null){
				if(loverPhone.equals(cursor.getString(0))){
					if(userPhone.equals(cursor.getString(3))){
						//���� �����ϴ»���� ���� �����ϴ��� �˻�					
						congratulation=(TextView) findViewById(R.id.congratext);
						congratulation.setText("�����մϴ�!����� �����ϴ� ����� ����� �����մϴ�!");
					}
				}
			}
		}

		Button regLoverBtn = (Button) findViewById(R.id.regloverbtn);
		regLoverBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(isLoverExist)
				{
					Toast.makeText(mContext,"�̹� �����ϴ� ����� ��ȣ�� �����մϴ�!",Toast.LENGTH_SHORT).show();
				}
				else
				{
					LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View layout = inflater.inflate(R.layout.reg_lover, (ViewGroup) ((Activity) mContext).findViewById(R.id.popup));
					AlertDialog.Builder aDialog = new AlertDialog.Builder(mContext);//�����ϴ� ��ȣ ���â �˾�

					aDialog.setTitle("����ϱ�"); //Ÿ��Ʋ�� ����
					aDialog.setView(layout); //dialog.xml ������ ��� ����
					aDialog.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					aDialog.setNeutralButton("���", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							PhoneNumber=(EditText) layout.findViewById(R.id.regloveredit);
							loverPhone=PhoneNumber.getText().toString();

							boolean isProperID=false;
							if(loverPhone.length()==11)
								isProperID=true;

							if(!isProperID)
								Toast.makeText(mContext,"��ȣ ���̰� �������� �ʽ��ϴ�.",Toast.LENGTH_SHORT).show();
							else
							{
								AlertDialog.Builder chkDialog = new AlertDialog.Builder(mContext);//�ѹ��� Ȯ�� �˾�
								chkDialog.setTitle(loverPhone);
								chkDialog.setMessage("�ѹ� ����ϸ� ������ �� �����ϴ�.��� �Ͻðڽ��ϱ�?");
								chkDialog.setNegativeButton("���", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(mContext,"��ҵǾ����ϴ�.",Toast.LENGTH_SHORT).show();
									}
								});
								chkDialog.setNeutralButton("���", new DialogInterface.OnClickListener() {
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
													// ����Ǿ��� �ڵ尡 ���ϵǸ�.
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
											Toast.makeText(mContext,"��ϵǾ����ϴ�!",Toast.LENGTH_SHORT).show();
											isLoverExist=true;
											cursor=db.rawQuery("select * from userinfo;",null);
											while(cursor.moveToNext()) {
												if(userPhone.equals(cursor.getString(0))){
													if(cursor.getString(3)!=null){
														isLoverExist=true;
														loverNumber.setText(cursor.getString(3)+" �Դϴ�.");													
													}
												}	
												if(loverPhone!=null){
													if(loverPhone.equals(cursor.getString(0))){
														if(userPhone.equals(cursor.getString(3))){
															//���� �����ϴ»���� ���� �����ϴ��� �˻�					
															congratulation=(TextView) findViewById(R.id.congratext);
															congratulation.setText("�����մϴ�!����� �����ϴ� ����� ����� �����մϴ�!");
														}
													}
												}
											}
										}
										else
											Toast.makeText(mContext,"�ڱ��ڽŰ� ����� ���� ���� �����ϴ�!",Toast.LENGTH_SHORT).show();
									}
								});		
								AlertDialog Warning = chkDialog.create();
								Warning.show();
							}
						}
					});

					//�˾�â ����
					AlertDialog ad = aDialog.create();
					ad.show();//������!
				}
			}
		});		
	}
}
