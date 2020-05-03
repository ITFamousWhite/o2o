package com.imooc.o2o.web.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.service.ShopCategoryService;

@Controller
@RequestMapping(value = "/frontend")
public class MainPageController {
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    
    @RequestMapping(value = "/listmainpageinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listMainPageInfo(){
    	Map<String,Object> modelMap = new HashMap<>();
    	List<ShopCategory> shopCategoryList = new ArrayList<>();
    	try {
    		shopCategoryList = shopCategoryService.getShopCategoryList(null);
    		modelMap.put("shopCategoryList", shopCategoryList);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg",e.getMessage());
		}
    	@SuppressWarnings("unused")
		List<HeadLine> headLineList = new ArrayList<HeadLine>();
    	try {
    		HeadLine headLine = new HeadLine();
    		headLine.setEnableStatus(1);
    		headLineList = headLineService.getHeadLineList(headLine);
    		modelMap.put("headLineList", headLineList);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg",e.getMessage());
		}
    	modelMap.put("success", true);
    	return modelMap;
    }
}
