package com.example.ojg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ForgotPassword extends Activity {

	String myNumber;
	public static String email2 = "";
	EditText email;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgotpw);
		
		EditText phoneNumber = (EditText) findViewById(R.id.ph_num);
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myNumber = mTelephonyMgr.getLine1Number();	
		Log.e("myNumber", myNumber.substring(0, 3));
		if(!myNumber.substring(0, 3).equals("010"))
			myNumber="0"+myNumber.substring(3);
		phoneNumber.setText(myNumber);
		
		Spinner spinner = (Spinner)findViewById(R.id.email_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mail_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
		email = (EditText) findViewById(R.id.em);

		Button ok_btn=(Button) findViewById(R.id.ok_btn);
		ok_btn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	
	    		if(email.getText().toString().isEmpty())
	    			Toast.makeText(ForgotPassword.this,"이메일을 입력해주세요!",Toast.LENGTH_SHORT).show();
	    		else {
	    			//email1, email2랑 db에 있는 email주소 비교
	    			//if, else문 작성
	    			//if(1) { // db내용과 일치할 경우
	    				GMailSender sender = new GMailSender("euna8891@gmail.com","18166g11"); // gmail 아이디랑 비밀번호 필요               
		                try {  
		                    sender.sendMail(  
		                            "<오작교> 에서 비밀번호를 보내드립니다.",   //메일 제목,   
		                            "메일 본문입니다..~~ ",           //메일 본문,   
		                            "euna8891@gmail.com",          // gmail 아이디  
		                            email.getText().toString()+"@"+email2            //받는 사람 아이디
		                            );  
		                    Toast.makeText(ForgotPassword.this,"메일을 발송하였습니다.",Toast.LENGTH_SHORT).show();
		                    finish();
		                } catch (Exception e) {  
		                    Log.e("SendMail", e.getMessage(), e);  
		                }  
	    			//}
		    		//else { 
		    			//Toast.makeText(ForgotPassword.this,"메일을 잘못 입력하셨습니다.",Toast.LENGTH_SHORT).show();
	    			//}
	    		}
	    	}
	    });
		
		Button cancelBtn=(Button) findViewById(R.id.findpw_cancel);
		cancelBtn.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	
				finish();
	    	}
	    });
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // spinner item선택이 바꼈을때
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
				email2 = "naver.com";
				break;
			case 1:
				email2 = "nate.com";
				break;
			case 2:
				email2 = "gmail.com";
				break;
			default:
				email2 = "hanmail.net";
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}
}
