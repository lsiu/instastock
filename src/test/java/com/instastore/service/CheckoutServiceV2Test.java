package com.instastore.service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.instastore.InstastoreWebApplication;
import com.instastore.service.CheckoutServiceV2Test.TestConfiguration;
import com.mastercard.mcwallet.sdk.MasterPassService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class CheckoutServiceV2Test {
	
	@Configuration
	static class TestConfiguration {
		@Bean
		public MasterPassService masterPassService() {
			return (new InstastoreWebApplication()).masterPassService();
		}
		
		@Bean
		public CheckoutServiceV2 checkoutService() {
			return new CheckoutServiceV2();
		}
		
	}

	@Autowired
	private CheckoutServiceV2 checkoutService;
	
	@Test
	public void testCheckout() {
		CheckoutProduct product = new CheckoutProduct();
		product.setDescription("Some good stuff");
		product.setPrice(1.99d);
		checkoutService.checkout(product);
	}

}

