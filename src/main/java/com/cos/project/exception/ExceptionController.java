package com.cos.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cos.project.dto.ErrorMessageDTO;


//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.RestController;

//@ControllerAdvice
//@RestController
@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<?> handleArgumentException(Exception e) {		//수정 후
		 return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(new ErrorMessageDTO<>("요청 처리 중 에러가 발생했습니다",e.getMessage()));
	}
	

	
}
