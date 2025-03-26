// checkDuplicateId 함수는 사용자 아이디 중복 확인을 비동기적으로 처리하는 함수입니다.
function checkDuplicateId(userid) {
    // 새로운 Promise 객체를 생성하여 반환
    // Promise는 비동기 작업을 처리하기 위한 객체로, 성공(resolve) 또는 실패(reject) 상태로 결과를 반환
    return new Promise((resolve, reject) => {

        // $.ajax()를 이용해 서버로 아이디 중복 확인 요청
        $.ajax({
            url: "http://localhost:8081/checkId",  // 서버로 보낼 요청의 URL
            type: "POST",  // HTTP 메소드 (POST 방식 사용)
            contentType: "application/json",  // 요청 데이터의 형식 (JSON으로 보냄)
            data: JSON.stringify({ "userid": userid }),  // 아이디 데이터를 JSON 형식으로 서버에 전달

            // 서버 응답이 성공적일 경우 실행될 함수
            success: function(response) {
                // resolve(): 서버 응답이 정상적으로 왔을 때 Promise를 '성공' 상태로 처리
                // resolve는 Promise가 '성공' 상태일 때 반환값을 지정하는 역할
                resolve(response.available);  // 서버 응답에서 'available' 값을 반환 (true 또는 false)
            },

            // 서버 요청이 실패했을 경우 실행될 함수
            error: function(xhr, status, error) {
                console.error("Error: " + error);
                // reject(): 서버 요청이 실패했을 때 Promise를 '실패' 상태로 처리
                // reject는 Promise가 '실패' 상태일 때 오류 객체를 넘겨주는 역할
                reject(error);  // 실패 시 error 정보를 반환
            }
        });
    });
}




async function joinValidation(e) {
    let userid = document.getElementById("userid");
    let password = document.getElementById("password");
    let password_check = document.getElementById("password_check");
    let name = document.getElementById("name");
    let age = document.getElementById("age");
    let gender = document.getElementById("gender");
    let phone = document.getElementById("phone");
    let address = document.getElementById("address");
    let address2 = document.getElementById("address2");
    let password_admin = document.getElementById("password_admin");
    let isCheckedIdElement = document.getElementById("isCheckedId");

    // isCheckedId를 element에서 직접 가져옴
    let isCheckedId = isCheckedIdElement.value;
   	let isChangedId = document.getElementById("isChangedId");
   	
   	
    e.preventDefault();

    // 아이디가 비어있는지 체크
    if (userid.value === "") {
        alert("아이디를 입력해 주세요.");
        userid.focus();
        return false;
    }
    
     // 아이디 길이 체크 (최소 5자 이상, 최대 20자 이하)
    if (userid.value.length < 5 || userid.value.length > 20) {
        alert("아이디는 5자 이상 20자 이하로 입력해 주세요.");
        userid.focus();
        return false;
    }
    

    // 중복 확인을 하지 않으면 폼 제출을 막음
    if (isCheckedId === "false") {
        alert("아이디 중복을 확인해주세요");
        userid.focus();
        return false;
    }
    

    // 아이디 값이 변경되면 중복 확인을 다시 해야 하므로
//    userid.addEventListener('input', function() {
//        if (isCheckedId === "true") {
//            console.log("isChangedId", isChangedId);
//            isCheckedIdElement.value = "false";  // 중복 확인 상태를 false로 설정
//            isChangedId.value = "true";  // 아이디 변경 상태를 true로 설정
//        }
//    });

    // 비밀번호가 비어있는지 체크
    if (password.value === "") {
        alert("비밀번호를 입력해 주세요.");
        password.focus();
        return false;
    }

     // 비밀번호가 비어있는지 체크
    if (password_check.value === "") {
        alert("비밀번호를 확인해 주세요.");
        password_check.focus();
        return false;
    }

    // 비밀번호 길이 체크 (최소 8자 이상, 최대 16자 이하)
    if (password.value.length < 8 || password.value.length > 16) {
        alert("비밀번호는 8자 이상 16자 이하로 입력해 주세요.");
        password.focus();
        return false;
    }






    // 비밀번호 확인이 비밀번호와 일치하는지 체크
    if (password.value !== password_check.value) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        password_check.focus();
        return false;
    }
    

    

    // 이름이 비어있는지 체크
    if (name.value === "") {
        alert("이름을 입력해 주세요.");
        name.focus();
        return false;
    }





 // 이름 길이 체크 (최소 2자 이상, 최대 20자 이하)
    if (name.value.length < 2 || name.value.length > 20) {
        alert("이름은 2자 이상 20자 이하로 입력해 주세요.");
        name.focus();
        return false;
    }




    // 나이가 비어있거나 숫자가 아닌 경우 체크
    if (age.value === "" || isNaN(age.value)) {
        alert("유효한 나이를 입력해 주세요.");
        age.focus();
        return false;
    }

    // 성별이 선택되지 않은 경우 체크
    if (gender.value === "") {
        alert("성별을 선택해 주세요.");
        gender.focus();
        return false;
    }

    // 연락처가 비어있는지 체크 (선택 사항이지만 빈 경우 메시지 표시)
    if (phone.value === "") {
        alert("연락처를 입력해 주세요.");
        phone.focus();
        return false;
    }

          // 연락처 양식이 맞지 않는 경우 체크
    if (document.getElementById("phone").value.substring(0,3) !== "010" && document.getElementById("phone").value.substring(0,3) !== "011" ) {
         alert("연락처 입력 양식이 아닙니다 (시작 번호 : 010,011)");
        document.getElementById("phone").focus();
        return;
    }
		console.log(phone.value.substring(0,3) );
    // 연락처 양식이 맞지 않는 경우 체크
    if (phone.value.length > 0 && phone.value.length < 13) {
        alert("연락처 입력 양식이 아닙니다.");
        phone.focus();
        return false;
    }

    // 주소가 비어있는지 체크
    if (address.value === "") {
        alert("주소를 입력해 주세요.");
        address.focus();
        return false;
    }

    // 세부 주소가 비어있는지 체크
    if (address2.value === "") {
        alert("세부 주소를 입력해 주세요.");
        address2.focus();
        return false;
    }

    // 아이디가 변경되었을 때 중복 확인이 필요하도록 처리
//    if (isCheckedId === "true" && isChangedId.value === "true") {
//        isCheckedIdElement.value = "false";
//        alert("아이디가 변경되었습니다. 중복 확인을 다시 해주세요.");
//        userid.focus();
//        return false;
//    }







 try {
        const isAvailable = await checkDuplicateId(userid.value);
        if (!isAvailable) {
            alert("이미 등록된 아이디입니다");
            isCheckedIdElement.value = "false";
            userid.focus();
//            isChangedId.value = "false";
            return false;  // 회원가입 중단
        } else {
//            alert("사용 가능한 아이디입니다");
            isCheckedIdElement.value = "true";
             userid.focus();
//            isChangedId.value = "false";
        }
    } catch (error) {
        alert("아이디 확인 중 오류가 발생했습니다.");
        return false;
    }










    alert("회원가입이 완료되었습니다");

    // 모든 유효성 검사가 통과되면 폼 제출
     e.target.submit(); // 실제 폼 제출
}
