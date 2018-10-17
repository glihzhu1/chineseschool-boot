package com.xilin.management.school.model;

import java.io.Serializable;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;


/**
 * The persistent class for the classassignment database table.
 * 
 */
@Entity
@NamedQuery(name="Classassignment.findAll", query="SELECT c FROM Classassignment c")
public class Classassignment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String description;

	@Column(name = "duedate")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
	private Calendar duedate;

	private String externallink;

	private BigDecimal fullgradepoint;

	private String updatedby;

	@Column(name = "updatedtime")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar updatedtime;

	//bi-directional many-to-one association to Classassignmentcategory
	@ManyToOne
	@JoinColumn(name="classassignmentcategoryid")
	private Classassignmentcategory classassignmentcategory;

	//bi-directional many-to-one association to Semestercourse
	@ManyToOne
	@JoinColumn(name="classid")
	private Semestercourse semestercourse;

	//bi-directional many-to-one association to Classassignmentstudentgrade
	@OneToMany(mappedBy="classassignment")
	private List<Classassignmentstudentgrade> classassignmentstudentgrades;

	public Classassignment() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public Calendar getDuedate() {
		return duedate;
	}

	public void setDuedate(Calendar duedate) {
		this.duedate = duedate;
	}

	public String getExternallink() {
		return this.externallink;
	}

	public void setExternallink(String externallink) {
		this.externallink = externallink;
	}

	public BigDecimal getFullgradepoint() {
		return this.fullgradepoint;
	}

	public void setFullgradepoint(BigDecimal fullgradepoint) {
		this.fullgradepoint = fullgradepoint;
	}

	public String getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	public Calendar getUpdatedtime() {
		return updatedtime;
	}

	public void setUpdatedtime(Calendar updatedtime) {
		this.updatedtime = updatedtime;
	}

	public Classassignmentcategory getClassassignmentcategory() {
		return this.classassignmentcategory;
	}

	public void setClassassignmentcategory(Classassignmentcategory classassignmentcategory) {
		this.classassignmentcategory = classassignmentcategory;
	}

	public Semestercourse getSemestercourse() {
		return this.semestercourse;
	}

	public void setSemestercourse(Semestercourse semestercourse) {
		this.semestercourse = semestercourse;
	}

	public List<Classassignmentstudentgrade> getClassassignmentstudentgrades() {
		return this.classassignmentstudentgrades;
	}

	public void setClassassignmentstudentgrades(List<Classassignmentstudentgrade> classassignmentstudentgrades) {
		this.classassignmentstudentgrades = classassignmentstudentgrades;
	}

	public Classassignmentstudentgrade addClassassignmentstudentgrade(Classassignmentstudentgrade classassignmentstudentgrade) {
		getClassassignmentstudentgrades().add(classassignmentstudentgrade);
		classassignmentstudentgrade.setClassassignment(this);

		return classassignmentstudentgrade;
	}

	public Classassignmentstudentgrade removeClassassignmentstudentgrade(Classassignmentstudentgrade classassignmentstudentgrade) {
		getClassassignmentstudentgrades().remove(classassignmentstudentgrade);
		classassignmentstudentgrade.setClassassignment(null);

		return classassignmentstudentgrade;
	}

}