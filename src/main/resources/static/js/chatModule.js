// chatting 기능 관련 함수들을 모듈로 내보냅니다.

// 특정 채팅방(roomId) 메시지를 불러오는 함수
export async function loadMessages(roomId) {
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

		messages.forEach(msg => {
//			 if (msg.exitedSenderId !== null && String(msg.exitedSenderId) === String(loggedId)) {
//        return; // 현재 메시지는 출력하지 않고 다음 메시지로 이동
//    }
			const messageElement = document.createElement("div");
			messageElement.classList.add("message-item");


			// 로그인한 사용자와 보낸 사용자가 동일하면 삭제 버튼 추가
			// 메시지 내용과 좋아요 상태 처리
			messageElement.textContent = `${msg.senderUserId}: ${msg.messageContent} ${msg.sendTime} ${msg.liked ? "❤️" : "🤍"}`;
			if (String(msg.senderUserId) === String(loggedUserId)) {
				messageElement.classList.add("message-right");

				// 삭제 버튼 생성
				const deleteMessageButton = document.createElement("button");
				deleteMessageButton.textContent = "삭제";
				deleteMessageButton.classList.add("delete-button");
				deleteMessageButton.onclick = async () => {
					const confirmDelete = confirm("메시지를 삭제하시겠습니까?");
					if (confirmDelete) {
						try {
							const response = await fetch(`/chat/deleteMessage/${msg.id}`, { method: "DELETE" });
							if (response.ok) {
								messageElement.remove(); // 삭제 성공 시 메시지 삭제
								loadMessages(roomId);
							} else {
								alert("메시지 삭제에 실패했습니다.");
							}
						} catch (error) {
							console.error("메시지 삭제 중 오류 발생", error);
						}
					}
				};
				messageElement.appendChild(deleteMessageButton);
			} else {
				messageElement.textContent = `${msg.exited ? '나간 사용자' : msg.senderUserId}: ${msg.messageContent} ${msg.sendTime}  ${msg.read? "<읽음>": "<읽지않음>"}`;
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
document.getElementById("chat-container").addEventListener("click", async function () {
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
export async function sendMessage(roomId, userid) {
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
		loadMessages(roomId); // 전송 후 메시지 갱신
	} catch (error) {
		console.error("메시지 전송 중 오류 발생", error);
	}
}










// 특정 채팅방을 열고 메시지 기능을 연결하는 함수
export async function openChatRoom(roomId, title, loggedUserId, userid) {
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

		// 메시지 불러오기 및 새로고침
		loadMessages(roomId);
		setInterval(() => loadMessages(roomId), 2000);

		// 메시지 전송 이벤트 추가
		document.getElementById(`send-button-${roomId}`).addEventListener("click", () => sendMessage(roomId, userid));
		document.getElementById(`message-input-${roomId}`).addEventListener("keypress", (event) => {
			if (event.key === "Enter") sendMessage(roomId, userid);
		});

		// 닫기 버튼 이벤트 추가
		chatWindow.querySelector(".close-chat").addEventListener("click", function() {
			chatWindow.remove();
		});
	}
}

































// chatModule.js

export async function loadChatRooms(loggedId) {
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
		button.addEventListener("click", function() {
			const roomId = this.getAttribute("data-room-id");
			const title = this.getAttribute("data-title");
			const userid = this.getAttribute("data-userid");
			openChatRoom(roomId, title, loggedUserId, userid);
		});
	});
}

export function setUpExitRoomButton() {
	document.querySelectorAll(".deleteRoom").forEach(button => {
		button.addEventListener("click", async function() {
			const deleteRoomId = this.getAttribute("data-deleteRoomId");
			const deleteUserId = this.getAttribute("data-deleteUserid");

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
					alert("채팅방을 나갔습니다.");
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














