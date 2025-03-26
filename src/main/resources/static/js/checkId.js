// 회원가입 폼에서 아이디 유효성 검사 및 중복 확인 함수
async function checkId() {
	let userid = document.getElementById("userid").value;
	let isCheckedId = document.getElementById("isCheckedId");


	let loggedUserId = null;
	let loggedUserElement = document.getElementById("loggedUserId");

	if (loggedUserElement) {
		loggedUserId = loggedUserElement.value;
	}






	//    let isChangedId = document.getElementById("isChangedId");

	if (!userid) {
		alert("아이디를 입력해주세요.");
		return;
	}

	if (loggedUserId === userid && loggedUserId !== null && loggedUserId !== "anonymousUser") {		// 비교대상이 로그인한 유저 아이디 본인일 경우
				alert("현재 접속 중인 아이디입니다.");
		//		isChangedId.value = "true";
		isCheckedId.value = "true";
				return;
			} 


	try {
		const isAvailable = await checkDuplicateId(userid);
		if (isAvailable) {
			alert("사용 가능한 아이디입니다.");
			isCheckedId.value = "true";
			//            isChangedId.value = "false";
		} else {
			alert("이미 등록된 아이디입니다.");
				isCheckedId.value = "false";
		}
	} catch (error) {
		alert("아이디 확인 중 오류가 발생했습니다.");
	}
}


//document.addEventListener("DOMContentLoaded",() =>{
//	document.getElementById("checkedIdButton").addEventListener("click",checkId);
//
//})
