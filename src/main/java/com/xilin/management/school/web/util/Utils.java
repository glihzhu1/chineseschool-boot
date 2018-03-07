package com.xilin.management.school.web.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;
import org.springframework.context.MessageSource;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.xilin.management.school.model.Student;
import com.xilin.management.school.web.reports.ExportingErrorException;
import com.xilin.management.school.web.reports.JasperReportsExporter;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
    
    public static final String[] TABLE_STUDENT_COLUMNS = new String[] {"id", "firstname", "lastname", "email", "gender", "dob"};
    public static final String[] TABLE_FAMILY_COLUMNS = new String[] {"id", "fatherfirstname", "fatherlastname", "motherfirstname", "motherlastname", "homephone", "familyemail"};
    //public static final String[] TABLE_FAMILY_COLUMNS = new String[] {"id", "fatherfirstname", "fatherlastname", "familyemail"};
    private static MessageSource messageSource;
    
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
        else if(response.getStatusCode() == HttpStatus.FOUND){
        	logger.error("The loginId or email is used by anotehr user, updateUserJson possible error with input: " + input);
        }
        else if(response.getStatusCode() == HttpStatus.MULTI_STATUS){
        	logger.error("Too many users with this loginId or email, updateUserJson possible error with input: " + input);
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
	
	public static void export(List<?> allobjects, String[] datatablesColumns, HttpServletResponse response, JasperReportsExporter exporter, String fileName, Locale locale) {
	//public static <T> void export(List<T> allobjects, String[] datatablesColumns, HttpServletResponse response, JasperReportsExporter exporter, String fileName, Locale locale) {
	//public static void export(List<Student> allstudents, String[] datatablesColumns, HttpServletResponse response, JasperReportsExporter exporter, String fileName, Locale locale) {
        // Obtain the filtered and ordered elements
        //Page<Alluser> allusers = getAlluserService().findAll(search, pageable);
        
        // Prevent generation of reports with empty data
        if (allobjects == null || allobjects.isEmpty()) {
            return;
        }
        
        // Creates a new ReportBuilder using DynamicJasper library
        FastReportBuilder builder = new FastReportBuilder();
        
        // IMPORTANT: By default, this application uses "export_default.jrxml"
        // to generate all reports. If you want to customize this specific report,
        // create a new ".jrxml" template and customize it. (Take in account the 
        // DynamicJasper restrictions: 
        // http://dynamicjasper.com/2010/10/06/how-to-use-custom-jrxml-templates/)
        builder.setTemplateFile("templates/reports/export_default.jrxml");
        
        // The generated report will display the same columns as the Datatables component.
        // However, this is not mandatory. You could edit this code if you want to ignore
        // the provided datatablesColumns
        if (datatablesColumns != null) {
            for (String column : datatablesColumns) {
                // Delegates in addColumnToReportBuilder to include each datatables column
                // to the report builder
                addColumnToReportBuilder(column, builder, locale, fileName);
            }
        }
        
        // This property resizes the columns to use full width page.
        // Set false value if you want to use the specific width of each column.
        builder.setUseFullPageWidth(true);
        
        // Creates a new Jasper Reports Datasource using the obtained elements
        JRDataSource ds = new JRBeanCollectionDataSource(allobjects);
        
        // Generates the JasperReport
        JasperPrint jp;
        try {
            jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
        }
        catch (JRException e) {
            String errorMessage = getMessageSource().getMessage("error_exportingErrorException", 
                new Object[] {StringUtils.substringAfterLast(fileName, ".").toUpperCase()}, 
                String.format("Error while exporting data to StringUtils file", StringUtils.
                    substringAfterLast(fileName, ".").toUpperCase()), locale);
            throw new ExportingErrorException(errorMessage);
        }
        
        // Converts the JaspertReport element to a ByteArrayOutputStream and
        // write it into the response stream using the provided JasperReportExporter
        try {
            exporter.export(jp, fileName, response);
        }
        catch (JRException e) {
            String errorMessage = getMessageSource().getMessage("error_exportingErrorException", 
                new Object[] {StringUtils.substringAfterLast(fileName, ".").toUpperCase()}, 
                String.format("Error while exporting data to StringUtils file", StringUtils.
                    substringAfterLast(fileName, ".").toUpperCase()), locale);
            throw new ExportingErrorException(errorMessage);
        }
        catch (IOException e) {
            String errorMessage = getMessageSource().getMessage("error_exportingErrorException", 
                new Object[] {StringUtils.substringAfterLast(fileName, ".").toUpperCase()}, 
                String.format("Error while exporting data to StringUtils file", StringUtils.
                    substringAfterLast(fileName, ".").toUpperCase()), locale);
            throw new ExportingErrorException(errorMessage);
        }
    }

	public static void addColumnToReportBuilder(String columnName, FastReportBuilder builder, Locale locale, String fileName) {
        try {
	        if (columnName.equals("id")) {
	            builder.addColumn("Id", "id", Integer.class.getName(), 50);
	        }
	        else if (columnName.equals("fatherfirstname")) {
	            builder.addColumn("Father Firstname", "fatherfirstname", String.class.getName(), 50);
	        }
	        else if (columnName.equals("fatherlastname")) {
	            builder.addColumn("Father Lastname", "fatherlastname", String.class.getName(), 50);
	        }
	        else if (columnName.equals("familyemail")) {
	            builder.addColumn("Email", "email", String.class.getName(), 50);
	        }
	        else if (columnName.equals("motherfirstname")) {
	            builder.addColumn("Mother Firstname", "motherfirstname", String.class.getName(), 50);
	        }
	        else if (columnName.equals("homephone")) {
	            builder.addColumn("Home Phone", "homephone", String.class.getName(), 50);
	        }
	        else if (columnName.equals("motherlastname")) {
	            builder.addColumn("Mother Lastname", "motherlastname", String.class.getName(), 50);
	        }
	        else if (columnName.equals("gender")) {
	            builder.addColumn("Gender", "gender", Character.class.getName(), 50);
	        }
	        else if (columnName.equals("email")) {
	            builder.addColumn("Email", "familyid.email", String.class.getName(), 100);
	        }
	        else if (columnName.equals("firstname")) {
	            builder.addColumn("Firstname", "firstname", String.class.getName(), 100);
	        }
	        else if (columnName.equals("lastname")) {
	            builder.addColumn("Lastname", "lastname", String.class.getName(), 100);
	        }
	        else if (columnName.equals("dob")) {
	        	ColumnBuilder time = ColumnBuilder.getNew();
	        	time.setTitle("Date of Birth");
	        	time.setWidth(100);
	        	//time.setColumnProperty("dob", GregorianCalendar.class.getName()).setPattern("dd/MM/yyyy hh:mm:ss a");
	        	time.setColumnProperty("dob", Calendar.class.getName());
	        	
	        	time.setCustomExpression(new CustomExpression() {
                    public Object evaluate(Map fields, Map variables, Map parameters) {
                    	Calendar cal = (Calendar)fields.get("dob");
                    	int style = DateFormat.MEDIUM;
                    	DateFormat format1 = DateFormat.getDateInstance(style, Locale.US);
                    	//SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                    	String formatted = format1.format(cal.getTime());
                        return formatted;
                    }

                    public String getClassName() {
                        return Calendar.class.getName();
                    }
                });
	        	
	        	builder.addColumn(time.build());
	        	//new SimpleDateFormat("dd/MM/yyyy").format($F{columnName});
	            //builder.addColumn("Date of Birth", "dob", Calendar.class.getName(), 100);
	        }
	        else if (columnName.equals("lastUpdateDate")) {
	            builder.addColumn("Last Update Date", "lastUpdateDate", Calendar.class.getName(), 100);
	        }
        }
        catch (ColumnBuilderException e) {
            String errorMessage = getMessageSource().getMessage("error_exportingErrorException", 
                new Object[] {StringUtils.substringAfterLast(fileName, ".").toUpperCase()}, 
                String.format("Error while exporting data to StringUtils file", StringUtils.
                    substringAfterLast(fileName, ".").toUpperCase()), locale);
            throw new ExportingErrorException(errorMessage);
        }
        catch (ClassNotFoundException e) {
            String errorMessage = getMessageSource().getMessage("error_exportingErrorException", 
                new Object[] {StringUtils.substringAfterLast(fileName, ".").toUpperCase()}, 
                String.format("Error while exporting data to StringUtils file", StringUtils.
                    substringAfterLast(fileName, ".").toUpperCase()), locale);
            throw new ExportingErrorException(errorMessage);
        }
    }
	
	public static MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
