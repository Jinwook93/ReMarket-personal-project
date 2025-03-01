// chatting 기능 관련 함수들을 모듈로 내보냅니다.

import { formatDate } from "./formatDate.js";



//전역변수(채팅창 내 메시지내용을 실시간으로 업데이트)
let intervalId;



function findContentByParentMessageId(messages, msgId) {
	let result = null;
	for (let message of messages) {
		if (message.id === msgId) {
			result = message;
			break;  // 원하는 메시지를 찾으면 루프를 종료
		}
	}
	//	console.log("결과" + result);
	return result;
}


//function findProfileImageAndUserId(userid) {
//	return fetch('/getProfileImage', {
//		method: 'POST',
//		headers: { 'Content-Type': 'application/json;charset=utf-8' },
//		body: JSON.stringify({ userid })
//	})
//		.then(response => {
//			if (!response.ok) {
//				throw new Error("응답 실패 : 데이터를 조회할 수 없습니다!");
//			}
//			return response.text();
//		})
//		.then(profileImagePath => {
//			//        console.log("잘 도착 1:", profileImagePath);
//			return profileImagePath;
//		})
//		.catch(error => {
//			console.log("데이터 조회 실패:", error);
//			return "/boardimage/nullimage.jpg";
//		});
//}

//function loadProfileImage(userid,messageElement) {
//    findProfileImageAndUserId(userid)
//        .then(profileImageUrl => {
//            console.log("잘 도착 2??:", profileImageUrl);
//
//            if (profileImageUrl) {
//                const profileImage = document.createElement("img");
//                profileImage.src = profileImageUrl;
//                profileImage.alt = `${userid}'s profile picture`;
//                profileImage.classList.add("profile-image");
//                messageElement.appendChild(profileImage);
//            }
//        })
//        .catch(error => {
//            console.log("프로필 이미지 로딩 실패:", error);
//        });
//}


function insertLineBreaks(text, maxLength) {
	let result = '';
	for (let i = 0; i < text.length; i += maxLength) {
		result += text.slice(i, i + maxLength) + '<br>';
	}
	return result;
}





function findTradeByBoardId(trades) {
	let result = null;
	for (let trade of trades) {
		console.log(trade);
		//		if (trade.boardEntity?.id === boardId && trade.accept1 && trade.accept2) {
		if (trade.accept1 && trade.accept2) {
			result = trade;
			break;  // 원하는 메시지를 찾으면 루프를 종료
		}
	}
	//	console.log("결과" + result);
	return result;
}


