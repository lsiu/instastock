package com.instastore;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.Enumeration;

import java.security.cert.CertificateException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mastercard.mcwallet.sdk.MasterPassService;
import com.mastercard.mcwallet.sdk.MasterPassServiceRuntimeException;

@SpringBootApplication
public class InstastoreWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstastoreWebApplication.class, args);
    }
    
    @Bean
    public MasterPassService masterPassService() {
    	final String consumerKey = "cLb0tKkEJhGTITp_6ltDIibO5Wgbx4rIldeXM_jRd4b0476c!414f4859446c4a366c726a327474695545332b353049303d";
    	final PrivateKey privateKey = getPrivateKey("Certs/SandboxMCOpenAPI.p12", "changeit");
    	final String originUrl = "http://localhost:8080"; //TODO
    	return new MasterPassService(consumerKey, privateKey, originUrl);
    }
    
    private PrivateKey getPrivateKey(String fileName, String password) {
		
		KeyStore ks;
		Key	key;
		try {
			ks = KeyStore.getInstance("PKCS12");
			// get user password and file input stream
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream stream = cl.getResourceAsStream(fileName);	
			ks.load(stream, password.toCharArray());
			
			Enumeration<String> enumeration = ks.aliases ();
				
			// uses the default alias
			String keyAlias = (String) enumeration.nextElement();

			key = ks.getKey(keyAlias, password.toCharArray());
		} catch (KeyStoreException e) {
			throw new MasterPassServiceRuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new MasterPassServiceRuntimeException(e);
		} catch (CertificateException e) {
			throw new MasterPassServiceRuntimeException(e);
		} catch (IOException e) {
			throw new MasterPassServiceRuntimeException(e);
		} catch (UnrecoverableKeyException e) {
			throw new MasterPassServiceRuntimeException(e);
		}

		return (PrivateKey) key;
	}
}
