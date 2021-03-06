package com.xilin.management.school.model;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Component
public class SemesterDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Semester> data;

	@Autowired
    SemesterRepository semesterRepository;

	public Semester getNewTransientSemester(int index) {
        Semester obj = new Semester();
        setActive(obj, index);
        setDescription(obj, index);
        setDiscountamount(obj, index);
        setDiscountdate(obj, index);
        setEnddate(obj, index);
        setPayenddate(obj, index);
        setPaystartdate(obj, index);
        setPodfee(obj, index);
        setPodrefundamount(obj, index);
        setRegisterenddate(obj, index);
        setRegisterstartdate(obj, index);
        setRegistrationfee(obj, index);
        setReturnedcheckfee(obj, index);
        setStartdate(obj, index);
        setUpdatedby(obj, index);
        setUpdatedtime(obj, index);
        return obj;
    }

	public void setActive(Semester obj, int index) {
        Boolean active = true;
        obj.setActive(active);
    }

	public void setDescription(Semester obj, int index) {
        String description = "description_" + index;
        if (description.length() > 50) {
            description = description.substring(0, 50);
        }
        obj.setDescription(description);
    }

	public void setDiscountamount(Semester obj, int index) {
        BigDecimal discountamount = BigDecimal.valueOf(index);
        if (discountamount.compareTo(new BigDecimal("0"))==1) {
            discountamount = new BigDecimal("0");
        }
        obj.setDiscountamount(discountamount);
    }

	public void setDiscountdate(Semester obj, int index) {
        Calendar discountdate = Calendar.getInstance();
        obj.setDiscountdate(discountdate);
    }

	public void setEnddate(Semester obj, int index) {
        Calendar enddate = Calendar.getInstance();
        obj.setEnddate(enddate);
    }

	public void setPayenddate(Semester obj, int index) {
        Calendar payenddate = Calendar.getInstance();
        obj.setPayenddate(payenddate);
    }

	public void setPaystartdate(Semester obj, int index) {
        Calendar paystartdate = Calendar.getInstance();
        obj.setPaystartdate(paystartdate);
    }

	public void setPodfee(Semester obj, int index) {
        BigDecimal podfee = BigDecimal.valueOf(index);
        if (podfee.compareTo(new BigDecimal("0"))==1) {
            podfee = new BigDecimal("0");
        }
        obj.setPodfee(podfee);
    }

	public void setPodrefundamount(Semester obj, int index) {
        BigDecimal podrefundamount = BigDecimal.valueOf(index);
        if (podrefundamount.compareTo(new BigDecimal("0"))==1) {
            podrefundamount = new BigDecimal("0");
        }
        obj.setPodrefundamount(podrefundamount);
    }

	public void setRegisterenddate(Semester obj, int index) {
        Calendar registerenddate = Calendar.getInstance();
        obj.setRegisterenddate(registerenddate);
    }

	public void setRegisterstartdate(Semester obj, int index) {
        Calendar registerstartdate = Calendar.getInstance();
        obj.setRegisterstartdate(registerstartdate);
    }

	public void setRegistrationfee(Semester obj, int index) {
        BigDecimal registrationfee = BigDecimal.valueOf(index);
        if (registrationfee.compareTo(new BigDecimal("0"))==1) {
            registrationfee = new BigDecimal("0");
        }
        obj.setRegistrationfee(registrationfee);
    }

	public void setReturnedcheckfee(Semester obj, int index) {
        BigDecimal returnedcheckfee = BigDecimal.valueOf(index);
        if (returnedcheckfee.compareTo(new BigDecimal("0"))==1) {
            returnedcheckfee = new BigDecimal("0");
        }
        obj.setReturnedcheckfee(returnedcheckfee);
    }

	public void setStartdate(Semester obj, int index) {
        Calendar startdate = Calendar.getInstance();
        obj.setStartdate(startdate);
    }

	public void setUpdatedby(Semester obj, int index) {
        String updatedby = "updatedby_" + index;
        if (updatedby.length() > 40) {
            updatedby = updatedby.substring(0, 40);
        }
        obj.setUpdatedby(updatedby);
    }

	public void setUpdatedtime(Semester obj, int index) {
        Calendar updatedtime = Calendar.getInstance();
        obj.setUpdatedtime(updatedtime);
    }

	public Semester getSpecificSemester(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Semester obj = data.get(index);
        Integer id = obj.getId();
        return semesterRepository.findOne(id);
    }

	public Semester getRandomSemester() {
        init();
        Semester obj = data.get(rnd.nextInt(data.size()));
        Integer id = obj.getId();
        return semesterRepository.findOne(id);
    }

	public boolean modifySemester(Semester obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = semesterRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Semester' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Semester>();
        for (int i = 0; i < 10; i++) {
            Semester obj = getNewTransientSemester(i);
            try {
                semesterRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            semesterRepository.flush();
            data.add(obj);
        }
    }
}
