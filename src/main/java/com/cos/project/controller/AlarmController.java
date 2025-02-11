package com.cos.project.controller;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.AlarmDTO;
import com.cos.project.dto.LoginRequest;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.AlarmService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

	private final MemberRepository memberRepository;
   
    
    
    // 특정 유저의 알림 목록 조회
    @GetMapping("/list/{loggedId}")
    public ResponseEntity<List<AlarmEntity>> getUserAlarms(@PathVariable(name = "loggedId") Long loggedId,@AuthenticationPrincipal PrincipalDetails principalDetail) {
        List<AlarmEntity> alarms = alarmService.findAllAboutLoggedId(principalDetail.getMemberEntity().getId());
        return ResponseEntity.ok(alarms);
    }

    //새로운 알림, 읽지 않은 메시지 알림
    @GetMapping("/list/unReadAndNew/{loggedId}")
    public ResponseEntity<?> unReadAlarmsAndNewAlarms(@PathVariable(name = "loggedId") Long loggedId,@AuthenticationPrincipal PrincipalDetails principalDetail, @RequestBody AlarmDTO alarmDTO) {
       String alarms = alarmService.NewAlarmMessage(principalDetail.getMemberEntity().getId(), alarmDTO.getMember1Id(), alarmDTO.getMember2Id(), alarmService.findCount(loggedId));
        return ResponseEntity.ok(alarms);
    }
    
    //사용자의 알람 갯수를 수시로 확인 (setInterval, 1000)
    @GetMapping("/list/findCount/{loggedId}")
    public ResponseEntity<?> LookUpUserAlarmCount(@PathVariable(name = "loggedId") Long loggedId,@AuthenticationPrincipal PrincipalDetails principalDetail) {
       Long alarmCount = alarmService.findCount(loggedId);
        return ResponseEntity.ok(alarmCount);
    }
    
    
    
    
    
    
    // 특정 알림 읽음 처리
    @PostMapping("/read/{alarmId}")
    public ResponseEntity<?> markAsRead(@PathVariable(name = "alarmId") Long alarmId, @AuthenticationPrincipal PrincipalDetails principalDetail, @RequestBody AlarmDTO alarmDTO) {
    	alarmService.readAlarm(alarmId, principalDetail.getMemberEntity().getId(), alarmDTO.getMember1Id(), alarmDTO.getMember2Id());
        return ResponseEntity.ok(alarmId);
    }

//    // 모든 알림 읽음 처리
//    @PostMapping("/read-all")
//    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal PrincipalDetails principalDetail) {
//        alarmService.markAllAsRead(principalDetail.getUser().getId());
//        return ResponseEntity.ok().build();
//    }

    // 특정 알림 삭제
    @PostMapping("/delete/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable(name = "alarmId") Long alarmId, @AuthenticationPrincipal PrincipalDetails principalDetail, @RequestBody AlarmDTO alarmDTO) {
        alarmService.hideAlarm(alarmId, principalDetail.getMemberEntity().getId(), alarmDTO.getMember1Id(), alarmDTO.getMember2Id());
        return ResponseEntity.ok().build();
    }

    // 모든 알림 삭제
//    @DeleteMapping("/delete-all")
//    public ResponseEntity<Void> deleteAllAlarms(@AuthenticationPrincipal PrincipalDetails principalDetail) {
//        alarmService.deleteAllAlarms(principalDetail.getUser().getId());
//        return ResponseEntity.ok().build();
//    }
    
    
    
//    //로그인 알람
//    @PostMapping("/loginSuccess/{loggedId}")			// 로그인 알람
//    @ResponseBody
//    public ResponseEntity<?> loginAlarm(@PathVariable(name = "loggedId") Long loggedId, @RequestBody AlarmDTO alarmDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
////    	Long loggedId = principalDetails.getMemberEntity().getId();
//    	System.out.println("갔나요?"+ loggedId);
//    	
//    	alarmService.postAlarm(loggedId,alarmDTO.getMember1Id(), alarmDTO.getMember2Id(), alarmDTO.getType(), alarmDTO.getChildType(), alarmDTO.getObject(), alarmDTO.getAction(), alarmDTO.getPriority());
//    	String result = "success";
//        return ResponseEntity.ok(result);
//    }
    
    
    
    
    
    
    
    
    
}
