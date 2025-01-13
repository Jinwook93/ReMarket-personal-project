package com.cos.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.MemberRepository;


import java.util.List;
import java.util.stream.Collector;


@Service
public class MemberService {
    
    private final MemberRepository memberRepository;

    
    @Autowired
    public BCryptPasswordEncoder passwordEncoder;
    
    
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly =  true)
    // 회원 목록 조회
    public List<MemberEntity> showAllMember() {
        return memberRepository.findAll();
    }
    
    
    /*
    @Transactional
    // 회원 가입
    public String joinMember(MemberDTO memberDTO) {
        MemberEntity memberEntity = DTOtoEntity(memberDTO);

        // 중복 ID 체크
        memberRepository.findById(memberEntity.getId())								--안된 이유 : memberDTO에 이미 id값이 없으므로,  memberEntity의 id는 null이다
        																													`DB에 저장이 될 때, id가 자동으로 저장이 되기 때문에`  저장하기 전 시점에서는 계속 id가 null
        																													이어서 id null관련 메시지가 떴던것임 -> 해결 : unique인 userid로 대상을 변경함
                .ifPresent(existingMember -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

        memberRepository.save(memberEntity);
        return "회원가입 완료";
    }
    
    */
    
    
    
    
    
    
    
    @Transactional
    // 회원 가입
    public String joinMember(MemberDTO memberDTO) {
    	
    	memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
    	
        MemberEntity memberEntity = DTOtoEntity(memberDTO);

        // 중복 ID 체크
        memberRepository.findByUserid(memberEntity.getUserid())
                .ifPresent(existingMember -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

        memberRepository.save(memberEntity);
        return "회원가입 완료";
    }
    
    
    @Transactional
    // 회원 수정
    public String updateMember(Long id, MemberDTO memberDTO) {
    	
    	// MemberEntity memberEntity = null;
    	 
    	 MemberEntity memberEntity = memberRepository.findById(id)
                 .orElseThrow(() -> new IllegalArgumentException("회원 조회를 할 수 없습니다"));
//       memberEntity = memberRepository.findByUserid(memberEntity.getUserid())
//                .orElseThrow(() -> new IllegalArgumentException("회원 조회를 할 수 없습니다"));

        memberEntity.setUserid(memberDTO.getUserid());
        memberEntity.setName(memberDTO.getName());
        //memberEntity.setPassword(memberDTO.getPassword());
        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberEntity.setAddress(memberDTO.getAddress());
        memberEntity.setAge(memberDTO.getAge());
        memberEntity.setGender(memberDTO.getGender());
        memberEntity.setPhone(memberDTO.getPhone());

        memberRepository.save(memberEntity);
        return "회원수정 완료";
    }
    
    
    
    
    @Transactional
    // 회원 탈퇴
    public String deleteMember(Long id) {
    	
    	if(!memberRepository.findById(id).isPresent()) {
    		return "이미 삭제된 계정입니다";
    	}
    	
        memberRepository.deleteById(id);
        return "회원삭제 완료";
    }

    
    
//    @Transactional(readOnly = true)
//    // 회원 정보 조회
//    public MemberDTO userInfo(MemberEntity memberEntity) throws IllegalAccessException {
//    	
//    List<MemberEntity> members =	showAllMember();
//    
//   members.stream()
//   .filter(member -> member.getUserid().equals(memberEntity.getUserid()))
//   .map(MemberDTO::new)
//   .toList();
//    	
//    //	return memberRepository.findByUserid(memberEntity.getUserid());
//    	
//    //    return memberRepository.findById(id)
//     //           .orElseThrow(() -> new IllegalAccessException("회원 정보를 조회할 수 없습니다"));
//    }

    
    
    
    
    
    @Transactional(readOnly = true)
    public MemberDTO userInfo(MemberEntity memberEntity) throws IllegalAccessException {
        List<MemberEntity> members = showAllMember();

        // 필터링한 후 첫 번째 매칭된 MemberEntity를 MemberDTO로 변환하여 반환
        return members.stream()
                      .filter(member -> member.getUserid().equals(memberEntity.getUserid()))
                      .map(this::EntitytoDTO)
                      .findFirst()
                      .orElseThrow(() -> new IllegalAccessException("회원 정보를 조회할 수 없습니다"));
    }

    
    
    
    
    
    
    
    
    @Transactional
    // DTO를 Entity로 변환
    public MemberEntity DTOtoEntity(MemberDTO memberDTO) {
        return MemberEntity.builder()
          //      .id(memberDTO.getId())			
                .userid(memberDTO.getUserid())
                .password(memberDTO.getPassword())
                .address(memberDTO.getAddress())
                .age(memberDTO.getAge())
                .phone(memberDTO.getPhone())
                .gender(memberDTO.getGender())
                .name(memberDTO.getName())
               .roles(memberDTO.getRoles()) 
                
                .build();
    }
    
    @Transactional
    // Entity를 DTO로 변환		
    public MemberDTO EntitytoDTO(MemberEntity memberEntity) {
        return MemberDTO.builder()
                .id(memberEntity.getId())			
                .userid(memberEntity.getUserid())
                .password(memberEntity.getPassword())
                .address(memberEntity.getAddress())
                .age(memberEntity.getAge())
                .phone(memberEntity.getPhone())
                .gender(memberEntity.getGender())
                .name(memberEntity.getName())
                .roles(memberEntity.getRoles()) 
                .build();
         }
     
    
}