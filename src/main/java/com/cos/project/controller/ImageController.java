//package com.cos.project.controller;
//
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cos.project.details.PrincipalDetails;
//import com.cos.project.dto.LoginRequest;
//import com.cos.project.dto.MemberDTO;
//import com.cos.project.entity.CommentEntity;
//import com.cos.project.entity.MemberEntity;
//import com.cos.project.service.BoardService;
//import com.cos.project.service.CommentService;
//import com.cos.project.service.MemberService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.annotation.Resource;
//
//import java.net.MalformedURLException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//
//@RestController
////@RequestMapping("/member")
//public class ImageController {
//
//	private MemberService memberService;
//		
//    @Autowired
//	public ImageController(MemberService memberService) {
//		this.memberService = memberService;
//	}
//
//
//    @Autowired
//    private BoardService boardService;
//    
//    @Autowired
//    private CommentService commentService;
//    
//
//    @GetMapping("/image")
//    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
//        Path filePath = Paths.get("image").resolve(filename).normalize();
//        Resource resource = (Resource) new UrlResource(filePath.toUri());
//
//        if (resource != null) {
//            return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG) // 이미지 타입 설정
//                .body(resource);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//
//
//	
//}
