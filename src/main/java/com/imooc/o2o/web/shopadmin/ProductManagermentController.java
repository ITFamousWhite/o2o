package com.imooc.o2o.web.shopadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.service.ProductCategoryService;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping(value = "/shopadmin")
public class ProductManagermentController {
	 	@Autowired
	 	private ProductService productService;
	 	@Autowired
	 	private ProductCategoryService productCategoryService;
	 	//上传图片的最大数量
	 	private static final int IMAGEMAXCOUNT = 6;
	 	
	 	
	 	/**
	 	 * 获取商品信息列表
	 	 * @param request
	 	 * @return
	 	 */
	 	@RequestMapping(value = "getproductlist",method = RequestMethod.GET)
	 	@ResponseBody
	 	public Map<String,Object> getProductList(HttpServletRequest request){
	 		Map<String,Object> modelMap = new HashMap<>();
	 		//获取前台传来的页码
	 		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
	 		//获取前台传过来的每页要求返回的商品数
	 		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
	 		//从当前Session中获取店铺，主要获取店铺id
	 		long shopId = HttpServletRequestUtil.getInt(request, "shopId");
	 		Shop shop = new Shop();
	 		shop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", shop);
	 		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
	 		//控制判断
	 		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
				// 获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品名去筛选某个店铺下的商品列表
				// 筛选的条件可以进行排列组合
				long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
				String productName = HttpServletRequestUtil.getString(request, "productName");
				Product productCondition = compactProductCondition(currentShop.getShopId(), productCategoryId, productName);
				// 传入查询条件以及分页信息进行查询，返回相应商品列表以及总数
				ProductExecution pe = productService.queryProductList(productCondition, pageIndex, pageSize);
				modelMap.put("productList", pe.getProductList());
				modelMap.put("count", pe.getCount());
				modelMap.put("success", true);
			} else {
				modelMap.put("success", false);
				modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
			}
			return modelMap;
	 	}
	 	
	 	/**
	 	 * 根据Id查询商品信息
	 	 * @param productId
	 	 * @return
	 	 */
		@RequestMapping(value = "getproductbyid",method = RequestMethod.GET)
	 	@ResponseBody
	 	public Map<String,Object> getProductById(@RequestParam Long productId){
	 		Map<String,Object> modelMap = new HashMap<String,Object>();
	 		List<ProductCategory> productCategoryList = new ArrayList<>();
	 		if(productId > -1) {
	 			Product product = productService.queryProductById(productId);
	 			productCategoryList = productCategoryService.queryProductCategoryList(product.getShop().getShopId());
	 				modelMap.put("product", product);
	 				modelMap.put("productCategoryList", productCategoryList);
	 				modelMap.put("success", true);
	 		}else{
				modelMap.put("success", false);
 				modelMap.put("errMeg","empty productId");
			}
	 		return modelMap;
	 	}
	 	
		/**
		 * 更新商品信息
		 * @param request
		 * @return
		 */
	 	@RequestMapping(value = "/modifyproduct",method = RequestMethod.POST)
	 	@ResponseBody
	 	public Map<String,Object> modifyProduct(HttpServletRequest request){
	 		Map<String,Object> modelMap = new HashMap<>();
	 		// 是商品编辑时候调用还是上下架操作的时候调用
			// 若为前者则进行验证码判断，后者则跳过验证码判断
			boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
	 		//1.对验证码进行校验
	 		if(!statusChange && !CodeUtil.checkVerifyCode(request)) {
	 			modelMap.put("success", false);
	 			modelMap.put("errMsg", "验证码输入错误！");
	 			return modelMap;
	 		}
	 		//2.接受前端参数变量的初始化，包括商品，缩略图，商品详情图片列表实体类
	 		ObjectMapper mapper = new ObjectMapper();
	 		Product product = null;
	 		ImageHolder thumbnail = null;
	 		List<ImageHolder> productImgList = new ArrayList<>();
	 		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
	 				request.getSession().getServletContext());
	 		try {
				//若请求中有文件流传入
	 			if(multipartResolver.isMultipart(request)) {
	 				thumbnail = handleImage(request,thumbnail,productImgList);
	 			}else {
	 				modelMap.put("success", false);
	 				modelMap.put("errMsg", "上传图片不能为空");
	 			}
			} catch (Exception e) {
				modelMap.put("success", false);
 				modelMap.put("errMsg", e.getMessage());
			}
	 		try {
		 		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		 		//尝试将前端传过来的表单String流 并将其转换成实体类
		 		product = mapper.readValue(productStr, Product.class);
			} catch (Exception e) {
				modelMap.put("success", false);
 				modelMap.put("errMsg", e.toString());
 				return modelMap;
			}
	 		//如果product信息，缩略图以及详情图片列表为空，则开始进行商品添加操作
	 		if(product != null) {
	 			try {
					//从session中获取前端id并赋值给product，减少其对前端数据的依赖
	 				Shop currrentShop = (Shop) request.getSession().getAttribute("currentShop");
	 				product.setShop(currrentShop);
	 				//执行添加操纵
	 				ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);
	 				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
	 					modelMap.put("success", true);
	 				}else {
	 					modelMap.put("success", false);
	 					modelMap.put("errMsg", pe.getStateInfo());
	 				}
				} catch (Exception e) {
					modelMap.put("success", false);
 					modelMap.put("errMsg", e.getMessage());
 					return modelMap;
				}
	 		}else {
	 			modelMap.put("success", false);
				modelMap.put("errMsg","请输入商品信息！");
	 		}
	 		return modelMap;
	 	}
	 	
	 	@RequestMapping(value = "/addproduct",method = RequestMethod.POST)
	 	@ResponseBody
	 	public Map<String,Object> addProduct(HttpServletRequest request){
	 		Map<String,Object> modelMap = new HashMap<>();
	 		//1.对验证码进行校验
	 		if(!CodeUtil.checkVerifyCode(request)) {
	 			modelMap.put("success", false);
	 			modelMap.put("errMsg", "验证码输入错误！");
	 			return modelMap;
	 		}
	 		//2.接受前端参数变量的初始化，包括商品，缩略图，商品详情图片列表实体类
	 		ObjectMapper mapper = new ObjectMapper();
	 		Product product = null;
	 		ImageHolder thumbnail = null;
	 		List<ImageHolder> productImgList = new ArrayList<>();
	 		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
	 				request.getSession().getServletContext());
	 		try {
				//若请求中有文件流传入
	 			if(multipartResolver.isMultipart(request)) {
	 				thumbnail = handleImage(request,thumbnail,productImgList);
	 			}else {
	 				modelMap.put("success", false);
	 				modelMap.put("errMsg", "上传图片不能为空");
	 			}
			} catch (Exception e) {
				modelMap.put("success", false);
 				modelMap.put("errMsg", e.getMessage());
			}
	 		try {
		 		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		 		//尝试将前端传过来的表单String流 并将其转换成实体类
		 		product = mapper.readValue(productStr, Product.class);
			} catch (Exception e) {
				modelMap.put("success", false);
 				modelMap.put("errMsg", e.toString());
 				return modelMap;
			}
	 		//如果product信息，缩略图以及详情图片列表为空，则开始进行商品添加操作
	 		if(product != null && thumbnail != null && productImgList.size()>0) {
	 			try {
					//从session中获取前端id并赋值给product，减少其对前端数据的依赖
	 				Shop currrentShop = (Shop) request.getSession().getAttribute("currentShop");
	 				product.setShop(currrentShop);
	 				//执行添加操纵
	 				ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);
	 				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
	 					modelMap.put("success", true);
	 				}else {
	 					modelMap.put("success", false);
	 					modelMap.put("errMsg", pe.getStateInfo());
	 				}
				} catch (Exception e) {
					modelMap.put("success", false);
 					modelMap.put("errMsg", e.getMessage());
 					return modelMap;
				}
	 		}else {
	 			modelMap.put("success", false);
				modelMap.put("errMsg","请输入商品信息！");
	 		}
	 		return modelMap;
	 	}
	 	
	 	/**
	 	 * 对前端传入的图片文件流进行处理
	 	 * @param request
	 	 * @param thumbnail
	 	 * @param productImgList
	 	 * @return
	 	 * @throws Exception
	 	 */
		private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail,
				List<ImageHolder> productImgList) throws Exception {
	 		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	 		//取出缩略图并构建ImageHandler对象
	 		CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
			if(thumbnailFile != null) {
				thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(),thumbnailFile.getInputStream());
			}
			//取出详情列表并构建List<ImageHolder>列表对象，最多支持6张图片上传
			for (int i = 0; i <IMAGEMAXCOUNT; i++) {
				CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg"+i);
				if(productImgFile != null) {
					//若取出的详情图片文件流不为空，将其加入详情图列表
					ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(),productImgFile.getInputStream());
					productImgList.add(productImg);
				}else {
					break;
				}
			}
			return thumbnail;
		}
		
		/**
		 * 封装查询条件在Product实例之中
		 * @param shopId
		 * @param productCategoryId
		 * @param productName
		 * @return
		 */
		private Product compactProductCondition(long shopId, long productCategoryId, String productName) {
			Product productCondition = new Product();
			Shop shop = new Shop();
			shop.setShopId(shopId);
			productCondition.setShop(shop);
			// 若有指定类别的要求则添加进去
			if (productCategoryId != -1L) {
				ProductCategory productCategory = new ProductCategory();
				productCategory.setProductCategoryId(productCategoryId);
				productCondition.setProductCategory(productCategory);
			}
			// 若有商品名模糊查询的要求则添加进去
			if (productName != null) {
				productCondition.setProductName(productName);
			}
			return productCondition;
		}
}
