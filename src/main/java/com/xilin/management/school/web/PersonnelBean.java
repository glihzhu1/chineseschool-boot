package com.xilin.management.school.web;
import com.xilin.management.school.model.Semestercourse;
import com.xilin.management.school.model.Family;
import com.xilin.management.school.model.Personnel;
import com.xilin.management.school.model.PersonnelRepository;
import com.xilin.management.school.web.util.MessageFactory;
import com.xilin.management.school.web.util.TransientUser;
import com.xilin.management.school.web.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.validator.LengthValidator;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.message.Message;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.FacesContextUtils;

@Component("personnelBean")
@Scope("session")
public class PersonnelBean implements Serializable {

	@Autowired
    PersonnelRepository personnelRepository;

	private String name = "Personnels";

	private Personnel personnel;

	private List<Personnel> allPersonnels;

	/*private boolean dataVisible = false;

	private List<String> columns;

	private HtmlPanelGrid createPanelGrid;

	private HtmlPanelGrid editPanelGrid;

	private HtmlPanelGrid viewPanelGrid;*/
	
	@Value("${user.api.server.rest.uri}")
	public String uri;
	
	@Value("${user.api.server.rest.username}")
	public String apiusername;
	
	@Value("${user.api.server.rest.password}")
	public String apipassword;

	private boolean createDialogVisible = false;

	private List<Semestercourse> selectedSemestercourse;

	private String personnelLogin;
	private String personnelPassword;
	private String personnelConfirmPassword;
	private List<Personnel> filteredPersonnels;
	
	private DashboardModel teacherBoardModel;
	
	@PostConstruct
    public void init() {
		FacesContextUtils.getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
		.getAutowireCapableBeanFactory().autowireBean(this);
		
		teacherBoardModel = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        //DashboardColumn column3 = new DefaultDashboardColumn();
         
        column1.addWidget("actions");
        column2.addWidget("accountSetting");
 
        teacherBoardModel.addColumn(column1);
        teacherBoardModel.addColumn(column2);
        
        if(apiusername == null || apiusername.isEmpty()) {
			apiusername =  System.getenv(Utils.API_USERNAME_KEY);
		}
        
        if(apipassword == null || apipassword.isEmpty()) {
        	apipassword =  System.getenv(Utils.API_PASSWORD_KEY);
		}
    }

	public String getName() {
        return name;
    }


	public List<Personnel> getAllPersonnels() {
        return allPersonnels;
    }

	public void setAllPersonnels(List<Personnel> allPersonnels) {
        this.allPersonnels = allPersonnels;
    }

	public String findAllPersonnels() {
        //allPersonnels = personnelRepository.findAll();
        allPersonnels = personnelRepository.findAllByOrderByLastnameAsc();
        return "/pages/admin/personnel";
    }

	public Personnel getPersonnel() {
        if (personnel == null) {
            personnel = new Personnel();
        }
        return personnel;
    }

	public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

	public List<Semestercourse> getSelectedSemestercourse() {
        return selectedSemestercourse;
    }

	public void setSelectedSemestercourse(List<Semestercourse> selectedSemestercourse) {
        if (selectedSemestercourse != null) {
            personnel.setSemestercourse(new HashSet<Semestercourse>(selectedSemestercourse));
        }
        this.selectedSemestercourse = selectedSemestercourse;
    }

	public String onEdit() {
        if (personnel != null && personnel.getSemestercourse() != null) {
            selectedSemestercourse = new ArrayList<Semestercourse>(personnel.getSemestercourse());
        }
        return null;
    }

	public boolean isCreateDialogVisible() {
        return createDialogVisible;
    }

	public void setCreateDialogVisible(boolean createDialogVisible) {
        this.createDialogVisible = createDialogVisible;
    }

	public DashboardModel getTeacherBoardModel() {
		return teacherBoardModel;
	}

	public void setTeacherBoardModel(DashboardModel teacherBoardModel) {
		this.teacherBoardModel = teacherBoardModel;
	}

