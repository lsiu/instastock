/**
 * 
 */
package com.mastercard.mcwallet.sdk;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.PrivateKey;

import com.mastercard.api.common.Connector;
import com.mastercard.api.common.OAuthParameters;
import com.mastercard.mcwallet.sdk.util.HtmlEncoder;
import com.mastercard.mcwallet.sdk.xml.allservices.Checkout;
import com.mastercard.mcwallet.sdk.xml.allservices.ExpressCheckoutRequest;
import com.mastercard.mcwallet.sdk.xml.allservices.ExpressCheckoutResponse;
import com.mastercard.mcwallet.sdk.xml.allservices.MerchantTransactions;
import com.mastercard.mcwallet.sdk.xml.allservices.PrecheckoutDataRequest;
import com.mastercard.mcwallet.sdk.xml.allservices.PrecheckoutDataResponse;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartItem;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartRequest;
import com.mastercard.mcwallet.sdk.xml.allservices.ShoppingCartResponse;


import com.mastercard.mcwallet.sdk.xml.switchapiservices.MerchantInitializationRequest;
import com.mastercard.mcwallet.sdk.xml.switchapiservices.MerchantInitializationResponse;




import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;




/**
 * @author Brady Georgen - brady.georgen@daugherty.com
 *
 */
public class MasterPassService extends Connector {
	
    protected static final String OAUTH_TOKEN = "oauth_token";
    protected static final String OAUTH_VERIFIER = "oauth_verifier";
	protected static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	protected static final String OAUTH_CALLBACK = "oauth_callback";
	protected static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";
	protected static final String OAUTH_EXPIRES_IN = "oauth_expires_in";
	protected static final String REALM = "realm";
	protected static final String REALM_TYPE = "eWallet";
	protected static final String ORIGIN_URL = "originUrl";
	
	protected static final String REQUEST_AUTH_URL = "xoauth_request_auth_url";
	protected static final String URL_FORMAT = "?oauth_token=%s&acceptable_cards=%s&checkout_identifier=%s&version=%s";
	
	protected static final String SUPPRESS_SHIPPING_URL_PARAMETER = "&suppress_shipping_address=";
	protected static final String AUTH_LEVEL_BASIC_URL_PARAMETER = "&auth_level=basic";
	protected static final String ACCEPT_REWARD_PROGRAM = "accept_reward_program";
	protected static final String SHIPPING_LOCATION_PROFILE = "shipping_location_profile";
    
	protected static final String DEFAULT_VERSION = "v1";
	protected static final String XML_VERSION_REGEX = "v[0-9]+";
	
