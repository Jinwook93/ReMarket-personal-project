// 회원가입 폼에서 아이디 유효성 검사 및 중복 확인 함수
async function checkNickname() {
	let nickname = document.getElementById("nickname");
	let isCheckedNickname = document.getElementById("isCheckedNickname");


//	let loggedUserId = null;
	let loggedNickname = null;
	let loggedNicknameElement = document.getElementById("loggedNickname");
		let loggedUserElement = document.getElementById("loggedUserId");
	if (loggedUserElement && loggedNicknameElement) {
		loggedNickname = loggedNicknameElement.value;
	}






	//    let isChangedId = document.getElementById("isChangedId");

	if (!nickname) {
		alert("닉네임을 입력해주세요.");
		   nickname.focus();
		return;
	}


		if (nickname.value.includes(" ")) {
			alert("닉네임은 공백을 허용하지 않습니다!");
			return;
		}
		
    // 닉네임 길이 체크 (최소 2자 이상, 최대 8자 이하)
    if (nickname.value.length < 2 || nickname.value.length > 8) {
        alert("닉네임은 2자 이상 8자 이하로 입력해 주세요.");
        nickname.focus();
        return false;
    }
	

	if (loggedNickname === nickname.value && loggedNickname != null) {		// 비교대상이 로그인한 유저 아이디 본인일 경우
				alert("현재 접속 중인 닉네임입니다.");
		//		isChangedId.value = "true";
		isCheckedNickname.value = "true";
				return;
			} 


	try {
		const isAvailable = await checkDuplicateNickname(nickname.value);
		if (isAvailable) {
			alert("사용 가능한 닉네임입니다.");
			isCheckedNickname.value = "true";
			//            isChangedId.value = "false";
		} else {
			alert("이미 등록된 닉네임입니다.");
				isCheckedNickname.value = "false";
		}
	} catch (error) {
		alert("아이디 확인 중 오류가 발생했습니다.");
	}
}


//document.addEventListener("DOMContentLoaded",() =>{
//	document.getElementById("checkedIdButton").addEventListener("click",checkId);
//
//})
