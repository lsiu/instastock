package com.mastercard.api.common;

import com.mastercard.api.common.openapiexception.MCOpenApiRuntimeException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.*;
import java.util.*;



public abstract class Connector  {




//------------------------------------Constructors Methods-------------------------------------------------------------------------------------------------------------------------------


    /**
     * Constructor to create class. Assigns all URL's and class variables that
     * merchant and environment dependent.
     *
     * 	@param consumerKey - ConsumerKey obtained in the Wallet registration
     *
     *  @param privateKey - Merchants private key used in signing and encryption
     *
     */
    public Connector(String consumerKey,PrivateKey privateKey) {
        this.consumerKey = consumerKey;
        this.privateKey = privateKey;
    }


//------------------------------------Class Variables-------------------------------------------------------------------------------------------------------------------------------------

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String UTF_8 = "UTF-8";
    public static final String EQUALS = "=";
    public static final String AMP = "&";
    public static final String COLON_2X_BACKSLASH = "://";
    public static final String MESSAGE = "Message";
    public static final String HTTP_CODE = "HttpCode";

    protected String consumerKey;
    protected PrivateKey privateKey;

    protected String signatureBaseString;
    protected String authHeader;

    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final String REALM = "realm";
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_VERSION = "oauth_version";
    public static final String OAUTH_SIGNATURE = "oauth_signature";
    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    public static final String OAUTH_NONCE = "oauth_nonce";
    public static final String OAUTH_BODY_HASH = "oauth_body_hash";

    public static final String HTML_TAG = "<html>";
    public static final String BODY_OPENING_TAG = "<body>";
    public static final String BODY_CLOSING_TAG = "</body>";

    public static final String QUESTION_MARK = "?";


    public static final String COMMA = ",";
    public static final String DOUBLE_QOUTE = "\"";

    public static final String EMPTY_STRING = "";


    public static final String OAUTH_START_STRING = "OAuth ";

    public static final String ONE_POINT_ZERO = "1.0";
    public static final String SHA1 = "SHA-1";
    public static final String RSA_SHA1 = "RSA-SHA1";
    public static final String SHA1_WITH_RSA = "SHA1withRSA";

    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String APPLICATION_XML = "application/xml; charset=utf-8";
    public static final String AUTHORIZATION = "Authorization";

    public static final String NULL_RESPONSE_PARAMETERS_ERROR = "ResponseParameters can not be null.";
    public static final String NULL_PRIVATEKEY_ERROR_MESSAGE = "Private Key is null";
    public static final String NULL_PARAMETERS_ERROR = "Null parameters passed to method call";

    public static SecureRandom random = new SecureRandom();
    public static final String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";




    public String getSignatureBaseString() {
        return signatureBaseString;
    }


    public String getAuthHeader() {
        return authHeader;
    }

//------------------------------------protected Helper Methods------------------------------------------------------------------------------------------------------------------------------

    /**
     * Method used to generate the basis configuration used in each connection.
     *
     * @return OAuthParameters object with basis configuration for each connection.
     */
    protected OAuthParameters OAuthParametersFactory() {

        OAuthParameters oparams = new OAuthParameters();
        oparams.addParameter(OAUTH_CONSUMER_KEY,consumerKey);
        oparams.addParameter(OAUTH_NONCE,getNonce());
        oparams.addParameter(OAUTH_TIMESTAMP,getTimestamp());
        oparams.addParameter(OAUTH_SIGNATURE_METHOD,RSA_SHA1);
        oparams.addParameter(OAUTH_VERSION, ONE_POINT_ZERO);
        return oparams;
    }


