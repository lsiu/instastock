/**
 * 
 */
package com.mastercard.mcwallet.sdk;

/**
 * @author e049408
 *
 */
public class RequestTokenResponse extends AccessTokenResponse {
	
	private String authorizationUrl;
	private String oauthCallbackConfirmed;
	private String oauthExpiresIn;
	private String redirectUrl;
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getAuthorizationUrl() {
		return authorizationUrl;
	}
	public void setAuthorizationUrl(String authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
	}
	public String getOauthCallbackConfirmed() {
		return oauthCallbackConfirmed;
	}
	public void setOauthCallbackConfirmed(String oauthCallbackConfirmed) {
		this.oauthCallbackConfirmed = oauthCallbackConfirmed;
	}
	public String getOauthExpiresIn() {
		return oauthExpiresIn;
	}
	public void setOauthExpiresIn(String oauthExpiresIn) {
		this.oauthExpiresIn = oauthExpiresIn;
	}
}
