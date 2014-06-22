package com.example.ojg;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity{
	Context mContext;
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);
		TabHost mTab = getTabHost();
		TabHost.TabSpec spec;
		//LayoutInflater.from(this).inflate(R.layout.activity_tab, mTab.getTabContentView(), true);
		
		mContext=this;
		
		Intent intent=getIntent();
		Bundle myData=intent.getExtras();		
		
		// 탭의 백그라운드 이미지
		ImageView tabwidget1 = new ImageView(this);
		tabwidget1.setImageResource(R.drawable.widget1);
		ImageView tabwidget2 = new ImageView(this);
		tabwidget2.setImageResource(R.drawable.widget2);
		ImageView tabwidget3 = new ImageView(this);
		tabwidget3.setImageResource(R.drawable.widget3);
		ImageView tabwidget4 = new ImageView(this);
		tabwidget4.setImageResource(R.drawable.widget4);
		

		intent = new Intent(this, FirstTab.class);
		intent.putExtras(myData);
		spec = mTab.newTabSpec("FirstTab").setIndicator(tabwidget1).setContent(intent);
		mTab.addTab(spec);
		
		intent = new Intent(this, SecondTab.class);
		intent.putExtras(myData);
		spec = mTab.newTabSpec("SecondTab").setIndicator(tabwidget2).setContent(intent);
		mTab.addTab(spec);
		
		intent = new Intent(this, ThirdTab.class);
		intent.putExtras(myData);
		spec = mTab.newTabSpec("ThirdTab").setIndicator(tabwidget3).setContent(intent);
		mTab.addTab(spec);
		
		intent = new Intent(this, FourthTab.class);
		intent.putExtras(myData);
		spec = mTab.newTabSpec("FourthTab").setIndicator(tabwidget4).setContent(intent);
		mTab.addTab(spec);
	}
}


