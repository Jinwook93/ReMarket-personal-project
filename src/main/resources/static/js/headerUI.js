import { checkUnReadMessageCount, checkUnReadMessageCount2, loadChatRooms, loadMessages, reloadDetails, searchMessage, setUpEnterRoomButton, setUpExitRoomButton, toggleChattingRoomList } from './chatModule.js';
import { toggleAlarmList, checkUserAlarmCount, checkUserAlarmList } from './alarmModule.js';
import { formatDate } from "./formatDate.js";
//import { enrollTrade2 } from "./tradeModule.js";


let prevState = null;
let alarmCountInterval = null;
document.addEventListener("DOMContentLoaded", async () => {
	const isLoggedIn = document.getElementById("isLoggedIn")?.value;
	const loggedId = document.getElementById("loggedId")?.value;
	const loggedUserId = document.getElementById("loggedUserId")?.value;

	if (isLoggedIn === "true") {
		await checkUserAlarmCount(loggedId);
		await unReadMessageCount(loggedId);
		//    await loadChatRooms(loggedId);
		//    setUpEnterRoomButton(loggedUserId);
		//    setUpExitRoomButton();

		if (alarmCountInterval !== null) {
			clearInterval(alarmCountInterval);
		}

		// ✅ 5초마다 알람 데이터 확인 (중복 실행 방지)
		alarmCountInterval = setInterval(async () => {
			await checkUserAlarmData(loggedId);
		}, 5000);
	}
	else {
		prevState = null;
	}

	toggleChattingRoomList();
	toggleAlarmList();
});





// 🔹 검색된 메시지를 저장할 전역 변수
let matchedMessages = [];
let currentIndex = -1;

document.addEventListener("click", (event) => {
	const targetId = event.target.id;

	if (targetId.startsWith("search-button-")) {
		handleSearch(event);
	} else if (targetId.startsWith("search-prev-button-")) {
		//		console.log("Prev 버튼 클릭 감지됨"); // 디버깅
		handlePrev(event);
	} else if (targetId.startsWith("search-next-button-")) {
		//		console.log("next 버튼 클릭 감지됨"); // 디버깅
		handleNext(event);
	} else if (targetId.startsWith("close-search-message-") || targetId.startsWith("data-search-button-")) {
		//		console.log("메시지 닫기 버튼 클릭 감지됨"); // 디버깅
		toggleSearchMessageContainer(event);
	}

});

// 🔍 **검색 버튼 클릭 시 실행**
function handleSearch(event) {
	const roomId = event.target.id.split('-')[2];
	const searchBox = document.getElementById(`search-box-${roomId}`);
	const chatBox = document.getElementById(`chat-box-${roomId}`);
	//    console.log("roomId:", roomId, "chatBox:", chatBox);
	const messages = chatBox.querySelectorAll(".message-item");

	const prevButton = document.getElementById(`search-prev-button-${roomId}`);
	const nextButton = document.getElementById(`search-next-button-${roomId}`);



	const searchTerm = searchBox.value.trim().toLowerCase();
	matchedMessages = []; // 검색될 때마다 초기화
	currentIndex = -1;

	messages.forEach(message => {
		const sendTimeElem = message.querySelector(".send-time"); // 날짜 요소 찾기
		let messageText = message.textContent.trim().toLowerCase();

		if (sendTimeElem) {
			const sendTimeText = sendTimeElem.textContent.trim().toLowerCase();
			messageText = messageText.replace(sendTimeText, ""); // 날짜 부분 제거
		}

		if (messageText.includes(searchTerm)) {
			matchedMessages.push(message);
			message.style.backgroundColor = "#ffff99"; // 배경 강조
		} else if (!messageText.includes(searchTerm) || searchTerm === "") {
			message.style.backgroundColor = ""; // 원래 배경으로
		}
	});

	if (matchedMessages.length > 0) {
		currentIndex = 0;
		scrollToMessage(matchedMessages[currentIndex], chatBox);
		prevButton.style.display = "block";
		nextButton.style.display = "block";
	} else {
		prevButton.style.display = "none";
		nextButton.style.display = "none";
		alert("검색 결과가 없습니다.");
	}
}

