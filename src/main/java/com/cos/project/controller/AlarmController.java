package com.cos.project.controller;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.AlarmDTO;
import com.cos.project.dto.LoginRequest;
import com.cos.project.dto.PagedResponse;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.repository.MemberRepository;
import com.cos.project.service.AlarmService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
   
//    private final BoardService boardService;
    
    // 특정 유저의 알림 목록 조회
//    @GetMapping("/list/{loggedId}")
//    public ResponseEntity<List<AlarmEntity>> getUserAlarms(@PathVariable(name = "loggedId") Long loggedId,@AuthenticationPrincipal PrincipalDetails principalDetail) {
//        List<AlarmEntity> alarms = alarmService.findAllAboutLoggedId(principalDetail.getMemberEntity().getId());
//        return ResponseEntity.ok(alarms);
//    }

    

	@GetMapping("/list/{loggedId}")
	public ResponseEntity<PagedResponse<AlarmDTO>> getUserAlarms(
	        @PathVariable(name = "loggedId") Long loggedId,
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "10") int size,
	        @RequestParam(name = "sort", defaultValue = "createTime,desc") String sort,
	        @AuthenticationPrincipal PrincipalDetails principalDetail) {

	    // sort 파라미터를 ','로 분리하여 처리
	    String[] sortParams = sort.split(",");
	 // 'sortParams[1]'은 정렬 방향(예: "asc", "desc")이고, 'sortParams[0]'은 정렬할 필드(예: "createTime")입니다.
	 // 'Sort.Order'를 사용하여 정렬 기준을 설정합니다.
	 // 'Sort.Direction.fromString'을 사용하여 정렬 방향을 'asc' 또는 'desc' 문자열로부터 'Sort.Direction' 객체로 변환합니다.
	 Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(order));

	    PagedResponse<AlarmDTO> alarms = alarmService.getUserAlarms(loggedId, pageable);
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
    public ResponseEntity<?> markAsRead(@PathVariable(name = "alarmId") Long alarmId, @AuthenticationPrincipal PrincipalDetails principalDetail) {
    	
    	 alarmService.readAlarm(alarmId, principalDetail.getMemberEntity().getId());
        return ResponseEntity.ok(alarmId);
    }
    
    
    
//    // 특정 알림 읽음 처리
//    @PostMapping("/read/{alarmId}")
//    public ResponseEntity<?> markAsRead(@PathVariable(name = "alarmId") Long alarmId, @AuthenticationPrincipal PrincipalDetails principalDetail, @RequestBody AlarmDTO alarmDTO) {
//    	alarmService.readAlarm(alarmId, principalDetail.getMemberEntity().getId(), alarmDTO.getMember1Id(), alarmDTO.getMember2Id());
//        return ResponseEntity.ok(alarmId);
//    }

//    // 모든 알림 읽음 처리
    @GetMapping("/read-all")
    public ResponseEntity<AlarmDTO> markAllAsRead(@AuthenticationPrincipal PrincipalDetails principalDetail) {
        alarmService.markAllAsRead(principalDetail.getMemberEntity().getId());
        return ResponseEntity.ok().build();
    }
    
    
    
    
    
    
    

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
    
    
    
 // 읽지 않은 알람 개수 파악
    @GetMapping("/unReadAlarmCount/{loggedId}")
    public ResponseEntity<?> unReadAlarmCount(
            @PathVariable(name = "loggedId") Long loggedId,
            @AuthenticationPrincipal PrincipalDetails principalDetail) {

    	
    	List<AlarmEntity> filteredAlarms = alarmService.unReadAlarmList(loggedId);
    	
        return ResponseEntity.ok(filteredAlarms.size());
    }

    
    //읽지 않은 알람 데이터 파악 
    
    @GetMapping("/unReadAlarmData/{loggedId}")
    public ResponseEntity<?> unReadAlarmData(
            @PathVariable(name = "loggedId") Long loggedId,
            @AuthenticationPrincipal PrincipalDetails principalDetail
          ) {

        // 읽지 않은 알람 목록 가져오기
        List<AlarmEntity> filteredAlarms = alarmService.unReadAlarmList(loggedId);

        // MESSAGE 타입 중 '채팅방 만듬' 액션 필터링
        List<AlarmDTO> filteredAlarms1 = filteredAlarms.stream()
                .filter(alarm -> alarm.getMember1().getId().equals(loggedId) || alarm.getMember2().getId().equals(loggedId)
                        && alarm.getType().equals("MESSAGE") 
                        && alarm.getAction().equals("채팅방 만듬"))
                .map(AlarmEntity::toDTO)
                .collect(Collectors.toList());

        // MESSAGE 타입 중 '송수신' 또는 '나가기' 액션 필터링
        List<AlarmDTO> filteredAlarms2 = filteredAlarms.stream()
                .filter(alarm -> alarm.getMember1().getId().equals(loggedId)  || alarm.getMember2().getId().equals(loggedId)
                        && alarm.getType().equals("MESSAGE") 
                        && (alarm.getAction().equals("송수신") || alarm.getAction().equals("나가기") || alarm.getAction().equals("재입장")) )
                .map(AlarmEntity::toDTO)
                .collect(Collectors.toList());
        
        
        List<AlarmDTO> filteredAlarms3 = filteredAlarms.stream()
                .filter(alarm -> alarm.getMember1().getId().equals(loggedId)  || alarm.getMember2().getId().equals(loggedId)
                        && alarm.getType().equals("TRADE")
                        && alarm.getObject() != null) 
                .map(AlarmEntity::toDTO)
                .collect(Collectors.toList());

        // 두 리스트를 합치기
        List<AlarmDTO> filteredAlarms4 = new ArrayList<>();

      
        filteredAlarms4.addAll(filteredAlarms3);
        filteredAlarms4.addAll(filteredAlarms2);
        filteredAlarms4.addAll(filteredAlarms1);
        return ResponseEntity.ok(filteredAlarms4);
    }

    	//trade 알람 조회하기
    @GetMapping("/findTradeAlarm/{roomId}")
    public ResponseEntity<?> findTradeAlarm (@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails){
    	
    	Long loggedId = principalDetails.getMemberEntity().getId();
    	
    	AlarmDTO responseDTO = alarmService.findTradeAlarmService(roomId, loggedId);	
    	
    	
System.out.println(responseDTO.toString());
    	
		return ResponseEntity.ok(responseDTO);
    }
    
}
