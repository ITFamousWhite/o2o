package com.imooc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShopServiceTest extends BaseTest{

	@Autowired
	private ShopService shopService;
	
	@Test
	public void testBgetShopList() {
		Shop shop=new Shop();
		PersonInfo owner=new PersonInfo();
		owner.setUserId(1l);
		shop.setOwner(owner);
		ShopExecution se=shopService.getShopList(shop, 0, 5);
		System.out.println(se.getCount());
	}
	
	@Test
	public void testCModifyShop() throws FileNotFoundException,ShopOperationException{
		Shop shop=new Shop();
		shop.setShopId(1l);
		shop.setShopName("修改后店铺名称");
		File file=new File("C:\\Users\\14024\\Desktop\\images\\xx.jpg");
		InputStream is=new FileInputStream(file);
		ImageHolder imageHolder = new ImageHolder("xx.jpg",is);
		ShopExecution se=shopService.modifyShop(shop,imageHolder);
		System.out.println("新的图片地址为："+se.getShop().getShopImg());
	}
	
	@Test
	public void testAinsertShop() throws FileNotFoundException {
		Shop shop=new Shop();
		PersonInfo personInfo=new PersonInfo();
		Area area=new Area();
		ShopCategory shopCategory=new ShopCategory();
		personInfo.setUserId(1L);
		area.setAreaId(4);
		shopCategory.setShopCategoryId(1L);
		shop.setOwner(personInfo);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("测试店铺2");
		shop.setShopDesc("test2");
		shop.setShopAddr("test2");
		shop.setPhone("test2");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
		shop.setAdvice("审核中");
		
		File shopImg=new File("C:\\Users\\14024\\Desktop\\images\\timg.jpg");
		InputStream is=new FileInputStream(shopImg);
		ImageHolder imageHolder = new ImageHolder("xx.jpg",is);
		ShopExecution shopExecution=shopService.addShop(shop, imageHolder );
		assertEquals(ShopStateEnum.CHECK.getState(),shopExecution.getState());
	}
}