	public String queryPersonnelInfo() {
		String loginUser = Utils.retrieveLoginUsername();
		List<Personnel> loginPersonnels = personnelRepository.findByLoginIdIgnoreCase(loginUser);
		
		if(loginPersonnels != null && loginPersonnels.size() > 0) {
			personnel = loginPersonnels.get(0);
		}
		else {
			personnel = null;
		}
		
        return null;
    }
	
	public String displayList() {
        createDialogVisible = false;
        findAllPersonnels();
        return "/pages/admin/personnel";
    }

	public String displayCreateDialog() {
        personnel = new Personnel();
        personnel.setType(Utils.PERSONNEL_TEACHER);  // Teacher as the default
        personnel.setJobtitle("");
        personnel.setMiddlename("");
        personnel.setAddress2("");
		personnel.setState("IL");
        createDialogVisible = true;
        return "/pages/admin/personnel";
    }

	public String persistUserChangePassword() {
        String message = "";
        String pwd = personnelPassword;
        
        //Check whether the external user id exist thru REST
  		if(!Utils.checkUserExternalIdExistJson(personnel.getExternaluserid(), uri, apiusername, apipassword)){
  			//create the user
  			TransientUser auser = Utils.createUserJson(uri, apiusername, apipassword, personnel.getLoginId(), pwd, 
  					personnel.getEmail(), Utils.retrieveRoleBasedOnType(personnel.getType()));
  			if(auser != null) {
  				personnel.setExternaluserid(auser.getId());
  				personnel.setUpdatedBy(Utils.retrieveLoginUsername());
  				personnel.setUpdatedtime(GregorianCalendar.getInstance());
  				personnelRepository.save(personnel);
  			}
  		}
  		
        // REST update the user site
        if(Utils.updateUserPwdJson(personnel.getExternaluserid(), uri, apiusername, apipassword, pwd)){
        	message = "message_successfully_updated";
		}
        else {
        	message = "message_insuccessfully_updated";
        }
       
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('changePasswordDialogWidget').hide()");
        
        FacesMessage facesMessage = MessageFactory.getMessage(message, "Personnel User");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return null;
    }
	
	public String persist() {
        String message = "";
        String loginUsername = Utils.retrieveLoginUsername();
        
        if (personnel.getId() != null) {
        	if(personnel.getCellphone().isEmpty() 
    			&& personnel.getHomephone().isEmpty()) {
    			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					" At least one contct phone number is needed!", "");
    			FacesContext.getCurrentInstance().addMessage("editForm:homephone", facesMessage);
    			return null;
    	    }
    		
    		//Check whether the personnel possibly already existing??
			List<Personnel> result = personnelRepository.findByEmailIgnoreCase(personnel.getEmail());
			if (result.size() > 1) {
				FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"More than one personnel with the provided email has existed!", "");
				FacesContext.getCurrentInstance().addMessage("editForm:personnelemailedit", facesMessage);
    			return null;
			}
			
			//Check whether the external user id exist thru REST
			TransientUser auser = null;
			if(!Utils.checkUserExternalIdExistJson(personnel.getExternaluserid(), uri, apiusername, apipassword)){
				//create the user
				auser = Utils.createUserJson(uri, apiusername, apipassword, personnel.getLoginId(), Utils.INVALID_PWD, 
						personnel.getEmail(), Utils.retrieveRoleBasedOnType(personnel.getType()));
				if(auser != null) {
					personnel.setExternaluserid(auser.getId());
				}
			}
			
			//Also update the user information thru REST (loginId and email)
			if(Utils.updateUserJson(personnel.getExternaluserid(), uri, apiusername, apipassword,
					personnel.getLoginId().trim(), personnel.getEmail().trim())){
				personnel.setUpdatedBy(loginUsername);
    			personnel.setUpdatedtime(GregorianCalendar.getInstance());
    			personnelRepository.save(personnel);
	            
			}
			
