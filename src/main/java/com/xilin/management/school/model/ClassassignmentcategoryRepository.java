package com.xilin.management.school.model;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassassignmentcategoryRepository extends JpaSpecificationExecutor<Classassignmentcategory>, JpaRepository<Classassignmentcategory, Integer> {

	//public List<Classassignment> findAllByOrderByBookcodeAsc();
	public List<Classassignmentcategory> findBySemestercourse(Semestercourse semestercourse);
}
