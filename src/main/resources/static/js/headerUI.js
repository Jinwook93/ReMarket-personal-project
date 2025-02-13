    import { loadChatRooms, setUpEnterRoomButton, setUpExitRoomButton, toggleChattingRoomList } from './chatModule.js';
	import { toggleAlarmList, checkUserAlarmCount, checkUserAlarmList} from './alarmModule.js';
	import { formatDate } from "./formatDate.js";




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

            alarmCountInterval = setInterval(async () => {
                await checkUserAlarmCount(loggedId);
            }, 1000);
        }

        toggleChattingRoomList();
		toggleAlarmList();
    });

    document.getElementById("alarmButton").addEventListener("click", async () => {
        const isLoggedIn = document.getElementById("isLoggedIn")?.value;
        const loggedId = document.getElementById("loggedId")?.value;

        if (isLoggedIn === "true" || isLoggedIn === true) {
            const alarmList = await checkUserAlarmList(loggedId);
            const alarmListBody = document.getElementById("alarmListBody");

            if (alarmList && alarmList.length > 0) {
                alarmListBody.innerHTML = '';

			alarmList.sort((a, b) => Number(b.id) - Number(a.id));
				
                alarmList.forEach(alarm => {
					
                    const row = document.createElement("tr");
                    row.innerHTML = `
 							 ${alarm.member1Visible && Number(alarm.member1.id) === Number(loggedId) ? `
 							 <td>${alarm.id}</td>
 						<td>${alarm.priority}</td>
 							 <td>${alarm.member1Content}</td>
 							      <td>${formatDate(alarm.createTime)}</td>
					  <td>${alarm.member1Read}</td>
					 <td> <button onclick="markAsRead(${alarm.id})">읽음 처리</button> </td>` : ""}
							 			 ${alarm.member2Visible && Number(alarm.member2.id) === Number(loggedId) ? `
 							 <td>${alarm.id}</td>
 						<td>${alarm.priority}</td>
 							 <td>${alarm.member2Content}</td>
 							      <td>${formatDate(alarm.createTime)}</td>
					  <td>${alarm.member2Read}</td>
					  <td> <button onclick="markAsRead(${alarm.id})">읽음 처리</button> </td>` : ""}         
                    `;
                    alarmListBody.appendChild(row);
                });
            } else {
                alarmListBody.innerHTML = '<tr><td colspan="3">알림이 없습니다.</td></tr>';
            }
        }
    });