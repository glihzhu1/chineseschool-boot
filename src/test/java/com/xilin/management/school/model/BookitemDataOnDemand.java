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
public class BookitemDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Bookitem> data;

	@Autowired
    BookitemRepository bookitemRepository;

	public Bookitem getNewTransientBookitem(int index) {
        Bookitem obj = new Bookitem();
        setActive(obj, index);
        setBookchinesename(obj, index);
        setBookcode(obj, index);
        setBookdescription(obj, index);
        setBookname(obj, index);
        setAmountinstock(obj, index);
        setAmountsold(obj, index);
        /*setBookdiscountprice(obj, index);
        setBookprice(obj, index);*/
        setUpdatedby(obj, index);
        setUpdatedtime(obj, index);
        return obj;
    }

	public void setActive(Bookitem obj, int index) {
        Boolean active = Boolean.TRUE;
        obj.setActive(active);
    }

	public void setBookchinesename(Bookitem obj, int index) {
        String bookchinesename = "bookchinesename_" + index;
        if (bookchinesename.length() > 100) {
            bookchinesename = bookchinesename.substring(0, 100);
        }
        obj.setBookchinesename(bookchinesename);
    }

	public void setBookcode(Bookitem obj, int index) {
        String bookcode = "bookcode_" + index;
        if (bookcode.length() > 100) {
            bookcode = bookcode.substring(0, 100);
        }
        obj.setBookcode(bookcode);
    }

	public void setBookdescription(Bookitem obj, int index) {
        String bookdescription = "bookdescription_" + index;
        if (bookdescription.length() > 100) {
            bookdescription = bookdescription.substring(0, 100);
        }
        obj.setBookdescription(bookdescription);
    }

	public void setBookname(Bookitem obj, int index) {
        String bookname = "bookname_" + index;
        if (bookname.length() > 100) {
            bookname = bookname.substring(0, 100);
        }
        obj.setBookname(bookname);
    }

	public void setAmountinstock(Bookitem obj, int index) {
        Integer amountinstock = new Integer(index);
        obj.setAmountinstock(amountinstock);
    }

	public void setAmountsold(Bookitem obj, int index) {
        Integer amountsold = new Integer(index);
        obj.setAmountsold(amountsold);
    }

	public void setBookdiscountprice(Bookitem obj, int index) {
        BigDecimal bookdiscountprice = BigDecimal.valueOf(index);
        if (bookdiscountprice.compareTo(new BigDecimal("0"))==1) {
        	bookdiscountprice = new BigDecimal("0");
        }
        obj.setBookdiscountprice(bookdiscountprice);
    }

	public void setBookprice(Bookitem obj, int index) {
        BigDecimal bookprice = BigDecimal.valueOf(index);
        if (bookprice.compareTo(new BigDecimal("0"))==1) {
            bookprice = new BigDecimal("0");
        }
        obj.setBookprice(bookprice);
    }
	
	public void setUpdatedby(Bookitem obj, int index) {
        String updatedby = "updatedby_" + index;
        if (updatedby.length() > 40) {
            updatedby = updatedby.substring(0, 40);
        }
        obj.setUpdatedby(updatedby);
    }

	public void setUpdatedtime(Bookitem obj, int index) {
        Calendar updatedtime = Calendar.getInstance();
        obj.setUpdatedtime(updatedtime);
    }

	public Bookitem getSpecificBookitem(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Bookitem obj = data.get(index);
        Integer id = obj.getId();
        return bookitemRepository.findOne(id);
    }

	public Bookitem getRandomBookitem() {
        init();
        Bookitem obj = data.get(rnd.nextInt(data.size()));
        Integer id = obj.getId();
        return bookitemRepository.findOne(id);
    }

	public boolean modifyBookitem(Bookitem obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = bookitemRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Bookitem' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Bookitem>();
        for (int i = 0; i < 10; i++) {
            Bookitem obj = getNewTransientBookitem(i);
            try {
                bookitemRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            bookitemRepository.flush();
            data.add(obj);
        }
    }
}
