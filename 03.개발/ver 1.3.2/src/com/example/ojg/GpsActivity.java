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
	// 위치 좌표 설정 
	CameraPosition cp;
	MarkerOptions marker;

	//위치정보 객체
	LocationManager lm = null;
	Location location=null;
	//위치정보 장치 이름
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
		//데이터베이스에 위치정보 업로드
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

		/**위치정보 객체를 생성한다.*/
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		//위치정보 하드웨어 목록
		Criteria c = new Criteria();

		//최적의 하드웨어 이름을 리턴받는다.
		provider = lm.getBestProvider(c, true);

		// 최적의 값이 없거나, 해당 장치가 사용가능한 상태가 아니라면, 
		//모든 장치 리스트에서 사용가능한 항목 얻기
		if(provider == null || !lm.isProviderEnabled(provider)){
			// 모든 장치 목록
			List<String> list = lm.getAllProviders();

			for(int i = 0; i < list.size(); i++){
				//장치 이름 하나 얻기
				String temp = list.get(i);
				//사용 가능 여부 검사
				if(lm.isProviderEnabled(temp)){
					provider = temp;
					break;
				}
			}
		}// (end if)위치정보 검색 끝
		/**마지막으로  조회했던 위치 얻기*/
		location = lm.getLastKnownLocation(provider);

		if(location == null){
			Toast.makeText(this, "사용가능한 위치 정보 제공자가 없습니다.", Toast.LENGTH_SHORT).show();
		}else{
			//최종 위치에서 부터 이어서 GPS 시작...	// Getting latitude of the current location
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
					// 연결되었음 코드가 리턴되면.
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
			marker = new MarkerOptions().position(loc); // 구글맵에 기본마커 표시

			mGoogleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();  
			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // 지정위치로 이동      
			mGoogleMap.addMarker(marker); // 지정위치에 마커 추가		
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
				Toast.makeText(mContext,"가장가까운 사람 거리 : "+douDistance,Toast.LENGTH_SHORT).show();	
			}	
			else{				
				Toast.makeText(mContext,"주위에 아무도 없습니다",Toast.LENGTH_SHORT).show();
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
		//Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
		super.onPause();
		//위치정보 객체에 이벤트 해제
		lm.removeUpdates(this);
	}

	@Override
	public void onResume(){
		//Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
		super.onResume();
		//위치정보 객체에 이벤트 연결
		lm.requestLocationUpdates(provider, 500, 1, this);
	}

	/** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
	public String getAddress(double lat, double lng){
		String address = null;
		//위치정보를 활용하기 위한 구글 API 객체
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		//주소 목록을 담기 위한 HashMap
		List<Address> list = null;
		try{
			list = geocoder.getFromLocation(lat, lng, 1);
		} catch(Exception e){
			e.printStackTrace();
		}
		if(list == null){
			Log.e("getAddress", "주소 데이터 얻기 실패");
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
