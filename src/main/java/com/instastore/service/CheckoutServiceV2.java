package com.instastore.service;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastercard.mcwallet.sdk.MasterPassService;
import com.mastercard.mcwallet.sdk.RequestTokenResponse;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCart;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartItem;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartRequest;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartResponse;

@Service
@Slf4j
public class CheckoutServiceV2 {
	
	@Autowired
	private MasterPassService mpService;
	
	public CheckoutResult checkout(CheckoutProduct product) {
		final String requestTokenUrl = "https://sandbox.api.mastercard.com/oauth/consumer/v1/request_token";
		final String shoppingCartUrl = "https://sandbox.api.mastercard.com/oauth/consumer/v1/request_token";
		final String oauthCallback = "http://localhost:8080/oauthcallback";
		final String originUrl = "http://localhost:8080";
		final String oauthSignature = "";
		final String oauthConsumerKey = "";
				
//		final String oauth = String.format("OAuth oauth_callback=\"%s\"," //
//				+ "oauth_signature=\"%s\"," //
//				+ "oauth_version=\"1.0\"," //
//				+ "oauth_nonce=\"%s\"," //
//				+ "oauth_signature_method=\"RSA-SHA1\"," //
//				+ "oauth_consumer_key=\"%s\"," //
//				+ "oauth_timestamp=\"%s\"," //
//				+ "realm=\"eWallet\"", //
//				oauthCallback, // oauth_callback
//				oauthSignature, // oauth_signature
//				UUID.randomUUID().toString(), // oauth_nonce
//				oauthConsumerKey, // oauth_consumer_key
//				System.currentTimeMillis() // oauth_timestamp
//				);

		RequestTokenResponse tokenResponse = mpService.getRequestTokenAndRedirectUrl(
				requestTokenUrl, //requestUrl, 
				"http://localhost:8080/callback/oauth", //callbackUrl
				"master,amex,diners,discover,maestro,visa", // acceptableCards
				"a4a6x1ywxlkxzhensyvad1hepuouaesuv", // checkoutProjectId, 
				"v6", // xmlVersion
				false, // shippingSuppression
				false, // authLevelBasic
				false, // rewards, 
				null); // redirectShippingProfile

		log.debug("tokenResponse: {}", ToStringBuilder.reflectionToString(tokenResponse));
		
		ShoppingCartRequest request = new ShoppingCartRequest();
		request.setOriginUrl(originUrl);
		request.setOAuthToken(tokenResponse.getOauthToken());
		
		ShoppingCart cart = new ShoppingCart();
		request.setShoppingCart(cart);
		
		List<ShoppingCartItem> itemList = cart.getShoppingCartItem();
		
		int price100 = (int)(product.getPrice() * 100);
		ShoppingCartItem item = new ShoppingCartItem();
		item.setDescription(product.getDescription());
		item.setQuantity(1);;
		item.setValue(price100);
		item.setImageURL("http://localhost:8080/product/image/" + product.getId());
		itemList.add(item);
		
		cart.setCurrencyCode("USD");
		cart.setSubtotal(price100);
		
		try {
			if (log.isDebugEnabled()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
				StringWriter writer = new StringWriter();
				jaxbContext.createMarshaller().marshal(request, writer);
				log.debug("request: {}", writer.toString());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(String.format(
					"Cannot unmarshell request: '%s'", 
					ToStringBuilder.reflectionToString(request)));
		}
		
		ShoppingCartResponse cartResponse = mpService.postShoppingCartData(shoppingCartUrl, request);
		
		log.debug("cartResponse: {}", ToStringBuilder.reflectionToString(cartResponse));

		return new CheckoutResult();
	}

}
