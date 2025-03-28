package com.cos.project.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.Gender;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.Roles;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
//	@GetMapping("/")
//	public String  home() {
//		return "index";
//	}
	@Autowired
	MemberService memberService;
	
	@Autowired
	BoardService boardService;
	
	@Autowired
	CommentService commentService;
	

	
	private final static String ADMIN_PASSWORD  = "admin!@#$";
	
			
	
	@GetMapping("/")
	public String mainPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {

//		   if (principal != null) {
//	        // Principal 정보 활용
//	        model.addAttribute("isLoggedIn", true);
//	        model.addAttribute("name", principal.getMemberEntity().getName());
//	        model.addAttribute("id", principal.getMemberEntity().getId());
//	    } else {
//	        // 비로그인 상태
//	        model.addAttribute("isLoggedIn", false);
//	    }
	    return "index";
	}

	
	@GetMapping("/formlogin")
	public String  loginpage() {
		return "formlogin";
	}
	
	@GetMapping("/join")
	public String  joinpage() {
		return "join";
	}
	
	@PostMapping("/serverjoin")
	public String serverJoin(
	        @RequestParam(name = "userid") String userid,
	        @RequestParam(name = "password") String password,
	        @RequestParam(name = "nickname") String nickname,
	        @RequestParam(name = "name") String name,
	        @RequestParam(name = "phone") String phone,
	        @RequestParam(name = "age") int age,
	        @RequestParam(name = "address") String address,
	        @RequestParam(name = "address2") String address2,
	        @RequestParam(name = "gender") Gender gender,
	        @RequestParam(name = "password_admin") String password_admin,
	        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage // 프로필 이미지 추가
	) throws IOException {
	    Roles role;

	    // 관리자 비밀번호 확인
	    if (password_admin == null || password_admin.isEmpty() || !password_admin.equals(ADMIN_PASSWORD)) {
	        role = Roles.USER;
	    } else {
	        role = Roles.ADMIN;
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
//	        profileImagePath = filePath.toString(); // 저장된 파일 경로
	        profileImagePath = "/profileimage/"+uniqueFileName; 
	        System.out.println("저장된 경로" + profileImagePath);
	    }
	    // Build the MemberDTO using builder pattern
	    MemberDTO memberDTO = MemberDTO.builder()
	            .userid(userid)
	            .password(password)
	            .nickname(nickname)
	            .name(name)
	            .phone(phone)
	            .age(age)
	            .address(address+" "+address2)
	            .gender(gender)
	            .roles(role)
	            .profileImage(profileImagePath) // 이미지 경로를 설정
	            .build();

	    String result = memberService.joinMember(memberDTO);

	    if (result.equals("회원가입 완료")) {
	        return "redirect:/";  // Redirect to the main page after successful registration
	    } else {
	        return "redirect:/join";  // Redirect back to the join page if the signup fails
	    }
	}




	//내가 쓴 댓글 리스트 확인
//	@GetMapping("/commentlist/{id}")
//	public String myComment(@PathVariable("id") Long id, Model model) {
//		List<CommentEntity> mycomments = commentService.findMyComments(id);
//		model.addAttribute("mycomments", mycomments);
//		
//		return "mycommentlist";
//	}

	//내가 쓴 댓글 리스트 확인 2 : 이렇게 찾아도 됨
	@GetMapping("/commentlist/{id}")
	public String myComment(@PathVariable("id") Long id, Model model) throws IllegalAccessException {
		MemberEntity member = memberService.findById(id);
		List<CommentEntity> comments = member.getComments();
		
		List<CommentEntity> filteredComments = comments.stream()														//삭제된 게시글에 대한 댓글은 제외
		.filter(comment-> comment.getBoardEntity().getDeleted().equals(Boolean.FALSE))
		.sorted(Comparator.comparing((CommentEntity comment)-> comment.getReCreateTime() != null? comment.getReCreateTime():comment.getCreateTime() ).reversed())
		.collect(Collectors.toList());
		
		//sort는 반환 값이 void
//		comments.sort(Comparator.comparing(CommentEntity::getCreateTime).reversed());

		model.addAttribute("mycomments", filteredComments);
		
		return "mycommentlist";
	}
	
	
	
	//내가 쓴 게시글 리스트 확인
	@GetMapping("/boardlist/{id}")
	public String myBoard(@PathVariable("id") Long id, Model model) {
	    List<BoardEntity> myboards = boardService.findMyBoards(id);

	    // 삭제되지 않은 게시글 필터링 후 정렬
	    myboards = myboards.stream()
	        .filter(board -> board.getDeleted().equals(Boolean.FALSE)) // 삭제되지 않은 게시글만 필터링
	        .sorted(Comparator.comparing((BoardEntity board) -> board.getReCreateTime() != null? board.getReCreateTime() : board.getCreateTime()).reversed()) // 최신 글 순으로 정렬
	        .collect(Collectors.toList()); // 결과를 리스트로 수집

	    model.addAttribute("myboards", myboards);
	    
	    return "myboardlist";
	}

	
	
	//마이페이지 확인
	@GetMapping("/mypage/{id}")
	public String myPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberEntity memberEntity = principalDetails.getMemberEntity();
