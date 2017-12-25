package com.xilin.management.school.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.xilin.management.school.web.util.TransientUser;
import com.xilin.management.school.web.util.Utils;

//import org.json.

public class SomeTest {
	//private static final String BASE_URL = "http://localhost:1990/confluence";
    //private static final String USERNAME = "admin";
    //private static final String PASSWORD = "admin";
	private static final String BASE_URL = "https://confluence.cdk.com";
    private static final String USERNAME = "lig";
    private static final String PASSWORD = "Janu01-5";
    private static final String ENCODING = "utf-8";

    private static String getContentRestUrl(final Long contentId, final String[] expansions) throws UnsupportedEncodingException
    {
        final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), ENCODING);

        return String.format("%s/rest/api/content/%s?expand=%s&os_authType=basic&os_username=%s&os_password=%s", BASE_URL, contentId, expand, URLEncoder.encode(USERNAME, ENCODING), URLEncoder.encode(PASSWORD, ENCODING));
    }

    public static void main(final String[] args) throws Exception
    {
    	//https://app-user-manage.herokuapp.com/user
    	//user.api.server.rest.username=testtwo
    	//user.api.server.rest.password=testtwo
    		
    	TransientUser tuser = null;
    	final String uri = "http://app-user-manage.herokuapp.com/user/allusers/loginId/" + "adminone";
    	RestTemplate restTemplate = Utils.createRestTemplate("guolong", "guolong");
        ResponseEntity<String> response = null;
        try {
        	response = restTemplate.exchange(uri, 
        		  HttpMethod.GET, null, String.class);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        if(response.getStatusCode() != HttpStatus.OK) {
        	System.out.println("no user found");
        }
        else {
        	try {
        		tuser = TransientUser.fromJsonToTransientUser(response.getBody());
        		System.out.println("msg="+tuser.toString());
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    	
    	//Utils.updateUserPwdJson(31, "testone");
        /*final long pageId = 138441603;
        HttpClient client = new DefaultHttpClient();

        String pageObj = null;
        HttpEntity pageEntity = null;
        try
        {
            HttpGet getPageRequest = new HttpGet(getContentRestUrl(pageId, new String[] {"body.storage", "version", "ancestors"}));
            HttpResponse getPageResponse = client.execute(getPageRequest);
            pageEntity = getPageResponse.getEntity();

            pageObj = IOUtils.toString(pageEntity.getContent());

            System.out.println("Get Page Request returned " + getPageResponse.getStatusLine().toString());
            System.out.println("");
            System.out.println(pageObj);
        }
        finally
        {
            if (pageEntity != null)
            {
                EntityUtils.consume(pageEntity);
            }
        }

        JSONObject page = new JSONObject(pageObj);

        String pageStorage = page.getJSONObject("body").getJSONObject("storage").getString("value");
        
        System.out.println("\nDone with: size is: " + pageStorage.length());
        */

    }
    
}
