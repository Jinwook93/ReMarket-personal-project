package com.cos.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByUserid(String userid);
	boolean existsByUserid(String userid);
	Optional<MemberEntity> findByPassword(String password);
	Optional<MemberEntity> findByUseridAndNicknameAndNameAndPhone (String nickname, String userid, String name, String phone);
	Optional<MemberEntity> findByNicknameAndNameAndPhone(String nickname, String name, String phone);
	boolean existsByNickname(String nickname);
}
