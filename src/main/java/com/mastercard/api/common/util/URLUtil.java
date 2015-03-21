package com.mastercard.api.common.util;

import com.mastercard.api.common.openapiexception.MCOpenApiRuntimeException;

import java.net.URLEncoder;

public class URLUtil {

    public static String addQueryParameter(String url, String descriptor, String value) throws MCOpenApiRuntimeException{
        if (value != null && !value.equals("null")){
            StringBuilder builder = new StringBuilder(url);
            return builder.append("&").append(descriptor).append("=").append(encode(value)).toString();
        } else {
            return url;
        }
    }

    public static String encode(String value){
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex){
            throw new MCOpenApiRuntimeException(ex);
        }
    }
}
