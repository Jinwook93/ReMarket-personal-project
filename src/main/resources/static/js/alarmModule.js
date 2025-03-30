//import { loadChatRooms, loadMessages, setUpEnterRoomButton, setUpExitRoomButton } from "./chatModule.js";
//import { loadPage } from "./headerUI.js";
//import { prevState } from "./state.js";

export function toggleAlarmList() {
	const alarmButton = document.getElementById("alarmButton");
	const alarmList = document.querySelector(".alarmList");
	const markAllAsReadCheckbox = document.getElementById("markAllAsReadCheckbox");	//체크박스

	if (alarmButton && alarmList) {
		alarmButton.addEventListener("click", (event) => {
			event.preventDefault();
			alarmList.style.display = alarmList.style.display === "none" ? "block" : "none";
			if(alarmList.style.display === "none"){
				markAllAsReadCheckbox.checked = "false";  // 체크 해제
			}
		});
	}
}

//export async function checkUserAlarmCount(loggedId) {
//	try {
//		const alarmResponse = await fetch(`/alarm/list/findCount/${loggedId}`);
//		const alarmCount = await alarmResponse.json();
//		return alarmCount;
//	} catch (error) {
//		console.error("알람 데이터를 불러오는 중 오류 발생:", error);
//	}
//}
export async function checkUserAlarmCount(loggedId) {
	try {
		const alarmResponse = await fetch(`/alarm/unReadAlarmCount/${loggedId}`);
		const alarmCount_text = await alarmResponse.text();
		const unReadAlarmCount = Number(alarmCount_text);
		//		return alarmCount;
		const unReadAlarmCountButton = document.getElementById("unReadAlarmCountButton");

		if (unReadAlarmCount > 0 && unReadAlarmCountButton) {
			unReadAlarmCountButton.style.display = "block";
			unReadAlarmCountButton.innerText = unReadAlarmCount;
		} else {
			unReadAlarmCountButton.style.display = "none";
			unReadAlarmCountButton.innerText = unReadAlarmCount;
		}
		return unReadAlarmCount;
	} catch (error) {
		console.error("알람 데이터를 불러오는 중 오류 발생:", error);
	}
}



//setInterval 사용
//let prevState = null; // 🔹 전역 변수로 이전 상태 저장
//
//export async function checkUserAlarmData(loggedId) {
//  try {
//    const alarmResponse = await fetch(`/alarm/unReadAlarmData/${loggedId}`);
//    const datas = await alarmResponse.json(); // 📌 읽지 않은 알람 목록
//    const loggedUserId = document.getElementById("loggedUserId").value;
//    const alarmListBody = document.getElementById("alarmListBody");
//    let currentPage = alarmListBody.getAttribute("data-current-page") || 0;
//
//    // 📌 데이터 변경 감지 (이전 상태와 비교)
////    if (prevState && JSON.stringify(prevState) === JSON.stringify(datas)) {
////		console.log(prevState);
////      return; // ✅ 동일한 데이터면 렌더링 X
////    }
////    prevState = datas; // 🔹 상태 업데이트
//	
//console.log("테스트");
//	
//    // 📌 알람 카운트 업데이트
//    await checkUserAlarmCount(loggedId);
//
//    // 📌 페이지 새로 로딩
//    await loadPage(currentPage, loggedId);
//
//    // 📌 채팅 관련 알람 처리
//    for (const data of datas) {
//      if (data.type === "MESSAGE") {
//        await loadChatRooms(loggedId);
//        setUpEnterRoomButton(loggedUserId);
//        setUpExitRoomButton();
//
//        if (data.action === "송수신" || data.action === "나가기") {
//          const room = await fetch(`/chat/findRoom/${Number(data.object)}`).then(res => res.json());
//
//          // ✅ 수신자의 메시지 로딩
//          if (Number(data.member1Id) === Number(loggedId)) {
//            loadMessages(Number(data.object), room.messageIndex1, room.recentExitedmemberId);
//          } else if (Number(data.member2Id) === Number(loggedId)) {
//            loadMessages(Number(room.id), room.messageIndex2, room.recentExitedmemberId);
//          }
//        }
//      }
//    }
//  } catch (error) {
//    console.error("알람 데이터를 불러오는 중 오류 발생:", error);
//  }
//}












//export async function checkUserAlarmList(loggedId) {
//	try {
//		const alarmListResponse = await fetch(`/alarm/list/${loggedId}`);
//		const alarmList = await alarmListResponse.json();
//		return alarmList;
//	} catch (error) {
//		console.error("알람 목록을 불러오는 중 오류 발생:", error);
//	}
//}

export async function checkUserAlarmList(loggedId, page = 0, size = 5) {
	try {
		const response = await fetch(`/alarm/list/${loggedId}?page=${page}&size=${size}`);
		const data = await response.json();
		//		console.log(data);
		return data; // { content, totalPages, totalElements, number, size }
	} catch (error) {
		console.error("알람 목록을 불러오는 중 오류 발생:", error);
	}
}

//
//export function markAsRead(alarmId) {
//	console.log(`알람 ${alarmId} 읽음 처리`);
//}



// 🔹 로그인 성공 알람 요청

export async function loginAlarm(responseData) {


	await fetch(`/alarm/loginSuccess/${responseData}`, {
		method: "POST",
		headers: { 'Content-Type': 'application/json' },
		// 		            credentials: "include", // ✅ 세션 유지 필수
		body: JSON.stringify({ type: "LOGIN" })
	});

};

// 🔹 게시판 등록 알람 요청
export async function postBoardAlarm(responseData) {


	await fetch(`/alarm/loginSuccess/${responseData}`, {
		method: "POST",
		headers: { 'Content-Type': 'application/json' },
		// 		            credentials: "include", // ✅ 세션 유지 필수
		body: JSON.stringify({ type: "LOGIN" })
	});

};





