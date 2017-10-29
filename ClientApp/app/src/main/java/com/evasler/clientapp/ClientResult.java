package com.evasler.clientapp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ClientResult implements Serializable {
	private static final long serialVersionUID = 1L;
	String poi;
	String poiName;
	String poiCategory;
	double latitude;
	double longtitude;
	int checkins; 
	Set<String> pictureUrls;
	
	public ClientResult(){
		checkins = 1;
		pictureUrls = new HashSet<>();
	}
	
	public String toString(){
		return "Poi" + poi +" Poi name " + poiName +"Number of Checkins " +checkins;
	}
	public void setFromDbResult(DbResult dbr){
	
		poi = dbr.getPoi();
		poiName = dbr.getPoiName();
		poiCategory = dbr.getPoiCategory();
		latitude = dbr.getLatitude();
		longtitude = dbr.getLongtitude();
		checkins++;
		String picUrl = dbr.getPictureUrl();
		if (picUrl != null && !picUrl.equals("Not exists")) pictureUrls.add(picUrl);
	}
	public void addCheckins(int checkins){
		this.checkins+= checkins;
	}
	public void addPictures(Set<String> pics){
		pictureUrls.addAll(pics);
	}
	
	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}
	
	public int getCheckins() {
		return checkins;
	}
	
	public void setPictureUrls(Set<String> pictureUrls) {
		this.pictureUrls = pictureUrls;
	}
	
	public Set<String> getPictureUrls() {
		return pictureUrls;
	}
	
	public String getPoi() {
		return poi;
	}
	public void setPoi(String poi) {
		this.poi = poi;
	}
	public String getPoiName() {
		return poiName;
	}
	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}
	public String getPoiCategory() {
		return poiCategory;
	}
	public void setPoiCategory(String poiCategory) {
		this.poiCategory = poiCategory;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poi == null) ? 0 : poi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientResult other = (ClientResult) obj;
		if (poi == null) {
			if (other.poi != null)
				return false;
		} else if (!poi.equals(other.poi))
			return false;
		return true;
	}
	
	
}
