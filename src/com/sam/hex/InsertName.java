package com.sam.hex;

public class InsertName{
	public static String insert(String text, String name){
		String inserted = text.replaceAll("#",name);
		return inserted;
	}
}