// 특정 채팅방(roomId) 메시지를 불러오는 함수
export async function loadMessages(roomId, messageIndex, recentExitedmemberId) {
	const loggedUserId = document.getElementById("loggedUserId").value;
	const loggedId = document.getElementById("loggedId").value;
	const chatBox = document.getElementById(`chat-box-${roomId}`);
	if (!chatBox) return;

	try {
		const response = await fetch(`/chat/loadmessages/${roomId}`);
		if (!response.ok) {
			console.error("Server returned an error:", response.status, response.statusText);
			chatBox.innerHTML = "<div>Error loading messages.</div>";
			return;
		}

		const messages = await response.json();
		chatBox.innerHTML = ""; // 기존 메시지 초기화

		if (!Array.isArray(messages)) {
			console.error("Expected messages to be an array, but got:", messages);
			chatBox.innerHTML = "<div>Invalid message format.</div>";
			return;
		}
		//		console.log(messages);

		messages.forEach((msg, index) => {

			if (recentExitedmemberId && Number(recentExitedmemberId) === Number(loggedId) && (messageIndex > 0 && index < messageIndex)) {
				return;
			}

			const messageElement = document.createElement("div");
			messageElement.classList.add("message-item");

			//비동기통신으로 데이터가 일치하지 않는 경우가 있어서 제외
			// 프로필 이미지와 사용자 ID 추가
			//    findProfileImageAndUserId(msg.senderUserId)
			//        .then(profileImageUrl => {
			//            if (profileImageUrl) {
			//                const profileContainer = document.createElement("div");
			//                profileContainer.classList.add("profile-container");
			//
			//                const profileImage = document.createElement("img");
			//                profileImage.src = profileImageUrl;
			//                profileImage.alt = `${msg.senderUserId}'s profile picture`;
			//                profileImage.classList.add("profile-image", "message-Left");
			//
			//                const userid = document.createElement("p");
			//                userid.textContent = msg.senderUserId;
			//
			//                profileContainer.appendChild(profileImage);
			//                profileContainer.appendChild(userid);
			//
			//                chatBox.appendChild(profileContainer);
			//            }
			//        })
			//        .catch(error => {
			//            console.log("프로필 이미지 로딩 실패:", error);
			//        });


			const formattedMessage = insertLineBreaks(msg.messageContent, 20); // 20글자마다 줄바꿈


			const profileContainer = document.createElement("div");
			profileContainer.classList.add("profile-container");

			const profileImage = document.createElement("img");
			profileImage.src = msg.exited ? "/boardimage/nullimage.jpg" : msg.profileImageUrl1 == null ? "/boardimage/nullimage.jpg" : msg.profileImageUrl1;
			profileImage.alt = `${msg.senderUserId}'s profile picture`;
			profileImage.classList.add("profile-image", "message-Left");
			const userid = document.createElement("p");
			userid.textContent = msg.exited ? '(나간 사용자)' : msg.senderUserId;

			profileContainer.appendChild(profileImage);
			profileContainer.appendChild(userid);

			//                chatBox.appendChild(profileContainer);


			// 메시지 내용 처리
			if (msg.messageContent === "⚠️삭제된 메시지입니다" && msg.deleted) {
				messageElement.innerHTML = ` <b>${msg.messageContent}</b>
				                  <br>
                       <br>
    <span class="send-time">${msg.sendTime}</span>`;
			} else {
				const parentMessageObject = findContentByParentMessageId(messages, msg.parentMessageId);
				if (parentMessageObject) {
					messageElement.innerHTML = `
                <b>${parentMessageObject.senderUserId}</b>: <b>${parentMessageObject.messageContent.length > 20 ? insertLineBreaks(parentMessageObject.messageContent, 20) : parentMessageObject.messageContent}</b>에 대한 답글
                <hr>
               <!--${msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent} -->
               	<!--	${msg.messageContent} -->
               	 	${msg.alarmType == false || msg.alarmType == null ? (msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent) : msg.messageContent}
                   <br>
                       <br>
    <span class="send-time">${msg.sendTime}</span>
							<span class="read-status">${msg.read ? "읽음" : "읽지않음"}</span>
						
`;
				} else {
					messageElement.innerHTML = `
      <!-- ${msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent} --> 
             	<!--	${msg.messageContent} -->
             	${msg.alarmType == false || msg.alarmType == null ? (msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent) : msg.messageContent}

       <br><br>
    <span class="send-time">${msg.sendTime}</span>
							<span class="read-status">${msg.read ? "읽음" : "읽지않음"}</span>
						
`;
				}
			}

			// 로그인한 사용자와 보낸 사용자가 동일한 경우
			if (String(msg.senderUserId) === String(loggedUserId)) {
				messageElement.classList.add("message-right");

				const deleteMessageButton = document.createElement("button");
				if (msg.messageContent === "⚠️삭제된 메시지입니다" && msg.deleted) {
					deleteMessageButton.style.display = "none";
				} else {
					deleteMessageButton.textContent = "삭제";
					deleteMessageButton.classList.add("delete-button");
					deleteMessageButton.onclick = async () => {
						const confirmDelete = confirm("메시지를 삭제하시겠습니까?");
						if (confirmDelete) {
							try {
								const response = await fetch(`/chat/deleteMessage/${msg.id}`, { method: "PUT" });
								if (response.ok) {
									loadMessages(roomId, messageIndex, recentExitedmemberId);
								} else {
									alert("메시지 삭제에 실패했습니다.");
								}
							} catch (error) {
								console.error("메시지 삭제 중 오류 발생", error);
							}
						}
					};
				}
				const likeState = document.createElement("p");
				likeState.textContent = msg.liked ? "❤️" : "🤍";


				const flexArray = document.createElement("div");
				flexArray.style.display = 'flex';
				flexArray.style.justifyContent = 'flex-end';  // 오른쪽 정렬
				flexArray.style.alignItems = 'center';
				flexArray.appendChild(likeState);
				flexArray.appendChild(deleteMessageButton);
				flexArray.appendChild(messageElement);
				flexArray.appendChild(profileContainer);
				chatBox.appendChild(flexArray);
			} else {
				messageElement.classList.add("message-left");
				messageElement.dataset.messageId = msg.id;

				// 좋아요 버튼 생성
				const likeButton = document.createElement("button");
				if (msg.messageContent === "⚠️삭제된 메시지입니다" && msg.deleted) {
					likeButton.style.display = "none";
				} else {
					likeButton.textContent = msg.liked ? "❤️" : "🤍";
					likeButton.classList.add("like-button");
					if (msg.liked) likeButton.classList.add("liked");
					likeButton.dataset.messageId = msg.id;

					likeButton.addEventListener("click", async function() {
						const messageId = this.dataset.messageId;
						const isLiked = this.classList.contains("liked");

						try {
							const response = await fetch(`/chat/${messageId}/like`, {
								method: "POST",
								headers: { "Content-Type": "application/json" },
								body: JSON.stringify(!isLiked),
							});

							if (response.ok) {
								const result = await response.json();
								if (result) {
									this.classList.add("liked");
									this.textContent = "❤️";
								} else {
									this.classList.remove("liked");
									this.textContent = "🤍";
								}
							}
						} catch (error) {
							console.error("좋아요 요청 중 오류 발생:", error);
						}
					});
				}

				messageElement.style.border = '0.5px solid black'; // 원하는 테두리 두께와 색상 설정
				messageElement.style.padding = '10px'; // 패딩 추가 (선택 사항)
				const flexArray = document.createElement("div");
				flexArray.style.display = 'flex';
				flexArray.style.justifyContent = 'flex-start';  // 왼쪽 정렬
				flexArray.style.alignItems = 'center';  // 세로 중앙 정렬 (필요에 따라 조정 가능)
				flexArray.appendChild(profileContainer);
				flexArray.appendChild(messageElement);
				flexArray.appendChild(likeButton);

				chatBox.appendChild(flexArray);
			}

			// 채팅 컨테이너 클릭 시 읽음 처리
			document.getElementById(`chat-container-${roomId}`).addEventListener("click", async function() {
				const unreadMessages = document.querySelectorAll(".message-item:not(.read)");

				if (unreadMessages.length === 0) return;

				try {
					const messageIds = [...unreadMessages].map(msg => msg.dataset.messageId);

					const response = await fetch(`/chat/markAsRead`, {
						method: "POST",
						headers: { "Content-Type": "application/json" },
						body: JSON.stringify({ messageIds }),
					});

					if (response.ok) {
						unreadMessages.forEach(msg => {
							msg.classList.add("read");
						});
					} else {
						console.error("❌ 읽음 처리 실패!");
					}
				} catch (error) {
					console.error("⚠️ 메시지 읽음 처리 중 오류 발생:", error);
				}
			});
			//   chatBox.appendChild(profileContainer);
			//    chatBox.appendChild(messageElement);

			// 메시지 클릭 이벤트 추가  ${msg.id}
			messageElement.addEventListener("click", function(e) {
				console.log("클릭된 메시지:", msg.messageContent, "보낸 사람:", msg.senderUserId, "로그인된 유저:", loggedUserId);
				const messageInput = document.getElementById(`message-input-${roomId}`);
				const replyText = `${msg.senderUserId} : ${msg.messageContent}에 대한 답글 >`;
				const parentMessageId = document.getElementById("parentMessageId");
				const parentMessageButton = document.getElementById("parentMessageButton");
				const clickedMsgId = msg.id; // 현재 클릭된 메시지 ID
				const currentMsgId = parentMessageId.value; // 현재 저장된 parentMessageId

				if (msg.alarmType === "false" || msg.alarmType === null) {
					// 같은 메시지를 다시 클릭한 경우 (토글)
					if (currentMsgId === String(clickedMsgId)) {
						//        messageInput.value = messageInput.value.replace(replyText, "").trim();
						parentMessageId.value = "";
						parentMessageButton.value = "";
						parentMessageButton.style.display = "none";
					} else {
						// 다른 메시지를 클릭한 경우, 기존 메시지 초기화 후 새 메시지 반영
						//        messageInput.value = replyText;
						parentMessageButton.value = replyText;
						parentMessageButton.style.display = "block";
						parentMessageId.value = clickedMsgId;
					}
				}
			});

			document.getElementById("parentMessageButton").addEventListener('click', () => {
				// 조건 비교 연산자 수정: = 대신 == 또는 === 사용
				if (parentMessageButton.style.display === "block") {
					parentMessageButton.style.display = "none";  // 숨기기
					document.getElementById("parentMessageId").value = "";  // parentMessageId 값 초기화
				}
			});




		});


		//msg 끝


		// 스크롤 최하단으로 이동
		//		chatBox.scrollTop = chatBox.scrollHeight;






	} catch (error) {
		console.error("Error loading messages:", error);
		chatBox.innerHTML = "<div>Error loading messages.</div>";
	}
}





