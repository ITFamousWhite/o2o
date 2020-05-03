package com.imooc.o2o.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCaiculator;
import com.imooc.o2o.util.PathUtil;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;

	@Override
	@Transactional
	// 1.处理缩略图并将缩略图返回给product
	// 2.往tb_product中写入信息，获取ProductId
	// 3.结合ProductId批量处理商品详情图片
	// 4.将商品详情图片插入到tb_product_img中
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList)
			throws ProductOperationException {
		// 1.进行空值判断
		if (product != null && product.getProductId() != null) {
			// 2.给对象赋上默认值
			product.setCreateTime(new Date());
			product.setLastEditTime(new Date());
			// 3.默认为上架的状态
			product.setEnableStatus(1);
			// 4.若商品的缩略图不为空进行添加
			if (thumbnail != null) {
				addThumbnail(product, thumbnail);
			}
			try {
				// 54.创建商品信息
				int effectedNum = productDao.insertProduct(product);
				if (effectedNum <= 0) {
					throw new ProductOperationException("创建商品信息失败");
				}
			} catch (Exception e) {
				throw new ProductOperationException("创建商品信息失败:" + e.getMessage());
			}
			// 6.若商品详情图不为空进行添加
			if (productImgList != null && productImgList.size() > 0) {
				addProductImgList(product, productImgList);
			}
			return new ProductExecution(ProductStateEnum.SUCCESS, product);
		} else {
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}
	
	@Override
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList)
			throws ProductOperationException {
		if(product != null && product.getShop() != null && product.getShop().getShopId() > 0) {
			product.setLastEditTime(new Date());
			if(thumbnail != null) {
				Product tempProduct = productDao.queryProductByProductId(product.getProductId());
				if(tempProduct.getImgAddr()!=null) {
					ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
				}
				addThumbnail(tempProduct, thumbnail);
			}
			if(productImgList != null && productImgList.size() > 0) {
				deleteProductImgs(product.getProductId());
				addProductImgList(product, productImgList);
			}
			try {
				int effectedNum = productDao.updateProduct(product);
				if(effectedNum <= 0) {
					throw new ProductOperationException("更新商品信息失败！");
				}
				return new ProductExecution(ProductStateEnum.SUCCESS,product);
			} catch (Exception e) {
				throw new ProductOperationException("更新商品信息失败！"+e.toString());
			}
		}else {
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}
	
	/**
	 * 获取缩略图路径
	 * @param product
	 * @param thumbnail
	 */
	private void addThumbnail(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		product.setImgAddr(thumbnailAddr);
	}
	
	/**
	 * 删除详情图片信息
	 * @param productId
	 */
	private void deleteProductImgs(long productId) {
		List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
		for (ProductImg productImg : productImgList) {
			ImageUtil.deleteFileOrPath(productImg.getImgAddr());
		}
		productImgDao.deleteProductImg(productId);
	}
	/**
	 * 获取详情图片列表
	 * @param product
	 * @param productImgList
	 */
	private void addProductImgList(Product product, List<ImageHolder> productImgList) {
		//获取图片的储存路径，存放在相应的文件夹底下
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		List<ProductImg> productImageList = new ArrayList<>();
		//对获取的图片进行遍历，添加至productImg实体类中
		for (ImageHolder productImgHolder : productImgList) {
			String thumbnailAddr = ImageUtil.generateThumbnail(productImgHolder, dest);
			ProductImg productImg = new ProductImg();
			productImg.setImgAddr(thumbnailAddr);
			productImg.setProductId(product.getProductId());
			productImg.setCreateTime(new Date());
			productImageList.add(productImg);	
		}
		//有图品进行添加就将图片进行批量添加操作
		if(productImageList.size() > 0) {
			try {
				int effectedNum = productImgDao.batchInsertProductImh(productImageList);
				if(effectedNum <= 0 ) {
					throw new ProductOperationException("创建图片信息失败");
				}
			} catch (Exception e) {
				throw new ProductOperationException("创建图片信息失败"+e.getMessage());
			}
		}
	}

	@Override
	public Product queryProductById(long productId) {
		return productDao.queryProductByProductId(productId);
	}

	@Override
	public ProductExecution queryProductList(Product productCondition, int rowIndex, int pageSize) {
		int rowsIndex=PageCaiculator.cageCaiculatorRowIndex(rowIndex, pageSize);
		List<Product> productList =productDao.queryProductList(productCondition, rowsIndex, pageSize);
		int count=productDao.queryProductCount(productCondition);
		ProductExecution pe=new ProductExecution();
		if(productList!=null) {
			pe.setProductList(productList);
			pe.setCount(count);
		}else {
			pe.setState(ProductStateEnum.INNER_ERROR.getState());
		}
		return pe;
	}

	
}
