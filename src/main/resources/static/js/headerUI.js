import { loadChatRooms, setUpEnterRoomButton, setUpExitRoomButton, toggleChattingRoomList } from './chatModule.js';
import { toggleAlarmList, checkUserAlarmCount, checkUserAlarmList } from './alarmModule.js';
import { formatDate } from "./formatDate.js";

import { enrollTrade2 } from "./tradeModule.js";


let alarmCountInterval = null;

document.addEventListener("DOMContentLoaded", async () => {
	const isLoggedIn = document.getElementById("isLoggedIn")?.value;
	const loggedId = document.getElementById("loggedId")?.value;
	const loggedUserId = document.getElementById("loggedUserId")?.value;

	if (isLoggedIn === "true" || isLoggedIn === true) {
		await loadChatRooms(loggedId);
		setUpEnterRoomButton(loggedUserId);
		setUpExitRoomButton();

		if (alarmCountInterval) {
			clearInterval(alarmCountInterval);
		}

		//		alarmCountInterval = setInterval(async () => {
		//			await checkUserAlarmCount(loggedId);
		//		}, 3000);
	}

	toggleChattingRoomList();
	toggleAlarmList();
});



function findAlarm(loggedId, alarmResult, alarmList, alarmListBody , page = 0) {

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
 		     if(checkbox.checked){
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
			row.innerHTML = `
                    ${alarm.member1Visible && Number(alarm.member1Id) === Number(loggedId) ? `
                   <!--     <td>${alarm.id}</td> -->
                        <td id = alarm-${alarm.id}>${alarm.member1Content} 
    <div class="date-container" style="display: flex; gap: 10px;"> 
        <p class="date-text">${formatDate(alarm.createTime)}</p>
        <p class="read-status">${alarm.member1Read === "READ" ? "읽음" : "읽지 않음"}</p>
    </div>
</td>

                        <!--     <td>${formatDate(alarm.createTime)}</td> -->
                       <!--  <td>${alarm.member1Read}</td> -->
                    <!--    <td><button onclick="markAsRead(${alarm.id})">읽음 처리</button>--></td>` : ""}
                    ${alarm.member2Visible && Number(alarm.member2Id) === Number(loggedId) ? `
                        <!--  <td>${alarm.id}</td> -->
                        <td>${alarm.member2Content}
                            ${alarm.action === "상대방 동의 확인" ? `
                                <button id="agreeMember2-${alarm.id}" onclick="enrollTrade2(${alarm.id})">거래하기</button>
                                <button id="denyMember2-${alarm.id}" onclick="denyCreateTrade(${alarm.id})">거절하기</button>
                          		` : ""}
                            ${alarm.action === "거래 완료 확인" ? `
                                <button id="complete1-Sell-${alarm.object}" onclick="enrollTrade2(${alarm.id})">거래완료</button>
             							 ` : ""}
                                    <div class="date-container" style="display: flex; gap: 10px;"> 
                              <p class="date-text">${formatDate(alarm.createTime)}</p>
                                       <p class="read-status">${alarm.member2Read === "READ" ? "읽음" : "읽지 않음"}</p></div>
                        </td>
                       <!--  <td>${formatDate(alarm.createTime)}</td> -->
                             <!--    <td>${alarm.member2Read}</td>     -->
                       <!-- <td><button onclick="markAsRead(${alarm.id})">읽음 처리</button> -->
                       </td>` : ""}
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







document.getElementById("alarmButton").addEventListener("click", async () => {
	const isLoggedIn = document.getElementById("isLoggedIn")?.value;
	const loggedId = document.getElementById("loggedId")?.value;

	if (isLoggedIn === "true" || isLoggedIn === true) {
		const alarmResult = await checkUserAlarmList(loggedId);
		const alarmList = alarmResult.content;
		const alarmListBody = document.getElementById("alarmListBody");

		findAlarm(loggedId, alarmResult, alarmList, alarmListBody);
	}
});

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
				},
			});

			if (response.ok) {
				console.log(`알림 ${alarmId} 읽음 처리 완료`);
				
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
					const alarmResult = await checkUserAlarmList(loggedId,alarmListBody.getAttribute("data-current-page"));
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
async function loadPage(page, loggedId) {
  console.log(`Loading page ${page + 1}`);
  
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
        },
      });

      // 📌 현재 페이지 유지하면서 알림 목록 다시 가져오기
      const alarmResult = await checkUserAlarmList(loggedId, currentPage);
      const alarmList = alarmResult.content;

      findAlarm(loggedId, alarmResult, alarmList, alarmListBody, currentPage); // ✅ 현재 페이지를 유지하면서 호출

    } catch (error) {
      console.error("알림 읽음 처리 중 오류 발생:", error);
    }
  }
}
