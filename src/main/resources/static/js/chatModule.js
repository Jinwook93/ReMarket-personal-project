// chatting 기능 관련 함수들을 모듈로 내보냅니다.

// 특정 채팅방(roomId) 메시지를 불러오는 함수
export async function loadMessages(roomId) {
	const loggedUserId = document.getElementById("loggedUserId").value;
    const chatBox = document.getElementById(`chat-box-${roomId}`);
    if (!chatBox) return;

    try {
        const response = await fetch(`/chat/loadmessages/${roomId}`);
        console.log(`${roomId}`);
        if (!response.ok) {
            console.error("Server returned an error:", response.status, response.statusText);
            chatBox.innerHTML = "<div>Error loading messages.</div>";
            return;
        }
        const messages = await response.json();
        console.log("Messages received:", messages);

        // 기존 메시지 초기화
        chatBox.innerHTML = "";
        
        if (!Array.isArray(messages)) {
            console.error("Expected messages to be an array, but got:", messages);
            chatBox.innerHTML = "<div>Invalid message format.</div>";
            return;
        }
        
messages.forEach(msg => {
    const messageElement = document.createElement("div");
    messageElement.textContent = `${msg.senderUserId}: ${msg.messageContent} ${msg.sendTime}`;
    
    console.log("로그인한 유저:", loggedUserId, "메시지 보낸 유저:", msg.senderUserId);

    // 로그인한 사용자와 보낸 사용자가 동일하면 삭제 버튼 추가
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
                        loadMessages(`${roomId}`);
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
        messageElement.classList.add("message-left");
    }

    chatBox.appendChild(messageElement);
});


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
            body: JSON.stringify({ messageContent: message })
        });
		
        messageInput.value = "";
        loadMessages(roomId); // 전송 후 메시지 갱신
    } catch (error) {
        console.error("메시지 전송 중 오류 발생", error);
    }
}

// 특정 채팅방을 열고 메시지 기능을 연결하는 함수
export async function openChatRoom(roomId, title, userid) {
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
        setInterval(() => loadMessages(roomId), 1000);

        // 메시지 전송 이벤트 추가
        document.getElementById(`send-button-${roomId}`).addEventListener("click", () => sendMessage(roomId, userid));
        document.getElementById(`message-input-${roomId}`).addEventListener("keypress", (event) => {
            if (event.key === "Enter") sendMessage(roomId, userid);
        });

        // 닫기 버튼 이벤트 추가
        chatWindow.querySelector(".close-chat").addEventListener("click", function () {
            chatWindow.remove();
        });
    }
}
