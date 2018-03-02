package com.xilin.management.school.web.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);
	
	public static final String DEFAULT_FAMILY_SORT_FIELD = "id";
	
	public static final String INVALID_PWD = "invalidpwd";
	
	//public static final int ACTIVE = 1;
    //public static final int INACTIVE = 0;
	public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    
    public static final int STATUS_OK = 0;
    public static final int STATUS_NO_PARENT_INFO = 1;
    public static final int STATUS_NO_PHONE_INFO = 2;
	
    public static final String ROLE_XILINFAMILY = "ROLE_XILINFAMILY";
    public static final String ROLE_XILINADMIN = "ROLE_XILINADMIN";
    public static final String ROLE_XILINBOARD = "ROLE_XILINBOARD";
    public static final String ROLE_XILINTEACHER = "ROLE_XILINTEACHER";
    public static final String ROLE_XILINSELLER = "ROLE_XILINSELLER";
    
    public static final Character PERSONNEL_ADMIN = 'A';
    public static final Character PERSONNEL_BOARD = 'B';
    public static final Character PERSONNEL_TEACHER = 'T';
    public static final Character PERSONNEL_SELLER = 'S';
    
    public static final String CLASS_STATUS_OPEN = "Open";
    public static final String CLASS_STATUS_FULL = "Full";
    public static final String CLASS_STATUS_CLOSED = "Closed";
    
    public static final String FAMILY_TRANSACTION_REGISTER = "RG";
    public static final String FAMILY_TRANSACTION_BUYBOOK = "BK";
    
    //public static final String FAMILY_BILLING_REGISTER = "RG";
    //public static final String FAMILY_BILLING_BUYBOOK = "BK";
    public static final String BOOK_TRANSACTION_ORERED = "ORDERED";
    public static final String BOOK_TRANSACTION_PROCESSED = "PROCESSED";
    public static final String BOOK_TRANSACTION_RETURNED = "RETURNED";
    
    public static final String BILLING_STATUS_PAID = "PAID";
    public static final String BILLING_STATUS_PROCESSED = "PROCESSED";
    public static final String BILLING_STATUS_DEACTIVATED = "DEACTIVATED";
    
    public static final String BILLING_TYPE_PAYMENT = "PAYMENT";
    public static final String BILLING_TYPE_CREDIT = "CREDIT";
    public static final String BILLING_TYPE_REFUND = "REFUND";
    public static final String BILLING_TYPE_FINE = "FINE";
    
    public static final String POD_PERIOD_ONE = "12:00-13:50";
    public static final String POD_PERIOD_TWO = "14:00-15:50";
    
    public static GregorianCalendar dateDaysAgo(int days) {
    	GregorianCalendar dataCalendar= new GregorianCalendar();
    	dataCalendar.add(GregorianCalendar.DAY_OF_YEAR, (-1) * days);
    	
    	return dataCalendar;
    }
    
    public static GregorianCalendar dateDaysLater(int days) {
    	GregorianCalendar dataCalendar= new GregorianCalendar();
    	dataCalendar.add(GregorianCalendar.DAY_OF_YEAR, (1) * days);
    	
    	return dataCalendar;
    }
    
    public static boolean dateSameday(Calendar cal1, Calendar cal2) {
    	return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && 
    			cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
	public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
	
	public static String retrieveRoleBasedOnType(Character type) {
        
        if(type.equals(PERSONNEL_ADMIN)) {
            return ROLE_XILINADMIN;
        }
        
        if(type.equals(PERSONNEL_TEACHER)) {
            return ROLE_XILINTEACHER;
        }
        
        if(type.equals(PERSONNEL_BOARD)) {
            return ROLE_XILINBOARD;
        }
        
        if(type.equals(PERSONNEL_SELLER)) {
            return ROLE_XILINSELLER;
        }
        return "ROLE_UNKNOWN";
    }
	
	public static String retrieveLoginUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	    	return auth.getName();
	    }
	    else {
	    	return("");
	    }
	}
    
	
	public static boolean hasRole(String role) {
        // get security context from thread local
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return false;

        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return false;

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (role.equals(auth.getAuthority()))
                return true;
        }

        return false;
    }
	
	public static RestTemplate createRestTemplate(String apiusername, String apipwd) {
		CredentialsProvider provider = new BasicCredentialsProvider();
	    UsernamePasswordCredentials credentials = 
	    		new UsernamePasswordCredentials(apiusername, apipwd);
	    provider.setCredentials(AuthScope.ANY, credentials);
	    
	    Header header = new BasicHeader(
				HttpHeaders.CONTENT_TYPE, "application/json");
		Collection<Header> headers = Arrays.asList(header);
		
		
		/*CloseableHttpClient client = null;
		try {
		SSLContextBuilder builder = new SSLContextBuilder();
		 builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		 SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
		 Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
		            .register("http", new PlainConnectionSocketFactory())
		            .register("https", sslConnectionSocketFactory)
		            .build();

		 PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		 cm.setMaxTotal(100);
		 client = HttpClients.custom()
				 .setDefaultHeaders(headers)
	        		.setDefaultCredentialsProvider(provider).useSystemProperties()
		            .setSSLSocketFactory(sslConnectionSocketFactory)
		            .setConnectionManager(cm)
		            .build();
		} catch (NoSuchAlgorithmException e) {
		    throw new RuntimeException(e);
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}*/
		
		
		
	    CloseableHttpClient client = HttpClientBuilder.create()
        		.setDefaultHeaders(headers)
        		.setDefaultCredentialsProvider(provider).useSystemProperties().build();
        ClientHttpRequestFactory requestFactory = 
            new HttpComponentsClientHttpRequestFactory(client);
        
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(
        		new BasicAuthorizationInterceptor(apiusername, apipwd));
        
        return restTemplate;
	}
	
	public static TransientUser createUserJson(String uri, String apiusername, String apipwd,
			String familyLogin, String familyPassword, String loginEmail, String role) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		String input =new JSONObject()
        	.put("loginId", familyLogin)
        	.put("passwordHash", familyPassword)
        	.put("appRole", role)
        	.put("userActive", true)
        	.put("email", loginEmail).toString();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);

	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(uri + 
	    	"/allusers/json/create", HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
        
        if(response.getStatusCode() == HttpStatus.MULTI_STATUS) {
        	logger.error("createFamilyUserJson user loginId or email already used multiple times with input: " + input);
        }
        else if(response.getStatusCode() == HttpStatus.CREATED ||
        		response.getStatusCode() == HttpStatus.FOUND){
        	try {
        		TransientUser auser = TransientUser.fromJsonToTransientUser(response.getBody());
        		return auser;
        	}
        	catch (Exception e) {
        		logger.error(e.getMessage());
        		e.printStackTrace();
        	}
        }
        else {
        	logger.error("createFamilyUserJson possible error with input: " + input);
        }
        return null;
	}
	
	public static boolean updateUserJson(int id, String uri, String apiusername, String apipwd, String familyLogin, String loginEmail) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		String input =new JSONObject()
        	.put("id", id)
        	.put("loginId", familyLogin)
        	.put("email", loginEmail).toString();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);

	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(uri + 
	    	"/allusers/update/"+ id, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
        
        if(response.getStatusCode() == HttpStatus.OK){
        	return true;
        }
        else {
        	logger.error("updateUserJson possible error with input: " + input);
        }
        return false;
	}
	
	public static boolean updateUserPwdJson(int id, String uri, String apiusername, String apipwd, String pwd) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		String input = new JSONObject()
        	.put("id", id)
        	.put("passwordHash", pwd).toString();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);

	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(uri + 
	    	"/allusers/pwd/"+ id, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
        
        if(response.getStatusCode() == HttpStatus.OK){
        	return true;
        }
        else {
        	logger.error("updateUserPwdJson possible error with input: " + input);
        }
        return false;
	}

	public static boolean checkUserExternalIdExistJson(int id, String uri, String apiusername, String apipwd) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		/*String input =new JSONObject()
        	.put("id", id).toString();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);*/

	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(uri + 
	    	"/allusers/extId/"+ id, HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException httpClientOrServerExc) {
        	if(HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
              return false;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        
        if(response.getStatusCode() == HttpStatus.OK){
        	return true;
        }
        
        return false;
	}
	
	public static TransientUser retrieveUserLoginIdExistJson(String loginId, String uri, String apiusername, String apipwd) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		final String userLoadUri = uri + "/allusers/loginId/" + loginId + "/";
		
	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(userLoadUri, 
          		  HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException httpClientOrServerExc) {
        	if(HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
              return null;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        
        if(response.getStatusCode() != HttpStatus.OK) {
        	return null;
        }
        else {
        	try {
        		TransientUser auser = TransientUser.fromJsonToTransientUser(response.getBody());
        		return auser;
        	}
        	catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
    	
        return null;
    }
	
	public static TransientUser retrieveUserEmailExistJson(String email, String uri, String apiusername, String apipwd) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		//with email has a . at its end, a traing / is needed...
		final String userLoadUri = uri + "/allusers/email/" + email + "/";
		
	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(userLoadUri, 
          		  HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException httpClientOrServerExc) {
        	if(HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
              return null;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        
        if(response.getStatusCode() != HttpStatus.OK) {
        	return null;
        }
        else {
        	try {
        		TransientUser auser = TransientUser.fromJsonToTransientUser(response.getBody());
        		return auser;
        	}
        	catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
    	
        return null;
        
	}
	
	public static TransientUser retrieveUserEmailAndLoginIdExistJson(String email, String loginId, String uri, String apiusername, String apipwd) {
		RestTemplate restTemplate = Utils.createRestTemplate(apiusername, apipwd);
		
		final String userLoadUri = uri + "/allusers/emailloginid/" + email + "/" + loginId + "/";
		
	    ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(userLoadUri, 
          		  HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException httpClientOrServerExc) {
        	if(HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
              return null;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        
        if(response.getStatusCode() != HttpStatus.OK) {
        	return null;
        }
        else {
        	try {
        		TransientUser auser = TransientUser.fromJsonToTransientUser(response.getBody());
        		return auser;
        	}
        	catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
    	
        return null;
        
	}
	
}
