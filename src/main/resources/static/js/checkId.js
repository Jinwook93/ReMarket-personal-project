// 회원가입 폼에서 아이디 유효성 검사 및 중복 확인 함수
async function checkId() {
	let userid = document.getElementById("userid");
	let isCheckedId = document.getElementById("isCheckedId");


	let loggedUserId = null;
	let loggedUserElement = document.getElementById("loggedUserId");

	if (loggedUserElement) {
		loggedUserId = loggedUserElement.value;
	}






	//    let isChangedId = document.getElementById("isChangedId");

	if (!userid) {
		alert("아이디를 입력해주세요.");
		   userid.focus();
		return;
	}
	
	
			if (userid.value.includes(" ")) {
			alert("아이디는 공백을 허용하지 않습니다!");
			return;
		}


	
	
     // 아이디 길이 체크 (최소 5자 이상, 최대 20자 이하)
    if (userid.value.length < 5 || userid.value.length > 20) {
        alert("아이디는 5자 이상 20자 이하로 입력해 주세요.");
        userid.focus();
        return false;
    }
	

	if (loggedUserId === userid.value && loggedUserId !== null && loggedUserId !== "anonymousUser") {		// 비교대상이 로그인한 유저 아이디 본인일 경우
				alert("현재 접속 중인 아이디입니다.");
		//		isChangedId.value = "true";
		isCheckedId.value = "true";
				return;
			} 


	try {
		const isAvailable = await checkDuplicateId(userid.value);
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
