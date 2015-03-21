package com.instastore.mvc;

import lombok.Data;

/**
 * Created by lsiu on 3/21/2015.
 */
@Data
public class Product {

    private String id;

    private String name;

    private String description;

    private double price;
    
    // base64 encoded image
    private String image;
    
    private String creditCard;

}