// 특정 채팅방(roomId)에 메시지를 전송하는 함수 (userid= 상대방)
export async function sendMessage(roomId, userid, messageIndex, recentExitedmemberId) {
	const messageInput = document.getElementById(`message-input-${roomId}`);
	const message = messageInput.value.trim();
	//	const parentMessageId = document.getElementById(`message-input-${roomId}`).value;		//클릭한 대상의 id
	const parentMessageId = document.getElementById("parentMessageId");			// ※ .value : 무조건 String으로 인식함
	const loggedId = document.getElementById("loggedId").value;
	const loggedUserId = document.getElementById("loggedUserId").value;
	if (message === "") return;

	try {
		await fetch(`/chat/send/${roomId}`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({
				receiverUserId: userid,
				messageContent: message,
				parentMessageId: Number(parentMessageId.value) ? Number(parentMessageId.value) : null
			})
		});

		messageInput.value = "";
		parentMessageId.value = "";
		//		loadMessages(roomId); // 전송 후 메시지 갱신
		await loadChatRooms(loggedId);
		setUpEnterRoomButton(loggedUserId);
		setUpExitRoomButton();
		loadMessages(Number(roomId), messageIndex, recentExitedmemberId);


		//const intervalId = setInterval(() => loadMessages(Number(roomId), messageIndex, recentExitedmemberId), 2000);

		// To stop the interval after some condition or action
		//clearInterval(intervalId);

	} catch (error) {
		console.error("메시지 전송 중 오류 발생", error);
	}
}