// ◀️ **이전 메시지 버튼 클릭 시 실행**
function handlePrev(event) {
	if (matchedMessages.length === 0) return;

	const roomId = event.target.id.split('-')[3];
	const chatBox = document.getElementById(`chat-box-${roomId}`);
	//	console.log("roomId:", roomId, "chatBox:", chatBox);
	currentIndex = (currentIndex - 1 + matchedMessages.length) % matchedMessages.length;
	scrollToMessage(matchedMessages[currentIndex], chatBox);
}

// ▶️ **다음 메시지 버튼 클릭 시 실행**
function handleNext(event) {
	if (matchedMessages.length === 0) return;

	const roomId = event.target.id.split('-')[3];
	const chatBox = document.getElementById(`chat-box-${roomId}`);
	//	console.log("roomId:", roomId, "chatBox:", chatBox);
	currentIndex = (currentIndex + 1) % matchedMessages.length;
	scrollToMessage(matchedMessages[currentIndex], chatBox);
}

// 📌 **메시지를 중앙에 위치시키는 함수**
function scrollToMessage(message, chatBox) {
	const chatBoxHeight = chatBox.clientHeight;
	const messagePosition = message.offsetTop - chatBox.offsetTop;
	chatBox.scrollTop = messagePosition - chatBoxHeight / 2; // 중앙 정렬
}

function toggleSearchMessageContainer(event) {
	const roomId = event.target.id.split('-')[3]; // roomId 추출
	const searchContainer = document.getElementById(`search-container-${roomId}`);

	// display가 flex일 때 none으로, none일 때 flex로 토글
	if (searchContainer.style.display === "flex") {
		searchContainer.style.display = "none"; // 숨기기
	} else {
		searchContainer.style.display = "flex"; // 보이기

	}



}
























document.getElementById("chatSearch").addEventListener("input", searchChat);

async function searchChat() {
	let input = document.getElementById("chatSearch").value.trim().toLowerCase();
	let searchResultText = document.getElementById("searchResultText");
	let messagedata = document.getElementById("messagedata");
	let chatRooms = document.querySelectorAll("#chattingRoomListBody tr");

	let isVisible = false; 	//검색 결과 창 태그

	const loggedUserId = document.getElementById("loggedUserId")?.value;
	// 채팅방 필터링
	chatRooms.forEach(row => {
		let roomName = row.textContent.toLowerCase();
		row.style.display = roomName.includes(input) ? "" : "none";
		if (row.style.display !== "none") {
			isVisible = true;
		}

	});

	// 검색 결과 텍스트 표시 여부
	searchResultText.style.display = input && isVisible ? "block" : "none";

	// 메시지 검색 및 결과 표시
	if (input) {
		const data = await searchMessage(input);
		if (data && data.length > 0) {
			// 비동기적으로 방 정보를 가져오기
			const roomPromises = data.map(async (msg) => {
				const roomResponse = await fetch(`/chat/findRoom/${msg.roomId}`);
				const room = await roomResponse.json();
				return { msg, room }; // msg와 room 정보를 함께 반환
			});

			// 모든 방 정보를 가져오기 완료 후 처리
			const roomsData = await Promise.all(roomPromises);

			messagedata.innerHTML = `
                <div>
                    <h3>메시지 검색 결과</h3>
                    <div>
                        ${roomsData.map(({ msg, room }) => `
                            <div class="message" id="msg-${msg.id}" style ="display:flex;">
                            		<div>
                            		<img src = ${msg.profileImageUrl1} width="50", height="50">
                            	 ${msg.senderUserId}
                            	 </div>
                                	<div  style="width:60%;">
                                 <p> ${msg.messageContent}   </p>
                               		<div class = "date-text"> ${msg.sendTime} </div>
                                    </div>
                                    <div>
                                    <button class="enterChat"
                                        data-search-room-id="${msg.roomId}"
                                        data-search-title="${room.title}"
                                        data-search-userid="${room.member2UserId}"
                                        data-search-message-id = "${msg.id}"
                                       >
                                            <div style="display: flex; align-items: center;">
                                                <img src="/icon/messageIcon.png" width="15" height="15" style="margin-right: 5px;">
                                                채팅하기
                                            </div>
                                    </button>
                                    </div>
                            
                            </div>
                        `).join("")}
                    </div>
                </div>
            `;
			messagedata.style.display = "block";
			setUpEnterRoomButton(loggedUserId);
			//           document.getElementById(`toggleDetails-${roomId}`).addEventListener('click', () => {
			//			const details = document.getElementById(`details-${roomId}`);
			//			details.style.display = (details.style.display === 'none' || details.style.display === '') ? 'block' : 'none';
			//		});
			roomsData.forEach(({ msg, room }) => {
				const roomId = msg.roomId; // msg에서 roomId 가져오기
				const toggleButton = document.getElementById(`toggleDetails-${roomId}`);
				const detailsSection = document.getElementById(`details-${roomId}`);

				if (toggleButton && detailsSection) {
					toggleButton.addEventListener('click', () => {
						detailsSection.style.display =
							(detailsSection.style.display === 'none' || detailsSection.style.display === '')
								? 'block' : 'none';
					});
				}
			});



		} else {
			//            messagedata.innerHTML = "<p>메시지 검색 결과가 없습니다</p>";
			messagedata.innerHTML = "";
			messagedata.style.display = "block";
		}
	} else {
		messagedata.style.display = "none";
	}
}






