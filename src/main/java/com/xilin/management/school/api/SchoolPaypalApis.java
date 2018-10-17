package com.xilin.management.school.api;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.PayPalRESTException;
import com.xilin.management.school.model.Family;
import com.xilin.management.school.model.FamilyRepository;
import com.xilin.management.school.model.Familybilling;
import com.xilin.management.school.model.FamilybillingRepository;
import com.xilin.management.school.model.Semester;
import com.xilin.management.school.model.SemesterRepository;
import com.xilin.management.school.web.util.Utils;


@RestController
@RequestMapping(value = "/apis", name = "SchoolPaypalApis",produces = MediaType.APPLICATION_JSON_VALUE)
public class SchoolPaypalApis {
	private static final Logger logger = LoggerFactory.getLogger(SchoolPaypalApis.class);
	
	@Autowired
    FamilyRepository familyRepository;
	
	@Autowired
	private FamilybillingRepository familybillingRepository;
	
	@Autowired
	private SemesterRepository semesterRepository;
	
	@RequestMapping(value = "/create-payment", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> createFamilyPaypalPayment(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
        	/*ObjectMapper mapper = new ObjectMapper();
        	ObjectNode node = mapper.readValue(json, ObjectNode.class);*/

        	String amt = "0.01";
        	String currency = "USD";
        	String returnUrl ="";
        	
        	String[] splitted = json.split("&");
            String amtPart = splitted[0];
            amt = amtPart.split("=")[1];
            
            String currencyPart = splitted[1];
            currency = currencyPart.split("=")[1];
            
            //String returnUrlPart = splitted[2];
            //returnUrl = returnUrlPart.split("=")[1];
            
        	Payment result = Utils.createPayment(amt, currency);
            if (result == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            else {
            	return new ResponseEntity<String>(result.toJSON(), headers, HttpStatus.OK);
            }
        } catch (Exception e) {
        	logger.error("An exception error occured: ", e);
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@RequestMapping(value = "/execute-payment", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> executeFamilyPaypalPayment(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
        	String id = "";
        	String payerId = "";
        	String amt = "";
        	
        	String[] splitted = json.split("&");
            String idPart = splitted[0];
            id = idPart.split("=")[1];
            
            String payerIdPart = splitted[1];
            payerId = payerIdPart.split("=")[1];
            
            String amtPart = splitted[2];
            amt = amtPart.split("=")[1];
            
			Payment result = Utils.executePayment(id, payerId);
            if (result == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            else {
            	//post the amt to DB
            	String loginUser = Utils.retrieveLoginUsername();
         		List<Family> loginFamilies = familyRepository.findByLoginIdIgnoreCase(loginUser);
         		
         		if(loginFamilies != null && loginFamilies.size() > 0) {
         			Family family = loginFamilies.get(0);
         			
         			Familybilling familybilling = new Familybilling();
         			
         			familybilling.setAmount(new BigDecimal(amt));
         			familybilling.setBillingtype("Paypal");
         			familybilling.setDescription("payed thru paypal");
         			familybilling.setFamilyid(family);
         			familybilling.setProcesstime(GregorianCalendar.getInstance());
         			familybilling.setStatus(Utils.BILLING_STATUS_PROCESSED);
         			familybilling.setUpdatedby(family.getFatherfirstname() + " " + family.getFatherlastname());
         			familybilling.setUpdatedtime(GregorianCalendar.getInstance());
         			
         			Semester top1sem = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
         			if(top1sem != null) {
         				Semester latestSemester = top1sem;
         				familybilling.setSemesterid(latestSemester);
         			}
         			familybillingRepository.save(familybilling);
         		}
            	return new ResponseEntity<String>(result.toJSON(), headers, HttpStatus.OK);
            }
        } catch (Exception e) {
        	logger.error("An exception error occured: ", e);
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@RequestMapping(value = "/postPaymentToSchool", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> postPaymentToSchool(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
        	String id = "";
        	String payerId = "";
        	
        	String[] splitted = json.split("&");
            String idPart = splitted[0];
            id = idPart.split("=")[1];
            
            String payerIdPart = splitted[1];
            payerId = payerIdPart.split("=")[1];
            
			return null;
			
        } catch (Exception e) {
        	logger.error("An exception error occured: ", e);
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