// 특정 채팅방을 열고 메시지 기능을 연결하는 함수
export async function openChatRoom(roomId, title, loggedUserId, userid, loggedFlag) {
	let chatWindow = document.getElementById(`chat-room-${roomId}`);


	if (chatWindow) {
		chatWindow.remove(); // 이미 열려있으면 닫기
	} else {
		chatWindow = document.createElement("div");
		chatWindow.id = `chat-room-${roomId}`;
		chatWindow.className = "chat-window";
		//   <!--         ${trade.tradeStatus=== '완료'? '<h2>판매완료</h2>':""} -->
		// board 정보 비동기 요청
		const boardresponse = await fetch(`/chat/findBoard/${roomId}`);
		const board = await boardresponse.json();
		//		const trade = findTradeByBoardId(board.trades);					//해당 보드가 속한 trades 탐색	
		//		console.log("트레이드 상태 : "+trade.tradeStatus);
		console.log(board);
		console.log(roomId, title, loggedUserId, userid);
		chatWindow.innerHTML = `
            <div class="chat-container" id="chat-container-${roomId}">
                <div style="display:flex;">
                    <h2>${userid} 님과의 채팅방</h2>
                    <button class="close-chat" data-room-id="${roomId}">닫기</button>
                </div>
                <!-- ※ Thymeleaf의 경우 enum 타입일 경우 .name을 써야함-->
<br>
               <!-- 토글 가능한 항목들 -->
        <button id = "BoardTitleButton-${board.id}">✍🏼 게시글 : ${title}</button>
        <br>
        <button id="toggleDetails-${roomId}">상세 정보 ▽</button>

        <div id="details-${roomId}" style="display:none;">
            <div>
                <h3>판매 종류: ${board.buy_Sell}</h3>
            </div>
            
            <div>
                <h3>카테고리: ${board.category}</h3>
            </div>

            <div>
                <h3>판매물: ${board.title}</h3>
            </div>

            <div>
                <h3>가격: ${board.price}원</h3>
            </div>
        </div>

                <div class="chat-header"></div>
                <div id="chat-box-${roomId}" class="chat-box"></div>
                    <input type = "button" style="display:none;" id = "parentMessageButton"></input>
                <div class="chat-input">
                  <input type = "hidden" id = "parentMessageId"></input>
                    <input type="text" id="message-input-${roomId}" placeholder="메시지를 입력하세요">
                    <button id="send-button-${roomId}" data-room-id="${roomId}">전송</button>
                </div>
            </div>
        `;
		document.body.appendChild(chatWindow);

		// 채팅창이 생성된 후 드래그 이벤트 핸들러 추가
		const chatContainer = document.getElementById(`chat-container-${roomId}`);


		// 화면의 중앙에 위치하도록 설정
		const centerChatContainer = () => {
			const width = chatContainer.offsetWidth;
			const height = chatContainer.offsetHeight;
			const screenWidth = window.innerWidth;
			const screenHeight = window.innerHeight;

			chatContainer.style.left = `${(screenWidth - width) / 2}px`;
			chatContainer.style.top = `${(screenHeight - height) / 2}px`;
		};

		// 채팅창이 생성된 후 중앙 위치 설정
		centerChatContainer();

		document.getElementById(`toggleDetails-${roomId}`).addEventListener('click', () => {
			const details = document.getElementById(`details-${roomId}`);
			details.style.display = (details.style.display === 'none' || details.style.display === '') ? 'block' : 'none';
		});

		chatContainer.addEventListener('mousedown', (e) => {
			let offsetX = e.clientX - chatContainer.getBoundingClientRect().left;
			let offsetY = e.clientY - chatContainer.getBoundingClientRect().top;

			const mouseMoveHandler = (e) => {
				chatContainer.style.left = `${e.clientX - offsetX}px`;
				chatContainer.style.top = `${e.clientY - offsetY}px`;
			};

			document.addEventListener('mousemove', mouseMoveHandler);

			document.addEventListener('mouseup', () => {
				document.removeEventListener('mousemove', mouseMoveHandler);
			}, { once: true });
		});



		const roomresponse = await fetch(`/chat/findRoom/${roomId}`);

		const room = await roomresponse.json();
		//		let messageIndex = 0;


		// ✅ 기존 setInterval이 존재하면 삭제 후 새롭게 설정
		if (intervalId) {
			clearInterval(intervalId);
			console.log("기존 interval 제거 완료");
		}

		//		console.log(room);
		if (loggedFlag === "logged1") {
			//			let messageIndex = Number(room.messageIndex1);
			//	loadMessages(Number(roomId), Number(room.messageIndex1), room.recentExitedmemberId);
			intervalId = setInterval(() => loadMessages(Number(roomId), Number(room.messageIndex1), Number(room.recentExitedmemberId)), 2000);
			// 메시지 전송 이벤트 추가
			//		intervalId = setInterval(() => loadMessages(Number(roomId), messageIndex, recentExitedmemberId), 2000);

			document.getElementById(`send-button-${roomId}`).addEventListener("click", () => sendMessage(roomId, userid, Number(room.messageIndex1), Number(room.recentExitedmemberId)));
			document.getElementById(`message-input-${roomId}`).addEventListener("keypress", (event) => {
				if (event.key === "Enter") sendMessage(roomId, userid, messageIndex1, recentExitedmemberId);
			});


		} else if (loggedFlag === "logged2") {
			//			let messageIndex = Number(room.messageIndex2);
			//		loadMessages(Number(roomId), Number(room.messageIndex2), room.recentExitedmemberId);
			intervalId = setInterval(() => loadMessages(Number(roomId), Number(room.messageIndex2), Number(room.recentExitedmemberId)), 2000);
			// 메시지 전송 이벤트 추가
			document.getElementById(`send-button-${roomId}`).addEventListener("click", () => sendMessage(roomId, userid, Number(room.messageIndex2), Number(room.recentExitedmemberId)));
			document.getElementById(`message-input-${roomId}`).addEventListener("keypress", (event) => {
				if (event.key === "Enter") sendMessage(roomId, userid, messageIndex2, recentExitedmemberId);
			});





		}

		// 닫기 버튼 이벤트 추가
		chatWindow.querySelector(".close-chat").addEventListener("click", function() {
			chatWindow.remove();
		});


		//해당 거래 게시판으로 이동하는 이벤트 추가		
		document.getElementById(`BoardTitleButton-${board.id}`).addEventListener('click', function() {
			const boardId = `${board.id}`; // board.id를 문자열로 받기
			window.location.href = `/board/view/${boardId}`; // 해당 URL로 이동
		});


	}

	//			setInterval(findMessageCount(roomId),1000);

}
//	let messageCount_prev = null;






















