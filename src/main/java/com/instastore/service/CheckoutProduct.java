package com.instastore.service;

import lombok.Data;

@Data
public class CheckoutProduct {

	private String id;

	private double price;
	
	private String description;

	private String sellerCreditCard;

	private String buyerCreditCard;

}