//		System.out.println("이미지2 : "+memberService.convertByteArrayToString(memberEntity.getProfileImage()));		Base64
		
		
		if(memberEntity.getProfileImage() != null) {
		memberEntity.setProfileImage(memberEntity.getProfileImage().replace("\\", "/"));
		}
		
		
		
		if(memberEntity == null) {
			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
		}
		else {
//			String updateAddress = memberEntity.getAddress().replace("/", " ");
//			memberEntity.setAddress(updateAddress);
			model.addAttribute("member", memberEntity);
		}
	
		
		return "mypage";
	}
	
	
	//마이페이지 수정창 이동
	@GetMapping("/updatemypage/{id}")
	public String goUpdatemyPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberEntity memberEntity = principalDetails.getMemberEntity();
		if(memberEntity.getProfileImage() != null) {
		memberEntity.setProfileImage(memberEntity.getProfileImage().replace("\\", "/"));
		}
		if(memberEntity == null) {
			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
		}
		else {
			
			model.addAttribute("member", memberEntity);
		}
	
		
		return "goUpdateMemberForm";
	}
	
	@GetMapping("/findid")
	public String goFindId() {
	    return "findid";
	}
	@PostMapping("/findid")
	@ResponseBody
	public ResponseEntity<?> findId(@RequestBody MemberDTO memberDTO) throws IllegalAccessException { //이름,전화번호로 아이디 검색
	    MemberEntity memberEntity = memberService.userInfoByNameAndPhone(memberDTO.getName(),memberDTO.getPhone());
	   
	    return ResponseEntity.ok(memberEntity.getUserid());
	}

	
	
	
	@GetMapping("/findpassword")
	public String goFindPassword() {
	    return "findpassword";
	}

	
//	//비밀번호 직접 추출 => 권장 사항이 아님
//	@PostMapping("/findpassword")
//	@ResponseBody
//	public ResponseEntity<?> findpassword(@RequestBody MemberDTO memberDTO) throws IllegalAccessException {
//	    MemberEntity memberEntity = memberService.userInfoByUseridAndNameAndPhone(memberDTO.getUserid(), memberDTO.getName(),memberDTO.getPhone());
//	    System.out.println(memberEntity.getPassword());
//	    return ResponseEntity.ok(memberEntity.getPassword());
//	}

	
	//비밀번호 변경 페이지를 이동 1
	@PostMapping("/findpassword")
	@ResponseBody
	public ResponseEntity<?> findpassword(@RequestBody MemberDTO memberDTO) throws IllegalAccessException {
	    Boolean result = memberService.userInfoByUseridAndNameAndPhone(memberDTO.getUserid(), memberDTO.getName(), memberDTO.getPhone());
	    return ResponseEntity.ok(result);
	}
	
	//비밀번호 변경 페이지를 이동 2
//	@PostMapping("/findpassword")
//	@ResponseBody
//	public String findpassword(@RequestBody MemberDTO memberDTO, Model model) throws IllegalAccessException {
//	    MemberEntity memberEntity = memberService.userInfoByUseridAndNameAndPhone(memberDTO.getUserid(), memberDTO.getName(), memberDTO.getPhone());
//	  if(memberEntity != null) {
//		  model.addAttribute("member", memberEntity);
//		  return "updatepassword";
//	  }
//	
//	 return "실패";
//	}
	
	
	
	
	//비밀번호 변경 페이지를 이동 
	@GetMapping("/updatepassword")
	public String goupdatepassword() {
	    return "updatepassword";
	}
	
	//비밀번호 변경 페이지  (비로그인 상태)
	@PostMapping("/updatepassword")
	@ResponseBody
	public ResponseEntity<?> updatepassword(@RequestBody MemberDTO memberDTO) throws IllegalAccessException {
		System.out.println(memberDTO.toString());
		MemberEntity member = memberService.findByUserId(memberDTO.getUserid());
		
	    String result = "회원수정 실패";
		result = memberService.updateMember(member.getId(), member.getUserid() ,member.getNickname(),memberDTO.getPassword() ,member.getName(), member.getAge(), member.getGender(), member.getPhone(),member.getAddress(),member.getProfileImage() , null,Boolean.FALSE);
		return ResponseEntity.ok(result);
	}
	
	
//	@PostMapping("/updatepassword")
//	@ResponseBody
//	public String updatepassword(@RequestBody MemberDTO memberDTO) throws IllegalAccessException {
//	    return "updatepassword";
//	}
	
	
	
	
//	//DB에 마이페이지 수정 적용
//	@PutMapping("/updatemypage/{id}")
//	public String updateMyPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//		MemberEntity memberEntity = principalDetails.getMemberEntity();
//		memberEntity.set
//		if(memberEntity == null) {
//			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
//		}
//		else {
//			
//			model.addAttribute("member", memberEntity);
//		}
//	
//		
//		return "mypage";
//	}
	
@ResponseBody	//게시자 마이페이지 
@PostMapping("/boarderpage/{boardId}")
	public ResponseEntity<?> boarderpage(@PathVariable("boardId")Long boardId){
		BoardEntity boardEntity = boardService.findByBoardId(boardId);
		MemberEntity memberEntity = boardEntity.getMemberEntity();
		
		String[] splitedAddress = memberEntity.getAddress().split(" ");
		
				
		
		
		MemberDTO memberDTO = MemberDTO.builder()
				.id(memberEntity.getId())
				.userid(memberEntity.getUserid())
				.name(memberEntity.getName())
				.age(memberEntity.getAge())
				.gender(memberEntity.getGender())
				.address(splitedAddress[0] + " "+	splitedAddress[1]+" "+	splitedAddress[2])
				.phone(memberEntity.getPhone())
				.profileImage(memberEntity.getProfileImage())
				.nickname(memberEntity.getNickname())
				.build();
					
				return ResponseEntity.ok(memberDTO);
			}


	
}