async function checkUserAlarmData(loggedId) {
	try {
		const loggedUserId = document.getElementById("loggedUserId").value;
		const alarmResponse = await fetch(`/alarm/unReadAlarmData/${loggedId}`);
		const datas = await alarmResponse.json(); // 📌 읽지 않은 알람 목록

		let currentPage = alarmListBody.getAttribute("data-current-page") || 0;



		//데이터가 아무것도 없을 경우
		if (!datas || !Array.isArray(datas) || datas.length === 0) {
			return;
		}


		// ✅ 같은 데이터라면 중복 호출 방지 (이전 상태 비교)
		if (prevState && JSON.stringify(prevState) === JSON.stringify(datas)) {
			//		      console.log("동일한 알람 데이터이므로 렌더링을 건너뜁니다.");
			return;
		}





		// 📌 알람 카운트 업데이트
		await checkUserAlarmCount(loggedId);

		await unReadMessageCount(loggedId);
		// 📌 페이지 새로 로딩
		await loadPage(currentPage, loggedId);



		if (prevState === null) {
			prevState = datas;
		}

		if (prevState && JSON.stringify(prevState) !== JSON.stringify(datas)) {
			prevState = datas;
		}




		// 📌 로드한 방 ID를 저장하는 지역 변수 (초기화)
		const loadedRooms = new Set();

		//		let loadChatRoomsCount = 1;
		//		let previousChatRoomHTML = ""; // 🔹 이전 채팅방 목록 HTML 저장 변수
		let chatRoomsUpdated1 = false;  // 🔹 중복 실행 방지 변수
		let chatRoomsUpdated2 = false;  // 🔹 중복 실행 방지 변수
		let room = null;
		let roomId = null;
		for (const data of datas) {
			if (data.type === "MESSAGE" && data.object != null) {
				room = await fetch(`/chat/findRoom/${Number(data.object)}`).then(res => res.json()); //MESSAGE일 경우
				roomId = Number(room.id);
			}

			else if (data.type === "TRADE" && data.object != null) {
				room = await fetch(`/chat/findRoomByBoardIdAndMemberId/${Number(data.object)}`, {
					method: 'POST',
					headers: { 'Content-Type': "application/json;charset=utf-8" },
					body: JSON.stringify({
						member1Id: data.member1Id,
						member2Id: data.member2Id
					})

				}).then(res => res.json()); //TRADE일 경우


				roomId = Number(room.id);
			}


			//		ㄴ	console.log(room);

			//				updateChatRoomOrder(roomId);

			if (Number(data.member1Id) === Number(loggedId) && !chatRoomsUpdated1) {

				await loadChatRooms(loggedId);
				setUpEnterRoomButton(loggedUserId);
				setUpExitRoomButton();
				chatRoomsUpdated1 = true;
			}
			// 🔹 상대방이 메시지를 보낸 경우에만 loadChatRooms 실행 (단, 한 번만 실행)
			else if (Number(data.member2Id) === Number(loggedId) && !chatRoomsUpdated2) {
				const chattingRoomListBody = document.getElementById("chattingRoomListBody");

				chattingRoomListBody.innerHTML = ``;
				await loadChatRooms(loggedId);
				setUpEnterRoomButton(loggedUserId);
				setUpExitRoomButton();

				chatRoomsUpdated2 = true; // ✅ 중복 실행 방지
				// ✅ 새로운 메시지가 도착한 방을 최상단으로딩 이동		
				//					updateChatRoomOrder(data.id);				//채팅방 재입장 시 나가기 직전 메시지 시간까지 계산됨 (버그)
			}


			// ✅ 메시지를 로드한 방 ID를 저장하여 중복 호출 방지
			if (!loadedRooms.has(roomId) && room !== null) {


				if ((data.type === "MESSAGE" || data.type === "TRADE")) {
					if ((Number(data.member1Id) === Number(loggedId) || Number(data.member2Id) === Number(loggedId))) {
						if (loggedUserId === room.member1UserId) {
							loadMessages(roomId, room.messageIndex1, room.recentExitedmemberId);
							await reloadDetails(room.id, loggedUserId);
						} else if (loggedUserId === room.member2UserId) {

							loadMessages(roomId, room.messageIndex2, room.recentExitedmemberId);
							await reloadDetails(room.id, loggedUserId);
						}
					}
				}
				loadedRooms.add(roomId);
			}


		}


		return datas;
	} catch (error) {
		console.error("알람 데이터를 불러오는 중 오류 발생:", error);
	}
}


