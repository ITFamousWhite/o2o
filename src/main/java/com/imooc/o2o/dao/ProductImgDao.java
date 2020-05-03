package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.entity.ProductImg;

public interface ProductImgDao {
     
	/**
	 * 一个商品可以有多张详情图片
	 * 批量插入详情图片
	 * @param productImgList
	 * @return
	 */
	int batchInsertProductImh(List<ProductImg> productImgList);
	/**
	 * 获取详情图片列表
	 * @return
	 */
	List<ProductImg> queryProductImgList(long productImgId);
	/**
	 * 删除详情图片
	 * @param productImgId
	 * @return
	 */
	int deleteProductImg(long productImgId);
}
