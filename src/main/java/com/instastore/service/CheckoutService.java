package com.instastore.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckoutService {

	private AtomicLong transactionNumber = new AtomicLong(4000000001010101020L);

	public CheckoutResult checkout(CheckoutProduct product) {
		HttpClient client = HttpClientBuilder.create().build();
		byte[] encoded;
		try {
			HttpPost httpPost = new HttpPost(
					"http://dmartin.org:8021/moneysend/v2/transfer");
			httpPost.addHeader("Content-Type", "application/xml");
			
			encoded = Files.readAllBytes(Paths
					.get("src/main/resources/xml.xml"));
			String xml = new String(encoded, StandardCharsets.UTF_8);
			String replaced=xml.replace("{transaction_reference}",
					String.valueOf(transactionNumber.addAndGet(1)))
					.replace("{funding_account_number}",
					product.getBuyerCreditCard())
					.replace("{receiving_card}", product.getSellerCreditCard())
					.replace("{price}", String.valueOf((long)Math.floor((product.getPrice()))));
			HttpEntity entity = new ByteArrayEntity(replaced.getBytes(StandardCharsets.UTF_8));
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			if (Objects.equals(Response.SC_OK, response.getStatusLine()
					.getStatusCode())) {
				CheckoutResult checkoutResult = new CheckoutResult();
				checkoutResult.setSuccess(true);
				return checkoutResult;
			} else {
				log.info("Response of send money is {}",
						response.getStatusLine());
				CheckoutResult checkoutResult = new CheckoutResult();
				checkoutResult.setSuccess(false);
				return checkoutResult;
			}
		} catch (IOException e) {
			log.error("Error in sending money {}", e);
			CheckoutResult checkoutResult = new CheckoutResult();
			checkoutResult.setSuccess(false);
			return checkoutResult;
		}
	}

}
