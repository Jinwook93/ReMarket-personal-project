package com.cos.project.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.LoginRequest;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.Gender;
import com.cos.project.entity.MemberEntity;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
//@RequestMapping("/member")
public class MemberController {

	private MemberService memberService;
		
    @Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}


    @Autowired
    private BoardService boardService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private AlarmService alarmService;
    
    @Autowired
    public BCryptPasswordEncoder passwordEncoder;
//	@GetMapping("/")
//	public String  home() {
//		return "메인 페이지";
//	}
//	
    
    
    @PostMapping("/validateMember")			//없어도 무방하나 로그인 알람때문에 만듬
    @ResponseBody
    public ResponseEntity<?> postMethodName(@RequestBody LoginRequest loginData) {
    	Long loggedId = memberService.checkLogin(loginData);
    	if(loggedId != null) {
    	alarmService.postAlarm(loggedId,null, null, "LOGIN", null, null, null, null);				//로그인 한 사용자에 로그인 알람 추가
    	}
    	System.out.println(loggedId);
        return ResponseEntity.ok(loggedId);
    }
    
    
    
    
    
    @PostMapping("/loginmember")			//   (요청 -> filterchain -> 인증/인가 -> 컨트롤러 순서 )
    																//시큐리티가 가로채서 loginProcessingUrl("/loginMember") POST 요청을 실행 
    															//  만약 Spring Security가 요청을 처리하면, 컨트롤러에 도달하지 않습니다. 따라서 , 이 컨트롤러는 구현을 할 수 없다
    
    												/*Spring Security가 가로채는 경로(/loginmember)와 컨트롤러에서 사용하는 경로(/loginMember)는 중복되지 않도록 분리해야 합니다.
    												Spring Security를 비활성화하지 않는 이상, 같은 경로(/loginmember)의 POST 요청을 컨트롤러에서 직접 처리할 수 없습니다.
    												프로젝트 요구사항에 따라, Spring Security의 인증 로직을 유지하면서 커스터마이징하거나, 완전히 비활성화하고 커스텀 구현을 선택해야 합니다. 😊 
    													=> 1. PostMapping url을 loginProcessingUrl과 다른 값으로 설정 (※ formLogin()을 계속 사용하기 원할 경우)	
    													-- 프론트 쪽에서는 전송 데이터 타입을 x-www-urlencoded 타입으로 해야한다 (시큐리티에서 기본으로 지원하고 있음)
    														2.  formLogin().disabled               -- formLogin 비활성화	
    														(※ PostMapping의 url을  "loginMember" (loginProcessingUrl 지정값)으로 설정을 해야할 경우)
    																-- 프론트 쪽에서는 전송 데이터 타입을 JSON 타입으로 해야한다 (컨트롤러에서 RequestBody (JSON)으로 받고 있음)
    																		--  @RequestBody는 JSON이나 XML 데이터를 처리할 때 사용되며, 
    																		--   x-www-form-urlencoded 데이터는 @RequestParam을 사용하여 받습니다.
    																		
  													
    */
       // public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    
    
    
    	 public ResponseEntity<?> login(@RequestParam (name = "userid") String userid, @RequestParam (name = "password") String password) {
    	LoginRequest loginRequest = new LoginRequest(userid,password); 
        System.out.println("loginMember 컨트롤러 실행 중...");
       return ResponseEntity.ok(memberService.checkLogin(loginRequest));
    }
	
    @PostMapping("/checkId")
   public Map<String,Boolean> checkId(@RequestBody String userid) throws JsonMappingException, JsonProcessingException {
    	System.out.println(userid);
    	ObjectMapper om = new ObjectMapper();
    	MemberDTO userid_change = om.readValue(userid, MemberDTO.class);		//DTO 클래스로 변환
       System.out.println("중복ID 컨트롤러");
       Map<String, Boolean> checkedId = new HashMap<>();
       checkedId.put("available", memberService.checkId(userid_change.getUserid()));
       return checkedId;
    }
   
    
    
	@GetMapping("/findall")
	public ResponseEntity<?> findAllUser() {
	    return ResponseEntity.ok(memberService.showAllMember());
	}
    


	@PostMapping("/join")
