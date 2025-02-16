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
export function denyCreateTrade(alarmId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 거절하시겠습니까")) {			//수락시
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





});
