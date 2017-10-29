package com.evasler.clientapp;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbResult {
	
	int id;
	int user;
	String poi;
	String poiName;
	String poiCategory;
	int poiCatId;
	double latitude;
	double longtitude;
	String time;
	String pictureUrl;
	
	public DbResult(ResultSet rs) {
		if (rs != null) {
			try {
				id = rs.getInt(1);
				user = rs.getInt(2);
				poi = rs.getString(3);
				poiName = rs.getString(4);
				poiCategory = rs.getString(5);
				poiCatId = rs.getInt(6);
				latitude = rs.getDouble(7);
				longtitude = rs.getDouble(8);
				time = rs.getString(9);
				pictureUrl = rs.getString(10);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int getId() {
		return id;
	}

	public int getUser() {
		return user;
	}

	public String getPoi() {
		return poi;
	}

	public String getPoiName() {
		return poiName;
	}

	public String getPoiCategory() {
		return poiCategory;
	}

	public int getPoiCatId() {
		return poiCatId;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public String getTime() {
		return time;
	}

	public String getPictureUrl() {
		return pictureUrl;
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
		DbResult other = (DbResult) obj;
		if (poi == null) {
			if (other.poi != null)
				return false;
		} else if (!poi.equals(other.poi))
			return false;
		return true;
	}
	
	
}
