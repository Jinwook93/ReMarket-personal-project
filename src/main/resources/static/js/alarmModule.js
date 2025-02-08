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