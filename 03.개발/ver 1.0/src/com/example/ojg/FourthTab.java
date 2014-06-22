package com.example.ojg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FourthTab extends Activity{	
	Context mContext;
	
	TextView textGpsStatus;
	LocationManager locationManager;
	ToggleButton GPStb;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab4);		
		
		mContext=this;
		
		String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(context);
		
		GPStb = (ToggleButton)this.findViewById(R.id.GPStb);

		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			GPStb.setChecked(false);
		else
			GPStb.setChecked(true);
		
		GPStb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GpsService();
				onResume();
			}
		});
		
		Button versionbtn = (Button)findViewById(R.id.version_btn);
		
		versionbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
					Intent intent = new Intent(mContext, VersionDlg.class); 
					startActivityForResult(intent, 1);
			}
			
		});
	}
	
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_tab4);
		
		mContext=this;
		
		String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(context);
		
		GPStb = (ToggleButton)this.findViewById(R.id.GPStb);

		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			GPStb.setChecked(false);
		else
			GPStb.setChecked(true);
		
		GPStb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GpsService();
				onResume();
			}
		});
	}
	

	private void GpsService() {
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		startActivity(intent);
	}
	
	
}
