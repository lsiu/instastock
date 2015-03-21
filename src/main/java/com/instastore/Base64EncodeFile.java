package com.instastore;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class Base64EncodeFile {
	
	public static void main(String[] args) throws IOException {
		File f = new File("src/main/resources/static/images/Diet_coke_can.png");
		byte[] bytes = FileUtils.readFileToByteArray(f);
		System.out.println(Base64.getEncoder().encodeToString(bytes));
	}

}
