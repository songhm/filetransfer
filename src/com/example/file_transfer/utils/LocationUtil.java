package com.example.file_transfer.utils;


public class LocationUtil {
		
	private final static double EARTH_RADIUS = 6378137.0;
	private final static double epc = 1e-10;
	public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
		
		if((lat_a<epc&&lng_a<epc)||(lat_b<epc&&lng_b<epc)) return 0;
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
		+ Math.cos(radLat1) * Math.cos(radLat2)
		* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
}
