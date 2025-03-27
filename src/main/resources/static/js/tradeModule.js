// 상대방의 수락 확인

//import { loadChatRooms, loadMessages, setUpEnterRoomButton, setUpExitRoomButton } from "./chatModule.js";

//상대방에게 거래 신청
export function enrollTrade1(boardId, loggedId, member2Id, loggedUserId) {
	fetch(`/trade/checkCreateTrade1/${boardId}`, {
		method: "POST",
		headers: { 'Content-Type': 'application/json;charset=utf-8' },
		body: JSON.stringify({
			member1Id: loggedId,
			member2Id: member2Id,
			boardEntityId: boardId,
			accept1: true,
			completed1: false,
			completed2: false
		})
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('응답 실패');
			}
			return response.json();
		})
		.then(data => {
			alert(data.member1Content);
			return data;
			// ✅ loadChatRooms가 끝난 후 버튼 설정 실행
			//			return loadChatRooms(loggedId);
		})
		//		.then(() => {
		//			setUpEnterRoomButton(loggedUserId);
		//			setUpExitRoomButton();
		//		})
		.catch(error => {
			alert("거래 신청을 실패하였습니다");
			console.error(error);
		});
}


//타겟 유저가 해당 거래 신청을 받을지 안 받을지 에 대한 판단
export function enrollTrade2(alarmId, loggedUserId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 수락하시겠습니까")) {			//수락시
		fetch(`/trade/checkCreateTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				booking1: false,
				booking2: false,
				accept1: true,
				accept2: true,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				//return response.json();  // JSON 응답으로 변환
				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}
			})
			.then(async data => {

				//				console.log(data);
				//				alert(data.member2Content);  // 응답 데이터 처리
				//				return data;

				//console.log(data);
				if (typeof data !== 'object' && data === "만료된 정보입니다") {
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					alert(data.member2Content);  // 응답 데이터 처리
					return data;
				}


			})

			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
				console.log(error);
			});
	}


}

//타겟 유저가 해당 거래 신청을 거절 에 대한 판단
export function denyCreateTrade(alarmId, loggedUserId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("거래를 거절하겠습니까? ")) {			//수락시
		fetch(`/trade/checkCreateTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				booking1: false,
				booking2: false,
				accept1: true,
				accept2: false,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				//return response.json();  // JSON 응답으로 변환



				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}


			})
			.then(data => {
				//				console.log(data);
				//				alert(data.member2Content);  // 응답 데이터 처리
				//				return data;


				
				if (typeof data !== 'object' && data === "만료된 정보입니다") {
//					console.log(data);
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					alert(data.member2Content);  // 응답 데이터 처리
					return data;
				}





				//				return loadChatRooms(loggedId);
			})
			//			.then(() => {
			//				setUpEnterRoomButton(loggedUserId);
			//				setUpExitRoomButton();
			//			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
//				console.log(error);
			});
	}


}


// 거래 완료 여부 상태
// ('보드 관리자 ('거래완료를 선수신 받을 쪽'') =  로그인 유저' 기준, member2를 기준으로 하므로 isCompleted2를 기준으로 하였다 )
// ('상대편(거래완료를 후수신 받을 쪽) =  로그인 유저' 기준, member1를 기준으로 하므로 isCompleted1를 기준으로 하였다 )
export function CompleteTrade(tradeId, isMember) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("상대방에게 거래완료를 요청하시겠습니까? ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)")) {			//수락시
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
				completed1: isMember === "isMember1" ? true : undefined,
				//				 completed2: isMember === "isMember1"?  true : undefined,
				completed2: isMember === "isMember1" || isMember === "isMember2" ? true : undefined
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				//	return response.json();  // JSON 응답으로 변환

				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}

			})
			.then(data => {



				if (typeof data !== 'object' && data === "만료된 정보입니다") {
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					if (data.isCompleted1 !== null && data.isCompleted2 !== null &&data.isCompleted1 === true && data.isCompleted2 === true) {
						alert("거래상태가 최종완료되었습니다");
					} else {
						alert("거래가 완료되었습니다");  // 응답 데이터 처리
					}
				}








				//				console.log(data);
				//				if (data.isCompleted1 === true && data.isCompleted2 === true) {
				//					alert("거래상태가 최종완료되었습니다");
				//				} else {
				//					alert("거래가 완료되었습니다  ( ※ 상대방도 거래를 완료해야 거래가 완료됩니다)");  // 응답 데이터 처리
				//				}

			})
			.catch(error => {  // 오류 처리
				alert("데이터 조회를 실패하였습니다");
//				console.log(error);
			});
	}


}







