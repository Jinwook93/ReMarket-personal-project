// 상대방의 수락 확인

export function enrollTrade1(boardId, loggedId, member2Id) {  // 로그인유저 : member1, 타겟 유저 : member2
	fetch(`/trade/checkCreateTrade1/${boardId}`, {  // 신청 알람을 만든다
		method: "POST",
		headers: { 'Content-Type': 'application/json;charset=utf-8' },
		body: JSON.stringify({
			member1Id: loggedId,
			member2Id: member2Id,
			boardEntityId: boardId,
			accept1: true
		})
	})
		.then(response => {
			if (!response.ok) {
				// 응답이 실패했을 경우
				throw new Error('응답 실패');
			}
			return response.json();  // JSON 응답으로 변환
		})
		.then(data => {
			console.log(data);
			alert(data.member1Content);  // 응답 데이터 처리
		})
		.catch(error => {  // 오류 처리
			alert("거래 신청을 실패하였습니다");
			console.log(error);
		});
}

//타겟 유저가 해당 거래 신청을 받을지 안 받을지 에 대한 판단
export function enrollTrade2(alarmId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 수락하시겠습니까")) {			//수락시
		fetch(`/trade/checkCreateTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				accept1: true,
				accept2: true
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				return response.json();  // JSON 응답으로 변환
			})
			.then(data => {
				console.log(data);
//				alert("거래가 성사되었습니다");
				alert(data.member2Content);  // 응답 데이터 처리
			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
				console.log(error);
			});
	}


}

//타겟 유저가 해당 거래 신청을 거절 에 대한 판단
export function denyCreateTrade (alarmId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 거절하겠습니까? ")) {			//수락시
		fetch(`/trade/checkCreateTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				accept1: true,
				accept2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				return response.json();  // JSON 응답으로 변환
			})
			.then(data => {
				console.log(data);
				alert(data.member2Content);  // 응답 데이터 처리
			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
				console.log(error);
			});
	}


}


// 거래 완료 여부 상태
// ('보드 관리자 ('거래완료를 선수신 받을 쪽'') =  로그인 유저' 기준, member2를 기준으로 하므로 isCompleted2를 기준으로 하였다 )
// ('상대편(거래완료를 후수신 받을 쪽) =  로그인 유저' 기준, member1를 기준으로 하므로 isCompleted1를 기준으로 하였다 )
export function CompleteTrade (tradeId, isMember) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 완료하시겠습니까? ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)")) {			//수락시
		fetch(`/trade/completeTrade/${tradeId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
//				completed1 : isCompleted1,
//				completed2: true,
				 completed1: isMember=== "isMember1" ? true : undefined,
//				 completed2: isMember === "isMember1"?  true : undefined,
   				 completed2: isMember === "isMember1" ||isMember=== "isMember2"? true : undefined		
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				return response.json();  // JSON 응답으로 변환
			})
			.then(data => {
				console.log(data);
				if(data.isCompleted1 === true && data.isCompleted2 === true){
					alert("거래상태가 최종완료되었습니다");
				}else{
				alert("거래가 완료되었습니다  ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)");  // 응답 데이터 처리
				}
			
			})
			.catch(error => {  // 오류 처리
				alert("데이터 조회를 실패하였습니다");
				console.log(error);
			});
	}


}




//// 거래 완료 여부 상태 ('상대 =  로그인 유저' 기준  )
//export function CompleteTrade1 (tradeId) {  // 로그인유저 : member1, 타겟 유저 : member2
//
//	if (confirm("거래를 완료하시겠습니까? ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)")) {			//수락시
//		fetch(`/trade/completeTrade/${tradeId}`, {  // 신청 알람을 만든다
//			method: "POST",
//			headers: { 'Content-Type': 'application/json;charset=utf-8' },
//			body: JSON.stringify({
//				//				id: alarmId,
//				//				member1Id: loggedId,
//				//				member2Id: member2Id,
//				//				boardEntityId: boardId,
//				completed1 : true
////				completed2: isCompleted2				
//			})
//		})
//			.then(response => {
//				if (!response.ok) {
//					// 응답이 실패했을 경우
//					throw new Error('응답 실패');
//				}
//				return response.json();  // JSON 응답으로 변환
//			})
//			.then(data => {
//				console.log(data);
//				if(data.isCompleted1 === true && data.isCompleted2 === true){
//					alert("거래상태가 최종완료되었습니다");
//				}else{
//				alert("거래가 완료되었습니다  ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)");  // 응답 데이터 처리
//				}
//			
//			})
//			.catch(error => {  // 오류 처리
//				alert("데이터 조회를 실패하였습니다");
//				console.log(error);
//			});
//	}
//
//
//}





















document.addEventListener('DOMContentLoaded', function() {
	// boardId를 동적으로 참조하기 위해 각 버튼에 이벤트 추가
	const buttons = document.querySelectorAll('[id^="enroll-Buy-"]'); // 'enroll-Buy-'로 시작하는 id를 가진 모든 버튼 선택

	buttons.forEach(button => {
		button.addEventListener('click', () => {
			// 'id' 속성에서 boardId를 추출

			const boardId = Number(button.getAttribute('data-TradeBuyBoardId'));  // data-updateBoardId 속성에서 boardId 추출
			const loggedId = Number(document.getElementById("loggedId").value);  // 로그된 사용자의 ID
			const member2Id = Number(document.getElementById("memberid").value);  // 다른 멤버의 ID
			console.log("거래 태스트 ", boardId, loggedId, member2Id);
			// enrollTrade1 함수 호출
			enrollTrade1(boardId, loggedId, member2Id);
		});
	});


//	//!!!!!!!innerHTML 처럼 동적으로 불러온 태그들은 querySelectorAll이 인식을 못할 수 있음
//	const agreeMember2buttons = document.querySelectorAll('[id^="agreeMember2-"]'); // agreeMember2-${alarm.id}에 해당하는 모든 버튼 선택
//
//	agreeMember2buttons.forEach(button => {
//		button.addEventListener('click', () => {
//			// 'id' 속성에서 boardId를 추출
//
//			const alarmId = button.id.replace("agreeMember2-", "");		//agreeMember2-${alarm.id} 에서 agreeMember2- 제거
//			console.log("거래 태스트 ", alarmId);
//			// enrollTrade1 함수 호출
//			enrollTrade2(Number(alarmId));
//		});
//	});

document.addEventListener('click', function(event) {
    if (event.target && event.target.id.startsWith("agreeMember2-")) {
        const alarmId = event.target.id.replace("agreeMember2-", ""); 
        console.log("거래승인 테스트 ", alarmId);
        enrollTrade2(Number(alarmId));
    }
});

document.addEventListener('click', function(event) {
    if (event.target && event.target.id.startsWith("denyMember2-")) {
        const alarmId = event.target.id.replace("denyMember2-", ""); 
        console.log("거래거절 테스트 ", alarmId);
        denyCreateTrade(Number(alarmId));
    }
});

document.addEventListener('click', function(event) {
    if (event.target && event.target.id.startsWith("complete2-Sell-")) {
        const tradeId = event.target.id.replace("complete2-Sell-", ""); 
        console.log("거래완료 테스트2 ", tradeId);

     CompleteTrade (tradeId,"isMember2")
    }
});

document.addEventListener('click', function(event) {
    if (event.target && event.target.id.startsWith("complete1-Sell-")) {
        const tradeId = event.target.id.replace("complete1-Sell-", ""); 
        console.log("거래완료 테스트1 ", tradeId);

     CompleteTrade (tradeId,"isMember1")
    }
});







});