// chatModule.js

export async function loadChatRooms(loggedId) {
	// 문자열 변환 후 trim 처리
	const numericLoggedId = Number(String(loggedId).trim());

	// 변환 실패 시 실행 중단
	if (Number.isNaN(numericLoggedId)) {
		console.error("Error: loggedId가 올바르지 않습니다.", loggedId);
		return;
	}

	const loggedUserId = document.getElementById("loggedUserId").value;

	const chattingRoomListBody = document.getElementById("chattingRoomListBody");
	chattingRoomListBody.innerHTML = ""; // 기존 목록 초기화

	const chattingRoomScroll = document.getElementById("chattingRoomScroll");

	try {
		const response = await fetch(`/chat/myChatRoom/${numericLoggedId}`); // 변환된 값 사용

		if (!response.ok) {
			throw new Error("Failed to fetch chat rooms");
		}

		const datas = await response.json();

	// member1Visible 또는 member2Visible이 true인 데이터만 필터링
const visibleDatas = datas.filter(data => 
    (data.member1UserId === loggedUserId && data.member1Visible)
 ||    (data.member2UserId === loggedUserId && data.member2Visible)
);

if (Array.isArray(visibleDatas) && visibleDatas.length > 0) {
    // 5개 이상이면 스크롤 추가
    if (visibleDatas.length > 5) {
        chattingRoomScroll.style.maxHeight = "300px"; // 5개 초과 시 스크롤
        chattingRoomScroll.style.overflowY = "auto";
    } else {
        chattingRoomScroll.style.maxHeight = ""; // 초기화
        chattingRoomScroll.style.overflowY = "";
    }


			//${(data.member1UserId === loggedUserId&& data.member1Visible ===true) ||(data.member2Visible ===true && data.member2UserId ===loggedUserId)?}

			// Iterate over each room and fetch the recent message for each room
for (const data of datas) {
    // 최근 메시지 가져오기
    const recentRoomMessage = await findRecentRoomMessage(Number(data.id));
    console.log(data);

    // 로그인한 사용자가 member1이고 member1Visible이 true이거나
    // 로그인한 사용자가 member2이고 member2Visible이 true인 경우만 표시
    const isRoomVisible = 
        (data.member1UserId === loggedUserId && data.member1Visible) || 
        (data.member2UserId === loggedUserId && data.member2Visible);



//	console.log("isRoomVisible?"+isRoomVisible);
    if (!isRoomVisible) continue; // 채팅방을 표시하지 않음
		console.log("isRoomVisible?"+isRoomVisible);

    const row = document.createElement("tr");
    row.innerHTML = `
        <td style="width:500px;">${data.title}
            <div class="date-text" style="margin-top:10px;font-size:15px;">
                대화 중인 유저: ${data.member2UserId}
            </div>
            <div>
                ${recentRoomMessage 
                    ? `${recentRoomMessage.senderUserId ? `최근 메시지: ${recentRoomMessage.senderUserId} : ` : ""} 
                       ${recentRoomMessage.messageContent || ""}` 
                    : `최근 메시지 없음`
                } 
                &nbsp;&nbsp;&nbsp;
                ${recentRoomMessage?.id ? formatDate(recentRoomMessage.sendTime) : formatDate(data.createTime)}
            </div>
        </td>
        <td>
            <div style="display:flex;">
                <button class="enterChat" 
                    data-room-id="${data.id}" 
                    data-title="${data.title}" 
                    data-userid="${data.member2UserId}">
                    입장
                </button>
                <button class="deleteRoom" 
                    data-deleteRoomId="${data.id}" 
                    data-deleteTitle="${data.title}" 
                    data-deleteUserid="${data.member2UserId}">
                    나가기
                </button>
            </div>
        </td>
    `;

//    console.log("멤버1테스트1", data.member1UserId);
//    console.log("멤버1테스트2", loggedUserId);
//    console.log("멤버1테스트3", data.member1Visible);
//    console.log("멤버2테스트1", data.member2UserId);
//    console.log("멤버2테스트2", loggedUserId);
//    console.log("멤버2테스트3", data.member2Visible);

    chattingRoomListBody.appendChild(row);
}

		} else {
			const row = document.createElement("tr");
			row.innerHTML = `<td colspan="3">채팅방이 존재하지 않습니다</td>`;
			chattingRoomListBody.appendChild(row);
			console.warn("No chatting rooms available.");
		}
	} catch (error) {
		const row = document.createElement("tr");
		row.innerHTML = `<td colspan="3">채팅방 데이터 수집에 실패하였습니다</td>`;
		chattingRoomListBody.appendChild(row);
		console.error("Failed to fetch chat rooms:", error);
	}
}







