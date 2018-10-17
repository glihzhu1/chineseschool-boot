package com.xilin.management.school.model;

import java.io.Serializable;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;


/**
 * The persistent class for the classassignmentstudentgrade database table.
 * 
 */
@Entity
@NamedQuery(name="Classassignmentstudentgrade.findAll", query="SELECT c FROM Classassignmentstudentgrade c")
public class Classassignmentstudentgrade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String commentnote;

	private BigDecimal earnedgradepoint;

	private String updatedby;

	@Column(name = "updatedtime")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar updatedtime;

	//bi-directional many-to-one association to Classassignment
	@ManyToOne
	@JoinColumn(name="classassignmentid")
	private Classassignment classassignment;

	//bi-directional many-to-one association to Student
	@ManyToOne
	@JoinColumn(name="studentid")
	private Student student;

	public Classassignmentstudentgrade() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCommentnote() {
		return this.commentnote;
	}

	public void setCommentnote(String commentnote) {
		this.commentnote = commentnote;
	}

	public BigDecimal getEarnedgradepoint() {
		return this.earnedgradepoint;
	}

	public void setEarnedgradepoint(BigDecimal earnedgradepoint) {
		this.earnedgradepoint = earnedgradepoint;
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

	public Classassignment getClassassignment() {
		return this.classassignment;
	}

	public void setClassassignment(Classassignment classassignment) {
		this.classassignment = classassignment;
	}

	public Student getStudent() {
		return this.student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}