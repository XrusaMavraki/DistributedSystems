package com.evasler.clientapp;

import java.io.Serializable;

public class ClientRequestInsert  implements Serializable {

	
	private static final long serialVersionUID = -6026975790912535189L;
	
	protected String POI;
	protected String POI_name;
	protected String POI_category;
	protected int POI_category_id;
	protected double latitude;
	protected double longitutde;
	protected String time;
	protected String photos;
	
	public ClientRequestInsert(String POI,String POI_name, String POI_category,
			int POI_category_id, double latitude,double longitude, String photos){
		super();
		
		this.POI=POI;
		this.POI_name=POI_name;
		this.POI_category=POI_category;
		this.POI_category_id=POI_category_id;
		this.latitude=latitude;
		this.longitutde=longitude;
		this.photos=photos;
	}
	public ClientRequestInsert(String POI,double latitude,double longitude, String photos){
		this.POI=POI;
		this.latitude=latitude;
		this.longitutde=longitude;
		this.photos=photos;
	}
	


	public String getPOI() {
		return POI;
	}

	public void setPOI(String pOI) {
		POI = pOI;
	}

	public String getPOI_name() {
		return POI_name;
	}

	public void setPOI_name(String pOI_name) {
		POI_name = pOI_name;
	}

	public String getPOI_category() {
		return POI_category;
	}

	public void setPOI_category(String pOI_category) {
		POI_category = pOI_category;
	}

	public int getPOI_category_id() {
		return POI_category_id;
	}

	public void setPOI_category_id(int pOI_category_id) {
		POI_category_id = pOI_category_id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitutde() {
		return longitutde;
	}

	public void setLongitutde(double longitutde) {
		this.longitutde = longitutde;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}
	
	
	
}