export function setUpEnterRoomButton(loggedUserId) {
	document.querySelectorAll(".enterChat").forEach(button => {
		button.addEventListener("click", async function() {
			const roomId = this.getAttribute("data-room-id");
			const title = this.getAttribute("data-title");
			const userid = this.getAttribute("data-userid");
			//			openChatRoom(roomId, title, loggedUserId, userid);

			// 채팅방 열기 및 메시지 로드
			if (loggedUserId !== userid) {
				openChatRoom(roomId, title, loggedUserId, userid, "logged1");

			} else {
				const roomResponse = await fetch('/chat/findRoom/${roomId}');
				const room = roomResponse.json();
				console.log(room);

				openChatRoom(roomId, title, loggedUserId, room.member1UserId, "logged2");
			}
		});
	});
}

export function setUpExitRoomButton() {
	document.querySelectorAll(".deleteRoom").forEach(button => {
		button.addEventListener("click", async function() {
			const deleteRoomId = this.getAttribute("data-deleteRoomId");
			const deleteUserId = this.getAttribute("data-deleteUserid");
			const chatWindow = document.getElementById(`chat-room-${deleteRoomId}`);
			const loggedId = document.getElementById("loggedId").value;

			try {
				const response = await fetch(`/chat/exitRoom/${deleteRoomId}`, {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
					body: JSON.stringify({ receiver: deleteUserId })
				});

				if (response.ok) {
					console.log(`채팅방 ${deleteRoomId} 나가기 성공`);

					// ✅ 특정 `roomId`에 해당하는 `tr`만 삭제
					const targetRow = document.querySelector(`tr[data-room-id="${deleteRoomId}"]`);
					if (targetRow) {
						targetRow.remove();
					}

					// ✅ 특정 채팅방 창 닫기
					if (chatWindow) {
						chatWindow.remove();
					}

					// ✅ 채팅방 목록 다시 불러오기
					await loadChatRooms(loggedId);

					// ✅ 이벤트 핸들러 다시 등록 (새 목록에도 적용)
					setUpExitRoomButton();

					//                    alert("채팅방을 나갔습니다.");
				} else {
					//					alert("채팅방 나가기에 실패했습니다.");
				}
			} catch (error) {
				console.error("Failed to exit chat room:", error);
			}
		});
	});
}



