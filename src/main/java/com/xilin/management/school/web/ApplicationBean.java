package com.xilin.management.school.web;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.xilin.management.school.model.Family;
import com.xilin.management.school.model.FamilyRepository;
import com.xilin.management.school.model.MyCustomFamilySpecs;
import com.xilin.management.school.web.reports.JasperReportsPdfExporter;
import com.xilin.management.school.web.reports.JasperReportsXlsExporter;
import com.xilin.management.school.web.util.TransientUser;
import com.xilin.management.school.web.util.Utils;

//@ManagedBean
//@SessionScoped
//@Configurable
@Component("applicationBean")
@Scope("session")
public class ApplicationBean  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationBean.class);
	
	private String username;
	
    private String password;

    private String strConfirmPassword;
    
    private String userEmail;
    
    private UserDetails userDetails;
    
    @Autowired
    FamilyRepository familyRepository;
    
    private LazyDataModel<Family> familyLazyModel;
    
    private String strSearchTerm = "";
    
    private List<Family> filteredFamily;
    
    @Value("${user.api.server.rest.uri}")
	public String uri;
	
	@Value("${user.api.server.rest.username}")
	public String apiusername;
	
	@Value("${user.api.server.rest.password}")
	public String apipassword;
	
    public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStrConfirmPassword() {
		return strConfirmPassword;
	}

	public void setStrConfirmPassword(String strConfirmPassword) {
		this.strConfirmPassword = strConfirmPassword;
	}

	public String getStrSearchTerm() {
		return strSearchTerm;
	}

	public void setStrSearchTerm(String strSearchTerm) {
		this.strSearchTerm = strSearchTerm;
	}

	public List<Family> getFilteredFamily() {
		return filteredFamily;
	}

	public void setFilteredFamily(List<Family> filteredFamily) {
		this.filteredFamily = filteredFamily;
	}
	
	public String login() throws ServletException, IOException {
        //String url = "/resources/j_spring_security_check?j_username=" + username + "&j_password=" + password;
        
        String strRedirect="/pages/public/login";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(this.username, this.password);
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
	        if (auth != null) {
	        	userDetails = (UserDetails)auth.getPrincipal();
	            
	        	//Collection<?extends GrantedAuthority> granted = auth.getAuthorities();
	    		if (Utils.hasRole(Utils.ROLE_XILINADMIN))
	    		{
	    			familyLazyModel = new MyLazyFamilyDataModel(familyRepository, "", null);
	    			//familyLazyModel.load(0, 15, Utils.DEFAULT_FAMILY_SORT_FIELD, SortOrder.ASCENDING, null);
	    			
	    			strRedirect = "/pages/admin/main";
	    		}
	    		else if (Utils.hasRole(Utils.ROLE_XILINSELLER))
	    		{
	    			familyLazyModel = new MyLazyFamilyDataModel(familyRepository, "", null);
	    			//familyLazyModel.load(0, 15, Utils.DEFAULT_FAMILY_SORT_FIELD, SortOrder.ASCENDING, null);
	    			
	    			strRedirect = "/pages/admin/mainseller";
	    		}
	    		else if (Utils.hasRole(Utils.ROLE_XILINTEACHER))
	    		{
	    			strRedirect = "/pages/admin/mainteacher";
	    		}
	    		else if (Utils.hasRole(Utils.ROLE_XILINFAMILY))
	    		{
	    			strRedirect = "/pages/family/main";
	    		}
	    		
	        }
        } catch (ServletException e) {
        	e.printStackTrace();
            logger.error("user: " + username + " login failed!", e);
            context.addMessage(null, new FacesMessage("Login failed, check your username/password."));
            //return "error";
        }
        return strRedirect;
        //return null;
	}

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
        	if (auth != null){    
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        	
            //request.logout();
            context.addMessage(null, new FacesMessage("Your logout is successful!"));
            reset();
            
            return "/pages/public/login";
        } catch (Exception e) {
        	logger.error("user: " + username + " logout failed!", e);
            context.addMessage(null, new FacesMessage("Your logout failed!"));
            return "error";
        }
    }
    
    public String searchFamilies() {
		familyLazyModel = new MyLazyFamilyDataModel(familyRepository, strSearchTerm, null);
		
		return "/pages/admin/mainseller";
	}
    
    public void exportFamiliesPdf() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		//Locale bLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();
		Locale aLocale = facesContext.getViewRoot().getLocale();
		
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("dataForm:list");
		Map<String, Object> myfilterMap = dataTable.getFilters();
		
		List<Family> familyData = familyRepository.findAll(
				MyCustomFamilySpecs.loadFullSearchFamilies(strSearchTerm, null, myfilterMap));
		
		Utils.export(familyData, Utils.TABLE_FAMILY_COLUMNS, response, new JasperReportsPdfExporter(), "families_report.pdf",  aLocale);
		
		facesContext.responseComplete();
        facesContext.renderResponse();
        
	}
	
	public void exportFamiliesXls() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		//Locale bLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();
		Locale aLocale = facesContext.getViewRoot().getLocale();
		
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("dataForm:list");
		Map<String, Object> myfilterMap = dataTable.getFilters();
		
		List<Family> familyData = familyRepository.findAll(
				MyCustomFamilySpecs.loadFullSearchFamilies(strSearchTerm, null, myfilterMap));
		
		Utils.export(familyData, Utils.TABLE_FAMILY_COLUMNS, response, new JasperReportsXlsExporter(), "families_report.xls",  aLocale);
		
		facesContext.responseComplete();
        facesContext.renderResponse();
        
	}
	
    public String processUserForgotLogin() {
    	FacesContext context = FacesContext.getCurrentInstance();
		// validate email
		String toEmails = "gli1_2000@yahoo.com";
		EmailValidator validator = new EmailValidator();
		if (!userEmail.isEmpty() && validator.isValid(userEmail, null)) {
			toEmails = toEmails + ";" + userEmail;
		} else {
			context.addMessage(null, new FacesMessage("The email provided: " + userEmail + " is invalid \n"));
            return null;
		}
		
		//check user existed with the entered email
    	TransientUser users = Utils.retrieveUserEmailExistJson(userEmail, uri, apiusername, apipassword);
		if(users == null){
			context.addMessage(null, new FacesMessage("No valid user found, the provided email has to be the email associated with your family account!"));
            return null;
		}
		
		// Get all the logins associate with the email
		String allLogins = "No logins found";
		if(users != null) {
			allLogins = users.getLoginId();
		}

		//String _fromEmail = namadPropertyResolver.resolveEmailFrom();
		try {
			/*send the email to user now....*/
			
			
		} catch (Exception e) {
			logger.error("processUserForgotLogin Failed to sendEmail", e);
		}
		
		logger.info("processUserForgotLogin notification email sent to: \n" + toEmails);
		
		reset();
		
		RequestContext reqContext = RequestContext.getCurrentInstance();
        reqContext.execute("PF('forgotLoginDialogWidget').hide()");
        
		return "/pages/public/login";
	}
    
    public String processUserForgotPassword() {
    	FacesContext context = FacesContext.getCurrentInstance();
		// validate email
    	String toEmails = "gli1_2000@yahoo.com";
		EmailValidator validator = new EmailValidator();
		if (!userEmail.isEmpty() && validator.isValid(userEmail, null)) {
			toEmails = toEmails + ";" + userEmail;
		} else {
			context.addMessage(null, new FacesMessage("The email provided: " + userEmail + " is invalid \n"));
            return null;
		}
		
		//check user existed with the entered email and username
    	TransientUser users = Utils.retrieveUserLoginIdExistJson(username.trim(), uri, apiusername, apipassword);
    	//TransientUser users = Utils.retrieveUserEmailAndLoginIdExistJson(userEmail.trim(), username.trim(), uri, apiusername, apipassword);
		if(users == null){
			context.addMessage(null, new FacesMessage("No valid user found associated with the provided username!"));
            return null;
		}
		
		// save a temp password to database -- hopefully just one user
		String newPwd = resetPassword();
		if(users != null) {
			// REST update the user site
	        if(!Utils.updateUserPwdJson(users.getId(), uri, apiusername, apipassword, newPwd)){
	        	context.addMessage(null, new FacesMessage("Failed to reset the user password, please try later!"));
	            return null;
	        }
		}
		
		//String _fromEmail = namadPropertyResolver.resolveEmailFrom();
		try {
			/*String _msg = EmailUtils.initEmailTemplate(_msgFileForgotPwd);
			_msg = _msg.replace("{0}", username);
	        _msg = _msg.replace("{1}", newPwd);
			boolean _emailCustomers = "1".equals(emailRelay);
			EmailUtils.sendEmail(_emailCustomers, relayEmails, emailFrom, _fromName, toEmails, _subjectForgotPwd, _msg);*/
		} catch (Exception e) {
			logger.error("processUserForgotPassword Failed to sendEmail", e);
		}
		
		logger.info("processUserForgotPassword notification email sent to: \n" + toEmails);
		
		reset();
		
		RequestContext reqContext = RequestContext.getCurrentInstance();
        reqContext.execute("PF('forgotPasswordDialogWidget').hide()");

        context.addMessage(null, new FacesMessage("A temporary password has been sent to your provided email address!"));
        
		return "/pages/public/login";
	}
    
    /*public String changePassword() {
		FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
    	if(request.getUserPrincipal() != null) {
    		String login = request.getUserPrincipal().getName();
    		List<NamadUser> namadUsers = namadUserRepository.findAllByLoginId(login);
            
            if(namadUsers != null && namadUsers.size() >0) {
            	NamadUser user = namadUsers.get(0);
            	//reset its password here
            	user.setPasswordHash(PasswordService.encrypt(password));
            	user.setPasswordDate(new Date());
            	user.setLastUpdateDate(GregorianCalendar.getInstance());
            	
            	namadUserRepository.save(user);
            }
            
            RequestContext reqContext = RequestContext.getCurrentInstance();
            reqContext.execute("PF('editUserDialogWidget').hide()");
            
            String message = "message_successfully_updated";
            FacesMessage facesMessage = MessageFactory.getMessage(message, "User");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    	}
    	return null;
    }*/
    
    public String getColumnName(String column) {
        if (column == null || column.length() == 0) {
            return column;
        }
        final Pattern p = Pattern.compile("[A-Z][^A-Z]*");
        final Matcher m = p.matcher(Character.toUpperCase(column.charAt(0)) + column.substring(1));
        final StringBuilder builder = new StringBuilder();
        while (m.find()) {
            builder.append(m.group()).append(" ");
        }
        return builder.toString().trim();
    }

	private MenuModel menuModel;
	private MenuModel taskMenuModel;
	private MenuModel classMenuModel;
	
	@PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        
        menuModel = new DefaultMenuModel();
        DefaultSubMenu submenu;
        DefaultMenuItem item;
        
        submenu = new DefaultSubMenu("Personnel");
        submenu.setId("personnelSubmenu");
        
        item = new DefaultMenuItem("Create Family");
        item.setId("createFamilyMenuItem");
        item.setCommand("#{familyBean.displayCreateDialog}");
        item.setIcon("ui-icon ui-icon-plus");
        item.setAjax(false);
        item.setAsync(false);
        item.setUpdate(":dataForm:data");
        submenu.addElement(item);
        
        item = new DefaultMenuItem("Create Personnel");
        item.setId("createPersonnelMenuItem");
        item.setCommand("#{personnelBean.displayCreateDialog}");
        item.setIcon("ui-icon ui-icon-plus");
        item.setAjax(false);
        item.setAsync(false);
        item.setUpdate(":dataForm:data");
        submenu.addElement(item);
        
        item = new DefaultMenuItem("List Family");
        item.setId("listFamilyMenuItem");
        item.setCommand("#{familyBean.displayList}");
        item.setIcon("ui-icon ui-icon-folder-open");
        item.setAjax(false);
        item.setAsync(false);
        item.setUpdate(":dataForm:data");
        submenu.addElement(item);
        
        item = new DefaultMenuItem("List Personnel");
        item.setId("listPersonnelMenuItem");
        item.setCommand("#{personnelBean.displayList}");
        item.setIcon("ui-icon ui-icon-folder-open");
        item.setAjax(false);
        item.setAsync(false);
        item.setUpdate(":dataForm:data");
        submenu.addElement(item);
        
        menuModel.addElement(submenu);
        
        
        taskMenuModel = new DefaultMenuModel();
        DefaultSubMenu taskSubmenu;
        DefaultMenuItem taskItem;
        
        taskSubmenu = new DefaultSubMenu("Tasks");
        taskSubmenu.setId("taskSubmenu");
        
        taskItem = new DefaultMenuItem("Create Semester");
        taskItem.setId("createSemesterMenuItem");
        taskItem.setCommand("#{semesterBean.displayCreateDialog}");
        taskItem.setIcon("ui-icon ui-icon-plus");
        taskItem.setAjax(false);
        taskItem.setAsync(false);
        taskItem.setUpdate(":dataForm:data");
        taskSubmenu.addElement(taskItem);
        
        taskItem = new DefaultMenuItem("Create Book");
        taskItem.setId("createBookitemMenuItem");
        taskItem.setCommand("#{bookitemBean.displayCreateDialog}");
        taskItem.setIcon("ui-icon ui-icon-plus");
        taskItem.setAjax(false);
        taskItem.setAsync(false);
        taskItem.setUpdate(":dataForm:data");
        taskSubmenu.addElement(taskItem);
        
        taskItem = new DefaultMenuItem("List Semester");
        taskItem.setId("listSemesterMenuItem");
        taskItem.setCommand("#{semesterBean.displayList}");
        taskItem.setIcon("ui-icon ui-icon-folder-open");
        taskItem.setAjax(false);
        taskItem.setAsync(false);
        taskItem.setUpdate(":dataForm:data");
        taskSubmenu.addElement(taskItem);
        
        taskItem = new DefaultMenuItem("List Books");
        taskItem.setId("listBookitemMenuItem");
        taskItem.setCommand("#{bookitemBean.displayList}");
        taskItem.setIcon("ui-icon ui-icon-folder-open");
        taskItem.setAjax(false);
        taskItem.setAsync(false);
        taskItem.setUpdate(":dataForm:data");
        taskSubmenu.addElement(taskItem);
        
        taskMenuModel.addElement(taskSubmenu);
        
        classMenuModel = new DefaultMenuModel();
        DefaultSubMenu classSubmenu;
        DefaultMenuItem classItem;
        
        classSubmenu = new DefaultSubMenu("Classes");
        classSubmenu.setId("classSubmenu");
        
        classItem = new DefaultMenuItem("Create Class");
        classItem.setId("createClassMenuItem");
        classItem.setCommand("#{semestercourseBean.displayCreateDialog}");
        classItem.setIcon("ui-icon ui-icon-plus");
        classItem.setAjax(false);
        classItem.setAsync(false);
        classItem.setUpdate(":dataForm:data");
        classSubmenu.addElement(classItem);
        
        classItem = new DefaultMenuItem("Create Course");
        classItem.setId("createCourseMenuItem");
        classItem.setCommand("#{courseinformationBean.displayCreateDialog}");
        classItem.setIcon("ui-icon ui-icon-plus");
        classItem.setAjax(false);
        classItem.setAsync(false);
        classItem.setUpdate(":dataForm:data");
        classSubmenu.addElement(classItem);
        
        classItem = new DefaultMenuItem("List Classes");
        classItem.setId("listClassMenuItem");
        classItem.setCommand("#{semestercourseBean.displayList}");
        classItem.setIcon("ui-icon ui-icon-folder-open");
        classItem.setAjax(false);
        classItem.setAsync(false);
        classItem.setUpdate(":dataForm:data");
        classSubmenu.addElement(classItem);
        
        classItem = new DefaultMenuItem("List Courses");
        classItem.setId("listCourseMenuItem");
        classItem.setCommand("#{courseinformationBean.displayList}");
        classItem.setIcon("ui-icon ui-icon-folder-open");
        classItem.setAjax(false);
        classItem.setAsync(false);
        classItem.setUpdate(":dataForm:data");
        classSubmenu.addElement(classItem);
        
        
        classMenuModel.addElement(classSubmenu);
        
        if(apiusername == null || apiusername.isEmpty()) {
			apiusername =  System.getenv(Utils.API_USERNAME_KEY);
		}
        
        if(apipassword == null || apipassword.isEmpty()) {
        	apipassword =  System.getenv(Utils.API_PASSWORD_KEY);
		}
        
        
        
    }

	public MenuModel getMenuModel() {
        return menuModel;
    }

	public MenuModel getTaskMenuModel() {
		return taskMenuModel;
	}

	public MenuModel getClassMenuModel() {
		return classMenuModel;
	}
	
	public String getAppName() {
        return "School";
    }
	
	public FamilyRepository getFamilyRepository() {
		return familyRepository;
	}

	public void setFamilyRepository(FamilyRepository familyRepository) {
		this.familyRepository = familyRepository;
	}

	public LazyDataModel<Family> getFamilyLazyModel() {
		return familyLazyModel;
	}

	public void setFamilyLazyModel(LazyDataModel<Family> familyLazyModel) {
		this.familyLazyModel = familyLazyModel;
	}

	private String resetPassword() {
		WordGenerator wgen = new RandomWordGenerator("aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ123456789~!$");
		String newPassword = wgen.getWord(new Integer(8));
		
		return newPassword;
	}
	
	private void reset() {
		this.userEmail = "";
		this.username = "";
		this.password = "";
		this.strConfirmPassword = "";
		this.userDetails = null;
	}
	
	/*private boolean hasRole(Authentication auth, String role) {
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
				auth.getAuthorities();
		boolean hasRole = false;
		for (GrantedAuthority authority : authorities) {
			hasRole = authority.getAuthority().trim().equalsIgnoreCase(role);
			if (hasRole) {
				break;
			}	
		}
		return hasRole;
	}*/
}