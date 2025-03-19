
//import { checkUserAlarmCount } from "./alarmModule.js";
import { getBoardMainFile } from "./boardModule.js";
import { formatCurrency } from "./formatCurrency.js";
import { formatDate } from "./formatDate.js";
import { CancelTrade, CompleteTrade, bookTrade1, bookTrade2, changeBookTrade, denyBookTrade, denyCreateTrade, enrollTrade1, enrollTrade2 } from "./tradeModule.js";



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
	return result;
}






function insertLineBreaks(text, maxLength) {
	let result = '';
	for (let i = 0; i < text.length; i += maxLength) {
		result += text.slice(i, i + maxLength) + '<br>';
	}
	return result;
}



//trade.accept1 && trade.accept2) || 

export function findTradeByBoardId(trades) {
	let result = null;
	for (let trade of trades) {
		console.log(trade);
		//		if (trade.boardEntity?.id === boardId && trade.accept1 && trade.accept2) {
		if ((trade.accept1 && trade.accept2) || (trade.booking1 && trade.booking2)) {
			result = trade;
			break;  // 원하는 메시지를 찾으면 루프를 종료
		}
	}
	//	console.log("결과" + result);
	return result;
}







// 특정 채팅방(roomId) 메시지를 불러오는 함수
export async function loadMessages(roomId, messageIndex, recentExitedmemberId, searchedMessageId) {

	console.log("✅ loadMessages 실행됨", { roomId, messageIndex, recentExitedmemberId });

	const loggedUserId = document.getElementById("loggedUserId").value;
	const loggedId = document.getElementById("loggedId").value;
	const chatBox = document.getElementById(`chat-box-${roomId}`);

	let matchedSearchedMessage = false;

	let filteredMessages = [];
	if (!chatBox) return [];

	//		await reloadDetails(roomId);
	try {
		const response = await fetch(`/chat/loadmessages/${roomId}`);
		if (!response.ok) {
			console.error("Server returned an error:", response.status, response.statusText);
			chatBox.innerHTML = "<div>Error loading messages.</div>";
			return;
		}

		const messages = await response.json();
		chatBox.innerHTML = `<div class="search-container" id ="search-container-${roomId}" style="display:none;">
            <input type="text" id="search-box-${roomId}" placeholder="메시지 검색...">
            <button id="search-button-${roomId}">🔍</button>
            <button id = "search-prev-button-${roomId}" style="display:none;"><	</button>
              <button id = "search-next-button-${roomId}"  style="display:none;"> >	</button>
              <button id = "close-search-message-${roomId}"> X	</button>
   	    </div>`; // 기존 메시지 초기화

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

			filteredMessages.push(msg);			//필터링된 메시지에 msg 저장


			
						
			if (msg.statusBar === true) {
				const statusBar = document.createElement("p");
				statusBar.className = "statusBar"; // className으로 클래스 추가
				statusBar.innerText = "- " + msg.messageContent + " -";

				// 직접 스타일 적용
				
				
				
			 if (
            filteredMessages[0] === msg &&
            msg.statusBar === true &&
            msg.senderUserId === loggedUserId &&
            msg.messageContent.includes("님이 입장하였습니다")
        ) {
            statusBar.style.display = "none";
        } else {
            statusBar.style.display = "flex";
        }
//				statusBar.style.display = "flex";
				statusBar.style.justifyContent = "center";
				statusBar.style.alignItems = "center";
				statusBar.style.width = "100%";
				statusBar.style.padding = "8px 0";
				statusBar.style.fontSize = "14px";
				statusBar.style.fontWeight = "bold";
				statusBar.style.color = "#555";
				statusBar.style.backgroundColor = "#f1f1f1";
				statusBar.style.borderRadius = "8px";
				statusBar.style.margin = "10px 0";

				chatBox.appendChild(statusBar);
				return;
			}






			const messageElement = document.createElement("div");
			messageElement.classList.add("message-item");
			messageElement.id = `msg-${msg.id}`;
			//			let msgId = messageElement.id.split("-")[1];

			const formattedMessage = insertLineBreaks(msg.messageContent, 20); // 20글자마다 줄바꿈


			const profileContainer = document.createElement("div");
			profileContainer.classList.add("profile-container");

			const profileImage = document.createElement("img");
			//			profileImage.src = msg.exited  ? "/boardimage/nullimage.jpg" : msg.profileImageUrl1 == null ? "/boardimage/nullimage.jpg" : msg.profileImageUrl1;
			profileImage.src = msg.profileImageUrl1 == null ? "/boardimage/nullimage.jpg" : msg.profileImageUrl1;

			profileImage.alt = `${msg.senderUserId}'s profile picture`;
			profileImage.classList.add("profile-image", "message-Left");
			const userid = document.createElement("p");
			//			userid.textContent =  msg.exited ? '(나간 사용자)' : msg.senderUserId;
			userid.textContent = msg.senderUserId;
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
               
               	 	${msg.alarmType == false || msg.alarmType == null ? (msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent) : msg.messageContent}
                   <br>
                       <br>
    <span class="send-time">${msg.sendTime}</span>
												${String(msg.senderUserId) === String(loggedUserId) ? `
							<span class="read-status">${msg.read ? "읽음" : "읽지않음"}</span> ` : ""}
`;
				} else {
					messageElement.innerHTML = `
             	${msg.alarmType == false || msg.alarmType == null ? (msg.messageContent.length > 20 ? insertLineBreaks(msg.messageContent, 20) : msg.messageContent) : msg.messageContent}
		       <br><br>
    <span class="send-time">${msg.sendTime}</span>
						${String(msg.senderUserId) === String(loggedUserId) ? `
							<span class="read-status">${msg.read ? "읽음" : "읽지않음"}</span> ` : ""}
`;
				}
			}

			// 로그인한 사용자와 보낸 사용자가 동일한 경우
			if (String(msg.senderUserId) === String(loggedUserId)) {
				messageElement.classList.add("message-right");

				// 여러 개의 요소를 가져오므로 forEach()로 순회하면서 숨김 처리
				const messageButtonSelect = messageElement.querySelectorAll(".messageButtonSelect");
				messageButtonSelect.forEach(buttons => {
					buttons.style.display = "none";
				});
				//				if(msg.alarmType === true && msg.messageContent.includes("거래를 희망합니다")){
				//				 // 버튼 컨테이너 생성
				//        const buttonContainer = document.createElement("div");
				//        buttonContainer.classList.add("messageButtonSelect");
				//        buttonContainer.innerHTML = `
				//            <hr>
				//            <button id="agreeMember2-4107">거래하기</button>
				//            <button id="denyMember2-4107">거절하기</button>
				//        `;
				//
				//        // 메시지에 버튼 추가
				//        messageElement.appendChild(buttonContainer);
				//				}

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
			//			document.getElementById(`chat-container-${roomId}`).addEventListener("click", async function() {
			//				const unreadMessages = document.querySelectorAll(".message-item:not(.read)");
			//
			//				if (unreadMessages.length === 0) return;
			//
			//				try {
			//					const messageIds = [...unreadMessages].map(msg => msg.dataset.messageId);
			//
			//					const response = await fetch(`/chat/markAsRead`, {
			//						method: "POST",
			//						headers: { "Content-Type": "application/json" },
			//						body: JSON.stringify({ messageIds }),
			//					});
			//
			//					if (response.ok) {
			//						unreadMessages.forEach(msg => {
			//							msg.classList.add("read");
			//						});
			//					} else {
			//						console.error("❌ 읽음 처리 실패!");
			//					}
			//				} catch (error) {
			//					console.error("⚠️ 메시지 읽음 처리 중 오류 발생:", error);
			//				}
			//			});


			let markAsReadTimeout = null; // 디바운스 타이머

			document.getElementById(`chat-container-${roomId}`).addEventListener("click", () => {
				clearTimeout(markAsReadTimeout); // 기존 타이머 제거
				markAsReadTimeout = setTimeout(async () => {
					const chatContainer = document.getElementById(`chat-container-${roomId}`);
					if (!chatContainer) return;

					const unreadMessages = chatContainer.querySelectorAll(".message-item:not(.read)");
					if (unreadMessages.length === 0) return; // 읽을 메시지가 없으면 요청 X

					// ✅ 읽음 표시를 먼저 UI에서 적용
					unreadMessages.forEach(msg => msg.classList.add("read"));

					try {
						const messageIds = [...unreadMessages].map(msg => msg.dataset.messageId);
						const response = await fetch(`/chat/markAsRead`, {
							method: "POST",
							headers: { "Content-Type": "application/json" },
							body: JSON.stringify({ messageIds }),
						});

						if (!response.ok) {
							console.error("❌ 읽음 처리 실패!");
							// ❌ 실패 시 다시 읽음 해제 (UI 복구)
							unreadMessages.forEach(msg => msg.classList.remove("read"));
						}
					} catch (error) {
						console.error("⚠️ 메시지 읽음 처리 중 오류 발생:", error);
						// ❌ 실패 시 다시 읽음 해제 (UI 복구)
						unreadMessages.forEach(msg => msg.classList.remove("read"));
					}
				}, 300); // ✅ 300ms 디바운스 적용
			});






			//   chatBox.appendChild(profileContainer);
			//    chatBox.appendChild(messageElement);

			// 메시지 클릭 이벤트 추가  ${msg.id}
			messageElement.addEventListener("click", function(e) {
				//				console.log("클릭된 메시지:", msg.messageContent, "보낸 사람:", msg.senderUserId, "로그인된 유저:", loggedUserId);
				const messageInput = document.getElementById(`message-input-${msg.roomId}`);
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


			if ((searchedMessageId !== null) && (msg.id === Number(searchedMessageId))) {
				const chatBoxHeight = chatBox.clientHeight; // 채팅창 높이
				const messageHeight = messageElement.clientHeight; // 메시지 높이
				const messagePosition = messageElement.offsetTop - chatBox.offsetTop; // 메시지의 상대적 위치

				// 메시지를 채팅창 중앙에 위치하도록 스크롤 이동
				chatBox.scrollTop = messagePosition - (chatBoxHeight / 2) + (messageHeight / 2);

				matchedSearchedMessage = true;
			}





		});


		//msg 끝


		// 스크롤 최하단으로 이동
		if (!matchedSearchedMessage) {
			chatBox.scrollTop = chatBox.scrollHeight;
		}




		//		if ((searchedMessageId !== null) && (msg.id === Number(searchedMessageId))) {
		//			const chatBox = document.getElementById(`chat-box-${msg.roomId}`);
		//
		//				const chatBoxHeight = chatBox.clientHeight; // 채팅창 높이
		//				const messagePosition = messageElement.offsetTop - chatBox.offsetTop; // 메시지 위치
		//				chatBox.scrollTop = messagePosition - chatBoxHeight / 2; // 중앙에 위치하도록 조정
		//				
		//		} else {
		//			chatBox.scrollTop = chatBox.scrollHeight;
		//		}


		//		console.log(filteredMessages);

		return filteredMessages;			//다시 나갔다 들어온 사용자의 메시지 로드에 쓰일 용도


	} catch (error) {
		console.error("Error loading messages:", error);
		chatBox.innerHTML = "<div>Error loading messages.</div>";
		return [];
	}
	//	console.log(filteredMessages);
	//	return filteredMessages;			//다시 나갔다 들어온 사용자의 메시지 로드에 쓰일 용도
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
		//=====
		//	updateChatRoomOrder(roomId);
		//				await loadChatRooms(loggedId);
		//				setUpEnterRoomButton(loggedUserId);
		//				setUpExitRoomButton();
		//				loadMessages(Number(roomId), messageIndex, recentExitedmemberId);
		//==========

		//		await checkUserAlarmCount(loggedId);

		//const intervalId = setInterval(() => loadMessages(Number(roomId), messageIndex, recentExitedmemberId), 2000);

		// To stop the interval after some condition or action
		//clearInterval(intervalId);

	} catch (error) {
		console.error("메시지 전송 중 오류 발생", error);
	}
}










