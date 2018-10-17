package com.xilin.management.school.web;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.FacesContextUtils;

import com.xilin.management.school.model.Bookitem;
import com.xilin.management.school.model.BookitemRepository;
import com.xilin.management.school.model.Classassignment;
import com.xilin.management.school.model.ClassassignmentRepository;
import com.xilin.management.school.model.Classassignmentcategory;
import com.xilin.management.school.model.ClassassignmentcategoryRepository;
import com.xilin.management.school.model.Classassignmentstudentgrade;
import com.xilin.management.school.model.ClassassignmentstudentgradeRepository;
import com.xilin.management.school.model.Courseinformation;
import com.xilin.management.school.model.CourseinformationRepository;
import com.xilin.management.school.model.Familytransaction;
import com.xilin.management.school.model.FamilytransactionRepository;
import com.xilin.management.school.model.MyCustomSchoolService;
import com.xilin.management.school.model.Personnel;
import com.xilin.management.school.model.PersonnelRepository;
import com.xilin.management.school.model.Semester;
import com.xilin.management.school.model.SemesterRepository;
import com.xilin.management.school.model.Semestercourse;
import com.xilin.management.school.model.SemestercourseRepository;
import com.xilin.management.school.model.Student;
import com.xilin.management.school.model.StudentRepository;
import com.xilin.management.school.web.reports.JasperReportsPdfExporter;
import com.xilin.management.school.web.reports.JasperReportsXlsExporter;
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
	private FamilytransactionRepository familytransactionRepository;
	
	@Autowired
	private CourseinformationRepository courseinformationRepository;
	
	@Autowired
	private ClassassignmentcategoryRepository classassignmentcategoryRepository;
	
	@Autowired
	private ClassassignmentRepository classassignmentRepository;
	
	@Autowired
	private ClassassignmentstudentgradeRepository classassignmentstudentgradeRepository;
	
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
	private List<SelectItem> classAssignmentCategoryDropdown;
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
	private List<Classassignmentcategory> selectedClassassignmentcategory;
	private Classassignmentcategory classassignmentcategory;
	private List<Classassignment> selectedClassassignment;
	private Classassignment classassignment;
	private Integer classassignmentcategoryId;
	private List<Classassignmentstudentgrade> selectedClassassignmentstudentgrade;
	private Classassignmentstudentgrade classassignmentstudentgrade;
	
	private boolean emailStudentDialogVisible = false;
	private boolean emailStudentDialogOneVisible = false;
	
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
	
	/*public void onRowToggle(ToggleEvent event) {
		semestercourse = (Semestercourse) event.getData();
		if(semestercourse != null) {
			selectedStudents = studentRepository.queryAllStudentsForClass(semestercourse);
		}
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected Class", "Code:" + semestercourse.getSemestercoursecode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }*/
	
	public void onGradeAllStudents() {
		String loginUsername = Utils.retrieveLoginUsername();
        Calendar calendar = GregorianCalendar.getInstance();
        
		if(classassignment != null) {
			selectedStudents = studentRepository.queryAllStudentsForClass(classassignment.getSemestercourse());
			//retrieve previous grades for all students of the class
			selectedClassassignmentstudentgrade = classassignmentstudentgradeRepository.findByClassassignment(classassignment);
	    }
		
		if(selectedClassassignmentstudentgrade == null || 
				selectedClassassignmentstudentgrade.isEmpty()) {
			selectedClassassignmentstudentgrade = new ArrayList<Classassignmentstudentgrade>();
			if(selectedStudents != null && !selectedStudents.isEmpty()) {
				for(Student stu: selectedStudents) {
					Classassignmentstudentgrade grade = new Classassignmentstudentgrade();
					grade.setClassassignment(classassignment);
					grade.setEarnedgradepoint(new BigDecimal("0.0"));
					grade.setStudent(stu);
					grade.setUpdatedby(loginUsername);
					grade.setUpdatedtime(calendar);
					selectedClassassignmentstudentgrade.add(grade);
				}
			}
		}
		else {
			if(selectedStudents != null && !selectedStudents.isEmpty()) {
				List<Classassignmentstudentgrade> newGradeNeeded = 
						new ArrayList<Classassignmentstudentgrade>();
				for(Student stu: selectedStudents) {
					boolean studentHasGrade = false;
					for(Classassignmentstudentgrade agrade: selectedClassassignmentstudentgrade) {
						if(agrade.getStudent().getId().compareTo(stu.getId()) == 0) {
							studentHasGrade = true;
							break;
						}
					}
					if(!studentHasGrade) {
						Classassignmentstudentgrade grade = new Classassignmentstudentgrade();
						grade.setClassassignment(classassignment);
						grade.setEarnedgradepoint(new BigDecimal("0.0"));
						grade.setStudent(stu);
						grade.setUpdatedby(loginUsername);
						grade.setUpdatedtime(calendar);
						newGradeNeeded.add(grade);
					}
				}
				
				if(!newGradeNeeded.isEmpty()) {
					selectedClassassignmentstudentgrade.addAll(newGradeNeeded);
				}
			}
		}
	}
	
	public void gradeAllStudents() {
        classassignmentstudentgradeRepository.save(selectedClassassignmentstudentgrade);
        //semestercourseRepository.saveAndFlush(semestercourse);
        
    	RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('gradeAllStudentsDialogWidget').hide()");
    	//classassignment = null;
    	
    	String message = "message_successfully_updated";
		FacesMessage facesMessage = MessageFactory.getMessage(message, "Grade");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
	}
	
	public void onAddClassAssignment() {
		selectedClassassignmentcategory = classassignmentcategoryRepository.findBySemestercourse(semestercourse);
		
		classAssignmentCategoryDropdown = new ArrayList<SelectItem>();
		if(selectedClassassignmentcategory != null && !selectedClassassignmentcategory.isEmpty()) {
			for(Classassignmentcategory itm : selectedClassassignmentcategory) {
				SelectItem item = new SelectItem(itm.getId(), 
						itm.getDescription());
				classAssignmentCategoryDropdown.add(item);
			}
		}
		classassignment = new Classassignment();
		classassignment.setSemestercourse(semestercourse);
		classassignment.setDuedate(GregorianCalendar.getInstance());
	}
	
	public void addClassAssignment() {
    	String loginUsername = Utils.retrieveLoginUsername();
        Calendar calendar = GregorianCalendar.getInstance();
        classassignment.setUpdatedby(loginUsername);
        classassignment.setUpdatedtime(calendar);
        
        classassignmentcategory = classassignmentcategoryRepository.findOne(classassignmentcategoryId);
        
        if(classassignmentcategory != null)
        	classassignment.setClassassignmentcategory(classassignmentcategory);
        
        classassignmentRepository.save(classassignment);
		
        selectedClassassignment.add(classassignment);
        
    	RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addClassAssignmentDialogWidget').hide()");
        
    	String message = "message_successfully_updated";
		FacesMessage facesMessage = MessageFactory.getMessage(message, "Assignment");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
	}
	
	public String onListClassAssignment() {
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String semestercourserowid = params.get("semestercourserowid");
			
		semestercourse = semestercourseRepository.findOne(Integer.valueOf(semestercourserowid));
			
		selectedClassassignment = classassignmentRepository.findBySemestercourse(semestercourse);
		
        return null;
    }
	
	public String batchAssignmentCategoryUpdate() {
		String username = Utils.retrieveLoginUsername();
		Calendar cal = GregorianCalendar.getInstance();
		
		utilBatchClassassignmentcategoryUpdate(selectedClassassignmentcategory, username, cal);
		
		String message = "message_successfully_updated";
		FacesMessage facesMessage = MessageFactory.getMessage(message, "Assignment Category");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        return null;
	}
	
	public void onAddAssignmentCategory() {
		classassignmentcategory = new Classassignmentcategory();
		classassignmentcategory.setSemestercourse(semestercourse);
	}
	
	public void addClassAssignmentCategory() {
    	String loginUsername = Utils.retrieveLoginUsername();
        Calendar calendar = GregorianCalendar.getInstance();
        classassignmentcategory.setUpdatedby(loginUsername);
        classassignmentcategory.setUpdatedtime(calendar);
        classassignmentcategoryRepository.save(classassignmentcategory);
		
        selectedClassassignmentcategory.add(classassignmentcategory);
        
    	RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addAssignmentCategoryDialogWidget').hide()");
        
    	String message = "message_successfully_updated";
		FacesMessage facesMessage = MessageFactory.getMessage(message, "Assignment Category");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
	}
	
	public String onManageAssignmentCategory() {
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String semestercourserowid = params.get("semestercourserowid");
			
		semestercourse = semestercourseRepository.findOne(Integer.valueOf(semestercourserowid));
			
		//retrieve all its classassignmentcategories
		selectedClassassignmentcategory = classassignmentcategoryRepository.findBySemestercourse(semestercourse);
		
        return null;
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
		emailStudentDialogVisible = false;
		emailStudentDialogOneVisible = false;
        createDialogVisible = false;
        
        latestSemester = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
        findAllSemestercourses();
        
        semesterDropdown = myCustomSchoolService.queryTop2SemesterItems();
        
        return "/pages/admin/semestercourse";
    }

	public void exportStudentsPdf() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		//Locale bLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();
		Locale aLocale = facesContext.getViewRoot().getLocale();
		
		Utils.export(selectedStudents, Utils.TABLE_STUDENT_COLUMNS, response, new JasperReportsPdfExporter(), "students_report.pdf",  aLocale);
		
		facesContext.responseComplete();
        facesContext.renderResponse();
        
	}
	
	public void exportStudentsXls() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		//Locale bLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();
		Locale aLocale = facesContext.getViewRoot().getLocale();
		
		Utils.export(selectedStudents, Utils.TABLE_STUDENT_COLUMNS, response, new JasperReportsXlsExporter(), "students_report.xls",  aLocale);
		
		facesContext.responseComplete();
        facesContext.renderResponse();
        
	}
	
	public void displayEmailStudentDialog() {
		emailStudentDialogVisible = true;
		emailStudentDialogOneVisible = false;
		
    }
	
	public String sendAllStudentsEmail() {
		emailStudentDialogVisible = false;
		emailStudentDialogOneVisible = false;
		FacesMessage facesMessage = null;
		String toEmails = "gli1_2000@yahoo.com";
		
		EmailValidator validator = new EmailValidator();
		for (Student student : selectedStudents) {
			String _email = student.getFamilyid().getEmail();
			if (!_email.isEmpty() && validator.isValid(_email, null)) {
				if(!toEmails.contains(_email))
					toEmails = toEmails + "," + _email;
			} else {
				logger.info("the email : " + _email + " is invalid \n");
				continue;
			}
		}
		
		try {
			SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setTo(toEmails); 
	        message.setSubject(this.toStudentEmailSubject); 
	        message.setText(this.toStudentEmailMsg);
	        emailSender.send(message);
		} catch (Exception e) {
			// Send frontend a facemsg
			facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    				"Failed to sendAllStudentsEmail!", "");
			logger.error("Failed to sendAllStudentsEmail to: " + toEmails, e);
		}
		logger.info("All families email sent to: \n" + toEmails);
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('emailStudentDialogWidget').hide()");
        
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, 
				"Successfully sendAllStudentEmail to: " + toEmails, "");
        
        if(facesMessage != null)
        	FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        this.toStudentEmailSubject = "";
        this.toStudentEmailMsg = "";

        return null;
	}
	
	public void displayEmailStudentDialogOne(ActionEvent evt) {
		emailStudentDialogVisible = false;
		emailStudentDialogOneVisible = true;
		createDialogVisible = false;
		
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String studentrowid = params.get("studentrowid");
		student = studentRepository.findOne(Integer.valueOf(studentrowid));
    }
	
	public String sendSelectedStudentEmail() {
		emailStudentDialogVisible = false;
		emailStudentDialogOneVisible = false;
		createDialogVisible = false;
		String toEmails = "gli1_2000@yahoo.com";
		
		EmailValidator validator = new EmailValidator();
		String _email = student.getFamilyid().getEmail();
		if (!_email.isEmpty() && validator.isValid(_email, null)) {
			toEmails = toEmails + "," + _email;
		} else {
			logger.info("sendSelectedStudentEmail with email : " + _email + " is invalid \n");
		}
		
		
		FacesMessage facesMessage = null;
		try {
			SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setTo("gli1_2000@yahoo.com"); 
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
	        
        	//also create classassignmentcategory -- default
        	List<Classassignmentcategory> defaultcategory = new ArrayList<Classassignmentcategory>();
        	for(String[] cat: Utils.CLASS_ASSIGNMENT_CATEGORY_DEFAULT) {
        		Classassignmentcategory assignmentcat = new Classassignmentcategory();
        		assignmentcat.setDescription(cat[0]);
        		assignmentcat.setGradepercentage(Integer.valueOf(cat[1]));
        		assignmentcat.setSemestercourse(semestercourse);
        		assignmentcat.setUpdatedby(loginUsername);
        		assignmentcat.setUpdatedtime(GregorianCalendar.getInstance());
        		defaultcategory.add(assignmentcat);
        	}
        	classassignmentcategoryRepository.save(defaultcategory);
        	
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

	public String onDeleteFromStudentList() {
		//latestSemester = semesterRepository.findFirstByOrderByRegisterstartdateDesc();
		return null;
	}
	
	public void deleteFromStudentListRegisteredStudent() {
		List<Familytransaction> familytransactions = 
			familytransactionRepository.findBySemestercourseidAndStudentidAndTransactiontype(semestercourse, student, Utils.FAMILY_TRANSACTION_REGISTER);
		
		//expect exactly one in this case
		if(familytransactions != null && familytransactions.size()>0) {
			myCustomSchoolService.deleteRegisteredCls(familytransactions.get(0));
			selectedStudents.remove(student);
		}
	}
	
	public void reset() {
        //semestercourse = null;
        createDialogVisible = false;
    }

	public void handleDialogClose(CloseEvent event) {
        reset();
    }

	private void utilBatchClassassignmentcategoryUpdate(List<Classassignmentcategory> cats, String username, Calendar cal) {
		for(int index=0; index<cats.size(); index++) {
			Classassignmentcategory cat = cats.get(index);
			
			cat.setUpdatedby(username);
			cat.setUpdatedtime(cal);
		}
		
		if(!cats.isEmpty()) {
			classassignmentcategoryRepository.save(cats);
		}
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


	public List<Classassignmentcategory> getSelectedClassassignmentcategory() {
		return selectedClassassignmentcategory;
	}

	public void setSelectedClassassignmentcategory(List<Classassignmentcategory> selectedClassassignmentcategory) {
		this.selectedClassassignmentcategory = selectedClassassignmentcategory;
	}

	public Classassignmentcategory getClassassignmentcategory() {
		return classassignmentcategory;
	}

	public void setClassassignmentcategory(Classassignmentcategory classassignmentcategory) {
		this.classassignmentcategory = classassignmentcategory;
	}


	public List<Classassignment> getSelectedClassassignment() {
		return selectedClassassignment;
	}

	public void setSelectedClassassignment(List<Classassignment> selectedClassassignment) {
		this.selectedClassassignment = selectedClassassignment;
	}

	public Classassignment getClassassignment() {
		return classassignment;
	}

	public void setClassassignment(Classassignment classassignment) {
		this.classassignment = classassignment;
	}


	public Integer getClassassignmentcategoryId() {
		return classassignmentcategoryId;
	}

	public void setClassassignmentcategoryId(Integer classassignmentcategoryId) {
		this.classassignmentcategoryId = classassignmentcategoryId;
	}

	public List<SelectItem> getClassAssignmentCategoryDropdown() {
		return classAssignmentCategoryDropdown;
	}

	public void setClassAssignmentCategoryDropdown(List<SelectItem> classAssignmentCategoryDropdown) {
		this.classAssignmentCategoryDropdown = classAssignmentCategoryDropdown;
	}

	public List<Classassignmentstudentgrade> getSelectedClassassignmentstudentgrade() {
		return selectedClassassignmentstudentgrade;
	}

	public void setSelectedClassassignmentstudentgrade(
			List<Classassignmentstudentgrade> selectedClassassignmentstudentgrade) {
		this.selectedClassassignmentstudentgrade = selectedClassassignmentstudentgrade;
	}

	public Classassignmentstudentgrade getClassassignmentstudentgrade() {
		return classassignmentstudentgrade;
	}

	public void setClassassignmentstudentgrade(Classassignmentstudentgrade classassignmentstudentgrade) {
		this.classassignmentstudentgrade = classassignmentstudentgrade;
	}


	private static final long serialVersionUID = 1L;
}