//타겟 유저가 해당 거래 취소을 거절 에 대한 판단
export function CancelTrade(tradeId, loggedId) {  // 로그인유저 : member1, 타겟 유저 : member2

	//		const member2Id = Number(document.getElementById("memberid").value);  // 다른 멤버의 ID
	if (confirm("거래를 취소하겠습니까? ")) {			//수락시
		fetch(`/trade/cancelTrade/${tradeId}`, {  // 취소 API
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				id: Number(tradeId),
				member1Id: Number(loggedId),
				//								member2Id: member2Id,
				//								boardEntityId: boardId,
				booking1: false,
				booking2: false,
				accept1: false,
				accept2: false,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				return response.text();  //text 응답으로 변환
			})
			.then(data => {
//				console.log(data);
				alert(data);  // 응답 데이터 처리
				return data;
				//				return loadChatRooms(loggedId);
			})
			//			.then(() => {
			//				setUpEnterRoomButton(loggedUserId);
			//				setUpExitRoomButton();
			//			})
			.catch(error => {  // 오류 처리
				alert("거래 취소를 실패하였습니다");
//				console.log(error);
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



//상대방에게 예약 신청
export function bookTrade1(boardId, loggedId, member2Id, loggedUserId) {
	fetch(`/trade/checkBookTrade1/${boardId}`, {
		method: "POST",
		headers: { 'Content-Type': 'application/json;charset=utf-8' },
		body: JSON.stringify({
			member1Id: Number(loggedId),
			member2Id: Number(member2Id),
			boardEntityId: Number(boardId),
			accept1: false,
			accept2: false,
			booking1: true,
			booking2: false,
			completed1: false,
			completed2: false
		})
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('응답 실패');
			}
			return response.json();
		})
		.then(data => {
//			console.log(data);
			alert(data.member1Content);

			// ✅ loadChatRooms가 끝난 후 버튼 설정 실행
			//			return loadChatRooms(loggedId);
		})
		//		.then(() => {
		//			setUpEnterRoomButton(loggedUserId);
		//			setUpExitRoomButton();
		//		})
		.catch(error => {
			alert("거래 신청을 실패하였습니다");
			console.error(error);
		});
}




//타겟 유저가 해당 거래 신청을 받을지 안 받을지 에 대한 판단
export function bookTrade2(alarmId, loggedUserId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("예약을 수락하시겠습니까")) {			//수락시
		fetch(`/trade/checkBookTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				accept1: false,
				accept2: false,
				booking1: true,
				booking2: true,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				//return response.json();  // JSON 응답으로 변환

				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}
			})
			.then(data => {
				
				
				
				
				if (typeof data !== 'object' && data === "만료된 정보입니다") {
//					console.log(data);
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					alert(data.member1Content);  // 응답 데이터 처리
//					return data;
				}
				
				
				
				
				
				
//				console.log(data);
//				alert(data.member1Content);  // 응답 데이터 처리


				//				return loadChatRooms(loggedId);
			})
			//			.then(() => {
			//				setUpEnterRoomButton(loggedUserId);
			//				setUpExitRoomButton();
			//			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
//				console.log(error);
			});
	}


}

//타겟 유저가 해당 거래 신청을 거절 에 대한 판단
export function denyBookTrade(alarmId, loggedUserId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("예약을 거절하겠습니까? ")) {			//수락시
		fetch(`/trade/checkBookTrade2/${alarmId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				accept1: false,
				accept2: false,
				booking1: true,
				booking2: false,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
				//	return response.json();  // JSON 응답으로 변환
				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}
			})
			.then(data => {
				
				
				
						
				if (typeof data !== 'object' && data === "만료된 정보입니다") {
//					console.log(data);
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					alert(data.member2Content);  // 응답 데이터 처리
//					return data;
				}
				
				
				
				
				
				
				
				
				
				
				
				
//				console.log(data);
//				alert(data.member2Content);  // 응답 데이터 처리
				
				
				//				return loadChatRooms(loggedId);
			})
			//			.then(() => {
			//				setUpEnterRoomButton(loggedUserId);
			//				setUpExitRoomButton();
			//			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
//				console.log(error);
			});
	}


}


//보드 관리자가 예약 중 -> 거래 중으로 변환
export function changeBookTrade(roomId, loggedUserId) {  // 로그인유저 : member1, 타겟 유저 : member2

	if (confirm("예약 상태를 거래 중으로 변경하겠습니까? ")) {			//수락시
		fetch(`/trade/changeBookTrade2/${roomId}`, {  // 신청 알람을 만든다
			method: "POST",
			headers: { 'Content-Type': 'application/json;charset=utf-8' },
			body: JSON.stringify({
				//				id: alarmId,
				//				member1Id: loggedId,
				//				member2Id: member2Id,
				//				boardEntityId: boardId,
				accept1: true,
				accept2: true,
				booking1: false,
				booking2: false,
				completed1: false,
				completed2: false
			})
		})
			.then(response => {
				if (!response.ok) {
					// 응답이 실패했을 경우
					throw new Error('응답 실패');
				}
			//	return response.json();  // JSON 응답으로 변환
			
				const contentType = response.headers.get("Content-Type");

				if (contentType && contentType.includes("application/json")) {
					return response.json();  // Parse as JSON
				} else {
					return response.text();  // Parse as text
				}
			
			})
			.then(data => {
				
				
				
				if (typeof data !== 'object' && data === "만료된 정보입니다") {
//					console.log(data);
					alert(data);
					return;
				}
				if (typeof data === 'object') {
//					console.log(data);
					alert(data.member1Content);  // 응답 데이터 처리
//					return data;
				}
				
				
				
				
				
				
				
				
				
//				console.log(data);
//				alert(data.member2Content);  // 응답 데이터 처리
				
				
				
				
				
				
				
				
				//				return loadChatRooms(loggedId);
			})
			//			.then(() => {
			//				setUpEnterRoomButton(loggedUserId);
			//				setUpExitRoomButton();
			//			})
			.catch(error => {  // 오류 처리
				alert("거래 신청을 실패하였습니다");
//				console.log(error);
			});
	}


}









