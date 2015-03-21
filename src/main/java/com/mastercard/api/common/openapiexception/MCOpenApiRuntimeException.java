package com.mastercard.api.common.openapiexception;

public class MCOpenApiRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	private int httpCode;

	public MCOpenApiRuntimeException(Exception e) {
		e.printStackTrace();
		this.initCause(e);
		this.message = e.getMessage();
	}
	
	public MCOpenApiRuntimeException(String message,int code) {
		this.message = message;
		this.httpCode = code;
	}
	public MCOpenApiRuntimeException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	public int getHttpCode(){
		return httpCode;
	}
}