// 특정 채팅방을 열고 메시지 기능을 연결하는 함수
export async function openChatRoom(roomId, title, loggedUserId, userid, loggedFlag, messageId) {
	let chatWindow = document.getElementById(`chat-room-${roomId}`);


	if (chatWindow) {
		chatWindow.remove(); // 이미 열려있으면 닫기
	} else {
		chatWindow = document.createElement("div");
		chatWindow.id = `chat-room-${roomId}`;
		chatWindow.className = "chat-window";
		//   <!--         ${trade.tradeStatus=== '완료'? '<h2>판매완료</h2>':""} -->
		// board 정보 비동기 요청
		const boardresponse = await fetch(`/chat/findBoard/${Number(roomId)}`);
		const board = await boardresponse.json();
		//		const trade = findTradeByBoardId(board.trades);					//해당 보드가 속한 trades 탐색	
		//		console.log("트레이드 상태 : "+trade.tradeStatus);
		//		console.log(board);
		//		console.log(roomId, title, loggedUserId, userid);
		const boardMainFile = await getBoardMainFile(board.id);
		const trade = findTradeByBoardId(board.trades);

		const alarmResponse = await fetch(`/alarm/findTradeAlarm/${Number(roomId)}`);
		const alarm = await alarmResponse.json();
		console.log(alarm);
		chatWindow.innerHTML = `
    <div class="chat-container" id="chat-container-${roomId}">
        <div style="display: flex; justify-content: space-between; align-items: center; 
                    background-color: lightgray; padding: 10px; border-radius: 5px; 
                    margin-top: 10px; margin-bottom: 10px;">
            <div>
                <h2 style="margin: 0;">${userid}</h2>
            </div>
               	   
            <div style="display: flex;">
         
         		 <button  id="toggleDetails-${roomId}"
                        style="background-color: orange; color: white; border: none; 
                        padding: 5px 10px; cursor: pointer; border-radius: 3px;margin-right:5px;">
                    <b>i</b>
            		    </button>
         
               <button id="data-search-button-${roomId}" 
                        style="background-color: green; color: white; border: none; 
                        padding: 3px 5px; cursor: pointer; border-radius: 3px;margin-right:5px;">
                    🔍
            		    </button>
            
                <button class="close-chat" data-room-id="${roomId}" 
                        style="background-color: red; color: white; border: none; 
                        padding: 5px 10px; cursor: pointer; border-radius: 3px;">
                    X
                </button>
            </div>
        </div>

        <!-- 🔹 토글 가능한 항목들 -->
    

        <div id="details-${roomId}" style="display:block;">
            <div id="BoardTitleButton-${board.id}" style="display:flex;background-Color:white;">
                <div>
                    <img src=${boardMainFile} width="100" height="100" style="margin-left:5px;margin-right:5px;">
                </div>
            <div style="display: flex; justify-content: center; flex-direction: column;">
                    <h3 style = "margin-bottom:5px;"> ${board.title}</h3>
                    <h3>가격: ${formatCurrency(board.price)}원</h3>
                    <div class="buttons-container" style="display: flex;flex-direction: row;">

				
                      </div>
                </div>
            </div> 
        </div>

        <!-- 🔹 검색 기능 추가 -->
     <!--   <div class="chat-header"></div>
		 <div class="search-container" style="display:flex;">
            <input type="text" id="search-box-${roomId}" placeholder="메시지 검색...">
            <button id="search-button-${roomId}">검색</button>
            <button id = "search-prev-button-${roomId}"><	</button>
              <button id = "search-next-button-${roomId}"> >	</button>
    	    </div>    -->

        <!-- 🔹 채팅 메시지 박스 -->
        <div id="chat-box-${roomId}" class="chat-box" style="overflow-y: auto; height: 400px;">

        </div>

        <input type="button" style="display:none;" id="parentMessageButton">
        <div class="chat-input">
            <input type="hidden" id="parentMessageId">
            <input type="text" id="message-input-${roomId}" placeholder="메시지를 입력하세요">
            <button id="send-button-${roomId}" data-room-id="${roomId}">전송</button>
        </div>
    </div>
`;

		document.body.appendChild(chatWindow);

		await reloadDetails(roomId, loggedUserId);

		// 채팅창이 생성된 후 드래그 이벤트 핸들러 추가
		const chatContainer = document.getElementById(`chat-container-${roomId}`);
		chatContainer.style.zIndex = 11;

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
		//		if (intervalId) {
		//			clearInterval(intervalId);
		//			console.log("기존 interval 제거 완료");
		//		}

		//		console.log(room);
		if (loggedFlag === "logged1") {
			//			let messageIndex = Number(room.messageIndex1);
			loadMessages(Number(roomId), Number(room.messageIndex1), Number(room.recentExitedmemberId), messageId);
			//-		intervalId = setInterval(() => loadMessages(Number(roomId), Number(room.messageIndex1), Number(room.recentExitedmemberId)), 2000);
			// 메시지 전송 이벤트 추가
			//		intervalId = setInterval(() => loadMessages(Number(roomId), messageIndex, recentExitedmemberId), 2000);

			document.getElementById(`send-button-${roomId}`).addEventListener("click", () => sendMessage(roomId, userid, Number(room.messageIndex1), Number(room.recentExitedmemberId)));
			document.getElementById(`message-input-${roomId}`).addEventListener("keypress", (event) => {
				if (event.key === "Enter") sendMessage(roomId, userid, messageIndex1, recentExitedmemberId);
			});


		} else if (loggedFlag === "logged2") {
			//			let messageIndex = Number(room.messageIndex2);
			loadMessages(Number(roomId), Number(room.messageIndex2), Number(room.recentExitedmemberId), messageId);
			//-			intervalId = setInterval(() => loadMessages(Number(roomId), Number(room.messageIndex2), Number(room.recentExitedmemberId)), 2000);
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


		//				const boardTitleButton = document.getElementById(`BoardTitleButton-${board.id}`);
		//		if (boardTitleButton) {
		//			boardTitleButton.removeEventListener("click", (event) => boardTitleButtonClickHandler(event, board)); // 기존 이벤트 제거
		//			boardTitleButton.addEventListener("click", (event) => boardTitleButtonClickHandler(event, board)); // 새 이벤트 추가
		//		}




	}

	//			setnterval(findMessageCount(roomId),1000);

}
//	let messageCount_prev = null;









export async function boardTitleButtonClickHandler(event, board) {
	const loggedId = document.getElementById("loggedId").value;
	const loggedUserId = document.getElementById("loggedUserId").value;


	const roomResponse = await fetch(`/chat/findRoomByBoardId/${board.id}`);
	const room = await roomResponse.json();


	const alarmResponse = await fetch(`/alarm/findTradeAlarm/${room.id}`);
	const alarm = await alarmResponse.json();

	let messageIndex = null;
	let trade = null;


	//거래확정된 거래 검색
	for (const selectedTrade of board.trades) {
		if ((selectedTrade.accept1 && selectedTrade.accept2)
			|| (selectedTrade.booking1 === true && selectedTrade.booking2 === true)
			&& selectedTrade !== null) {
			{
				trade = selectedTrade;
				break;
			}
		}

}
		//예약 확정된 거래 검색
		//	let bookedTrade = null;
		//	for (const selectedTrade of board.trades) {
		//		if (selectedTrade.booking1 === true && selectedTrade.booking2 === true  && selectedTrade !== null) {
		//			bookedTrade = selectedTrade;
		//			break;
		//		}
		//	}





		// 클릭된 요소가 <button> 태그인지 확인
		if (event.target.tagName.toLowerCase() === 'button') {
			console.log("버튼 클릭은 제외됩니다.");
			if (event.target.id === `chat-enroll-Book1-${board.id}`) {			//예약신청
				const boardId = event.target.id.replace("chat-enroll-Book1-", "");
				console.log("상대 아이디" + board.memberEntity.id);

				bookTrade1(Number(boardId), Number(loggedId), Number(board.memberEntity.id), loggedUserId);
			} else if (event.target.id === `chat-enroll-Book2-${alarm.id}`) {	//예약승인
				const alarmId = event.target.id.replace("chat-enroll-Book2-", "");
				bookTrade2(Number(alarmId), loggedUserId);
			} else if (event.target.id === `chat-deny-enroll-Book2-${alarm.id}`) {	//예약거절
				const alarmId = event.target.id.replace("chat-deny-enroll-Book2-", "");
				denyBookTrade(Number(alarmId), loggedUserId);
			} else if (event.target.id === `chat-change-enroll-Book2-${board.id}`) {	//거래 상태 변경 (보드 관리자가  승인)
				const boardId = event.target.id.replace("chat-change-enroll-Book2-", "");
				//		  const roomResponse = await fetch(`/chat/findRoomByBoardId/${boardId}`);
				//       		 const room = await roomResponse.json(); 	
				changeBookTrade(Number(room.id), loggedUserId);
			}


			if (event.target.id === `chat-enroll-trade-${board.id}`) {			//거래신청
				enrollTrade1(Number(board.id), Number(loggedId), Number(board.memberEntity.id), loggedUserId);
			} else if (event.target.id === `chat-agreeMember2-${alarm.id}`) {	//거래승인
				const alarmId = event.target.id.replace("chat-agreeMember2-", "");
				enrollTrade2(Number(alarmId), loggedUserId);
			} else if (event.target.id === `chat-denyMember2-${alarm.id}`) {	//거래거절
				const alarmId = event.target.id.replace("chat-denyMember2-", "");
				denyCreateTrade(Number(alarmId), loggedUserId);
			} else if (trade != null && event.target.id === `chat-complete2-Sell-${trade.id}`) {	//거래 완료2 (보드 관리자가 먼저 승인)
				const tradeId = event.target.id.replace("chat-complete2-Sell-", "");
				CompleteTrade(tradeId, "isMember2");
			} else if (trade != null && event.target.id === `chat-complete1-Sell-${trade.id}`) {	//거래 완료1 (거래 희망자가 승인)
				const tradeId = event.target.id.replace("chat-complete1-Sell-", "");
				CompleteTrade(tradeId, "isMember1");
			}
			else if (trade != null && event.target.id === `chat-cancel-trade-${trade.id}`) {			//거래취소
				const tradeId = event.target.id.replace("chat-cancel-trade-", "");
				CancelTrade(Number(tradeId), loggedId);
			}




			if (loggedUserId === room.member1UserId && room !== null) {
				messageIndex = room.messageIndex1;
				//					openChatRoom(roomId, board.title, room.member1UserId, room.member2UserId, "logged1");
			} else if (loggedUserId === room.member2UserId && room !== null) {
				messageIndex = room.messageIndex2;
				//					openChatRoom(roomId, room.title, room.member2UserId, room.member1UserId, "logged2");
			}


			await loadChatRooms(loggedId);
			setUpEnterRoomButton(loggedUserId);
			setUpExitRoomButton();
			console.log("여기 실행 1");
			//        document.getElementById(`BoardTitleButton-${board.id}`).innerHTML =``;
			//			 loadMessages(roomId, messageIndex, room.recentExitedmemberId);
			return; // 이벤트 중단 (페이지 이동 X)
		}

		// board.id를 문자열로 받아서 페이지 이동
		const boardId = `${board.id}`;
		window.location.href = `/board/view/${boardId}`;
	}









	export async function loadChatRooms(loggedId) {
		const numericLoggedId = Number(String(loggedId).trim());
		if (Number.isNaN(numericLoggedId)) {
			console.error("Error: loggedId가 올바르지 않습니다.", loggedId);
			return;
		}

		const loggedUserId = document.getElementById("loggedUserId").value;
		const chattingRoomListBody = document.getElementById("chattingRoomListBody");
		const chattingRoomScroll = document.getElementById("chattingRoomScroll");

		try {
			const response = await fetch(`/chat/myChatRoom/${numericLoggedId}`);
			if (!response.ok) throw new Error("Failed to fetch chat rooms");

			const datas = await response.json();

			// 채팅방 필터링
			const visibleDatas = datas.filter(data =>
				(data.member1UserId === loggedUserId && data.member1Visible) ||
				(data.member2UserId === loggedUserId && data.member2Visible)
			);

			// 채팅방 정렬 (예: createTime 기준으로 정렬)
			//        visibleDatas.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

			// 기존 목록과 비교 후 변경된 부분만 업데이트
			const existingRows = [...chattingRoomListBody.children];
			const fragment = document.createDocumentFragment();
			let hasChanged = false;

			if (visibleDatas.length === 0) {
				// 기존 목록을 초기화
				chattingRoomListBody.innerHTML = "";
				chattingRoomListBody.innerHTML = `
                <tr>
                    <td colspan="2" style="text-align: center; padding: 20px; font-size: 18px; color: gray;">
                        입장할 수 있는 채팅방이 없습니다.
                    </td>
                </tr>
            `;
			} else {
				for (const data of visibleDatas) {
					const recentRoomMessage = await findRecentRoomMessage(Number(data.id)); // 메시지 객체 데이터임
					const mainFile = await getBoardMainFileByRoomId(data.id);
					const roomId = data.id.toString();
					const unReadMessageCount = await checkUnReadMessageCount2(data.id);
					const tradeResponse = await fetch(`/trade/findTrade/${data.id}`);			//거래 상태
					const trade = await tradeResponse.json();
						console.log("트레이드");
					console.log(trade);
					// 기존 DOM에서 같은 roomId가 있는지 확인
					const existingRow = existingRows.find(row => row.dataset.roomId === roomId);
					const newContent = `
                    <td style="width:500px;">
                        <div style="display: flex; justify-content: space-between; align-items: center;
                                    background-color: lightgray; padding: 10px; border-radius: 0px;
                                    margin-top: 10px; margin-bottom: 10px;">
                            <span>
                      ${trade !== null  && (trade.booking1 !== null || trade.booking2 !== null) && trade.booking1 === true && trade.booking2 === true && trade.tradeStatus !== '완료' ? `<button class ="small-btn reserve">예약 중</button>` : ""}      
                      ${trade !== null  && (trade.accept1 !== null || trade.accept2 !== null)   && (trade.accept1 === true && trade.accept2 === true)   && trade.tradeStatus !== '완료'  ? `<button  class ="small-btn progress">거래 중</button>` : ""}      
                       ${trade !== null && trade.tradeStatus === '완료' ? `<button class="small-btn complete">거래완료</button>` : ""}
                       <b>${data.title}</b> 
                            </span>
                            <button style="background-color: red; color: white; border: none;
                                           paddng: 5px 10px; cursor: pointer; border-radius: 3px;"
                                    class="deleteRoom"
                                    data-deleteRoomId="${data.id}"
                                    data-deleteTitle="${data.title}"
                                    data-deleteUserid="${loggedUserId === data.member1UserId ? data.member2UserId : data.member1UserId}">
                                X
                            </button>
                        </div>
                        <div style="display:flex;">
                            <div style="display: flex; justify-content: center; align-items: center; margin: 10px 0;">
                                <img src="${mainFile}" width="100" height="100" alt="대표 이미지"
                                    style="margin-left: 10px; margin-right: 10px; border-radius: 5px; border: 0.5px solid black;">
                            </div>
                            <div style="width: 100%; max-width: 1200px; margin: 0 auto; padding: 10px; background-color: #f5f5f5; border-radius: 10px;">
                                <div style="margin-top: 15px;display: flex;">
                                    ${recentRoomMessage
							? `${recentRoomMessage.senderUserId ? ` 
                                            ${unReadMessageCount > 0 ? `<div id="unReadMessageCountButton2">  <b>${unReadMessageCount}</b></div>` : ""} 
                                            <img src="/icon/userIcon.png" width="20" height="20" alt="상대방"> ${recentRoomMessage.senderUserId} : </b>` : ""} 
                                            ${recentRoomMessage.messageContent || ""}`
							: `최근 메시지 없음`}
                                </div>
                                <div style="margin-top: 5px; color:gray; font-weight: 300;">
                    <!--    ${recentRoomMessage?.id ? formatDate(recentRoomMessage.sendTime) : formatDate(data.createTime)}  -->
                    			 ${formatDate(data.createTime)}  
                                </div>
                       <div style="margin-top: 10px;">
                                    <button class="enterChat"
                                            data-room-id="${data.id}"
                                            data-title="${data.title}"
                                            data-userid="${data.member2UserId}"
                                            style="background-color: #800080; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold;
                                                   transition: background-color 0.3s ease, transform 0.2s ease;">
                                        <div style="display: flex; align-items: center;">
                                            <img src="/icon/messageIcon.png" width="15" height="15" style="margin-right: 5px;">
                                            채팅하기
                                        </div>
                                    </button>
                                </div>
                                <div class="date-text" style="font-size:15px;">
                                    <img src="/icon/userIcon.png" width="20" height="20" alt="상대방">
                                    ${loggedUserId === data.member2UserId ? data.member1UserId : data.member2UserId}
                                </div>
                            </div>
                        </div>
                    </td>
                `;

					if (!existingRow) {
						// 새로운 채팅방이면 추가
						const row = document.createElement("tr");
						row.dataset.roomId = roomId;
						row.innerHTML = newContent;
						fragment.appendChild(row);
						hasChanged = true;
					} else if (existingRow.innerHTML !== newContent) {
						// 기존 채팅방 내용이 변경되었으면 업데이트
						existingRow.innerHTML = newContent;
						hasChanged = true;
					}
				}
			}

			// 필요 없는 행 삭제
			existingRows.forEach(row => {
				if (!visibleDatas.some(data => data.id.toString() === row.dataset.roomId)) {
					row.remove();
					hasChanged = true;
				}
			});

			// 변경 사항이 있을 때만 DOM 업데이트
			if (hasChanged) {
				chattingRoomListBody.appendChild(fragment);
			}

			// 스크롤 처리
			if (visibleDatas.length > 5) {
				chattingRoomScroll.style.maxHeight = "80%";
				chattingRoomScroll.style.overflowY = "auto";
			} else {
				chattingRoomScroll.style.maxHeight = "";
				chattingRoomScroll.style.overflowY = "";
			}

		} catch (error) {
			console.error("Failed to fetch chat rooms:", error);
		}
	}

	export async function updateChatRoomOrder(roomId) {
		const chattingRoomListBody = document.getElementById("chattingRoomListBody");
		const roomElement = document.querySelector(`[data-room-id="${roomId}"]`);

		if (roomElement) {
			// 🔹 해당 채팅방을 목록의 최상단으로 이동
			chattingRoomListBody.prepend(roomElement);
		}
	}




	//return에 반드시 await을 쓸 필요는 없다.
	// 다만, async 함수 내부에서 await을 사용하지 않으면, 반환값이 Promise 객체가 된다.
	//※수정해야 할 문제점
	//fetch가 비동기 작업인데도 await 없이 return data;를 사용하여, 반환값이 Promise<void>가 되어버립니다.
	//return data;는 then 내부에 있기 때문에 searchChatRoomsAndMessage가 데이터를 반환하지 않습니다.
	//호출하는 쪽에서 await으로 값을 받을 수 있도록 수정해야 합니다.

	//export async function searchChatRoomsAndMessage(searchcontent) {
	//
	//
	//try{
	//const response = await  fetch(`/search/room/result?search=${searchcontent}`);
	//const data = await response.json();
	//	if (!response.ok) {
	//            throw new Error(`데이터를 가져올 수 없습니다: ${response.status}`);
	//        }
	//	return data;
	//} catch (error) {
	//        console.error('Error:', error);
	//        return null; // 오류 발생 시 null 반환
	//    }
	//
	////    .then(response => response.json())
	////    .then(data => {
	////        console.log("Rooms:", data.rooms);
	////        console.log("Messages:", data.messages);
	////
	////        // 각 요소를 개별적으로 접근
	////        data.rooms.forEach(room => {
	////            console.log(`Room ID: ${room.id}, Name: ${room.roomName}`);
	////        });
	////
	////        data.messages.forEach(msg => {
	////            console.log(`Message ID: ${msg.id}, Content: ${msg.content}`);
	////        });
	////        
	////        return data;
	////    })
	////    .catch(error => console.error('Error:', error));	
	////
	////
	////}
	//
	//}



	export async function searchMessage(searchcontent) {
		try {
			const response = await fetch(`/search/message/result?search=${searchcontent}`);
			if (!response.ok) {
				throw new Error(`메시지를 가져올 수 없습니다: ${response.status}`);
			}
			const datas = await response.json();
			console.log("📌 검색된 메시지 데이터:", datas); // ✅ 데이터 출력 확인
			return datas;
		} catch (error) {
			console.error('Error:', error);
			return null; // 오류 발생 시 null 반환
		}
	}





	export function setUpEnterRoomButton(loggedUserId) {
		document.querySelectorAll(".enterChat").forEach(button => {
			button.addEventListener("click", async function() {

				const roomId = Number(this.getAttribute("data-room-id")) ? Number(this.getAttribute("data-room-id")) : Number(this.getAttribute("data-search-room-id"));
				const title = this.getAttribute("data-title") ? this.getAttribute("data-title") : this.getAttribute("data-search-title");
				const userid = this.getAttribute("data-userid") ? this.getAttribute("data-userid") : this.getAttribute("data-search-userid");
				//			openChatRoom(roomId, title, loggedUserId, userid);

				const messageId = Number(this.getAttribute("data-search-message-id")) ? Number(this.getAttribute("data-search-message-id")) : null;

				// 채팅방 열기 및 메시지 로드
				if (loggedUserId !== userid) {

					if (messageId !== null) {
						openChatRoom(roomId, title, loggedUserId, userid, "logged1", messageId);
					} else {
						openChatRoom(roomId, title, loggedUserId, userid, "logged1");
					}
				} else {
					const roomResponse = await fetch(`/chat/findRoom/${roomId}`);
					const room = await roomResponse.json();
					if (messageId !== null) {
						openChatRoom(roomId, title, loggedUserId, room.member1UserId, "logged2", messageId);
					} else {
						openChatRoom(roomId, title, loggedUserId, room.member1UserId, "logged2");
					}
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
						body: JSON.stringify({ receiver: deleteUserId })			//나가기에 관한  상대방 Id
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
				return data;  // Return the data
			})
			.catch(error => {
				console.error("Error fetching message count:", error);  // Print any error that occurs
			});
	}



	// 게시글의 대표 사진을 받아오는 함수
	export async function getBoardMainFileByRoomId(roomId) {
		// DB에서 roomId 내의 loggedUserId, userid 간의 메일 크기를 실시간으로 조회
		return fetch(`/chat/getBoardMainFileByRoomId/${roomId}`, {
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
				return response.text();
			})
			.then(data => {
				//        console.log(data);  // Print the received data
				return data;  // Return the data
			})
			.catch(error => {
				console.error("Error fetching message count:", error);  // Print any error that occurs
			});
	}

	//로그인 id 당 읽지않음 메시지 총 갯수
	export async function checkUnReadMessageCount(loggedId) {
		try {
			const response = await fetch(`/chat/unReadMessagesCount/${loggedId}`);
			const data = await response.text();
			//		console.og(data);
			return Number(data); // { content, totalPages, totalElements, number, size }
		} catch (error) {
			console.error("목록을 불러오는 중 오류 발생:", error);
		}
	}

	//채팅방 하나당 읽지않음 메시지 갯수
	export async function checkUnReadMessageCount2(roomId) {
		try {
			const response = await fetch(`/chat/unReadMessageCount2/${roomId}`);
			const data = await response.text();
			//		console.log(data);
			return Number(data); // { content, totalPages, totalElements, number, size }
		} catch (error) {
			console.error("목록을 불러오는 중 오류 발생:", error);
		}
	}






	//이벤트 중복 실행 방지 변수 
	const boardButtonHandlers = new Map();

	export async function reloadDetails(roomId, loggedUserId) {
		//console.log(loggedUserId);
		const boardresponse = await fetch(`/chat/findBoard/${Number(roomId)}`);
		const board = await boardresponse.json();
		//		const trade = findTradeByBoardId(board.trades);					//해당 보드가 속한 trades 탐색	
		//		console.log("트레이드 상태 : "+trade.tradeStatus);
		//		console.log(board);
		//		console.log(roomId, title, loggedUserId, userid);
		const boardMainFile = await getBoardMainFile(board.id);
		const trade = findTradeByBoardId(board.trades);

		const alarmResponse = await fetch(`/alarm/findTradeAlarm/${Number(roomId)}`);
		const alarm = await alarmResponse.json();
		const detailsElement = document.querySelector(`#details-${roomId}`);
		if (!detailsElement) {
			console.error(`details-${roomId} 요소를 찾을 수 없습니다.`);
			return;
		}

		// 버튼 부분만 업데이트 (기존 내용을 유지하면서 버튼 상태만 변경)
		const buttonsContainer = detailsElement.querySelector(".buttons-container");
		if (buttonsContainer) {
			buttonsContainer.innerHTML = ``;
			buttonsContainer.innerHTML = `
        ${(board.memberEntity.userid !== loggedUserId) && board.trades.length === 0 ?
					`<button id="chat-enroll-trade-${board.id}" class = "applyTrade">거래신청</button> 
             <button id="chat-enroll-Book1-${board.id}" class = "bookTrade">예약신청</button> 
 ` : ""}

   <!--     ${(board.memberEntity.userid === loggedUserId)
					&& board.trades.length === 0
					&& alarm.action === "상대방 동의 확인" ?
					`<button id="chat-agreeMember2-${alarm.id}" >수락</button> 
             <button id="agreeMember2-${alarm.id}">거절</button>` : ""}

        ${board.memberEntity.userid === loggedUserId
					&& board.trades.length === 0
					&& alarm.action === "예약" ?
					`<button id="chat-enroll-Book2-${alarm.id}">예약 수락</button> 
             <button id="chat-deny-enroll-Book2-${alarm.id}">예약 거절</button>` : ""} -->

        ${trade !== null
					&& trade.booking1 === true && trade.booking2 === true
					&& trade.accept1 !== true && trade.accept2 !== true ?
					`<button class = "booking">예약 중</button>` : ""}

        ${(board.memberEntity.userid === loggedUserId)
					&& trade !== null
					&& trade.booking1 === true && trade.booking2 === true
					&& trade.accept1 !== true && trade.accept2 !== true ?
					`<button id="chat-change-enroll-Book2-${board.id}" class = "changeTrading">거래 중으로 전환</button>` : ""}

        ${trade !== null
					&& trade.booking1 !== true && trade.booking2 !== true
					&& trade.accept1 === true && trade.accept2 === true
					&& trade.tradeStatus !== '완료' ?
					`<button type="button"  class = "trading">거래 진행 중</button>` : ""}

        ${trade !== null
					&& trade.booking1 !== true && trade.booking2 !== true
					&& trade.accept1 === true && trade.accept2 === true
					&& trade.tradeStatus !== '완료' && board.memberEntity.userid === loggedUserId ?
					`<button type="button" id="chat-complete2-Sell-${trade.id}"
           class = "completeTrade">거래완료</button>` : ""}

        ${trade !== null && trade.tradeStatus !== null && trade.tradeStatus === '완료' ?
					`<h3 style="color:red;">※ 거래가 완료된 게시글입니다</h3>` : ""}
				
					 ${trade !== null && trade.tradeStatus !== '완료' ?
					`<button id = "chat-cancel-trade-${trade.id}" class = "cancelTrade">거래취소</button>
				` : ""}
    `;



			// 화살표 함수는 호출될 때마다 '새로운' 함수로 생성된다
			//1. 위 코드는 매번 새로운 함수로 인식되어서 remove가 안 됨
			// addEventListener는 누적되고 removeEventListener는 의미 없어서 계속 중복 발생

			// 2. 함수 참조가 같아야 removeEventListener가 동작한다
			//같은 코드처럼 보여도 함수 객체 자체가 다릅니다
			//그래서 removeEventListener()가 찾지 못하고 제거가 안 됩니다

			//3. 해결 방법 - 함수를 변수로 저장하고 동일한 참조를 사용
			// ※ 해결 방법 - 핸들러를 별도로 저장해서 관리


			//		const boardTitleButton = document.getElementById(BoardTitleButton-${board.id});
			//		if (boardTitleButton) {
			//			boardTitleButton.removeEventListener("click", (event) => boardTitleButtonClickHandler(event, board)); // 기존 이벤트 제거
			//			boardTitleButton.addEventListener("click", (event) => boardTitleButtonClickHandler(event, board)); // 새 이벤트 추가
			//		}





			const boardTitleButton = document.getElementById(`BoardTitleButton-${board.id}`);
			if (boardTitleButton) {
				// ✅ 이전 핸들러가 있으면 제거
				if (boardButtonHandlers.has(board.id)) {			//boardButtonHandlers = 전역 상태를 관리하 변수
					boardTitleButton.removeEventListener("click", boardButtonHandlers.get(board.id));
				}

				// ✅ 새 핸들러 생성 후 등록
				const handler = (event) => boardTitleButtonClickHandler(event, board);
				boardTitleButton.addEventListener("click", handler);

				// ✅ 핸들러 저장
				boardButtonHandlers.set(board.id, handler);
			}




		} else {
			console.error("buttons-container 요소를 찾을 수 없습니다.");
		}

	}






//export async function filterChatRooms() {
//        let input = document.getElementById("chatSearch").value.toLowerCase();
//        let rows = document.querySelectorAll("#chattingRoomListBody tr");
//		let searchResultText  = document.getElementById("searchResultText");
//        rows.forEach(row => {
//            let roomName = row.textContent.toLowerCase();
//            row.style.display = roomName.includes(input) ? "" : "none";
//        });
//
//        if(input !== ""){
//        	searchResultText.style.display="block";
//        }else{
//        	searchResultText.style.display="none";
//        }
//
//        document.getElementById("searchResultText").addEventListener("input",
//		filterChatRooms()
//		);
//
//       // let messagedata = document.getElementById("messagedata");
//      //  const searchData =  await searchMessageResult(searchResultText);
//
//        // 검색 결과를 렌더링
////      messagedata.innerHTML = `
////
////            <h3>메시지 검색 결과</h3>
////            <ul>
////                ${searchData.messages.map(msg => `<li>메시지: ${msg.content}</li>`).join("")}
////            </ul>
////        `;
////        if(input !== ""){
////        	messagedata.style.display="block";
////        }else{
////        	messagedata.style.display="none";
////        }
//// searchChatRoomMessage(input);
//    }










//export async function searchChatRoomMessage(searchResultText) {
//
//
//
// const chatSearch = document.getElementById("chatSearch");
//    const searchResultsContainer = document.getElementById("messagedata"); // 결과를 표시할 요소
//
//    chatSearch.addEventListener("input", async () => {
//        const searchValue = chatSearch.value.trim();
//
//        if (searchValue === "") {
//            await loadChatRooms(loggedId); // 기본 채팅방 로드
//            searchResultsContainer.innerHTML = ""; // 검색 결과 초기화
//        } else {
//            try {
//                const searchData = await searchMessage(searchResultText);
//
//                // 검색 결과를 렌더링
//                searchResultsContainer.innerHTML = `
//
//                    <h3>메시지 검색 결과</h3>
//                    <ul>
//                        ${searchData.messages.map(msg => `<li>메시지: ${msg.messageContent}</li>`).join("")}
//                    </ul>
//                `;
//            } catch (error) {
//                console.error("검색 중 오류 발생:", error);
//                searchResultsContainer.innerHTML = `<p style="color:red;">검색 중 오류가 발생했습니다.</p>`;
//            }
//        }
//    });
//
//
//}


//export async function searchMessageResult(searchResultText) {
//
//
// const chatSearch = document.getElementById("chatSearch");
//    const searchResultsContainer = document.getElementById("searchdatas"); // 결과를 표시할 요소
//
//    chatSearch.addEventListener("input", async () => {
//        const searchValue = chatSearch.value.trim();
//
//        if (searchValue === "") {
//            await loadChatRooms(loggedId); // 기본 채팅방 로드
//            searchResultsContainer.innerHTML = ""; // 검색 결과 초기화
//        } else {
//            try {
//                const searchData = await searchMessage(searchResultText);
//
//                // 검색 결과를 렌더링
//                searchResultsContainer.innerHTML = `
//                    <h3>메시지 검색 결과</h3>
//                    <ul>
//                        ${searchData.messages.map(msg => `<li>메시지: ${msg.messageContent}</li>`).join("")}
//                    </ul>
//                `;
//            } catch (error) {
//                console.error("검색 중 오류 발생:", error);
//                searchResultsContainer.innerHTML = `<p style="color:red;">검색 중 오류가 발생했습니다.</p>`;
//            }
//        }
//    });


//}





//export function appendMessage(responseDTO) {
//	// 메시지 요소 생성
//	const messageElement = document.createElement("div");
//	messageElement.innerHTML = responseDTO.messageContent;
//
//	// 버튼 컨테이너 생성
//	const buttonContainer = document.createElement("div");
//	buttonContainer.classList.add("messageButtonSelect");
//	buttonContainer.innerHTML = `
//            <hr>
//            <button id="agreeMember2-${responseDTO.id}">거래하기</button>
//            <button id="denyMember2-${responseDTO.id}">거절하기</button>
//        `;
//
//	// 메시지에 버튼 추가
//	messageElement.appendChild(buttonContainer);
//	messageContainer.appendChild(messageElement);
//}
