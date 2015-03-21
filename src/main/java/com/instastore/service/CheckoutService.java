package com.instastore.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckoutService {

	public CheckoutResult checkout(CheckoutProduct product) {
		HttpClient client = HttpClientBuilder.create()
				.disableCookieManagement()
				.disableContentCompression()
				.disableAuthCaching()
				.disableConnectionState().build();
		byte[] encoded;
		try {
			Random random=new Random();
			long transactionNumber=random.nextInt(2000000000)+8000000000000000000L;
			HttpPost httpPost = new HttpPost(
					"http://dmartin.org:8021/moneysend/v2/transfer");
			
			encoded = Files.readAllBytes(Paths
					.get("src/main/resources/xml.xml"));
			String xml = new String(encoded, StandardCharsets.UTF_8);
			String replaced=xml.replace("{transaction_reference}",
					String.valueOf(transactionNumber))
					.replace("{funding_account_number}",
					product.getBuyerCreditCard())
					.replace("{receiving_card}", product.getSellerCreditCard())
					.replace("{price}", String.valueOf((long)Math.floor((product.getPrice()))));
			StringEntity entity = new StringEntity(replaced);
			entity.setContentType((new BasicHeader(HTTP.CONTENT_TYPE, "application/xml")));
			entity.setContentEncoding(StandardCharsets.UTF_8.displayName());
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
				ResponseHandler<String> handler = new BasicResponseHandler();
				log.info(handler.handleResponse(response));
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
