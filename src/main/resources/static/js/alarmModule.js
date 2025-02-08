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

   export  async function checkUserAlarmCount(loggedId) {
        try {
            const alarmResponse = await fetch(`/alarm/list/findCount/${loggedId}`);
            const alarmCount = await alarmResponse.json();
            return alarmCount;
        } catch (error) {
            console.error("알람 데이터를 불러오는 중 오류 발생:", error);
        }
    }

    export async function checkUserAlarmList(loggedId) {
        try {
            const alarmListResponse = await fetch(`/alarm/list/${loggedId}`);
            const alarmList = await alarmListResponse.json();
            return alarmList;
        } catch (error) {
            console.error("알람 목록을 불러오는 중 오류 발생:", error);
        }
    }
    
       export  function markAsRead(alarmId) {
        console.log(`알람 ${alarmId} 읽음 처리`);
    }
    
    
    	export function loginAlarm(){
			
			
			
		}; 
    
    
    
    
    