package com.instastore;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.instastore.service.CheckoutProduct;
import com.instastore.service.CheckoutResult;
import com.instastore.service.CheckoutService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = InstastoreWebApplication.class)
@WebAppConfiguration
public class SendMoneyPost {
	
	@Autowired
	private CheckoutService checkoutService;
	
	@Test
	public void test(){
		CheckoutProduct checkoutProduct=new CheckoutProduct();
		checkoutProduct.setId("10");
		checkoutProduct.setPrice(100);
		checkoutProduct.setBuyerCreditCard("5184680430000006");
		checkoutProduct.setSellerCreditCard("5184680430000014");
		CheckoutResult checkoutResult=checkoutService.checkout(checkoutProduct);
		assertTrue(checkoutResult.isSuccess());
	}
}