export async function fetchCompleted2Trade(alarm) {
	const boardId = Number(alarm.id);
	try {
		const response = await fetch(`/trade/findCompleted2TradeByBoardId/${boardId}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			throw new Error("서버로부터 데이터를 가져오지 못했습니다.");
		}

		const tradeData = await response.json();

		if (tradeData) {
			console.log("가져온 Trade 데이터:", tradeData);

			// `complete-class-${alarm.id}` id를 가진 div를 찾음
			const targetElement = document.getElementById(`complete-class-${alarm.id}`);

			if (targetElement) {
				// 버튼 생성
				const button = document.createElement("button");
				button.id = `complete1-Sell-${tradeData.id}`;  // 버튼의 id 설정
				button.textContent = "거래완료";  // 버튼 텍스트 설정

				// 버튼을 div에 추가
				targetElement.appendChild(button);
			}

			return tradeData;  // 필요한 경우 tradeData를 반환
		} else {
			console.log("조건에 맞는 Trade 데이터가 없습니다.");
		}

	} catch (error) {
		console.error("에러 발생:", error);
	}
}





export async function findAlarm(loggedId, alarmResult, alarmList, alarmListBody, page = 0) {

	if (alarmList && alarmList.length > 0) {
		alarmListBody.innerHTML = '';

		const container = document.getElementById('notification-container'); // 체크박스를 추가할 위치

		// 체크박스가 이미 추가되었는지 확인
		if (!document.getElementById('markAllAsReadCheckbox')) {
			// 체크박스를 감싸는 <label> 요소 생성
			const label = document.createElement('label');

			// 체크박스 <input> 요소 생성
			const checkbox = document.createElement('input');
			checkbox.type = 'checkbox';
			checkbox.id = 'markAllAsReadCheckbox';
			checkbox.onclick = toggleMarkAllAsRead; // 체크박스 클릭 시 실행될 함수
			// 체크박스 해제
			if (checkbox.checked) {
				checkbox.checked = false;
			}
			// 텍스트 노드 생성
			const text = document.createTextNode(' 모든 알림 읽음 처리');

			// <label> 요소에 체크박스와 텍스트 추가
			label.appendChild(checkbox);
			label.appendChild(text);

			// <label>을 container에 추가
			container.appendChild(label);
		}




		alarmList.sort((a, b) => Number(b.id) - Number(a.id));

		alarmList.forEach(alarm => {
			const row = document.createElement("tr");
			fetchCompleted2Trade(alarm);
			//			console.log(trade);
			row.innerHTML = `
                    ${alarm.member1Visible && Number(alarm.member1Id) === Number(loggedId) ? `
                        <td id = alarm-${alarm.id}>${alarm.member1Content} 
    <div class="date-container" style="display: flex; gap: 10px;"> 
        <p class="date-text">${formatDate(alarm.createTime)}</p>
        <p class="read-status">${alarm.member1Read === "READ" ? "읽음" : "읽지 않음"}</p>
    </div>
</td>

                ` : ""}
                    ${alarm.member2Visible && Number(alarm.member2Id) === Number(loggedId) ? `
                        <td  id = alarm-${alarm.id}>${alarm.member2Content}
                            ${alarm.action === "상대방 동의 확인" ? `
                                <button id="agreeMember2-${alarm.id}" >거래하기</button>
                                <button id="denyMember2-${alarm.id}" >거절하기</button>
                          		` : ""}
                          		
                          		 ${alarm.action === "예약" ? `
                                <button id="enroll-Book2-${alarm.id}" >예약하기</button>
                                <button id="deny-enroll-Book2-${alarm.id}" >거절하기</button>
                          		` : ""}

                           <!-- ${alarm.action === "거래 완료 확인" ? `
                            <div id = "complete-class-${alarm.id}">
                               <button id="complete1-Sell-${alarm.object}">거래완료</button> 
                               </div>
             							 ` : ""} -->
                                    <div class="date-container" style="display: flex; gap: 10px;"> 
                              <p class="date-text">${formatDate(alarm.createTime)}</p>
                                       <p class="read-status">${alarm.member2Read === "READ" ? "읽음" : "읽지 않음"}</p></div>
                        </td>
                       ` : ""}
                `;

			alarmListBody.appendChild(row);

		});
		// 📌 현재 페이지 번호 저장
		alarmListBody.setAttribute("data-current-page", page);


		// Create pagination container
		const pageList = document.createElement("div");
		pageList.id = "pagination";
		alarmListBody.appendChild(pageList); // Make sure the pagination div is appended to the body

		// Create pagination buttons
		createPagination(alarmResult, loggedId);
	} else {
		alarmListBody.innerHTML = '<tr><td colspan="3">알림이 없습니다.</td></tr>';
	}

}



document.getElementById("myChattingRoomList").addEventListener("click", async () => {
	const isLoggedIn = document.getElementById("isLoggedIn")?.value;
	const loggedId = document.getElementById("loggedId")?.value;
	const loggedUserId = document.getElementById("loggedUserId")?.value;
	if (isLoggedIn === "true" || isLoggedIn === true) {
		await loadChatRooms(loggedId);
		await checkUserAlarmCount(loggedId);
		setUpEnterRoomButton(loggedUserId);
		setUpExitRoomButton();
	}
});



document.getElementById("alarmButton").addEventListener("click", async () => {
	const isLoggedIn = document.getElementById("isLoggedIn")?.value;
	const loggedId = document.getElementById("loggedId")?.value;

	if (isLoggedIn === "true" || isLoggedIn === true) {
		const alarmResult = await checkUserAlarmList(loggedId);
		await checkUserAlarmCount(loggedId);
		const alarmList = alarmResult.content;
		const alarmListBody = document.getElementById("alarmListBody");

		findAlarm(loggedId, alarmResult, alarmList, alarmListBody);
	}
});

//클릭한 알람에 대해 읽음 처리
// 문서 전체 클릭 이벤트는 한 번만 등록
document.addEventListener("click", async function(event) {
	const target = event.target;

	if (target.tagName === "TD" && target.id.startsWith("alarm-")) {
		const alarmId = target.id.split("-")[1];

		try {
			const response = await fetch(`/alarm/read/${alarmId}`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				}
			});

			if (response.ok) {
				//				console.log(`알림 ${alarmId} 읽음 처리 완료`);

				// ✅ 클릭한 알림을 바로 삭제 (부모 <tr> 요소 제거)
				//				const row = target.closest("tr"); // 가장 가까운 <tr> 찾기
				//				if (row) {
				//					row.remove();
				//				}

				// ✅ 만약 전체 목록을 다시 불러와야 한다면 아래 코드 사용
				const isLoggedIn = document.getElementById("isLoggedIn")?.value;
				const loggedId = document.getElementById("loggedId")?.value;
				if (isLoggedIn === "true" || isLoggedIn === true) {
					const alarmListBody = document.getElementById("alarmListBody");
					const alarmResult = await checkUserAlarmList(loggedId, alarmListBody.getAttribute("data-current-page"));
					//					const alarmList = alarmResult.content;


					//					  alarmListBody.setAttribute("data-current-page", alarmResult.page); // 현재 페이지 저장
					// 						console.log(alarmResult);
					//					  console.log("페이지 확인1 =>" +Number(alarmResult.page));
					//					  console.log("페이지 확인2 =>" +alarmListBody.getAttribute("data-current-page"));

					// 						findAlarm(loggedId, alarmResult, alarmList, alarmListBody, alarmListBody.getAttribute("data-current-page"));
					loadPage(alarmListBody.getAttribute("data-current-page"), loggedId);
				}
			} else {
				console.error(`알림 ${alarmId} 읽음 처리 실패`);
			}
		} catch (error) {
			console.error("API 요청 중 오류 발생:", error);
		}
	}
});





// 페이지네이션 버튼을 생성하는 함수
function createPagination(data, loggedId) {
	const paginationContainer = document.getElementById('pagination');
	paginationContainer.innerHTML = ''; // 기존 버튼 초기화
	const totalPages = data.totalPages;
	const currentPage = data.page;
	const maxVisiblePages = 5; // 한 번에 보여줄 최대 페이지 개수

	// 이전 버튼
	const prevButton = document.createElement('button');
	prevButton.textContent = '<';
	prevButton.disabled = currentPage === 0;
	prevButton.addEventListener('click', () => {
		if (currentPage > 0) {
			loadPage(currentPage - 1, loggedId);
		}
	});
	paginationContainer.appendChild(prevButton);

	// 페이지 번호 버튼 생성
	if (totalPages <= maxVisiblePages) {
		// 전체 페이지가 적으면 그대로 표시
		for (let i = 0; i < totalPages; i++) {
			addPageButton(i, currentPage, loggedId, paginationContainer);
		}
	} else {
		// 첫 페이지 항상 표시
		addPageButton(0, currentPage, loggedId, paginationContainer);

		// 첫 페이지와 현재 페이지 사이에 ... 추가
		if (currentPage > 2) {
			const dots = document.createElement('span');
			dots.textContent = '...';
			paginationContainer.appendChild(dots);
		}

		// 현재 페이지 주변만 표시 (예: 3, 4, 5 -> 현재 페이지가 4일 때)
		let startPage = Math.max(1, currentPage - 1);
		let endPage = Math.min(totalPages - 2, currentPage + 1);

		for (let i = startPage; i <= endPage; i++) {
			addPageButton(i, currentPage, loggedId, paginationContainer);
		}

		// 마지막 페이지와 현재 페이지 사이에 ... 추가
		if (currentPage < totalPages - 3) {
			const dots = document.createElement('span');
			dots.textContent = '...';
			paginationContainer.appendChild(dots);
		}

		// 마지막 페이지 항상 표시
		addPageButton(totalPages - 1, currentPage, loggedId, paginationContainer);
	}

	// 다음 버튼
	const nextButton = document.createElement('button');
	nextButton.textContent = '>';
	nextButton.disabled = currentPage === totalPages - 1;
	nextButton.addEventListener('click', () => {
		if (currentPage < totalPages - 1) {
			loadPage(currentPage + 1, loggedId);
		}
	});
	paginationContainer.appendChild(nextButton);
}

// 개별 페이지 버튼 추가 함수
function addPageButton(page, currentPage, loggedId, container) {
	const pageButton = document.createElement('button');
	pageButton.textContent = page + 1;
	pageButton.disabled = page === currentPage;
	pageButton.addEventListener('click', () => loadPage(page, loggedId));
	container.appendChild(pageButton);
}




// 페이지를 로드하는 함수 (실제 데이터 로드를 구현할 곳)
export async function loadPage(page, loggedId) {
	//	console.log(`Loading page ${page + 1}`);

	const unReadAlarmCount = await checkUserAlarmCount(loggedId);
	const unReadAlarmCountButton = document.getElementById("unReadAlarmCountButton");
	unReadAlarmCountButton.innerText = unReadAlarmCount;

	const alarmResult = await checkUserAlarmList(loggedId, page); // 페이지네이션 지원
	const alarmList = alarmResult.content;
	const alarmListBody = document.getElementById("alarmListBody");

	// 📌 페이지 정보 업데이트 후 데이터 로드
	alarmListBody.setAttribute("data-current-page", page); // 현재 페이지 저장
	findAlarm(loggedId, alarmResult, alarmList, alarmListBody, page);
}






async function toggleMarkAllAsRead() {
	const isChecked = document.getElementById("markAllAsReadCheckbox").checked;
	const loggedId = document.getElementById("loggedId").value;
	const alarmListBody = document.getElementById("alarmListBody");

	// 📌 현재 페이지 번호 유지
	let currentPage = alarmListBody.getAttribute("data-current-page") || 0;

	if (isChecked) {
		try {
			// 모든 알림 읽음 처리 API 호출
			await fetch(`/alarm/read-all`, {
				method: "GET",
				headers: {
					"Content-Type": "application/json;charset=utf-8",
				}
			});

			// 📌 현재 페이지 유지하면서 알림 목록 다시 가져오기
			const alarmResult = await checkUserAlarmList(loggedId, currentPage);
			const alarmList = alarmResult.content;



			findAlarm(loggedId, alarmResult, alarmList, alarmListBody, currentPage); // ✅ 현재 페이지를 유지하면서 호출
			const unReadAlarmCount = await checkUserAlarmCount(loggedId);
			//    const unReadAlarmCountButton = document.getElementById("unReadAlarmCountButton");
			//    
			//    if (unReadAlarmCount > 0 && unReadAlarmCountButton) {
			//		unReadAlarmCountButton.style.display ="block";
			//      unReadAlarmCountButton.innerText = unReadAlarmCount;
			//    }else{
			//			unReadAlarmCountButton.style.display ="none";
			//      unReadAlarmCountButton.innerText = unReadAlarmCount;
			//	}
		} catch (error) {
			console.error("알림 읽음 처리 중 오류 발생:", error);
		}
	}
}

export async function unReadMessageCount(loggedId) {
	try {
		const unReadMessageCountButton = document.getElementById("unReadMessageCountButton");
		const unReadMessageCount = await checkUnReadMessageCount(loggedId);
		if (unReadMessageCount > 0 && unReadMessageCountButton) {
			unReadMessageCountButton.style.display = "block";
			unReadMessageCountButton.innerText = unReadMessageCount;
		} else {
			unReadMessageCountButton.style.display = "none";
			unReadMessageCountButton.innerText = unReadMessageCount;
		}
	} catch (error) {
		console.error("읽지 않은 메시지를 불러올 수 없습니다:", error);
	}


}




export async function unReadMessageCount2(roomId) {
	try {
		const unReadMessageCountButton2 = document.getElementById("unReadMessageCountButton2");


		const unReadMessageCount2 = await checkUnReadMessageCount2(roomId);
		if (unReadMessageCount > 0 && unReadMessageCountButton2) {
			unReadMessageCountButton2.style.display = "block";
			unReadMessageCountButton2.innerText = unReadMessageCount2;
		} else {
			unReadMessageCountButton2.style.display = "none";
			unReadMessageCountButton2.innerText = unReadMessageCount2;
		}
	} catch (error) {
		console.error("읽지 않은 메시지를 불러올 수 없습니다:", error);
	}


}



