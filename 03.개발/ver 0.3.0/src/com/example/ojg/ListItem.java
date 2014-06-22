package com.example.ojg;

public class ListItem {
	private String[] mData;

	public ListItem(String[] data ){
		mData = data;
	}

	public ListItem(String phone, String password, String name, String lover, double lat, double lng){
		mData = new String[6];
		mData[0] = phone;
		mData[1] = password;
		mData[2] = name;     
		mData[3] = lover;   
		mData[4] = String.valueOf(lat);   
		mData[5] = String.valueOf(lng);   
	}

	public String[] getData(){
		return mData;
	}

	public String getData(int index){
		return mData[index];
	}

	public void setData(String[] data){
		mData = data;
	}
}