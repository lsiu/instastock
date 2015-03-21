package com.instastore.service;

import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
	
	public CheckoutResult checkout(CheckoutProduct product) {
		return new CheckoutResult();
	}

}
