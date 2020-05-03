package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;

public class ShopDaoTest extends BaseTest{
	
	@Autowired
	private ShopDao shopDao;
	
	@Test
	public void testQueryByShopId() {
		Shop shop=shopDao.queryByShopId(1l);
		System.out.println(shop.getArea().getAreaId());
		System.out.println(shop.getArea().getAreaName());
	}
	
	@Test
	public void testQueryShopListCount() {
		Shop shop=new Shop();
		PersonInfo personInfo=new PersonInfo();
		Area area=new Area();
		ShopCategory shopCategory=new ShopCategory();
		personInfo.setUserId(1L);
		
		shop.setOwner(personInfo);
		
		
		
		List<Shop> shopList=shopDao.queryShopList(shop,0,5);
		System.out.println("店铺列表大小---："+shopList.size());
		System.out.println("店铺总数："+shopDao.queryShopCount(shop));
		shopCategory.setShopCategoryId(2l);
		shop.setShopCategory(shopCategory);
		System.out.println("更新店铺总数："+shopDao.queryShopCount(shop));
	}
	
	@Test
	public void testInsertShop() {
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
		shop.setShopName("test");
		shop.setShopDesc("test");
		shop.setShopAddr("test");
		shop.setPhone("test");
		shop.setShopImg("test");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(1);
		shop.setAdvice("审核中");
		
		int effectedNum=shopDao.insertShop(shop);
		assertEquals(1,effectedNum);
	}
	
	@Test
	public void testUpdateShop() {
		Shop shop=new Shop();
		shop.setShopId(1l);
		shop.setShopDesc("测试描述");
		shop.setShopAddr("测试地址");
		shop.setAdvice("审核中");
		shop.setLastEditTime(new Date());
		int effectedNum=shopDao.updateShop(shop);
		assertEquals(1,effectedNum);
	}

}