			if(auser != null) {
				message = "message_successfully_updated_need_reset_password";
			}
			else {
				message = "message_successfully_updated";
			}
        } else {
        	if(!personnelLogin.isEmpty() && !personnelPassword.isEmpty()
        			&& !personnel.getEmail().isEmpty()) {
        		if(personnel.getCellphone().isEmpty() 
        				&& personnel.getHomephone().isEmpty()) {
        			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
        					" At least one contct phone number is needed!", "");
        			FacesContext.getCurrentInstance().addMessage("createForm:homephone", facesMessage);
        			return null;
        	    }
        		
        		try {
        			//Check whether the personnel possibly already existing??
    				List<Personnel> result = personnelRepository.findByEmailOrLoginIdIgnoreCase(personnel.getEmail(), personnelLogin);
    				if (!result.isEmpty()) {
    					FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, 
            					" Personnel with the provided email or username has existed!", "");
    					FacesContext.getCurrentInstance().addMessage("createForm:personnelemail", facesMessage);
            			return null;
    				}
    				
        			TransientUser auser = Utils.createUserJson(uri, apiusername, apipassword, personnelLogin, personnelPassword, 
        						personnel.getEmail(), Utils.retrieveRoleBasedOnType(personnel.getType()));
        			if(auser != null) {
	        			personnel.setExternaluserid(auser.getId());
	        			personnel.setLoginId(auser.getLoginId());
	        			personnel.setActive(true);
	        			personnel.setHiredate(GregorianCalendar.getInstance());
	        			
	        			//below field to use the login username
	        			personnel.setUpdatedBy(Utils.retrieveLoginUsername());
	        			personnel.setUpdatedtime(GregorianCalendar.getInstance());
	        			personnel = personnelRepository.save(personnel);
	        			
        			}
        		}
        		catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
            message = "message_successfully_created";
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('createDialogWidget').hide()");
        context.execute("PF('editDialogWidget').hide()");
        
        FacesMessage facesMessage = MessageFactory.getMessage(message, "Personnel");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        
        if(Utils.hasRole(Utils.ROLE_XILINADMIN)) {
        	return findAllPersonnels();
        }
        else
        	return null;
    }

	public String deactivatePersonnel() {
		personnel.setActive(false);
		personnel.setUpdatedBy(Utils.retrieveLoginUsername());
		personnel.setUpdatedtime(GregorianCalendar.getInstance());
		personnelRepository.save(personnel);
        FacesMessage facesMessage = MessageFactory.getMessage("message_successfully_updated", "Personnel");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return findAllPersonnels();
    }
	
	public String delete() {
		personnelRepository.delete(personnel);
        FacesMessage facesMessage = MessageFactory.getMessage("message_successfully_deleted", "Personnel");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return findAllPersonnels();
    }

	public void onChangePersonnelAction() {
		Map<String,String> params =
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String personnelrowid = params.get("personnelrowid");
		
		personnel = personnelRepository.findOne(Integer.valueOf(personnelrowid));
	}
	
	public void reset() {
        personnel = null;
        selectedSemestercourse = null;
        createDialogVisible = false;
    }

	public void handleDialogClose(CloseEvent event) {
        reset();
    }

	public String getPersonnelLogin() {
		return personnelLogin;
	}

	public void setPersonnelLogin(String personnelLogin) {
		this.personnelLogin = personnelLogin;
	}

	public String getPersonnelPassword() {
		return personnelPassword;
	}

	public void setPersonnelPassword(String personnelPassword) {
		this.personnelPassword = personnelPassword;
	}

	public String getPersonnelConfirmPassword() {
		return personnelConfirmPassword;
	}

	public void setPersonnelConfirmPassword(String personnelConfirmPassword) {
		this.personnelConfirmPassword = personnelConfirmPassword;
	}

	public List<Personnel> getFilteredPersonnels() {
		return filteredPersonnels;
	}

	public void setFilteredPersonnels(List<Personnel> filteredPersonnels) {
		this.filteredPersonnels = filteredPersonnels;
	}

	private static final long serialVersionUID = 1L;
}
