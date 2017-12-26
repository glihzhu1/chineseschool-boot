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

@Component
@Configurable
public class SemestercourseDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Semestercourse> data;

	@Autowired
    BookitemDataOnDemand bookitemDataOnDemand;

	@Autowired
    CourseinformationDataOnDemand courseinformationDataOnDemand;

	@Autowired
    SemesterDataOnDemand semesterDataOnDemand;

	@Autowired
    PersonnelDataOnDemand personnelDataOnDemand;

	@Autowired
    SemestercourseRepository semestercourseRepository;

	public Semestercourse getNewTransientSemestercourse(int index) {
        Semestercourse obj = new Semestercourse();
        setCurrentcapacity(obj, index);
        setDiscountamount(obj, index);
        setDiscountstatus(obj, index);
        setEndtime(obj, index);
        setMaxcapacity(obj, index);
        setMincapacity(obj, index);
        setNote(obj, index);
        setPrice(obj, index);
        setRoom(obj, index);
        setSemestercoursecode(obj, index);
        setStarttime(obj, index);
        setStatus(obj, index);
        setUpdatedby(obj, index);
        setUpdatedtime(obj, index);
        setWaitingcapacity(obj, index);
        return obj;
    }

	public void setCurrentcapacity(Semestercourse obj, int index) {
        Integer currentcapacity = new Integer(index);
        obj.setCurrentcapacity(currentcapacity);
    }

	public void setDiscountamount(Semestercourse obj, int index) {
        BigDecimal discountamount = BigDecimal.valueOf(index);
        if (discountamount.compareTo(new BigDecimal("0")) == 1) {
            discountamount = new BigDecimal("0");
        }
        obj.setDiscountamount(discountamount);
    }

	public void setDiscountstatus(Semestercourse obj, int index) {
        Boolean discountstatus = Boolean.TRUE;
        obj.setDiscountstatus(discountstatus);
    }

	public void setStarttime(Semestercourse obj, int index) {
		String starttime = "starttime_" + index;
        if (starttime.length() > 20) {
        	starttime = starttime.substring(0, 20);
        }
        obj.setStarttime(starttime);
    }
	
	public void setEndtime(Semestercourse obj, int index) {
		String endtime = "endtime_" + index;
        if (endtime.length() > 20) {
        	endtime = endtime.substring(0, 20);
        }
        obj.setEndtime(endtime);
    }

	public void setMaxcapacity(Semestercourse obj, int index) {
        Integer maxcapacity = new Integer(index);
        obj.setMaxcapacity(maxcapacity);
    }

	public void setMincapacity(Semestercourse obj, int index) {
        Integer mincapacity = new Integer(index);
        obj.setMincapacity(mincapacity);
    }

	public void setNote(Semestercourse obj, int index) {
        String note = "note_" + index;
        if (note.length() > 200) {
            note = note.substring(0, 200);
        }
        obj.setNote(note);
    }

	public void setPrice(Semestercourse obj, int index) {
        BigDecimal price = BigDecimal.valueOf(index);
        if (price.compareTo(new BigDecimal("0")) == 1) {
            price = new BigDecimal("0");
        }
        obj.setPrice(price);
    }

	public void setRoom(Semestercourse obj, int index) {
        String room = "room_" + index;
        if (room.length() > 50) {
            room = room.substring(0, 50);
        }
        obj.setRoom(room);
    }

	public void setSemestercoursecode(Semestercourse obj, int index) {
        String semestercoursecode = "semestercoursecode_" + index;
        if (semestercoursecode.length() > 50) {
            semestercoursecode = semestercoursecode.substring(0, 50);
        }
        obj.setSemestercoursecode(semestercoursecode);
    }

	public void setStatus(Semestercourse obj, int index) {
        String status = String.valueOf(index);
        if (status.length() > 1) {
            status = status.substring(0, 10);
        }
        obj.setStatus(status);
    }

	public void setUpdatedby(Semestercourse obj, int index) {
        String updatedby = "updatedby_" + index;
        if (updatedby.length() > 40) {
            updatedby = updatedby.substring(0, 40);
        }
        obj.setUpdatedby(updatedby);
    }

	public void setUpdatedtime(Semestercourse obj, int index) {
        Calendar updatedtime = Calendar.getInstance();
        obj.setUpdatedtime(updatedtime);
    }

	public void setWaitingcapacity(Semestercourse obj, int index) {
        Integer waitingcapacity = new Integer(index);
        obj.setWaitingcapacity(waitingcapacity);
    }

	public Semestercourse getSpecificSemestercourse(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Semestercourse obj = data.get(index);
        Integer id = obj.getId();
        return semestercourseRepository.findOne(id);
    }

	public Semestercourse getRandomSemestercourse() {
        init();
        Semestercourse obj = data.get(rnd.nextInt(data.size()));
        Integer id = obj.getId();
        return semestercourseRepository.findOne(id);
    }

	public boolean modifySemestercourse(Semestercourse obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = semestercourseRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Semestercourse' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Semestercourse>();
        for (int i = 0; i < 10; i++) {
            Semestercourse obj = getNewTransientSemestercourse(i);
            try {
                semestercourseRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            semestercourseRepository.flush();
            data.add(obj);
        }
    }
}
