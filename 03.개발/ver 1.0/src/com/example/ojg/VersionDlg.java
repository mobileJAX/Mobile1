package com.example.ojg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VersionDlg extends Activity{	
	Context mContext;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.version_dlg);		
		
		mContext=this;
		
		Button xbtn = (Button) findViewById(R.id.xbtn);
		xbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				finish();
			}
			
		});
	}
}
