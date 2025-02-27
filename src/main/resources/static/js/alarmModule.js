export function toggleAlarmList() {
	const alarmButton = document.getElementById("alarmButton");
	const alarmList = document.querySelector(".alarmList");

	if (alarmButton && alarmList) {
		alarmButton.addEventListener("click", (event) => {
			event.preventDefault();
			alarmList.style.display = alarmList.style.display === "none" ? "block" : "none";
		});
	}
}

export async function checkUserAlarmCount(loggedId) {
	try {
		const alarmResponse = await fetch(`/alarm/list/findCount/${loggedId}`);
		const alarmCount = await alarmResponse.json();
		return alarmCount;
	} catch (error) {
		console.error("알람 데이터를 불러오는 중 오류 발생:", error);
	}
}

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
        console.log(data);
        return data; // { content, totalPages, totalElements, number, size }
    } catch (error) {
        console.error("알람 목록을 불러오는 중 오류 발생:", error);
    }
}


export function markAsRead(alarmId) {
	console.log(`알람 ${alarmId} 읽음 처리`);
}



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





