package com.xilin.management.school.model;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassassignmentstudentgradeRepository extends JpaSpecificationExecutor<Classassignmentstudentgrade>, JpaRepository<Classassignmentstudentgrade, Integer> {

	//public List<Classassignment> findAllByOrderByBookcodeAsc();
	public List<Classassignmentstudentgrade> findByClassassignment(Classassignment classassignment);
	
	@Query("SELECT casg FROM Classassignmentstudentgrade casg"
			+ " WHERE casg.classassignment.semestercourse = :courseCls"
			+ " and casg.student = :student")
	public List<Classassignmentstudentgrade> findByStudentAndClass(@Param("student") Student student, @Param("courseCls") Semestercourse courseCls);
	
}
