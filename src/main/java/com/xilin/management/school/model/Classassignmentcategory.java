package com.xilin.management.school.model;

import java.io.Serializable;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;


/**
 * The persistent class for the classassignmentcategory database table.
 * 
 */
@Entity
@NamedQuery(name="Classassignmentcategory.findAll", query="SELECT c FROM Classassignmentcategory c")
public class Classassignmentcategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String description;

	private Integer gradepercentage;

	private String updatedby;

	@Column(name = "updatedtime")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar updatedtime;

	//bi-directional many-to-one association to Classassignment
	@OneToMany(mappedBy="classassignmentcategory")
	private List<Classassignment> classassignments;

	//bi-directional many-to-one association to Semestercourse
	@ManyToOne
	@JoinColumn(name="classid")
	private Semestercourse semestercourse;

	public Classassignmentcategory() {
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

	public Integer getGradepercentage() {
		return this.gradepercentage;
	}

	public void setGradepercentage(Integer gradepercentage) {
		this.gradepercentage = gradepercentage;
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

	public List<Classassignment> getClassassignments() {
		return this.classassignments;
	}

	public void setClassassignments(List<Classassignment> classassignments) {
		this.classassignments = classassignments;
	}

	public Classassignment addClassassignment(Classassignment classassignment) {
		getClassassignments().add(classassignment);
		classassignment.setClassassignmentcategory(this);

		return classassignment;
	}

	public Classassignment removeClassassignment(Classassignment classassignment) {
		getClassassignments().remove(classassignment);
		classassignment.setClassassignmentcategory(null);

		return classassignment;
	}

	public Semestercourse getSemestercourse() {
		return this.semestercourse;
	}

	public void setSemestercourse(Semestercourse semestercourse) {
		this.semestercourse = semestercourse;
	}

}