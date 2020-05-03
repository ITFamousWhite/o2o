package com.imooc.o2o.service;

import java.util.List;

import com.imooc.o2o.entity.HeadLine;

public interface HeadLineService {

	/**
	 * 获取头条列表
	 * @param headLineContition
	 * @return
	 */
	List<HeadLine> getHeadLineList(HeadLine headLineContition);
}
