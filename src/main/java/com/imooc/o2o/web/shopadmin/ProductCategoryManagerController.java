package com.imooc.o2o.web.shopadmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.dto.Result;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.service.ProductCategoryService;

@Controller
@RequestMapping(value = "/shopadmin",method = RequestMethod.GET)
public class ProductCategoryManagerController {
	@Autowired
	private ProductCategoryService productCategoryService;
	
	/**
	 * 根据商铺id查询商铺分类列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getproductcategorylist",method = RequestMethod.GET)
	@ResponseBody
	public Result<List<ProductCategory>> getProductCategoryList(HttpServletRequest request){
		Shop shop = new Shop();
		shop.setShopId(20l);
		request.getSession().setAttribute("currentShop", shop);
		
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		List<ProductCategory> pcList = null;
		if(currentShop != null && currentShop.getShopId() >= 0) {
			pcList = productCategoryService.queryProductCategoryList(currentShop.getShopId());
			return new Result<List<ProductCategory>>(true,pcList);
		}else {
			ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
			return new Result<List<ProductCategory>>(false,ps.getState(),ps.getStateInfo());
		}
	}
	
	@RequestMapping(value = "/addproductcategorys",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList,
			HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<>();
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		for (ProductCategory pc : productCategoryList) {
			pc.setShopId(currentShop.getShopId());
		}
		if(productCategoryList != null && productCategoryList.size()>0) {
			try {
				ProductCategoryExecution pe = productCategoryService.batchAddProductCategory(productCategoryList);
				if(pe.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("batchAddProductCategory error", pe.getStateInfo());
				}
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
 		}else {
 			modelMap.put("success", false);
			modelMap.put("errMsg", "请至少输入一个商品类别");
 		}
		return modelMap;
	}
	
	@RequestMapping(value = "/removeproductcategory",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> removeproductcategory(Long productCategoryId,
			HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<>();
		if(productCategoryId != null && productCategoryId > 0) {
			try {
				Shop shop = (Shop)request.getSession().getAttribute("currentShop");
				ProductCategoryExecution pe = 
						productCategoryService.deleteProductCategory(productCategoryId, shop.getShopId());
				if(pe.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("batchAddProductCategory error", pe.getStateInfo());
				}
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
 		}else {
 			modelMap.put("success", false);
			modelMap.put("errMsg", "请至少选择商品类别");
 		}
		return modelMap;
	}
}