document.addEventListener('DOMContentLoaded', function() {
	// boardId를 동적으로 참조하기 위해 각 버튼에 이벤트 추가
	const buttons = document.querySelectorAll('[id^="enroll-Buy-"]'); // 'enroll-Buy-'로 시작하는 id를 가진 모든 버튼 선택
	const loggedUserId = document.getElementById("loggedUserId")?.value;
	buttons.forEach(button => {
		button.addEventListener('click', () => {
			// 'id' 속성에서 boardId를 추출

			const boardId = Number(button.getAttribute('data-TradeBuyBoardId'));  // data-updateBoardId 속성에서 boardId 추출
			const loggedId = Number(document.getElementById("loggedId").value);  // 로그된 사용자의 ID
			const member2Id = Number(document.getElementById("memberid").value);  // 다른 멤버의 ID
//			console.log("거래 테스트 ", boardId, loggedId, member2Id);
			// enrollTrade1 함수 호출
			enrollTrade1(boardId, loggedId, member2Id, loggedUserId)
			//			  loadChatRooms(loggedId);
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


	document.addEventListener('click', async function(event) {									//예약 신청
		if (event.target && (event.target.id.startsWith("enroll-Book1-"))) {
			const loggedId = document.getElementById("loggedId").value;
			const member2Id = Number(document.getElementById("memberid").value);  // 다른 멤버의 ID
			const boardId = event.target.id.replace("enroll-Book1-", "");
//			console.log("예약 신청 테스트 ", boardId);
//			console.log("상대방 아이디 :  " + member2Id);
//			console.log("내 아이디 :  " + loggedId);
//			console.log("내 아이디 :  " + loggedUserId);
			bookTrade1(Number(boardId), loggedId, member2Id, loggedUserId);
			//        loadChatRooms(loggedId);
		}
		if (event.target && (event.target.id.startsWith("enroll-Book2-"))) { //예약 확인 및 추가
			const alarmId = event.target.id.replace("enroll-Book2-", "");
//			console.log("예약 승인 테스트 ", alarmId);
			bookTrade2(Number(alarmId), loggedUserId);
			//        loadChatRooms(loggedId);
		}

		if (event.target && (event.target.id.startsWith("deny-enroll-Book2-"))) { 	//예약 거절
			const alarmId = event.target.id.replace("deny-enroll-Book2-", "");
//			console.log("예약 거절 테스트 ", alarmId);
			denyBookTrade(Number(alarmId), loggedUserId);
			//        loadChatRooms(loggedId);
		}
		if (event.target && (event.target.id.startsWith("change-enroll-Book2-"))) { 	//예약 -> 거래 중 변경 
			const boardId = event.target.id.replace("change-enroll-Book2-", "");
			const roomResponse = await fetch(`/chat/findRoomByBoardId/${boardId}`);
			const room = await roomResponse.json();
//			console.log("예약 -> 거래 중 변경 테스트 ", boardId);
//			console.log("예약 -> 거래 중 변경 테스트2 " + room.id);
			changeBookTrade(Number(room.id), loggedUserId);
			//        loadChatRooms(loggedId);
		}


		if (event.target && (event.target.id.startsWith("agreeMember2-"))) {
			const alarmId = event.target.id.replace("agreeMember2-", "");
//			console.log("거래승인 테스트 ", alarmId);
			enrollTrade2(Number(alarmId), loggedUserId);
			//        loadChatRooms(loggedId);
		}

		if (event.target && (event.target.id.startsWith("denyMember2-"))) {
			const alarmId = event.target.id.replace("denyMember2-", "");
//			console.log("거래거절 테스트 ", alarmId);
			denyCreateTrade(Number(alarmId), loggedUserId);
			//         loadChatRooms(loggedId);
		}

		if (event.target && (event.target.id.startsWith("complete2-Sell-"))) {
			const tradeId = event.target.id.replace("complete2-Sell-", "");
//			console.log("거래완료 테스트2 ", tradeId);

			CompleteTrade(tradeId, "isMember2");
			//      loadChatRooms(loggedId);
		}


		if (event.target && (event.target.id.startsWith("complete1-Sell-"))) {
			const tradeId = event.target.id.replace("complete1-Sell-", "");
//			console.log("거래완료 테스트1 ", tradeId);

			CompleteTrade(tradeId, "isMember1");
			//      loadChatRooms(loggedId);
		}

		if (event.target && (event.target.id.startsWith("cancel-trade-"))) {
			const tradeId = event.target.id.replace("cancel-trade-", "");
//			console.log("거래취소 테스트1 ", tradeId);

			CancelTrade(tradeId, loggedId);
			//      loadChatRooms(loggedId);
		}

	});







});
