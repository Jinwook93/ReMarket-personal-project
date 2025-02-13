package com.cos.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.LoginRequest;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.Gender;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;


@Service
public class MemberService {
    
    private final MemberRepository memberRepository;

    
    @Autowired
    public BCryptPasswordEncoder passwordEncoder;
    
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
    
	@Autowired
	HttpSession  session;
    
	@Autowired
	public MemberService(MemberRepository memberRepository) {
	    this.memberRepository = memberRepository;
	}

	@Transactional(readOnly = true)
	// 아이디 중복 확인
	public String checkId(String userid) {
	    // 아이디가 이미 존재하는지 확인
	    boolean isExist = memberRepository.existsByUserid(userid);
	    
	    if (isExist) {
	        // 아이디가 이미 존재하면 중복 아이디 메시지 반환
	    	System.out.println("이미 등록된 아이디입니다");
	        return "이미 등록된 아이디입니다";
	    } else {
	        // 아이디가 사용 가능하면 사용 가능 아이디 메시지 반환
	    	System.out.println("사용 가능한 아이디입니다");
	        return "사용 가능한 아이디입니다";
	    }
	}

    
    
    
    @Transactional
    //로그인
    public Long checkLogin(LoginRequest loginRequest) {
    	  UsernamePasswordAuthenticationToken authenticationToken = 
                  new UsernamePasswordAuthenticationToken(loginRequest.getUserid(), loginRequest.getPassword());

          Authentication authentication = authenticationManager.authenticate(authenticationToken);
          SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 처리
          
          PrincipalDetails principalDetails =(PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         
          return principalDetails.getMemberEntity().getId();
          
//          if(principalDetails != null) {
//          return   principalDetails.getUsername();
//          }else {
//        	  return "로그인 실패!";
//          }
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
        
        System.out.println("파일패스2"+memberEntity.getProfileImage());

        // 중복 ID 체크
        memberRepository.findByUserid(memberEntity.getUserid())
                .ifPresent(existingMember -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

        System.out.println("이미지 확인:"+memberEntity.getProfileImage());
        memberRepository.save(memberEntity);
        return "회원가입 완료";
    }
    
    
    
    
    
    
    
    @Transactional
    public String updateMember
    (Long id, String userid, String password, String name, int age, Gender gender, String phone, String address, String profileImage, PrincipalDetails principalDetails) {
        // 회원 정보 수정
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원 조회를 할 수 없습니다"));

        
        
//        String profileImagePath = null;
//	    if (memberDTO.getProfileImage() != null && !memberDTO.getProfileImage().isEmpty()) {
//	        // 이미지 파일의 원본 이름을 가져옴
//	        String originalFileName = memberDTO.getProfileImage();
//	        // 고유한 파일 이름 생성
//	        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
//	        // /image 폴더 경로로 이미지 저장
//	        Path filePath = Paths.get("src/main/resources/static/profileimage").resolve(uniqueFileName); // "image"는 저장 디렉토리 , 경로를 명시적으로 지정해주어야함
//	        Files.createDirectories(filePath.getParent()); // 디렉토리가 없으면 생성
//	        Files.write(filePath, profileImage.getBytes()); // 파일 저장
////	        profileImagePath = filePath.toString(); // 저장된 파일 경로
//	        profileImagePath = "/profileimage/"+uniqueFileName; 
//	        System.out.println("저장된 경로" + profileImagePath);
//	    }
        
        
        
        
        memberEntity.setUserid(userid);
        memberEntity.setName(name);
        memberEntity.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        memberEntity.setAddress(address);
        memberEntity.setAge(age);
        memberEntity.setGender(gender);
        memberEntity.setPhone(phone);
        memberEntity.setProfileImage(profileImage);
        

        // 업데이트된 회원 정보 저장
        memberRepository.save(memberEntity);

        // PrincipalDetails를 갱신된 MemberEntity로 업데이트		//시큐리티에서의 PrincipalDetails는 읽기 전용 , 캡슐화가 되어 있는 객체 이므로 이를 직접 수정하는 것은 규칙에 위배
        // 따라서 Authentication을 새로 만들어서 하는 것을 권장
        // getAuthorities 인자를 안 받아도 잘 됨
        PrincipalDetails updatedPrincipalDetails = new PrincipalDetails(memberEntity, principalDetails.getAuthorities());

        // 새로운 인증 객체 생성		       // getAuthorities 인자를 안 받아도 잘 됨
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedPrincipalDetails, updatedPrincipalDetails.getPassword(), updatedPrincipalDetails.getAuthorities());

        // SecurityContextHolder에 새로 생성된 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "회원수정 완료";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    @Transactional
//    public String updateMember(Long id, MemberDTO memberDTO, PrincipalDetails principalDetails) {
//        // 회원 정보 수정
//        MemberEntity memberEntity = memberRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("회원 조회를 할 수 없습니다"));
//
//        
//        
////        String profileImagePath = null;
////	    if (memberDTO.getProfileImage() != null && !memberDTO.getProfileImage().isEmpty()) {
////	        // 이미지 파일의 원본 이름을 가져옴
////	        String originalFileName = memberDTO.getProfileImage();
////	        // 고유한 파일 이름 생성
////	        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
////	        // /image 폴더 경로로 이미지 저장
////	        Path filePath = Paths.get("src/main/resources/static/profileimage").resolve(uniqueFileName); // "image"는 저장 디렉토리 , 경로를 명시적으로 지정해주어야함
////	        Files.createDirectories(filePath.getParent()); // 디렉토리가 없으면 생성
////	        Files.write(filePath, profileImage.getBytes()); // 파일 저장
//////	        profileImagePath = filePath.toString(); // 저장된 파일 경로
////	        profileImagePath = "/profileimage/"+uniqueFileName; 
////	        System.out.println("저장된 경로" + profileImagePath);
////	    }
//        
//        
//        
//        
//        memberEntity.setUserid(memberDTO.getUserid());
//        memberEntity.setName(memberDTO.getName());
//        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 비밀번호 암호화
//        memberEntity.setAddress(memberDTO.getAddress());
//        memberEntity.setAge(memberDTO.getAge());
//        memberEntity.setGender(memberDTO.getGender());
//        memberEntity.setPhone(memberDTO.getPhone());
//        memberEntity.setProfileImage(memberDTO.getProfileImage());
//        
//
//        // 업데이트된 회원 정보 저장
//        memberRepository.save(memberEntity);
//
//        // PrincipalDetails를 갱신된 MemberEntity로 업데이트		//시큐리티에서의 PrincipalDetails는 읽기 전용 , 캡슐화가 되어 있는 객체 이므로 이를 직접 수정하는 것은 규칙에 위배
//        // 따라서 Authentication을 새로 만들어서 하는 것을 권장
//        // getAuthorities 인자를 안 받아도 잘 됨
//        PrincipalDetails updatedPrincipalDetails = new PrincipalDetails(memberEntity, principalDetails.getAuthorities());
//
//        // 새로운 인증 객체 생성		       // getAuthorities 인자를 안 받아도 잘 됨
//        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
//                updatedPrincipalDetails, updatedPrincipalDetails.getPassword(), updatedPrincipalDetails.getAuthorities());
//
//        // SecurityContextHolder에 새로 생성된 인증 객체 설정
//        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
//
//        return "회원수정 완료";
//    }


    
    
    
    
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

    
    
    
    
    
    @Transactional(readOnly = true)			//회원 정보 조회
    public MemberEntity userInfoByUserid(String userid) throws IllegalAccessException {
        
        return memberRepository.findByUserid(userid)
                      .orElseThrow(() -> new IllegalAccessException("회원 정보를 조회할 수 없습니다"));
    }
    
    
    @Transactional(readOnly = true)			//회원 정보 조회
    public MemberEntity findById(Long id) throws IllegalAccessException {
        
        return memberRepository.findById(id)
                      .orElseThrow(() -> new IllegalAccessException("회원 정보를 조회할 수 없습니다"));
    }
    
    
    @Transactional(readOnly = true)			//회원 정보 조회
    public MemberEntity userInfoByNameAndPhone(String name, String password) throws IllegalAccessException {
        
        return memberRepository.findByNameAndPhone(name, password)
                      .orElseThrow(() -> new IllegalAccessException("회원 정보를 조회할 수 없습니다"));
    }
    
    
    
    
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

    
    
    
    
    
    
    
    
    
    
    
    // 파일 경로 방식으로 이미지를 불러올 경우 (DTO:String, Entity : String)
    
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
             .profileImage(memberDTO.getProfileImage() != null 
             ? memberDTO.getProfileImage() 
             : null) // 이미지 URL 또는 경로 설정
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
            .profileImage(memberEntity.getProfileImage() != null 
            ?  memberEntity.getProfileImage() 
            : null)
            .build();
     }
  
  
  
  
  
  
  
  
  
  //Base64 방식으로 이미지를 불러올 경우 (DTO:String, Entity : byte[])
  
    
    
//    @Transactional
//    // DTO를 Entity로 변환
//    public MemberEntity DTOtoEntity(MemberDTO memberDTO) {
//    	
//    	
//    	
//    	
//    	
//        return MemberEntity.builder()
//          //      .id(memberDTO.getId())			
//                .userid(memberDTO.getUserid())
//                .password(memberDTO.getPassword())
//                .address(memberDTO.getAddress())
//                .age(memberDTO.getAge())
//                .phone(memberDTO.getPhone())
//                .gender(memberDTO.getGender())
//                .name(memberDTO.getName())
//               .roles(memberDTO.getRoles())
//               .profileImage(memberDTO.getProfileImage() != null 
//               ? Base64.getDecoder().decode(memberDTO.getProfileImage()) 
//               : null) // 이미지 URL 또는 경로 설정
//                .build();
//    }
    
//    @Transactional
//    // Entity를 DTO로 변환		
//    public MemberDTO EntitytoDTO(MemberEntity memberEntity) {
//        return MemberDTO.builder()
//                .id(memberEntity.getId())			
//                .userid(memberEntity.getUserid())
//                .password(memberEntity.getPassword())
//                .address(memberEntity.getAddress())
//                .age(memberEntity.getAge())
//                .phone(memberEntity.getPhone())
//                .gender(memberEntity.getGender())
//                .name(memberEntity.getName())
//                .roles(memberEntity.getRoles()) 
//                .profileImage(memberEntity.getProfileImage() != null 
//                ? Base64.getEncoder().encodeToString(memberEntity.getProfileImage()) 
//                : null)
//                .build();
//         }
     

@Transactional
public MemberEntity findByUserId(String userid) {
    return memberRepository.findByUserid(userid).orElse(null); // Return null if no image data exists
}





    @Transactional
    //String -> byte[]
    public byte[] convertStringToByteArray(String profileImage) {
        if (profileImage != null) {
            // Convert the base64 string back to byte array
            return Base64.getDecoder().decode(profileImage); // Example of decoding base64 to byte[]
        }
        return null;
    }
    
    @Transactional
    //byte[]-> String
 // Convert byte[] (profile image) to String (Base64)
    public String convertByteArrayToString(byte[] profileImage) {
        if (profileImage != null) {
            // Convert byte[] to Base64 encoded String
            return Base64.getEncoder().encodeToString(profileImage);
        }
        return null; // Return null if no image data exists
    }
    
}