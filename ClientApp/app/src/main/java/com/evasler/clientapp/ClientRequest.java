package com.evasler.clientapp;

import java.io.Serializable;
import java.util.Date;

public class ClientRequest implements Serializable {

	private static final long serialVersionUID = 3087083294867027434L;
	double x1;
	double y1;
	double x2;
	double y2;
	String dateFrom;
	String dateTo;
	
	
	
	public ClientRequest(double x1, double y1, double x2, double y2,String dateFrom, String dateTo) {
		super();
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	
}
