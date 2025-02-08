package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.AlarmEntity;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

	    // member1으로 알람 찾기
	    List<AlarmEntity> findByMember1Id(Long member1Id);

	    // member2로 알람 찾기
	    List<AlarmEntity> findByMember2Id(Long member2Id);
	    
	    // 특정 사용자가 관련된 모든 알람 조회
	    @Query("SELECT a FROM AlarmEntity a WHERE a.member1.id = :loggedId OR a.member2.id = :loggedId")
	    List<AlarmEntity> findByLoggedId(@Param("loggedId") Long loggedId);

}
