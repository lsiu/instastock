/**
 * 
 */
package com.mastercard.mcwallet.sdk;

import com.mastercard.api.common.openapiexception.MCOpenApiRuntimeException;

/**
 * @author Brady Georgen - brady.georgen@daugherty.com
 *
 */
public class MasterPassServiceRuntimeException extends MCOpenApiRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param e
	 */
	public MasterPassServiceRuntimeException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param code
	 */
	public MasterPassServiceRuntimeException(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MasterPassServiceRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
