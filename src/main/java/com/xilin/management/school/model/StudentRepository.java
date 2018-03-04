package com.xilin.management.school.model;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaSpecificationExecutor<Student>, JpaRepository<Student, Integer> {
	public List<Student> findByFamilyid(Family familyid);
	//public List<Student> findByEmailOrLoginIdIgnoreCase(String email, String loginId);

	@Query(value="select m "
			+ "from Student m left join m.familytransactions u "
			+ "where u.transactiontype = 'RG' "
			+ "and u.semestercourseid = :cls ")
	public List<Student> queryAllStudentsForClass(@Param("cls") Semestercourse cls);
}
