package com.dachen.health.commons.utils;

/**
 * 根据经纬度计算两个地点之间的距离
 * 
 * @author 钟良
 * @date 2016年11月11日
 *
 */
public class MapDistance { 
    public static void main(String[] args) {
        //济南国际会展中心经纬度：117.11811  36.68484
        //趵突泉：117.00999000000002  36.66123
    	//计算两个地址之间的距离
       System.out.println(Distance(113.951154d,22.554007d,113.929815d,22.53783d));
       System.out.println(Distance(113.951154d,22.554007d,113.95605d,22.530516d));
       System.out.println(Distance(113.951154d,22.554007d,113.934784d,22.536553d));
       System.out.println(Distance(113.951154d,22.554007d,113.952566d,22.552405d));
       // double str=100.9;
       // System.out.println(test(str));
    	
    }
    
    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * @param lng1 用户经度
     * @param lat1 用户纬度
     * @param lng2 商家经度
     * @param lat2 商家纬度
     * @return
     */
    public static double Distance(double lng1, double lat1, double lng2,  
    		double lat2) {
    	double a, b, R;  
    	R = 6378137; // 地球半径  
    	lat1 = lat1 * Math.PI / 180.0;  
    	lat2 = lat2 * Math.PI / 180.0;  
    	a = lat1 - lat2;  
    	b = (lng1 - lng2) * Math.PI / 180.0;  
    	double d;  
    	double sa2, sb2;  
    	sa2 = Math.sin(a / 2.0);  
    	sb2 = Math.sin(b / 2.0);  
    	d = 2  
    			* R  
    			* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)  
    			* Math.cos(lat2) * sb2 * sb2));
    	d = Math.round(d)/1000d;
    	return d;  
    }  
   
   
     
}