package com.owen.pDoctor.util;

import java.util.Comparator;

public class ListComparator implements Comparator<Integer>{

	@Override
	public int compare(Integer a, Integer b) {
		if(a>b){
			return 1;
		}else{
			return -1;
		}
	}

}
