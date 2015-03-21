/**
 * 
 */
package com.mastercard.mcwallet.sdk;

/**
 * @author e049408
 *
 */
public class AccessTokenResponse {

	private String oauthToken;
	private String oauthTokenSecret;
		
	
	public String getOauthToken() {
		return oauthToken;
	}
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}
	public void setOauthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}
}
