package com.xilin.management.school.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

public class CustomUser implements Serializable {

    private Long id;

    private Integer version;

    private String loginId;

    private String passwordHash;

    private String email;

    private String appRole;

    private Boolean userActive;

    private Calendar lastUpdateDate;

    
	public CustomUser() {
		super();
	}

	public CustomUser(CustomUser user) {
		this.id = user.id;
		this.loginId = user.loginId;
		this.passwordHash = user.passwordHash;
		this.email = user.email;
		this.appRole = user.appRole;
		this.userActive = user.userActive;
	}
    
    

	/**
     * TODO Auto-generated attribute documentation
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
     * This `equals` implementation is specific for JPA entities and uses 
     * the entity identifier for it, following the article in 
     * https://vladmihalcea.com/2016/06/06/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
     * 
     * @param obj
     * @return Boolean
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        // instanceof is false if the instance is null
        if (!(obj instanceof CustomUser)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), ((CustomUser) obj).getId());
    }

	/**
     * This `hashCode` implementation is specific for JPA entities and uses a fixed `int` value to be able 
     * to identify the entity in collections after a new id is assigned to the entity, following the article in 
     * https://vladmihalcea.com/2016/06/06/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
     * 
     * @return Integer
     */
    public int hashCode() {
        return 31;
    }

	/**
     * TODO Auto-generated method documentation
     * 
     * @return String
     */
    public String toString() {
        return "Alluser {" + 
                "id='" + id + '\'' + 
                ", version='" + version + '\'' + 
                ", loginId='" + loginId + '\'' + 
                ", passwordHash='" + passwordHash + '\'' + 
                ", email='" + email + '\'' + 
                ", appRole='" + appRole + '\'' + 
                ", userActive='" + userActive + '\'' + 
                ", lastUpdateDate='" + lastUpdateDate + '\'' + 
                ", serialVersionUID='" + serialVersionUID + '\'' + 
                ", ITERABLE_TO_ADD_CANT_BE_NULL_MESSAGE='" + ITERABLE_TO_ADD_CANT_BE_NULL_MESSAGE + '\'' + 
                ", ITERABLE_TO_REMOVE_CANT_BE_NULL_MESSAGE='" + ITERABLE_TO_REMOVE_CANT_BE_NULL_MESSAGE + '\'' + "}" + super.toString();
    }

	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAppRole() {
		return appRole;
	}

	public void setAppRole(String appRole) {
		this.appRole = appRole;
	}

	public Boolean getUserActive() {
		return userActive;
	}

	public void setUserActive(Boolean userActive) {
		this.userActive = userActive;
	}

	public Calendar getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Calendar lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}



	public static final String ITERABLE_TO_ADD_CANT_BE_NULL_MESSAGE = "The given Iterable of items to add can't be null!";

	/**
     * TODO Auto-generated attribute documentation
     * 
     */
    public static final String ITERABLE_TO_REMOVE_CANT_BE_NULL_MESSAGE = "The given Iterable of items to add can't be null!";
}
