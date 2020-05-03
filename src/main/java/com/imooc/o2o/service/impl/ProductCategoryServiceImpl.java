package com.imooc.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ProductCategoryDao;
import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.exceptions.ProductCategoryOperationException;
import com.imooc.o2o.service.ProductCategoryService;
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService{
	
	@Autowired
	private ProductCategoryDao productCategoryDao;
	@Autowired
	private ProductDao  productDao;
	/**
	 * 根据shopId查询商品分类列表
	 */
	@Override
	public List<ProductCategory> queryProductCategoryList(long shopId) {
		return productCategoryDao.queryProductCategoryList(shopId);
	}
	/**
	 * 批量新增商品分类信息
	 */
	@Override
	@Transactional
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryOperationException {
		if(productCategoryList != null && productCategoryList.size() > 0) {
			try {
			   int effedNum= productCategoryDao.batchInsertProductCategory(productCategoryList);
			   if(effedNum <= 0) {
				throw  new ProductCategoryOperationException("创建商品类别失败");
			   }else {
				   return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			   }
			} catch (Exception e) {
				throw new ProductCategoryOperationException("batchAddProductCategory error:" + e.getMessage());
			}
		}else {
			return new  ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		}
	}
	@Override
	@Transactional
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId)
			throws ProductCategoryOperationException {
		// TODO 将此商品类别下的商品类别置为空
		try {
			int effedNum = productDao.updateProductCategoryToNull(productCategoryId);
			if(effedNum <= 0) {
				throw new ProductCategoryOperationException("商品类别更新失败！");
			}
		} catch (Exception e) {
			throw new ProductCategoryOperationException("商品类别失败:"+e.getMessage());
		}
		try {
			int count = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if(count <= 0) {
				throw new ProductCategoryOperationException("删除商品信息失败！");
			}else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		} catch (Exception e) {
			throw new ProductCategoryOperationException("delProductCategory Error:"+e.getMessage());
		}
	}
}
