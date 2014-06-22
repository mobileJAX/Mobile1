package com.example.ojg;

import java.util.Arrays;
import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecondTab extends Activity{
	String userPhone;
	TextView loveCountText;
	int loveCount=0;
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;
	Context mContext;
	Intent intent;

	double dist=99999.0;
	
	int totalUser=0;
	TextView userCntText;
	
	boolean isClickLocateBtn=false;
	
	private Button facebookbtn;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    Facebook facebook = new Facebook("265667620283183");
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;


	
	protected void onCreate(Bundle savedInstanceState)
	{
		uiHelper = new UiLifecycleHelper(this, statusCallback);
	    uiHelper.onCreate(savedInstanceState);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab2);		

		mContext=this;
		intent=getIntent();
		final Bundle myBundle=intent.getExtras();
		userPhone=myBundle.getString("userID");
		
		mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = mDBHelper.getReadableDatabase();
		}	
		
		loveCountText=(TextView) findViewById(R.id.counttext);
		
		cursor=db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			if(userPhone.equals(cursor.getString(3))){
				loveCount++;
			}
		}
		loveCountText.setText(Integer.toString(loveCount));

		//total user 계산
		userCntText=(TextView) findViewById(R.id.usercnt);	

		cursor=db.rawQuery("select * from userinfo;",null);
		while(cursor.moveToNext()) {
			totalUser++;
		}
		userCntText.setText("전체 사용자의 수는 "+Integer.toString(totalUser)+" 명입니다.");
		
		Button chkLocateBtn=(Button) findViewById(R.id.locatechkbtn);
		chkLocateBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				isClickLocateBtn=true;
				intent = new Intent(mContext, GpsActivity.class);
				intent.putExtras(myBundle);
				startActivityForResult(intent,1);
			}
		});
		
		/* for Facebook */
		facebookbtn = (Button) findViewById(R.id.adbtn);
		facebookbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {      		 
            	postStatusMessage();
            }
        });
    }
	
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                Exception exception) {
            if (state.isOpened()) {
               // buttonsEnabled(true);
                Log.d("ojg Seocndtab activity", "Facebook session opened");
            } else if (state.isClosed()) {
               // buttonsEnabled(false);
                Log.d("ojg Seocndtab activity", "Facebook session closed");
            }
        }
    };	
    
	 private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
	        return new FacebookDialog.ShareDialogBuilder(this)
	                .setName("오작교")
	                .setDescription("어차피 못 사귀겠지만 못먹는 감 찔러나보세요")
	                .setLink("http://canwelove.cafe24.com/xe/");        
	    }
	    
	    public void postStatusMessage() { 
	    	requestPermissions();
	        FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
	        uiHelper.trackPendingDialogCall(shareDialog.present());  
	    }	    	     
	    
	    public boolean checkPermissions() {
	        Session s = Session.getActiveSession();
	        if (s != null) {
	            return s.getPermissions().contains("publish_actions");
	        } else
	            return false;
	    }
	 
	    public void requestPermissions() {
	    	 /* for Facebook*/
            /* Get existing access_token if any */
           mPrefs = getPreferences(MODE_PRIVATE);
           String access_token = mPrefs.getString("access_token", null);
           long expires = mPrefs.getLong("access_expires", 0);
           if(access_token != null) {
               facebook.setAccessToken(access_token);
           }
           if(expires != 0) {
               facebook.setAccessExpires(expires);
           }
           
           /*
            * Only call authorize if the access_token has expired.
            */           
           if(!facebook.isSessionValid()) {


               facebook.authorize(this, new String[] {}, new DialogListener() {
                   @Override
                   public void onComplete(Bundle values) {
                       SharedPreferences.Editor editor = mPrefs.edit();
                       editor.putString("access_token", facebook.getAccessToken());
                       editor.putLong("access_expires", facebook.getAccessExpires());
                       editor.commit();
                   }
       
                   @Override
                   public void onFacebookError(FacebookError error) {}
       
                   @Override
                   public void onError(DialogError e) {}
       
                   @Override
                   public void onCancel() {}
               });
           }

	    }
	    
	protected void onActivityResult(int requestCode, int resultCode, Intent data){ 
		super.onActivityResult(requestCode, resultCode, data); 		
		Bundle mydata=new Bundle();		
		mydata=data.getExtras();
		if(resultCode==RESULT_OK){
			if(requestCode==1){
				Log.e("위치받기성공", String.valueOf(mydata.getDouble("dist")));	
				dist=mydata.getDouble("dist");
			}
		}
		facebook.authorizeCallback(requestCode, resultCode, data);
	}	
}
