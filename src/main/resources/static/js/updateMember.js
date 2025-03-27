// updateMember.js

document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('updateMember').addEventListener('click', async function() {
		let isCheckedIdElement = document.getElementById("isCheckedId");
		let isCheckedId = isCheckedIdElement.value;

		let isCheckedNicknameElement = document.getElementById("isCheckedNickname");
		let isCheckedNickname = isCheckedNicknameElement.value;

		let loggedUserId = document.getElementById("loggedUserId").value;
		let loggedNickname = document.getElementById("loggedNickname").value;


		// 중복 확인을 하지 않으면 폼 제출을 막음
		if (isCheckedId === "false") {
			alert("아이디 중복을 확인해주세요");
			userid.focus();
			return false;
		}

		if (isCheckedNickname === "false") {
			alert("닉네임 중복을 확인해주세요");
			nickname.focus();
			return false;
		}
		
		if (document.getElementById("userid").value.includes(" ")) {
			alert("아이디는 공백을 허용하지 않습니다!");
			return;
		}

		if (document.getElementById('nickname').value.includes(" ")) {
			alert("닉네임은 공백을 허용하지 않습니다!");
			return;
		}


		if (document.getElementById('password').value.length > 0 || document.getElementById('password_check').value.length > 0) {
			if (document.getElementById('password').value.length < 8 || document.getElementById('password').value.length > 16) {

				alert("비밀번호는 8자 이상 16자 이하로 입력해 주세요.");
				document.getElementById('password').focus();
				password.focus();
				return;
			}



			if (document.getElementById('password_check').value.length < 8 || document.getElementById('password_check').value.length > 16) {

				alert("비밀번호는 8자 이상 16자 이하로 입력해 주세요.");
				document.getElementById('password_check').focus();
				password.focus();
				return;
			}


		}




		if (document.getElementById('password').value !== document.getElementById('password_check').value) {
			alert("비밀번호, 비밀번호 확인 값이 같지 않습니다!");
			document.getElementById('password_check').focus();
			return;
		}







		// 아이디 길이 체크 (최소 2자 이상, 최대 20자 이하)
		if (document.getElementById("userid").value.length > 0 && (document.getElementById("userid").value.length < 2 || document.getElementById("userid").value.length > 20)) {
			alert("아이디는 1자 이상 20자 이하로 입력해 주세요.");
			document.getElementById("userid").focus();
			return;
		}

		// 닉네임 길이 체크 (최소 2자 이상, 최대 8자 이하)
		if (document.getElementById("nickname").value.length > 0 && (document.getElementById("nickname").value.length < 2 || document.getElementById("nickname").value.length > 8)) {
			alert("닉네임은 2자 이상 8자 이하로 입력해 주세요.");
			document.getElementById("nickname").focus();
			return;
		}



		// 이름 길이 체크 (최소 2자 이상, 최대 20자 이하)
		if (document.getElementById("name").value.length > 0 && (document.getElementById("name").value.length < 2 || document.getElementById("name").value.length > 20)) {
			alert("이름은 1자 이상 20자 이하로 입력해 주세요.");
			document.getElementById("name").focus();
			return;
		}




		// 나이가 비어있거나 숫자가 아닌 경우 체크
		if (document.getElementById("age").value === "" || isNaN(document.getElementById("age").value)) {
			alert("유효한 나이를 입력해 주세요.");
			document.getElementById("age").focus();
			return;
		}


		// 연락처 양식이 맞지 않는 경우 체크
		if (document.getElementById("phone").value.length > 0 && document.getElementById("phone").value.length < 13) {
			alert("연락처 입력 양식이 아닙니다.");
			document.getElementById("phone").focus();
			return;
		}




		// 연락처 양식이 맞지 않는 경우 체크
		if (document.getElementById("phone").value.substring(0, 3) !== "010" && document.getElementById("phone").value.substring(0, 3) !== "011") {
			alert("연락처 입력 양식이 아닙니다 (시작 번호 : 010,011)");
			document.getElementById("phone").focus();
			return;
		}

		console.log(phone.value.substring(0, 3));







		if (document.getElementById('prev_password').value === "" || document.getElementById('prev_password_check').value === "") {
			alert("비밀번호를 입력하세요!");

			if (document.getElementById('prev_password_check').value == "") {
				document.getElementById('prev_password_check').focus();
			}
			if (document.getElementById('prev_password').value == "") {
				document.getElementById('prev_password').focus();
			}

			return;
		}
		if (document.getElementById('prev_password').value !== document.getElementById('prev_password_check').value) {
			alert("비밀번호, 비밀번호 확인 값이 같지 않습니다!");
			document.getElementById('prev_password_check').focus();
			return;
		}



		//    		if (document.getElementById('age').value ==='e') {
		//			alert("숫자만 입력 가능합니다");
		//			document.getElementById('age').focus();
		//			return;
		//		}


		try {


			const isAvailable = await checkDuplicateId(userid.value);
			if (!isAvailable) {
				if (loggedUserId === userid.value && loggedUserId !== null && loggedUserId !== "anonymousUser") {
					isCheckedId.value = "true";
					//            return true;
				} else {

					alert("이미 등록된 아이디입니다");
					//            isCheckedIdElement.value = "false";
					userid.focus();
					isCheckedId = "false";
					return false;  // 회원가입 중단
				}
			} else {
				isCheckedIdElement.value = "true";
				//             userid.focus();
			}
		} catch (error) {
			alert("아이디 확인 중 오류가 발생했습니다.");
			return false;
		}




		try {


			const isAvailable = await checkDuplicateNickname(nickname.value);
			if (!isAvailable) {
				if (loggedNickname === nickname.value && loggedNickname != null) {
					isCheckedNickname.value = "true";
					//            return true;
				} else {

					alert("이미 등록된 닉네임입니다");
					//            isCheckedIdElement.value = "false";
					nickname.focus();
					isCheckedNickname = "false";
					return false;  // 회원가입 중단
				}
			} else {
				isCheckedNickname = "true";
				//             userid.focus();
			}
		} catch (error) {
			alert("닉네임 확인 중 오류가 발생했습니다.");
			return false;
		}









		// Create a FormData object to handle form data including file uploads
		const formData = new FormData();
		let id = document.getElementById('id').value;
		//        formData.append('id', id);
		formData.append('userid', document.getElementById('userid').value);
		formData.append('password', document.getElementById('password').value);
		formData.append('password_check', document.getElementById('password_check').value);
		formData.append('nickname', document.getElementById('nickname').value);
		formData.append('name', document.getElementById('name').value);
		formData.append('age', document.getElementById('age').value);
		formData.append('gender', document.getElementById('gender').value);
		formData.append('phone', document.getElementById('phone').value);
		formData.append('address', document.getElementById('address').value);
		formData.append('address2', document.getElementById('address2').value);
		formData.append('prev_password', document.getElementById('prev_password').value);
		formData.append('nullimageButton', document.getElementById('nullimageButton').checked);

		// If there is a profile image, append it to the form data
		const profileImage = document.getElementById('profileImage').files[0];
		if (profileImage) {
			formData.append('profileImage', profileImage);
		}








		// Use fetch to send the data to the backend
		fetch(`/updateMember/${id}`, {
			method: 'PUT',
			body: formData,
		})
			.then(response => {
				if (!response.ok) {
					alert("회원 업데이트 실패!!");
					return;
				}

				return response.text();
			})
			.then(data => {
				if (data === "회원 정보 업데이트 성공") {
					alert("회원 정보가 성공적으로 업데이트 되었습니다");
					window.location.href = "/";
				} else if (data === "기존 비밀번호 입력값이 일치하지 않습니다") {
					alert(data);
					document.getElementById('prev_password').focus();
				} else {
					//					console.log(data);
					alert('회원 정보 업데이트 실패');
				}
			})
			.catch(error => {
				console.error('Error:', error);
				alert('서버 오류가 발생했습니다.');
			});

	});
});