export function toggleChattingRoomList() {
	const myChattingRoomList = document.getElementById("myChattingRoomList");
	const chattingRoomList = document.querySelector(".chattingRoomList");

	if (myChattingRoomList && chattingRoomList) {
		myChattingRoomList.addEventListener("click", (event) => {
			event.preventDefault();
			chattingRoomList.style.display = chattingRoomList.style.display === "none" ? "block" : "none";
		});
	}
}






export function showChattingRoomList() {
	const chattingRoomList = document.querySelector(".chattingRoomList");
	if (chattingRoomList) {
		chattingRoomList.style.display = "block";
	}
}








//// 전역 변수로 메시지 수를 저장
//let messageCount_prev = null;

// 상대방의 수신 여부를 실시간으로 받는 함수
export async function findMessageCount(roomId) {
	try {
		// DB에서 roomId 내의 loggedUserId, userid 간의 메일 크기를 실시간으로 조회
		const response = await fetch(`/chat/findMessageCount/${roomId}`, {
			method: "GET",
			headers: { 'Content-Type': "application/json;charset=utf-8;" }
		});

		const data = await response.json();
		let messageCount = data.count;  // 메시지 갯수 받아오기




		// 이전 메시지 갯수와 비교
		if (messageCount !== messageCount_prev) {
			messageCount_prev = messageCount;  // 이전 메시지 갯수 업데이트


			const loggedFlag_response = await fetch(`/chat/findMember1or2/${roomId}`);
			const loggedFlag = await loggedFlag_response.text();

			// 조건 만족시 실행
			if (loggedFlag === "logged1") {
				loadMessages(Number(roomId), Number(room.messageIndex1), room.recentExitedmemberId);
			} else if (loggedFlag === "logged2") {
				loadMessages(Number(roomId), Number(room.messageIndex2), room.recentExitedmemberId);
			}
		}
	} catch (error) {
		console.error("Error fetching message count:", error);
	}
}

//// 일정 주기로 메시지 갯수를 확인
//setInterval(() => {
//  // 필요한 파라미터를 전달하여 채팅방을 확인
//  openChatRoom(roomId, title, loggedUserId, userid, loggedFlag);
//}, 1000); // 1초마다 메시지 갯수 확인



// 최근 메시지를 받아오는 함수
export async function findRecentRoomMessage(roomId) {
	// DB에서 roomId 내의 loggedUserId, userid 간의 메일 크기를 실시간으로 조회
	return fetch(`/chat/findRecentRoomMessage/${roomId}`, {
		method: "GET",
		headers: { 'Content-Type': "application/json;charset=utf-8;" }
	})
		.then(response => {
			// If the response is not OK (status is not in the range 200-299)
			//        if (!response.ok) {
			//            return response.text();  // If not OK, return the error message as text
			//        } else {
			//            return response.json();  // Otherwise, return the response as JSON
			//        }
			return response.json();
		})
		.then(data => {
			//        console.log(data);  // Print the received data
			return data;  // Return the data
		})
		.catch(error => {
			console.error("Error fetching message count:", error);  // Print any error that occurs
		});
}


