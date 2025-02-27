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



function findAlarm(loggedId, alarmResult, alarmList, alarmListBody){
	
	 if (alarmList && alarmList.length > 0) {
            alarmListBody.innerHTML = '';

            alarmList.sort((a, b) => Number(b.id) - Number(a.id));

            alarmList.forEach(alarm => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    ${alarm.member1Visible && Number(alarm.member1Id) === Number(loggedId) ? `
                        <td>${alarm.id}</td>
                        <td>${alarm.member1Content}</td>
                        <td>${formatDate(alarm.createTime)}</td>
                        <td>${alarm.member1Read}</td>
                        <td><button onclick="markAsRead(${alarm.id})">읽음 처리</button></td>` : ""}
                    ${alarm.member2Visible && Number(alarm.member2Id) === Number(loggedId) ? `
                        <td>${alarm.id}</td>
                        <td>${alarm.member2Content}
                            ${alarm.action === "상대방 동의 확인" ? `
                                <button id="agreeMember2-${alarm.id}" onclick="enrollTrade2(${alarm.id})">거래하기</button>
                                <button id="denyMember2-${alarm.id}" onclick="denyCreateTrade(${alarm.id})">거절하기</button>` : ""}
                            ${alarm.action === "거래 완료 확인" ? `
                                <button id="complete1-Sell-${alarm.object}" onclick="enrollTrade2(${alarm.id})">거래완료</button>` : ""}
                        </td>
                        <td>${formatDate(alarm.createTime)}</td>
                        <td>${alarm.member2Read}</td>
                        <td><button onclick="markAsRead(${alarm.id})">읽음 처리</button></td>` : ""}
                `;
                alarmListBody.appendChild(row);
            });

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

       findAlarm(loggedId,alarmResult,alarmList,alarmListBody);
    }
});

// 페이지네이션 버튼을 생성하는 함수
function createPagination(data, loggedId) {
    const paginationContainer = document.getElementById('pagination');
    const totalPages = data.totalPages;
    const currentPage = data.page;

    console.log("토탈 페이지:", totalPages);
    console.log("로그인아이디:", loggedId);
    console.log("현재 페이지:", currentPage);

    // 이전 버튼
    const prevButton = document.createElement('button');
    prevButton.textContent = 'Previous';
    prevButton.disabled = currentPage === 0;
    prevButton.addEventListener('click', () => {
        if (currentPage > 0) {
            loadPage(currentPage - 1, loggedId);
        }
    });
    paginationContainer.appendChild(prevButton);

    // 페이지 번호 버튼 생성
    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i + 1; // 페이지 번호 표시 (1부터 시작)
        pageButton.disabled = i === currentPage; // 현재 페이지는 비활성화
        pageButton.addEventListener('click', () => loadPage(i, loggedId));
        paginationContainer.appendChild(pageButton);
    }

    // 다음 버튼
    const nextButton = document.createElement('button');
    nextButton.textContent = 'Next';
    nextButton.disabled = currentPage === totalPages - 1;
    nextButton.addEventListener('click', () => {
        if (currentPage < totalPages - 1) {
            loadPage(currentPage + 1, loggedId);
        }
    });
    paginationContainer.appendChild(nextButton);
}



// 페이지를 로드하는 함수 (실제 데이터 로드를 구현할 곳)
async function loadPage(page, loggedId) {
    console.log(`Loading page ${page + 1}`);
    const alarmResult = await checkUserAlarmList(loggedId, page); // Assuming checkUserAlarmList supports pagination with page number
    const alarmList = alarmResult.content;
    const alarmListBody = document.getElementById("alarmListBody");

           findAlarm(loggedId,alarmResult,alarmList,alarmListBody);
}