    /**
     * Converts a XML class to a String containing all the data in the class in XML format
     *
     * @param xmlClass
     *
     * @return Marshaled string containing the data stored in merchantTransactions in an XML format
     *
     * @throws MCOpenApiRuntimeException
     */
    protected String xmlToString(Object xmlClass) {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(xmlClass.getClass());
            StringWriter st = new StringWriter();
           
            jaxbContext.createMarshaller().marshal(xmlClass, st);
            String xml = st.toString();
            return xml;

        } catch (JAXBException e) {
            throw new MCOpenApiRuntimeException(e);
        }
    }

    protected Object stringToXml(String xmlStr, Class<?> className){

        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance( className );
            Unmarshaller u = jc.createUnmarshaller();
            return u.unmarshal( new ByteArrayInputStream (xmlStr.getBytes()));
        } catch (JAXBException e) {
            throw new MCOpenApiRuntimeException(e);
        }
    }

    protected Map<String, String> doRequest(String url, String requestMethod, String body){
        return doRequest(
                url,
                requestMethod,
                OAuthParametersFactory(),
                body
        );
    }

    /**
     * Establish an OAuth connection to OpenAPI over HTTPS.
     *
     * @param httpsURL - The full URL to call, including any query string parameters.
     * @param requestMethod - The request method to be used in the connection
     * @param oparams - The OAuthParameters that hold the variables used to generate the signature base string and authorize header
     * @param body - The payload to send to the MasterCard web services.
     *
     * @return Response from the MasterCard web services
     */
    protected Map<String, String> doRequest(String httpsURL,String requestMethod,
                                            OAuthParameters oparams,String body) {

        if (privateKey == null) {
            throw new MCOpenApiRuntimeException(new UnrecoverableKeyException(NULL_PRIVATEKEY_ERROR_MESSAGE));
        }
        if(body != null && body.length() > 0){
            oparams = setOauthBodyHashParameter(body,oparams);
        }

        HttpsURLConnection con = null;
        try {
            con = setupConnection(httpsURL, requestMethod, oparams, body);
            con.connect();
            if (body != null) {
                writeBodyToConnection(body, con);
            }

            return checkForErrorsAndReturnResponse(con);
        } catch (IOException e) {
            throw new MCOpenApiRuntimeException(e);

        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }


    //------------------------------------private Helper Methods------------------------------------------------------------------------------------------------------------------------------

    /**
     * Method to create the oauth_body_hash for the PostTransaction logging step
     *
     * @param body
     * @param params
     *
     * @return OAuthParameters
     */
    protected OAuthParameters setOauthBodyHashParameter(String body,OAuthParameters params)  {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(SHA1);
        } catch (NoSuchAlgorithmException e) {
            throw new MCOpenApiRuntimeException(e);
        }

        digest.reset();
        byte[] hash;

        try {
            byte[] byteArray = body.getBytes(UTF_8);
            hash = digest.digest(byteArray);
        } catch (UnsupportedEncodingException e) {
            throw new MCOpenApiRuntimeException(e);
        }

        String encodedHash = DatatypeConverter.printBase64Binary(hash);
        params.addParameter(OAUTH_BODY_HASH,encodedHash);
        return params;
    }


    /**
     * Internal method used to set the signatureBaseString right before attempting the connection.
     *
     * @param signatureBaseString
     */
    protected void setSignatureBaseString(String signatureBaseString) {
        this.signatureBaseString = signatureBaseString;
    }

    /**
     * Method to read the response from the HTTPS connection
     *
     * @param in
     *
     * @return The data returned for the connection
     */
    protected String readResponse(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String inputLine;
        StringBuilder builder = new StringBuilder();

        try {
            while ((inputLine = reader.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        return builder.toString();
    }

    /**
     * Method to check the connection response for errors. Returns the response if the response code is less than 300.
     * Throws MCOpenApiRuntimeException is connection errors.
     *
     * @param connection
     *
     * @return Response from the MasterCard web services
     *
     * @throws IOException
     */
    protected Map<String, String> checkForErrorsAndReturnResponse(HttpsURLConnection connection) {
    	try {
            if (connection.getResponseCode() >= SC_MULTIPLE_CHOICES ) {
                String message = readResponse(connection.getErrorStream());
                // Cut the html off of the error message and leave the body
                if(message.contains(HTML_TAG)){
                    message = message.substring(message.indexOf(BODY_OPENING_TAG)+6, message.indexOf(BODY_CLOSING_TAG));
                }
                throw new MCOpenApiRuntimeException(message,connection.getResponseCode());
            } else {
                Map<String,String> responseMap = new HashMap<String,String>();
                responseMap.put(MESSAGE, readResponse(connection.getInputStream()));
                responseMap.put(HTTP_CODE, String.valueOf(connection.getResponseCode()));

                return responseMap;
            }
        } catch (IOException e) {
            throw new MCOpenApiRuntimeException(readResponse(connection.getErrorStream()));
        }
    }


    /**
     * Method parse the response returned from the readResponse method.
     *
     * @param responseParameters
     * @return Map of all parameters in the payload
     */
    protected Map<String, String> parseOAuthResponseParameters(String responseParameters) {
        if (responseParameters == null) {
            throw new MCOpenApiRuntimeException(NULL_RESPONSE_PARAMETERS_ERROR);
        }

        Map<String, String> result = new HashMap<String, String>();
        String[] parameters = responseParameters.split(AMP);

        for (String parameter : parameters) {
            String[] keyValue = parameter.split(EQUALS);
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
            // if the keyValue length is not 2 then they will be ignored
        }
        return result;
    }



    /**
     * Method to send the body to the connection passed in the parameter.
     *
     * @param body
     * @param con
     *
     * @throws IOException
     */
    protected void writeBodyToConnection(String body, HttpsURLConnection con) throws IOException {
    	OutputStreamWriter request = null;
    	try {
    		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    		request= new OutputStreamWriter(con.getOutputStream(), encoder );
    		request.append(body);
    		request.flush();
    	} catch (IOException e) {
    		throw new IOException(e);
    	}
    	finally {
    		if (request != null) {
    			request.close();
    		}
    	}
    } 


    /**
     * Sets up the HTTP connection with the supplied parameters
     *
     * @param httpsURL
     * @param requestMethod
     * @param oparams
     * @param body
     *
     * @return The HTTP connection
     *
     */
    protected HttpsURLConnection setupConnection(String httpsURL,
                                               String requestMethod, OAuthParameters oparams, String body){

        HttpsURLConnection con;
        URL url;
        try {
            url = new URL(httpsURL);
        } catch (MalformedURLException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        try {
            con = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        try {
            con.setRequestMethod(requestMethod);
        } catch (ProtocolException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        con.setSSLSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault());
        con.setDoOutput(true);
        con.setDoInput(true);
        con.addRequestProperty(AUTHORIZATION,buildAuthHeaderString(httpsURL, requestMethod,oparams));
//		con.setRequestProperty("User-Agent","MC Open API OAuth Framework v1.0-Java");

        // Setting the Content Type header depending on the body
        if (body != null) {
            con.addRequestProperty(CONTENT_TYPE, APPLICATION_XML);
            con.addRequestProperty(CONTENT_LENGTH,
                    Integer.toString(body.length()));
        }
        return con;
    }

    /**
     * Generates and signs the signature base string
     *
     * @param httpsURL
     * @param requestMethod
     * @param oparams
     *
     */
    protected void generateAndSignSignature(String httpsURL,String requestMethod, OAuthParameters oparams){
        OAuthParameters sbsParams = new OAuthParameters();
        sbsParams.putAll(oparams.getBaseParameters());

        sbsParams.removeBaseParameter(REALM);
        String baseString;
        baseString = generateSignatureBaseString(httpsURL,requestMethod, sbsParams.getBaseParameters());
        this.setSignatureBaseString(baseString);

        String signature;
        signature = sign(baseString,privateKey);
        oparams.addParameter(OAUTH_SIGNATURE, signature);
    }

    /**
     * Calculates the signature base url as per section 9.1 of the OAuth Spec.
     * This is a concatenation of http method, request url, and other request
     * parameters.
     *
     * @param httpsURL     the url of the request
     * @param requestMethod     the http method, for example "GET" or "PUT"
     * @param baseParameters the request parameters (see section 9.1.3)
     * @return the base string to be used in the OAuth signature
     * @throws com.mastercard.api.common.openapiexception.MCOpenApiRuntimeException if the input url is not formatted properly
     * @see <a href="http://oauth.net/core/1.0/#anchor14">9.1 Signature Base
     *      String</a>
     */
    protected String generateSignatureBaseString(String httpsURL,String requestMethod, Map<String, SortedSet<String>> baseParameters){

        URI requestUri = parseUrl(httpsURL);

        String encodedBaseString;
        encodedBaseString = encode(requestMethod.toUpperCase()) + AMP + encode(normalizeUrl(requestUri)) + AMP + encode(normalizeParameters(httpsURL, baseParameters));

        return encodedBaseString;
    }

    protected URI parseUrl(String httpsURL){
        // validate the request url
        if ((httpsURL == null) || (httpsURL.length() == 0)) {
            throw new MCOpenApiRuntimeException("Request Url cannot be empty");
        }
        // parse the url into its constituent parts.
        URI uri;
        try {
            uri = new URI(httpsURL);
        } catch (URISyntaxException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        catch (Exception e) {
            throw new MCOpenApiRuntimeException(e);
        }
        return uri;
    }


    /**
     * Calculates the normalized request url, as per section 9.1.2 of the OAuth
     * Spec.  This removes the querystring from the url and the port (if it is
     * the standard http or https port).
     *
     * @param uri the request url to normalize (not <code>null</code>)
     * @return the normalized request url, as per the rules in the link above
     * @throws com.mastercard.api.common.openapiexception.MCOpenApiRuntimeException if the input url is not formatted properly
     * @see <a href="http://oauth.net/core/1.0/#rfc.section.9.1.2">9.1.2
     *      Construct Request URL</a>
     */
    public static String normalizeUrl(URI uri) {
        return uri.getScheme() + COLON_2X_BACKSLASH + uri.getHost() + uri.getRawPath();
    }

    /**
     * Calculates the normalized request parameters string to use in the base
     * string, as per section 9.1.1 of the OAuth Spec.
     *
     * @param httpUrl        the request url to normalize (not <code>null</code>)
     * @param requestParameters key/value pairs of parameters in the request
     * @return the parameters normalized to a string
     * @see <a href="http://oauth.net/core/1.0/#rfc.section.9.1.1">9.1.1
     *      Normalize Request Parameters</a>
     */
    public static String normalizeParameters(String httpUrl, Map<String, SortedSet<String>> requestParameters) {

        // add the querystring to the base string (if one exists)
        if (httpUrl.indexOf(QUESTION_MARK) > 0) {
            Map<String, SortedSet<String>> queryParameters =  parseQuerystring(httpUrl.substring(httpUrl.indexOf(QUESTION_MARK) + 1));
            for (Map.Entry<String, SortedSet<String>> e : queryParameters.entrySet()) {
                TreeSet<String> elementsToAddBack = new TreeSet<String>();
                for (String value : e.getValue()) {
                    elementsToAddBack.add(encode(value));
                }
                queryParameters.put(e.getKey(), elementsToAddBack);
            }
            requestParameters.putAll(queryParameters);
        }
        // piece together the base string, encoding each key and value
        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, SortedSet<String>> e : requestParameters.entrySet()) {
            if (e.getValue().size() == 0) {
                continue;
            }
            if (paramString.length() > 0) {
                paramString.append(AMP);
            }
            int tempCounter = 0;
            for (String value : e.getValue()) {
                paramString.append(encode(e.getKey())).append(EQUALS)
                        .append((value));
                if (tempCounter != e.getValue().size() - 1) {
                    paramString.append(AMP);
                }
                tempCounter++;
            }
        }

        return paramString.toString();
    }

    /**
     * Parse a querystring into a map of key/value pairs.
     *
     * @param queryString the string to parse (without the '?')
     * @return key/value pairs mapping to the items in the querystring
     */
    public static Map<String, SortedSet<String>> parseQuerystring(String queryString) {

        Map<String, SortedSet<String>> map = new TreeMap<String, SortedSet<String>>();
        if ((queryString == null) || (queryString.equals(EMPTY_STRING))) {
            return map;
        }
        String[] params = new String[20];
        if (queryString.contains(AMP))
            params = queryString.split(AMP);
        else if (queryString.length() > 0)
            params = new String[]{queryString};
        if (params != null) {
            for (String param : params) {
                try {
                    String[] keyValuePair = param.split(EQUALS, 0);
                    String name = URLDecoder.decode(keyValuePair[0], UTF_8);
                    if (name == "") {
                        continue;
                    }
                    String value = keyValuePair.length > 1 ?
                            URLDecoder.decode(keyValuePair[1], UTF_8) : EMPTY_STRING;
                    if (map.containsKey(name)) {
                        SortedSet<String> tempSet = map.get(name);
                        tempSet.add(value);
                    } else {
                        SortedSet<String> tmpSet = new TreeSet<String>();
                        tmpSet.add(value);
                        map.put(name, tmpSet);
                    }

                } catch (UnsupportedEncodingException e) {
                    throw new MCOpenApiRuntimeException(e);
                    // ignore this parameter if it can't be decoded
                }
            }
        }

        return map;
    }

    /**
     * Formats the input string for inclusion in a url.  Account for the
     * differences in how OAuth encodes certain characters (such as the
     * space character).
     *
     * @param stringToEncode the string to encode
     * @return the url-encoded string
     */
    public static String encode(String stringToEncode) {

        String encoded;
        try {
            encoded = URLEncoder.encode(stringToEncode, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new MCOpenApiRuntimeException(e);
        }
        encoded = encoded.replace("*", "%2A");
        encoded = encoded.replace("~", "%7E");
        encoded = encoded.replace("+", "%20");

        return encoded;
    }

    /**
     * Method to sign the signature base string.
     *
     * @param baseString
     * @param privateKey
     *
     * @return Signature
     */
    protected String sign(String baseString, PrivateKey privateKey){
        try {
            Signature signer = Signature.getInstance(SHA1_WITH_RSA);
            signer.initSign(privateKey);
            signer.update(baseString.getBytes(UTF_8));
            return DatatypeConverter.printBase64Binary(signer.sign());
        } catch (InvalidKeyException e) {
            throw new MCOpenApiRuntimeException(e);
        } catch (SignatureException e) {
            throw new MCOpenApiRuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new MCOpenApiRuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new MCOpenApiRuntimeException(e);
        }
    }

    /**
     * Takes a OAuthParameters object and returns a formatted String for
     * appending to a URl
     *
     * @param params   OAuthParameters that contain all parameters to add to the Authorization Header
     *
     * @return a String containing all the parameters in the OAuthParameters
     *         object in a "paramName=value" format
     */
    protected String buildAuthHeaderString(String httpsURL,String requestMethod,OAuthParameters params) {
        generateAndSignSignature(httpsURL, requestMethod, params);

        StringBuffer buffer = new StringBuffer();

        buffer.append(OAUTH_START_STRING);
        Map<String, SortedSet<String>> paramMap = params.getBaseParameters();
        buffer = parseParameters(buffer, paramMap);

        this.authHeader = buffer.toString();
        return buffer.toString();
    }

    /**
     * Generates string of parameters used in the Authorize header
     *
     * @param buffer - StringBuffer object that the new parameters will be appended to.
     *
     * @param paramMap - Map of parameters that will be appended to the buffer string
     *
     * @return - The buffer string sent in the parameters with all key and value pairs appended to the end of the string.
     */
    protected StringBuffer parseParameters(StringBuffer buffer,Map<String, SortedSet<String>> paramMap) {

        int cnt = 0;
        // loop thru extra parameters and append to OAuth HTTP Header value
        int namesLength = paramMap.keySet().size();
        for (String key : paramMap.keySet()) {
            parseSortedSetValues(buffer,key,paramMap);
            cnt++;
            if (namesLength > cnt) {
                buffer.append(COMMA);
            }
        }

        return buffer;
    }

    protected StringBuffer parseSortedSetValues(StringBuffer buffer,String key,Map<String, SortedSet<String>> paramMap ){
        for(String value : paramMap.get(key)){
            buffer.append(key).append(EQUALS).append(DOUBLE_QOUTE).append(encode(value)).append(DOUBLE_QOUTE);
        }
        return buffer;
    }



    /**
     * Get a nonce for an OAuth request.  OAuth defines the nonce as "a random
     * string, uniquely generated for each request. The nonce allows the Service
     * Provider to verify that a request has never been made before and helps
     * prevent replay attacks when requests are made over a non-secure channel
     * (such as HTTP)."
     *
     * @return the nonce string to use in an OAuth request
     */
    protected static String getNonce() {
        int length = 8;
        StringBuilder nonceString = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            nonceString.append(validChars.charAt(random.nextInt( validChars.length() - 1)));
        }

        return nonceString.toString();
    }

    /**
     * Get a timestamp for an OAuth request.  OAuth defines the timestamp as
     * "the number of seconds since January 1, 1970 00:00:00 GMT. The timestamp
     * value MUST be a positive integer and MUST be equal or greater than the
     * timestamp used in previous requests."
     *
     * @return the timestamp string to use in an OAuth request.
     */
    protected static String getTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

}
