package com.xilin.management.school.web;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.FacesContextUtils;

import com.xilin.management.school.model.Bookitem;
import com.xilin.management.school.model.BookitemRepository;
import com.xilin.management.school.model.Courseinformation;
import com.xilin.management.school.model.CourseinformationRepository;
import com.xilin.management.school.model.Family;
import com.xilin.management.school.model.MyCustomSchoolService;
import com.xilin.management.school.model.Personnel;
import com.xilin.management.school.model.PersonnelRepository;
import com.xilin.management.school.model.Semester;
import com.xilin.management.school.model.SemesterRepository;
import com.xilin.management.school.model.Semestercourse;
import com.xilin.management.school.model.SemestercourseRepository;
import com.xilin.management.school.model.Student;
import com.xilin.management.school.model.StudentRepository;
import com.xilin.management.school.web.util.MessageFactory;
import com.xilin.management.school.web.util.Utils;

@Component("semestercourseBean")
@Scope("session")
public class SemestercourseBean implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(SemestercourseBean.class);
	
	@Autowired
    public JavaMailSender emailSender;
	
	@Autowired
    SemestercourseRepository semestercourseRepository;

	@Autowired
	private SemesterRepository semesterRepository;
	
	@Autowired
	private BookitemRepository bookitemRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private PersonnelRepository personnelRepository;
	
	@Autowired
	private CourseinformationRepository courseinformationRepository;
	
	@Autowired
    MyCustomSchoolService myCustomSchoolService;
	
	private String name = "Semestercourses";

	private Semestercourse semestercourse;

	private List<Semestercourse> allSemestercourses;


	private boolean createDialogVisible = false;

	private List<Semestercourse> filteredSemestercourses;
	
	private List<SelectItem> semesterDropdown;
	private List<SelectItem> courseinfoDropdown;
	private List<SelectItem> teacherDropdown;
	private List<SelectItem> bookitemDropdown;
	private Integer semesterId;
	private Integer courseinfoId;
	private Integer teacherId;
	private Integer bookitemId;
	private Courseinformation courseinfo;
	private Personnel teacher;
	private Bookitem bookitem;
	private Semester latestSemester;
	private String semesterCourseAction;
	private List<Student> selectedStudents;
	private Student student;
	
	private boolean emailStudentDialogVisible = false;
	private boolean emailStudentDialogOneVisible = false;
	//String toCandidateEmail;
	
	//@Value("${candidate.email.subject}")
	String toStudentEmailSubject;
	
	//@Value("${email.from.name}")
	//private String _fromName;
	
	//@Value("${email.relay.address}")
	String relayEmails;
	
	String toStudentEmailMsg;
	
	@PostConstruct
    public void init() {
		FacesContextUtils.getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
		.getAutowireCapableBeanFactory().autowireBean(this);
		
		latestSemester = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
		
		// Below is for teacher to get his or her classes
		if(Utils.hasRole(Utils.ROLE_XILINTEACHER)) {
        	String loginUser = Utils.retrieveLoginUsername();
     		List<Personnel> loginTeacher = personnelRepository.findByLoginIdIgnoreCase(loginUser);
     		
     		if(loginTeacher != null && loginTeacher.size() > 0) {
     			teacher = loginTeacher.get(0);
     			allSemestercourses = semestercourseRepository.findBySemesteridAndTeacherid(latestSemester, teacher);
     		}
        }
    }

	public String getName() {
        return name;
    }

	public void onChangeCourseinfoId(ValueChangeEvent e) {
		courseinfoId = (Integer)e.getNewValue();
		
		if(courseinfoId != null) {
			courseinfo = courseinformationRepository.findOne(courseinfoId);
		}
	}

	public void onChangeTeacherId(ValueChangeEvent e) {
		teacherId = (Integer)e.getNewValue();
		
		if(teacherId != null) {
			teacher = personnelRepository.findOne(teacherId);
		}
	}
	
	public void onChangeBookitemId(ValueChangeEvent e) {
		bookitemId = (Integer)e.getNewValue();
		
		if(bookitemId != null) {
			bookitem = bookitemRepository.findOne(bookitemId);
		}
	}
	
	public void onChangeSemesterSelection() {
		latestSemester = semesterRepository.findOne(semesterId);
		
		allSemestercourses = semestercourseRepository.findBySemesteridOrderBySemestercoursecodeAsc(latestSemester);
        
		//semestercourse = semestercourseRepository.findOne(semesterId)
	}
	
	public void onChangeSemesterCourseAction() {
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String semestercourserowid = params.get("semestercourserowid");
		
		semestercourse = semestercourseRepository.findOne(Integer.valueOf(semestercourserowid));
	}
	
	public List<Semestercourse> getAllSemestercourses() {
        return allSemestercourses;
    }

	public void setAllSemestercourses(List<Semestercourse> allSemestercourses) {
        this.allSemestercourses = allSemestercourses;
    }

	public String findAllSemestercourses() {
        allSemestercourses = semestercourseRepository.findBySemesteridOrderBySemestercoursecodeAsc(latestSemester);
        return "/pages/admin/semestercourse";
    }

	public Semestercourse getSemestercourse() {
        if (semestercourse == null) {
            semestercourse = new Semestercourse();
        }
        return semestercourse;
    }

	public void setSemestercourse(Semestercourse semestercourse) {
        this.semestercourse = semestercourse;
    }

	public List<Student> getSelectedStudents() {
		return selectedStudents;
	}

	public void setSelectedStudents(List<Student> selectedStudents) {
		this.selectedStudents = selectedStudents;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public boolean isEmailStudentDialogVisible() {
		return emailStudentDialogVisible;
	}

	public void setEmailStudentDialogVisible(boolean emailStudentDialogVisible) {
		this.emailStudentDialogVisible = emailStudentDialogVisible;
	}

	public boolean isEmailStudentDialogOneVisible() {
		return emailStudentDialogOneVisible;
	}

	public void setEmailStudentDialogOneVisible(boolean emailStudentDialogOneVisible) {
		this.emailStudentDialogOneVisible = emailStudentDialogOneVisible;
	}

	public String getToStudentEmailSubject() {
		return toStudentEmailSubject;
	}

	public void setToStudentEmailSubject(String toStudentEmailSubject) {
		this.toStudentEmailSubject = toStudentEmailSubject;
	}

	public String getToStudentEmailMsg() {
		return toStudentEmailMsg;
	}

	public void setToStudentEmailMsg(String toStudentEmailMsg) {
		this.toStudentEmailMsg = toStudentEmailMsg;
	}

	public String onEdit() {
		prepareClassNeededDropdowns();
		
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String semestercourserowid = params.get("semestercourserowid");
			
		semestercourse = semestercourseRepository.findOne(Integer.valueOf(semestercourserowid));
			
		if(semestercourse.getCourseinfoid() != null) {
			courseinfo = semestercourse.getCourseinfoid();
			courseinfoId = courseinfo.getId();
		}
		
		if(semestercourse.getTeacherid() != null) {
			teacher = semestercourse.getTeacherid();
			teacherId = teacher.getId();
		}
		
		if(semestercourse.getBookitemid() != null) {
			bookitem = semestercourse.getBookitemid();
			bookitemId = semestercourse.getBookitemid().getId();
		}
		
        return null;
    }

	public String onListStudent() {
		if(semestercourse != null) {
			selectedStudents = studentRepository.queryAllStudentsForClass(semestercourse);
		}
		
        return null;
    }
	
	public boolean isCreateDialogVisible() {
        return createDialogVisible;
    }

	public void setCreateDialogVisible(boolean createDialogVisible) {
        this.createDialogVisible = createDialogVisible;
    }

	public String displayList() {
        createDialogVisible = false;
        latestSemester = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
        findAllSemestercourses();
        
        semesterDropdown = myCustomSchoolService.queryTop2SemesterItems();
        
        return "/pages/admin/semestercourse";
    }

	public void displayEmailStudentDialogOne(ActionEvent evt) {
		//emailStudentDialogVisible = false;
		emailStudentDialogOneVisible = true;
		createDialogVisible = false;
		
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String studentrowid = params.get("studentrowid");
		student = studentRepository.findOne(Integer.valueOf(studentrowid));
    }
	
	public String sendSelectedStudentEmail() {
		//This email can  be from either manufacture or admin
		//emailCandidateDialogVisible = false;
		emailStudentDialogOneVisible = false;
		createDialogVisible = false;
		
		String toEmails = student.getFamilyid().getEmail();
		
		FacesMessage facesMessage = null;
		String _fromEmail = "";
		try {
			SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setTo(toEmails); 
	        message.setSubject(this.toStudentEmailSubject); 
	        message.setText(this.toStudentEmailMsg);
	        emailSender.send(message);
		} catch (Exception e) {
			facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    				"Failed to sendSelectedStudentEmail to: " + toEmails, "");
			
			logger.error("Failed to sendSelectedStudentEmail to: " + toEmails, e);
		}
		logger.info("Student email sent to: \n" + toEmails);
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('emailStudentDialogOneWidget').hide()");
        
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, 
				"Successfully sendSelectedStudentEmail to: " + toEmails, "");
        
        if(facesMessage != null)
        	FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        this.toStudentEmailSubject = "";
        this.toStudentEmailMsg = "";

        return null;
	}
	
	public String displayCreateDialog() {
        semestercourse = new Semestercourse();
        semestercourse.setStatus(Utils.CLASS_STATUS_OPEN);
        semestercourse.setMaxcapacity(30);
        semestercourse.setCurrentcapacity(0);
        semestercourse.setWaitingcapacity(0);
        semestercourse.setDiscountstatus(false);
        semestercourse.setDiscountamount(new BigDecimal(0));
        semestercourse.setSemesterid(latestSemester);
        
        prepareClassNeededDropdowns();
		
		latestSemester = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
		
        createDialogVisible = true;
        return "/pages/admin/semestercourse";
    }

	public String persist() {
        String message = "";
        String loginUsername = Utils.retrieveLoginUsername();
        //Calendar calendar = GregorianCalendar.getInstance();
        //calendar.setTime(startDate);
        
        if (semestercourse.getId() != null) {
        	//Check whether the semestercourse possibly already existing??
			/*List<Semestercourse> result = semestercourseRepository.findByEmailIgnoreCase(semestercourse.getEmail());
			if (result.size() > 1) {
				FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"More than one semestercourse with the provided email has existed!", "");
				FacesContext.getCurrentInstance().addMessage("editForm:semestercourseemailedit", facesMessage);
    			return null;
			}*/
        	handleClassRelatedData();
			semestercourse.setUpdatedby(loginUsername);
    		semestercourse.setUpdatedtime(GregorianCalendar.getInstance());
    		semestercourseRepository.save(semestercourse);
    		
            message = "message_successfully_updated";
        } else {
        	handleClassRelatedData();
        	semestercourse.setUpdatedby(Utils.retrieveLoginUsername());
        	semestercourse.setUpdatedtime(GregorianCalendar.getInstance());
        	semestercourse = semestercourseRepository.save(semestercourse);
	        
            message = "message_successfully_created";
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('createDialogWidget').hide()");
        context.execute("PF('editDialogWidget').hide()");
        
        FacesMessage facesMessage = MessageFactory.getMessage(message, "Semestercourse");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return findAllSemestercourses();
    }

	public String deactivateSemestercourse() {
		semestercourse.setStatus(Utils.CLASS_STATUS_CLOSED);
		semestercourse.setUpdatedby(Utils.retrieveLoginUsername());
		semestercourse.setUpdatedtime(GregorianCalendar.getInstance());
		semestercourseRepository.save(semestercourse);
        FacesMessage facesMessage = MessageFactory.getMessage("message_successfully_updated", "Semestercourse");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return findAllSemestercourses();
    }
	
	public String delete() {
		semestercourseRepository.delete(semestercourse);
        FacesMessage facesMessage = MessageFactory.getMessage("message_successfully_deleted", "Semestercourse");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return findAllSemestercourses();
    }

	public void reset() {
        semestercourse = null;
        createDialogVisible = false;
    }

	public void handleDialogClose(CloseEvent event) {
        reset();
    }

	private void prepareClassNeededDropdowns() {
		semesterDropdown = myCustomSchoolService.queryTop2SemesterItems();
		courseinfoDropdown = myCustomSchoolService.queryAllCourseinformationItems();
		teacherDropdown = myCustomSchoolService.queryTeacherItems();
		bookitemDropdown = myCustomSchoolService.queryAllBookitemItems();
	}
	
	private void handleClassRelatedData() {
		if(courseinfoId != null) {
    		Courseinformation classCourseinformation = courseinformationRepository.findOne(courseinfoId);
        	if(classCourseinformation != null) {
        		semestercourse.setCourseinfoid(classCourseinformation);
        	}
    	}
    	if(teacherId != null) {
        	Personnel classTeacher = personnelRepository.findOne(teacherId);
        	if(classTeacher != null) {
        		semestercourse.setTeacherid(classTeacher);
        	}
    	}
    	else {
    		semestercourse.setTeacherid(null);
    	}
    	
    	if(bookitemId != null) {
    		Bookitem classBookitem = bookitemRepository.findOne(bookitemId);
        	if(classBookitem != null) {
        		semestercourse.setBookitemid(classBookitem);
        	}
    	}
    	else {
    		semestercourse.setBookitemid(null);
    	}
	}
	public List<Semestercourse> getFilteredSemestercourses() {
		return filteredSemestercourses;
	}

	public void setFilteredSemestercourses(List<Semestercourse> filteredSemestercourses) {
		this.filteredSemestercourses = filteredSemestercourses;
	}

	public List<SelectItem> getTeacherDropdown() {
		return teacherDropdown;
	}

	public void setTeacherDropdown(List<SelectItem> teacherDropdown) {
		this.teacherDropdown = teacherDropdown;
	}


	public SemestercourseRepository getSemestercourseRepository() {
		return semestercourseRepository;
	}

	public void setSemestercourseRepository(
			SemestercourseRepository semestercourseRepository) {
		this.semestercourseRepository = semestercourseRepository;
	}

	public SemesterRepository getSemesterRepository() {
		return semesterRepository;
	}

	public void setSemesterRepository(SemesterRepository semesterRepository) {
		this.semesterRepository = semesterRepository;
	}

	public BookitemRepository getBookitemRepository() {
		return bookitemRepository;
	}

	public void setBookitemRepository(BookitemRepository bookitemRepository) {
		this.bookitemRepository = bookitemRepository;
	}

	public PersonnelRepository getPersonnelRepository() {
		return personnelRepository;
	}

	public void setPersonnelRepository(PersonnelRepository personnelRepository) {
		this.personnelRepository = personnelRepository;
	}

	public CourseinformationRepository getCourseinformationRepository() {
		return courseinformationRepository;
	}

	public void setCourseinformationRepository(
			CourseinformationRepository courseinformationRepository) {
		this.courseinformationRepository = courseinformationRepository;
	}

	public Integer getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}


	public List<SelectItem> getSemesterDropdown() {
		return semesterDropdown;
	}

	public void setSemesterDropdown(List<SelectItem> semesterDropdown) {
		this.semesterDropdown = semesterDropdown;
	}

	public List<SelectItem> getCourseinfoDropdown() {
		return courseinfoDropdown;
	}

	public void setCourseinfoDropdown(List<SelectItem> courseinfoDropdown) {
		this.courseinfoDropdown = courseinfoDropdown;
	}

	public List<SelectItem> getBookitemDropdown() {
		return bookitemDropdown;
	}

	public void setBookitemDropdown(List<SelectItem> bookitemDropdown) {
		this.bookitemDropdown = bookitemDropdown;
	}

	public Integer getSemesterId() {
		return semesterId;
	}

	public void setSemesterId(Integer semesterId) {
		this.semesterId = semesterId;
	}

	public Integer getCourseinfoId() {
		return courseinfoId;
	}

	public void setCourseinfoId(Integer courseinfoId) {
		this.courseinfoId = courseinfoId;
	}

	public Integer getBookitemId() {
		return bookitemId;
	}

	public void setBookitemId(Integer bookitemId) {
		this.bookitemId = bookitemId;
	}


	public Courseinformation getCourseinfo() {
		return courseinfo;
	}

	public void setCourseinfo(Courseinformation courseinfo) {
		this.courseinfo = courseinfo;
	}


	public Personnel getTeacher() {
		return teacher;
	}

	public void setTeacher(Personnel teacher) {
		this.teacher = teacher;
	}

	public Bookitem getBookitem() {
		return bookitem;
	}

	public void setBookitem(Bookitem bookitem) {
		this.bookitem = bookitem;
	}


	public Semester getLatestSemester() {
		return latestSemester;
	}

	public void setLatestSemester(Semester latestSemester) {
		this.latestSemester = latestSemester;
	}


	public String getSemesterCourseAction() {
		return semesterCourseAction;
	}

	public void setSemesterCourseAction(String semesterCourseAction) {
		this.semesterCourseAction = semesterCourseAction;
	}


	private static final long serialVersionUID = 1L;
}

