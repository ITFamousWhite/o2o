package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.HeadLine;

public interface HeadLineDao {
   
	/**
	 * 获取头条列表
	 * @param headLine
	 * @return
	 */
	List<HeadLine> queryHeadLineList(@Param("HeadLineCondition") HeadLine headLine);
}
