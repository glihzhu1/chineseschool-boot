package com.xilin.management.school.model;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassassignmentRepository extends JpaSpecificationExecutor<Classassignment>, JpaRepository<Classassignment, Integer> {

	public List<Classassignment> findBySemestercourse(Semestercourse semestercourse);
	
}
