package com.example.ojg;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GpsActivity extends FragmentActivity implements LocationListener{
	TextView tv1 = null;
	TextView tv2 = null;

	GoogleMap mGoogleMap;
	LatLng loc;
	// ��ġ ��ǥ ���� 
	CameraPosition cp;
	MarkerOptions marker;

	//��ġ���� ��ü
	LocationManager lm = null;
	Location location=null;
	//��ġ���� ��ġ �̸�
	String provider = null;

	Intent intent;
	Bundle myBundle;

	String userPhone;
	MyDBHelper mDBHelper;
	SQLiteDatabase db;
	Cursor cursor;

	float[] arrayDistance = new float[2];
	double douDistance = 99999.0;
	
	Context mContext;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps);
		mContext=this;
		//�����ͺ��̽��� ��ġ���� ���ε�
		intent=getIntent();
		myBundle=intent.getExtras();
		userPhone=myBundle.getString("userID");

		mDBHelper = myBundle.getParcelable("mdb");
		try{
			db=mDBHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db=mDBHelper.getReadableDatabase();
		}			

		tv1 = (TextView)findViewById(R.id.getlocatetext);
		tv2 = (TextView)findViewById(R.id.getaddresstext);

		/**��ġ���� ��ü�� �����Ѵ�.*/
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		//��ġ���� �ϵ���� ���
		Criteria c = new Criteria();

		//������ �ϵ���� �̸��� ���Ϲ޴´�.
		provider = lm.getBestProvider(c, true);

		// ������ ���� ���ų�, �ش� ��ġ�� ��밡���� ���°� �ƴ϶��, 
		//��� ��ġ ����Ʈ���� ��밡���� �׸� ���
		if(provider == null || !lm.isProviderEnabled(provider)){
			// ��� ��ġ ���
			List<String> list = lm.getAllProviders();

			for(int i = 0; i < list.size(); i++){
				//��ġ �̸� �ϳ� ���
				String temp = list.get(i);
				//��� ���� ���� �˻�
				if(lm.isProviderEnabled(temp)){
					provider = temp;
					break;
				}
			}
		}// (end if)��ġ���� �˻� ��
		/**����������  ��ȸ�ߴ� ��ġ ���*/
		location = lm.getLastKnownLocation(provider);

		if(location == null){
			Toast.makeText(this, "��밡���� ��ġ ���� �����ڰ� �����ϴ�.", Toast.LENGTH_SHORT).show();
		}else{
			//���� ��ġ���� ���� �̾ GPS ����...	// Getting latitude of the current location
			double latitude = location.getLatitude();
			// Getting longitude of the current location
			double longitude = location.getLongitude();			

			try{					
				String myUrl="http://canwelove.cafe24.com/dbupdate.php?LAT="+latitude+
						"&LNG="+longitude+"&PHONE="+userPhone;					
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
			
			onLocationChanged(location);
			loc = new LatLng(latitude,longitude);
			cp = new CameraPosition.Builder().target((loc)).zoom(16).build();
			marker = new MarkerOptions().position(loc); // ���۸ʿ� �⺻��Ŀ ǥ��

			mGoogleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();  
			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // ������ġ�� �̵�      
			mGoogleMap.addMarker(marker); // ������ġ�� ��Ŀ �߰�		
			CircleOptions circle=new CircleOptions().center(loc);
			circle.radius(100);
			mGoogleMap.addCircle(circle);
			
			String query="UPDATE userinfo SET LAT="+
					latitude+
					",LNG="+longitude+
					" WHERE PHONE='"+userPhone+"';";
			db.execSQL(query);
			Log.e("GpsActivity", "update location");

			double tmpDistance = 0.0;

			cursor=db.rawQuery("select * from userinfo;",null);
			double loverlat = 0;
			double loverlng = 0;
			while(cursor.moveToNext()) {
				if(userPhone.equals(cursor.getString(3))){
					loverlat=cursor.getDouble(4);
					loverlng=cursor.getDouble(5);				
				}
			}

			Location.distanceBetween(latitude, longitude, loverlat, loverlng, arrayDistance);
			tmpDistance = arrayDistance[0];

			if ( tmpDistance < 100.0 ){
				douDistance=0.0;
				douDistance = douDistance + tmpDistance;
				Toast.makeText(mContext,"���尡��� ��� �Ÿ� : "+douDistance,Toast.LENGTH_SHORT).show();	
			}	
			else{				
				Toast.makeText(mContext,"������ �ƹ��� �����ϴ�",Toast.LENGTH_SHORT).show();
			}
		}		
	}
	
	public void onBackPressed() {
		intent.putExtra("dist", douDistance);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();

		tv1.setText(String.valueOf(lat) + " / " + String.valueOf(lng));	
		tv2.setText(getAddress(lat, lng));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPause(){
		//Activity LifrCycle ���� �޼���� ������ ���� �޼��� ȣ�� �ʿ�
		super.onPause();
		//��ġ���� ��ü�� �̺�Ʈ ����
		lm.removeUpdates(this);
	}

	@Override
	public void onResume(){
		//Activity LifrCycle ���� �޼���� ������ ���� �޼��� ȣ�� �ʿ�
		super.onResume();
		//��ġ���� ��ü�� �̺�Ʈ ����
		lm.requestLocationUpdates(provider, 500, 1, this);
	}

	/** ������ �浵 ������� �ּҸ� �����ϴ� �޼���*/
	public String getAddress(double lat, double lng){
		String address = null;
		//��ġ������ Ȱ���ϱ� ���� ���� API ��ü
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		//�ּ� ����� ��� ���� HashMap
		List<Address> list = null;
		try{
			list = geocoder.getFromLocation(lat, lng, 1);
		} catch(Exception e){
			e.printStackTrace();
		}
		if(list == null){
			Log.e("getAddress", "�ּ� ������ ��� ����");
			return null;

		}
		if(list.size() > 0){
			Address addr = list.get(0);
			address = addr.getCountryName() + " "
					//+ addr.getPostalCode() + " "
					+ addr.getLocality() + " "
					+ addr.getThoroughfare() + " "
					+ addr.getFeatureName();
		}
		return address;
	}	
}
