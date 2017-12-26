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
public class FamilybillingDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Familybilling> data;

	@Autowired
    FamilyDataOnDemand familyDataOnDemand;

	@Autowired
    SemesterDataOnDemand semesterDataOnDemand;

	@Autowired
    FamilybillingRepository familybillingRepository;

	public Familybilling getNewTransientFamilybilling(int index) {
        Familybilling obj = new Familybilling();
        setAmount(obj, index);
        setBillingtype(obj, index);
        setChecknum(obj, index);
        setDescription(obj, index);
        setProcesstime(obj, index);
        setStatus(obj, index);
        setUpdatedby(obj, index);
        setUpdatedtime(obj, index);
        return obj;
    }

	public void setAmount(Familybilling obj, int index) {
        BigDecimal amount = BigDecimal.valueOf(index);
        if (amount.compareTo(new BigDecimal("0")) == 1) {
            amount = new BigDecimal("0");
        }
        obj.setAmount(amount);
    }

	public void setBillingtype(Familybilling obj, int index) {
        String billingtype = "billingtype_" + index;
        if (billingtype.length() > 20) {
            billingtype = billingtype.substring(0, 20);
        }
        obj.setBillingtype(billingtype);
    }

	public void setChecknum(Familybilling obj, int index) {
        String checknum = "checknum_" + index;
        if (checknum.length() > 20) {
            checknum = checknum.substring(0, 20);
        }
        obj.setChecknum(checknum);
    }

	public void setDescription(Familybilling obj, int index) {
        String description = "description_" + index;
        if (description.length() > 100) {
            description = description.substring(0, 100);
        }
        obj.setDescription(description);
    }

	public void setProcesstime(Familybilling obj, int index) {
        Calendar processtime = Calendar.getInstance();
        obj.setProcesstime(processtime);
    }

	public void setStatus(Familybilling obj, int index) {
        String status = "status_" + index;
        if (status.length() > 20) {
            status = status.substring(0, 20);
        }
        obj.setStatus(status);
    }

	public void setUpdatedby(Familybilling obj, int index) {
        String updatedby = "updatedby_" + index;
        if (updatedby.length() > 40) {
            updatedby = updatedby.substring(0, 40);
        }
        obj.setUpdatedby(updatedby);
    }

	public void setUpdatedtime(Familybilling obj, int index) {
        Calendar updatedtime = Calendar.getInstance();
        obj.setUpdatedtime(updatedtime);
    }

	public Familybilling getSpecificFamilybilling(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Familybilling obj = data.get(index);
        Integer id = obj.getId();
        return familybillingRepository.findOne(id);
    }

	public Familybilling getRandomFamilybilling() {
        init();
        Familybilling obj = data.get(rnd.nextInt(data.size()));
        Integer id = obj.getId();
        return familybillingRepository.findOne(id);
    }

	public boolean modifyFamilybilling(Familybilling obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = familybillingRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Familybilling' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Familybilling>();
        for (int i = 0; i < 10; i++) {
            Familybilling obj = getNewTransientFamilybilling(i);
            try {
                familybillingRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            familybillingRepository.flush();
            data.add(obj);
        }
    }
}
