package com.owen.pDoctor.util;

import com.google.gson.Gson;

public class GsonHelper {

	private static Gson gson = new Gson();

	/**
	 * 把json string 转化成类对象
	 * 
	 * @param str
	 * @param t
	 * @return
	 */
	public static <T> T toType(String str, Class<T> t) {
		T res = gson.fromJson(str, t);
		return res;
	}

	/**
	 * 把类对象转化成json string
	 * 
	 * @param t
	 * @return
	 */
	public static <T> String toJson(T t) {
		return gson.toJson(t);
	}

}
