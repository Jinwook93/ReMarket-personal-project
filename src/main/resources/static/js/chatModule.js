// chatting 기능 관련 함수들을 모듈로 내보냅니다.

//전역변수
let intervalId;


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

		const filteredMessages = messages.filter((msg, index) => {
			// 조건을 만족하지 않으면 메시지를 제외
			if (recentExitedmemberId && Number(recentExitedmemberId) === Number(loggedId) && messageIndex > 0 && index < messageIndex) {
				return false; // 해당 메시지는 제외
			}
			return true; // 조건에 맞는 메시지는 포함
		});


		filteredMessages.forEach((msg, index) => {
			//			 if (msg.exitedSenderId !== null && String(msg.exitedSenderId) === String(loggedId)) {
			//        return; // 현재 메시지는 출력하지 않고 다음 메시지로 이동
			//    }
			//			 if (
			//                (msg.exitedSenderId && String(msg.exitedSenderId) === String(loggedId)) || 
			//                (msg.receiverUserId && String(msg.receiverUserId) === String(loggedUserId))
			//            ) {
			//                return; // 해당 사용자의 메시지는 렌더링하지 않음
			//            }

			//		if(recentExitedmemberId && String(recentExitedmemberId) === String(loggedId) && (messageIndex>0 &&
			//		  index< messageIndex)){
			//			return;
			//		 }


			//			console.log('recentExitedmemberId:', recentExitedmemberId);
			//console.log('loggedId:', loggedId);
			//console.log('messageIndex:', messageIndex);
			//console.log('index:', index);



			const messageElement = document.createElement("div");
			messageElement.classList.add("message-item");


			// 로그인한 사용자와 보낸 사용자가 동일하면 삭제 버튼 추가
			// 메시지 내용과 좋아요 상태 처리
			messageElement.textContent = `${msg.senderUserId}: ${msg.messageContent} ${msg.sendTime} ${msg.liked ? "❤️" : "🤍"}`;
			if (String(msg.senderUserId) === String(loggedUserId)) {
				messageElement.classList.add("message-right");

				// 삭제 버튼 생성
				const deleteMessageButton = document.createElement("button");
				if (msg.messageContent === "⚠️삭제된 메시지입니다" && msg.deleted) {
					deleteMessageButton.style.display = "none"; // 버튼 숨기기
				} else {
					deleteMessageButton.textContent = "삭제";
					deleteMessageButton.classList.add("delete-button");
					deleteMessageButton.onclick = async () => {
						const confirmDelete = confirm("메시지를 삭제하시겠습니까?");
						if (confirmDelete) {
							try {
								const response = await fetch(`/chat/deleteMessage/${msg.id}`, { method: "PUT" });
								if (response.ok) {
									// messageElement.remove(); // 삭제 성공 시 메시지 삭제 (수정으로 대체)
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

				messageElement.appendChild(deleteMessageButton);
			} else {
				messageElement.textContent = `${msg.exited ? '나간 사용자' : msg.senderUserId}: ${msg.messageContent} ${msg.sendTime}  ${msg.read ? "<읽음>" : "<읽지않음>"}`;
				messageElement.classList.add("message-left");
				messageElement.dataset.messageId = msg.id;














				// 좋아요 버튼 생성
				const likeButton = document.createElement("button");
				likeButton.textContent = msg.liked ? "❤️" : "🤍";
				likeButton.classList.add("like-button");
				if (msg.liked) likeButton.classList.add("liked");
				likeButton.dataset.messageId = msg.id;

				// 좋아요 버튼 이벤트 추가
				likeButton.addEventListener("click", async function() {
					const messageId = this.dataset.messageId;
					const isLiked = this.classList.contains("liked"); // 현재 좋아요 상태 확인

					try {
						const response = await fetch(`/chat/${messageId}/like`, {
							method: "POST",
							headers: {
								"Content-Type": "application/json",
							},
							body: JSON.stringify(!isLiked), // 반대 상태로 변경
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






				//채팅 컨테이너 클릭 시 읽음으로 간주
				document.getElementById("chat-container").addEventListener("click", async function() {
					const unreadMessages = document.querySelectorAll(".message-item:not(.read)"); // 아직 읽지 않은 메시지들 찾기

					if (unreadMessages.length === 0) return; // 읽지 않은 메시지가 없으면 요청 안 함

					try {
						// 읽지 않은 메시지 ID 리스트 추출
						const messageIds = [...unreadMessages].map(msg => msg.dataset.messageId);

						// 서버로 읽음 처리 요청 (POST 요청)
						const response = await fetch(`/chat/markAsRead`, {
							method: "POST",
							headers: { "Content-Type": "application/json" },
							body: JSON.stringify({ messageIds }) // ID 배열을 서버로 전송
						});

						if (response.ok) {
							unreadMessages.forEach(msg => {
								msg.classList.add("read"); // 읽음 표시 스타일 추가
							});
							console.log("📩 모든 메시지가 읽음 처리됨!");
						} else {
							console.error("❌ 읽음 처리 실패!");
						}
					} catch (error) {
						console.error("⚠️ 메시지 읽음 처리 중 오류 발생:", error);
					}
				});















				messageElement.appendChild(likeButton);
			}

			chatBox.appendChild(messageElement);
		});

		//msg 끝













		// 스크롤 최하단으로 이동
		chatBox.scrollTop = chatBox.scrollHeight;
	} catch (error) {
		console.error("Error loading messages:", error);
		chatBox.innerHTML = "<div>Error loading messages.</div>";
	}
}









// 특정 채팅방(roomId)에 메시지를 전송하는 함수
export async function sendMessage(roomId, userid, messageIndex, recentExitedmemberId) {
	const messageInput = document.getElementById(`message-input-${roomId}`);
	const message = messageInput.value.trim();
	if (message === "") return;

	try {
		await fetch(`/chat/send/${roomId}`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ receiverUserId: userid, messageContent: message })
		});

		messageInput.value = "";
		//		loadMessages(roomId); // 전송 후 메시지 갱신
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

		// board 정보 비동기 요청
		const boardresponse = await fetch(`/chat/findBoard/${roomId}`);
		const board = await boardresponse.json();

		console.log(board);
		console.log(roomId, title, loggedUserId, userid);
		chatWindow.innerHTML = `
            <div id="chat-container">
                <div style="display:flex;">
                    <h2>채팅</h2>
                    <button class="close-chat" data-room-id="${roomId}">닫기</button>
                </div>
                <h3>${board.buy_Sell}</h3>
                <h3>카테고리 : ${board.category}</h3>
                <h3>판매물 ${board.title}</h3>
                <h3>가격 : ${board.price}</h3>
                <span>${title} (대화 상대: ${userid})</span>
                <div class="chat-header"></div>
                <div id="chat-box-${roomId}" class="chat-box"></div>
                <div class="chat-input">
                    <input type="text" id="message-input-${roomId}" placeholder="메시지를 입력하세요">
                    <button id="send-button-${roomId}" data-room-id="${roomId}">전송</button>
                </div>
            </div>
        `;
		document.body.appendChild(chatWindow);

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
	}

	//			setInterval(findMessageCount(roomId),1000);

}
//	let messageCount_prev = null;






















// chatModule.js

export async function loadChatRooms(loggedId) {			//채팅방 목록
	const chattingRoomListBody = document.getElementById("chattingRoomListBody");

	// 기존 목록 초기화 (중복 방지)
	chattingRoomListBody.innerHTML = "";

	try {
		const response = await fetch(`/chat/myChatRoom/${loggedId}`);
		const datas = await response.json();

		if (Array.isArray(datas) && datas.length > 0) {
			datas.forEach(data => {
				// 반복문 내부에서 새로운 <tr> 요소를 생성합니다.
				const row = document.createElement("tr");
				row.innerHTML = `
                    <td>${data.title}</td>
                    <td>${data.member2UserId}</td>
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
				chattingRoomListBody.appendChild(row);
			});
		} else {
			console.warn('No chatting rooms available.');
		}
	} catch (error) {
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

export function setUpExitRoomButton(loggedId) {
	document.querySelectorAll(".deleteRoom").forEach(button => {
		button.addEventListener("click", async function() {
			const deleteRoomId = this.getAttribute("data-deleteRoomId");
			const deleteUserId = this.getAttribute("data-deleteUserid");
			let chatWindow = document.getElementById(`chat-room-${deleteRoomId}`);

			try {
				const response = await fetch(`/chat/exitRoom/${deleteRoomId}`, {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
					body: JSON.stringify({ receiver: deleteUserId })
				});

				if (response.ok) {
					this.closest("tr").remove();

					if (intervalId) {
						clearInterval(intervalId); // 메시지 자동 로드 중지
						intervalId = null;
						console.log("채팅방 퇴장, 메시지 로드 중지");
					}

					loadChatRooms(loggedId);
					toggleChattingRoomList();
					chatWindow.remove();
					alert("채팅방을 나갔습니다.");

					//					location.reload();			//새로고침
				} else {
					alert("채팅방 나가기에 실패했습니다.");
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
