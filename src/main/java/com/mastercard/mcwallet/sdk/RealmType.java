/**
 * 
 */
package com.mastercard.mcwallet.sdk;

/**
 * @author e049408
 *
 */
public enum RealmType {
	eWallet,meWallet;
	
	 public static RealmType fromString(String text) {
		 if (text != null) {
		      for (RealmType b : RealmType.values()) {
		        if (text.equalsIgnoreCase(b.toString())) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }
}
