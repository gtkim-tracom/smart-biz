package org.fincl.miss.service.biz.bicycle.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GoogleAddressAPI {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private double latitude;
	private double longitude;
	
	private String regionAddress;
	
	public GoogleAddressAPI(double latitude, double longitude) throws Exception {
		this.latitude = latitude;
		this.longitude = longitude;
		this.regionAddress = getRegionAddress(getJSONData(getApiAddress()));
	}
	
	private String getApiAddress() {
		String apiURL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&language=ko";
		
		System.out.println("aa");
		
		return apiURL;
	}

	private String getJSONData(String apiURL) throws Exception {
		String jsonString = new String();
		String buf;
		
		try{
			URL url = new URL(apiURL);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			while((buf = br.readLine()) != null){
				jsonString += buf;
			}
		}catch (UnknownHostException e){
			
		}
		
		System.out.println("getJSONData");
		
		return jsonString;
	}

	private String getRegionAddress(String jsonString) throws Exception {
		JSONObject jObj = (JSONObject) JSONValue.parse(jsonString);
		JSONArray jArray = (JSONArray) jObj.get("results");
		jObj = (JSONObject) jArray.get(0);
		
		return (String) jObj.get("formatted_address");
	}
	
	public String getAddress(){
		return regionAddress;
	}
	
	public static void main(String args[]) throws Exception{
		double latitude = 37.8510672;
		double longitude = 125.6502733;
//		
//		double latitude = 39.566353;
//		double longitude = 129.977969;
		
		try {
			GoogleAddressAPI gps = new GoogleAddressAPI(latitude, longitude);

			System.out.println("cc");
			System.out.println("### test ### => " + gps.getAddress());
			
		} catch (Exception e) {
			System.out.println("dd");
		}

		
		
	}
	
}
