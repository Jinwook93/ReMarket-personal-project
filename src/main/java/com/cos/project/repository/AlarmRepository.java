package com.cos.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.MemberEntity;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

	    // member1으로 알람 찾기
	    List<AlarmEntity> findByMember1Id(Long member1Id);

	    // member2로 알람 찾기
	    List<AlarmEntity> findByMember2Id(Long member2Id);
	    
	    // 특정 사용자가 관련된 모든 알람 조회
	    @Query("SELECT a FROM AlarmEntity a WHERE a.member1.id = :loggedId OR a.member2.id = :loggedId")
	    List<AlarmEntity> findByLoggedId(@Param("loggedId") Long loggedId);

	    @Query("SELECT a FROM AlarmEntity a WHERE a.member1.id = :loggedId OR a.member2.id = :loggedId")
	    Page<AlarmEntity> findByLoggedId(@Param("loggedId") Long loggedId, Pageable pageable);

	    @Query("SELECT a FROM AlarmEntity a WHERE ((a.member1 = :member1 AND a.member2 = :member2) OR (a.member1 = :member2 AND a.member2 = :member1))  AND a.object = :object")
	    	List<AlarmEntity> findByMembersAndObject(@Param("member1") MemberEntity member1, 
	    	                                         @Param("member2") MemberEntity member2, 
	    	                                         @Param("object") String object);

}
