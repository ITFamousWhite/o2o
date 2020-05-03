package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.ProductCategory;

public interface ProductCategoryDao {
    /**
     * 查询商品列表信息
     * @param productCategory
     * @param rowsIndex
     * @param pageSize
     * @return
     */
	List<ProductCategory> queryProductCategoryList(long shopId);
	/**
	 * 批量新增商品分类信息
	 * @param productCategory
	 * @return
	 */
	int batchInsertProductCategory(List<ProductCategory> productCategory);
	/**
	 * 删除商品Id，然后删除商品分类信息
	 * @param productCategoryId
	 * @param shopId
	 * @return
	 */
	int deleteProductCategory(@Param("productCategoryId")long productCategoryId,@Param("shopId")long shopId);
}
