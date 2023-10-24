package com.kony.sbg.sideloading.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Console {
	
	private	static final SimpleDateFormat TIME = new SimpleDateFormat("hh:mm:ss:sss");
	
	public	static void print() {
		System.out.println();
	}
	
	public	static void print(String s) {
		System.out.println(TIME.format(new Date())+"::"+s);
	}
	
	public	static void print(boolean s) {
		System.out.println(TIME.format(new Date())+"::"+s);
	}
}