public ResponseEntity<?>  joinUser(@RequestBody MemberDTO entity) {
    	String result = memberService.joinMember(entity);
    
    return ResponseEntity.ok(result);
}
	

	
//	
	 @PutMapping("/updateMember/{id}")
	    public ResponseEntity<?> updateMember(
	       @PathVariable(name ="id") Long id,
	        @RequestParam(name = "userid") String userid,
	        @RequestParam(name = "password") String password,
	        @RequestParam(name = "password_check") String passwordCheck,
	        @RequestParam(name = "name") String name,
	        @RequestParam(name = "age") int age,
	        @RequestParam(name = "gender") Gender gender,
	        @RequestParam(name = "phone") String phone,
	        @RequestParam(name = "address") String address,
	        @RequestParam(name = "address2") String address2,
	        @RequestParam(name = "prev_password") String prev_password, 	//기존 패스워드
	        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
	        @RequestParam(name = "nullimageButton", required = false) Boolean nullimageButton,
	        @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

	        // Password validation logic (make sure passwords match)
	        if (!password.equals(passwordCheck)) {
	            return ResponseEntity.badRequest().body("패스워드가 일치하지 않습니다");
	        }
	        	
	        try {
				if( !passwordEncoder.matches(prev_password,memberService.findById(id).getPassword())){
					System.out.println(prev_password);
					
					return ResponseEntity.ok( "기존 비밀번호 입력값이 일치하지 않습니다");
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	        
	        
	        // 프로필 이미지 파일 저장 경로 생성 (예: /image 폴더에 저장)
		    String profileImagePath = null;
		    if (profileImage != null && !profileImage.isEmpty()) {
		        // 이미지 파일의 원본 이름을 가져옴
		        String originalFileName = profileImage.getOriginalFilename();
		        // 고유한 파일 이름 생성
		        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
		        // /image 폴더 경로로 이미지 저장
		        Path filePath = Paths.get("src/main/resources/static/profileimage").resolve(uniqueFileName); // "image"는 저장 디렉토리 , 경로를 명시적으로 지정해주어야함
		        Files.createDirectories(filePath.getParent()); // 디렉토리가 없으면 생성
		        Files.write(filePath, profileImage.getBytes()); // 파일 저장
//		        profileImagePath = filePath.toString(); // 저장된 파일 경로
		        profileImagePath = "/profileimage/"+uniqueFileName; 
		        System.out.println("저장된 경로" + profileImagePath);
		    }
	        
	        
	        
	        
	        
	        
	        
	        
	        // Perform the update through the service layer
	        try {
	        	address += " "+address2;
	        	
	            String isUpdated = memberService.updateMember(id, userid, password, name, age, gender, phone, address, profileImagePath, principalDetails,nullimageButton);
	            if (isUpdated != null) {
	                return ResponseEntity.ok( "회원 정보 업데이트 성공");
	            } else {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 업데이트 실패");
	            }
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
	        }
	    
	}
	
	
	
	
	
	
	
	
	//DB에 마이페이지 수정 적용	
	//   (프로필 이미지 추가로 RequestBody 대신 RequestParam 쓸 예정)
//@PutMapping("updateuser/{id}")
//public ResponseEntity<?> updateUser(@PathVariable(name = "id") Long id, @RequestBody MemberDTO memberDTO,@AuthenticationPrincipal PrincipalDetails principalDetails) {
//    String result = memberService.updateMember(id, memberDTO, principalDetails);		//"result : 회원수정 완료"
//    return ResponseEntity.ok(result);
//}



@DeleteMapping("deleteuser/{id}")
public ResponseEntity<?> deleteUser(@PathVariable(name = "id") long id) {
    String result = memberService.deleteMember(id);
    return ResponseEntity.ok(result);
}


//아이디 , 비밀번호 찾기

//@GetMapping("/findid")
//public String goFindId() {
//    return "findid";
//}
//@PostMapping("/findid")
//public ResponseEntity<?> findId(@RequestBody String userid) throws IllegalAccessException {
//    MemberEntity memberEntity = memberService.userInfoByUserid(userid);
//    return ResponseEntity.ok(memberEntity.getUserid());
//}

//@GetMapping("/findpassword")
//public String goFindPassword() {
//    return "findpassword";
//}
//
//@PostMapping("/findpassword")
//public ResponseEntity<?> findpassword(@RequestBody String password) throws IllegalAccessException {
//    MemberEntity memberEntity = memberService.userInfoByPassword(password);
//    return ResponseEntity.ok(memberEntity.getPassword());
//}


@PostMapping("/getProfileImage")
public ResponseEntity<?> getProfileImage(@RequestBody String userid) throws JsonMappingException, JsonProcessingException {
    // First, check if the user exists
	
	
	ObjectMapper om = new ObjectMapper();
	String userId = om.readValue(userid, MemberDTO.class).getUserid();
    String profileImage = memberService.findByUserId(userId).getProfileImage();
//    System.out.println(profileImage);


    // If profile image is empty, return the default image
    if (profileImage == null || profileImage.isEmpty()) {
        return ResponseEntity.ok("/boardimage/nullimage.jpg");
    }

    // Otherwise, return the profile image
    return ResponseEntity.ok(profileImage);
}

	
}
