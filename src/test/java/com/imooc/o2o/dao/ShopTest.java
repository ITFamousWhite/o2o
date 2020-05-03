package com.imooc.o2o.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.ShopCategory;

public class ShopTest extends BaseTest{
     
	@Autowired
	private ShopDao shopDao;
	
	@Test
	public void insertShop() {
		Shop shop = new Shop();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		ShopCategory parentCategory = new ShopCategory(); 
		
		
	}
}