	private String originUrl;
	/**
	 * @return the originUrl
	 */
	public String getOriginUrl() {
		return originUrl;
	}
	/**
	 * @param originUrl the originUrl to set
	 */
	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}
	/**
	 * @param consumerKey
	 * @param privateKey
	 * @param originUrl
	 */
	public MasterPassService(String consumerKey, PrivateKey privateKey, String originUrl) {
		super(consumerKey, privateKey);
		this.originUrl = originUrl;
	};
	/**
	 * 
	 */
	public RequestTokenResponse getRequestTokenAndRedirectUrl(String requestUrl,String callbackUrl,String acceptableCards,String checkoutProjectId
			,String xmlVersion,Boolean shippingSuppression,Boolean authLevelBasic, Boolean rewards,String redirectShippingProfile){
		RequestTokenResponse requestTokenResponse;
		requestTokenResponse = getRequestToken(requestUrl,callbackUrl);
		return requestTokenResponse;
	}
	/**
	 * 
	 */
	public RequestTokenResponse getPairingToken(String requestUrl,String callbackUrl) {
		RequestTokenResponse requestTokenResponse;
		requestTokenResponse = getRequestToken(requestUrl,callbackUrl);
		return requestTokenResponse;
	} 
	/**
	 * 
	 */
	protected OAuthParameters OAuthParametersFactory(){
		OAuthParameters params = super.OAuthParametersFactory();
		params.addParameter(REALM, REALM_TYPE);
		return params;
	}
	/**
	 * 
	 */
	private RequestTokenResponse getRequestToken(String requestUrl,String callbackUrl) {
		
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_CALLBACK, callbackUrl);
		
		String response =doRequest(requestUrl, POST,params,EMPTY_STRING).get(MESSAGE);
		Map<String, String> requestTokenResponseParameters = parseOAuthResponseParameters(response);
		RequestTokenResponse requestTokenResponse = new RequestTokenResponse();
		
		try {
			requestTokenResponse.setAuthorizationUrl(URLDecoder.decode(requestTokenResponseParameters.get(REQUEST_AUTH_URL),UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new MasterPassServiceRuntimeException(e);
		}	
		requestTokenResponse.setOauthToken(requestTokenResponseParameters.get(OAUTH_TOKEN));
		requestTokenResponse.setOauthCallbackConfirmed(requestTokenResponseParameters.get(OAUTH_CALLBACK_CONFIRMED));		
		requestTokenResponse.setOauthExpiresIn(requestTokenResponseParameters.get(OAUTH_EXPIRES_IN));	
		requestTokenResponse.setOauthTokenSecret(requestTokenResponseParameters.get(OAUTH_TOKEN_SECRET));	
		return requestTokenResponse;

	}
	/**
	 * 
	 */
	private String getConsumerSignInUrl (String requestToken, String authorizationUrl, String acceptedCards, String checkoutIdentifier
			,String xmlVersion,Boolean shippingSupression, boolean authLevelBasic, boolean rewards,String redirectShippingProfile) {
		
		if(requestToken == null || authorizationUrl == null || checkoutIdentifier == null) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}
		xmlVersion = xmlVersion.toLowerCase();
		
		// Use v1 if xmlVersion does not match correct pattern
		if ( !xmlVersion.matches(XML_VERSION_REGEX)) {
			xmlVersion = DEFAULT_VERSION;
		}
		try {
			String redirectUrl = String.format((URLDecoder.decode(authorizationUrl,UTF_8) + URL_FORMAT),
						requestToken, acceptedCards, checkoutIdentifier,xmlVersion);
			
			if(!xmlVersion.equals(DEFAULT_VERSION)) {
				if(shippingSupression){
					redirectUrl += SUPPRESS_SHIPPING_URL_PARAMETER + shippingSupression.toString();
				}
				if(authLevelBasic){
					redirectUrl += AUTH_LEVEL_BASIC_URL_PARAMETER;
				}
			}
			
			if(Integer.parseInt(xmlVersion.substring(1)) >= 4){
				if(rewards){
					redirectUrl += AMP + ACCEPT_REWARD_PROGRAM + EQUALS + rewards ;
				}
				
				// Add a shipping profile only if the shipping suppression is false and there is a redirectShippingProfile
				if(redirectShippingProfile != null && redirectShippingProfile.length() > 0 && !shippingSupression){
					redirectUrl += AMP + SHIPPING_LOCATION_PROFILE + EQUALS + redirectShippingProfile ;
				}
			}
			return redirectUrl;
		} catch (UnsupportedEncodingException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		
	}
	/**
	 * @param responseParameters
	 */
	public Map<String, String> parseConnectionResponse(String responseParameters){
		Map<String, String> params = super.parseOAuthResponseParameters(responseParameters);
		for (Map.Entry<String, String> entry : params.entrySet()){
			try {
				params.put(entry.getKey(), URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return params;
	}
	/**
	 * 
	 */
	public AccessTokenResponse getAccessToken(String accessUrl, String requestToken, String oauthVerifier){
		if(requestToken == null || oauthVerifier == null ) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}
	
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_TOKEN,requestToken);
		params.addParameter(OAUTH_VERIFIER,oauthVerifier);
			
		Map<String, String> response = doRequest(accessUrl, POST,params, EMPTY_STRING); 
		Map<String, String> oauthParameters = parseOAuthResponseParameters(response.get(MESSAGE));
		
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.setOauthToken(oauthParameters.get(OAUTH_TOKEN));
		accessTokenResponse.setOauthTokenSecret(oauthParameters.get(OAUTH_TOKEN_SECRET));
		return accessTokenResponse;

	}
	/**
	 * 
	 */
	public AccessTokenResponse getLongAccessToken(String accessUrl, String requestToken, String oauthVerifier){
		if(requestToken == null || oauthVerifier == null ) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}
	
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_TOKEN,requestToken);
		params.addParameter(OAUTH_VERIFIER,oauthVerifier);
			
		String response = doRequest(accessUrl, POST,params, EMPTY_STRING).get(MESSAGE); 
		
		Map<String, String> oauthParameters = parseOAuthResponseParameters(response);
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.setOauthToken(oauthParameters.get(OAUTH_TOKEN));
		accessTokenResponse.setOauthTokenSecret(oauthParameters.get(OAUTH_TOKEN_SECRET));
		return accessTokenResponse;

	}
	
	public MerchantInitializationResponse postMerchantInitData(String merchantInitUrl,
			MerchantInitializationRequest merchantInitXml) {
				
		String responseXml= postXmlData(merchantInitUrl,this.xmlToString(merchantInitXml)).get(MESSAGE);
		MerchantInitializationResponse merchantInitResponse;
		try {			
			StreamSource stream = new StreamSource(new StringReader(responseXml));
			JAXBContext jaxb = JAXBContext.newInstance(MerchantInitializationRequest.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<MerchantInitializationResponse> je = unmarshaller.unmarshal(stream, MerchantInitializationResponse.class);
			merchantInitResponse = (MerchantInitializationResponse) je.getValue();
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		return merchantInitResponse;
	}
	
	public PrecheckoutDataResponse getPreCheckoutData(String preCheckoutUrl, PrecheckoutDataRequest preCheckoutXml, String accessToken) {
		if(accessToken == null || preCheckoutUrl == null || preCheckoutXml == null) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}

		String xmlData = this.xmlToString(preCheckoutXml);
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_TOKEN,accessToken);
		params = setOauthBodyHashParameter(xmlData, params);
				
		String responseXml = doRequest(preCheckoutUrl,POST,params,xmlData).get(MESSAGE);
		PrecheckoutDataResponse preCheckoutDataResponse;
		try {
			StreamSource stream = new StreamSource(new StringReader(responseXml));
			JAXBContext jaxb = JAXBContext.newInstance(PrecheckoutDataRequest.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<PrecheckoutDataResponse> je = unmarshaller.unmarshal(stream, PrecheckoutDataResponse.class);
			preCheckoutDataResponse = (PrecheckoutDataResponse) je.getValue();
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		return preCheckoutDataResponse;
	}
	
	/**
	 * 
	 */
	public ShoppingCartResponse postShoppingCartData(String shoppingCartUrl, ShoppingCartRequest shoppingCartXml){
		
		List<ShoppingCartItem> shoppingCartItems = shoppingCartXml.getShoppingCart().getShoppingCartItem();
		for (int i = 0; i < shoppingCartItems.size(); i++) {
			ShoppingCartItem item = shoppingCartItems.get(i);
			String description = item.getDescription().substring(0, Math.min(item.getDescription().length(), 100));
			if (description.endsWith("&")) {
				description = description.substring(0, description.length()-1);
			}
			shoppingCartItems.get(i).setDescription(description);
		}
		
		String responseXml= postXmlData(shoppingCartUrl, xmlToString(shoppingCartXml)).get(MESSAGE);
		ShoppingCartResponse shoppingCartResponse;
		try {
			
			StreamSource stream = new StreamSource(new StringReader(responseXml));
			JAXBContext jaxb = JAXBContext.newInstance(ShoppingCartRequest.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<ShoppingCartResponse> je = unmarshaller.unmarshal(stream, ShoppingCartResponse.class);
			shoppingCartResponse = (ShoppingCartResponse) je.getValue();
			
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		return shoppingCartResponse;
	}
	/**
	 * 
	 */
	public Checkout getPaymentShippingResource(String checkoutResourceUrl,String accessToken) {
		if(accessToken == null || checkoutResourceUrl == null ) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_TOKEN,accessToken);

		String response = doRequest(checkoutResourceUrl,GET,params,null).get(MESSAGE);
		Checkout checkout;
		try {
			StreamSource stream = new StreamSource(new StringReader(response));
			JAXBContext jaxb = JAXBContext.newInstance(Checkout.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<Checkout> je = unmarshaller.unmarshal(stream, Checkout.class);
			checkout = (Checkout) je.getValue();
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		
		return checkout;		
	}
	/**
	 * 
	 */
	public MerchantTransactions postCheckoutTransaction(String postbackUrl,MerchantTransactions merchantTransactions ){	
		String response = postXmlData(postbackUrl,this.xmlToString(merchantTransactions)).get(MESSAGE);
		MerchantTransactions responseXml;
		try {
			StreamSource stream = new StreamSource(new StringReader(response));
			JAXBContext jaxb = JAXBContext.newInstance(MerchantTransactions.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<MerchantTransactions> je = unmarshaller.unmarshal(stream, MerchantTransactions.class);
			responseXml = (MerchantTransactions) je.getValue();
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}		
		return responseXml;
	}
	/*
	 * 
	 */
	public ExpressCheckoutResponse getExpressCheckoutData(String expressCheckoutUrl, ExpressCheckoutRequest expressCheckoutRequestXml, String accessToken) {
		if(accessToken == null || expressCheckoutUrl == null || expressCheckoutRequestXml == null) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}

		String xmlData = this.xmlToString(expressCheckoutRequestXml);
		OAuthParameters params = OAuthParametersFactory();
		params.addParameter(OAUTH_TOKEN,accessToken);
		params = setOauthBodyHashParameter(xmlData, params);
				
		String responseXml = doRequest(expressCheckoutUrl,POST,params,xmlData).get(MESSAGE);
		ExpressCheckoutResponse expressCheckoutResponse;
		try {
			StreamSource stream = new StreamSource(new StringReader(responseXml));
			JAXBContext jaxb = JAXBContext.newInstance(ExpressCheckoutRequest.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			JAXBElement<ExpressCheckoutResponse> je = unmarshaller.unmarshal(stream, ExpressCheckoutResponse.class);
			expressCheckoutResponse = (ExpressCheckoutResponse) je.getValue();
		} catch (JAXBException e) {
			throw new MasterPassServiceRuntimeException(e);
		}
		return expressCheckoutResponse;
	}
	
	/**
	 * Method used to post XML data to the MasterCards services. xmlData must conform to the schema defined in the WalletCheckoutService.xsd file.
	 * 
	 * @param url - URL to send the data
	 * @param xmlData - XML string that conforms to the Wallet schema
	 * 
	 * @return The confirmation string sent back from MasterCard Services.
	 */
	private Map<String, String> postXmlData(String url, String xmlData){
		if(xmlData == null  ) {
			throw new MasterPassServiceRuntimeException(NULL_PARAMETERS_ERROR);
		}
		
		OAuthParameters params = OAuthParametersFactory();
		params = setOauthBodyHashParameter(xmlData, params);		
		return doRequest(url,POST,params,xmlData);
	}
}
