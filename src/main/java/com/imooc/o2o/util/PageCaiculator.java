package com.imooc.o2o.util;

public class PageCaiculator {
	public static int cageCaiculatorRowIndex(int pageIndex,int pageSize) {
		return (pageIndex>0)?(pageIndex-1)*pageSize:0;
	}
}
