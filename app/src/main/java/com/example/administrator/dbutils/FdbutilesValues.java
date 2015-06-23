package com.example.administrator.dbutils;

import java.util.HashMap;

public class FdbutilesValues {

	private HashMap<String, String> equaltomap;
	
	private boolean distinct=false;

	private HashMap<String, String> setmap;
	public FdbutilesValues(){
		equaltomap=new HashMap<String,String>();
		setmap = new HashMap<String,String>();
	}
	
	public void addWhereequalto(String key,String values){
		equaltomap.put(key, values);
	}
	
	public HashMap<String, String> getWhereequalto(){
		return equaltomap;
	}
	
	public void addSetting(String key,String value){
		setmap.put(key, value);
	}
	
	public HashMap<String, String> getSetting(){
		return setmap;
	}
	
	public void setDistinct(){
		distinct=true;
	}
	
	public boolean getDistinct(){
		return distinct;
	}
	
}
