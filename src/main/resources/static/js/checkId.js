// 회원가입 폼에서 아이디 유효성 검사 및 중복 확인 함수
function checkId() {
    // 입력된 아이디 값을 가져옴
    let userid = document.getElementById("userid").value;
    let condition = false;
    let storeid = "";
    
 
    
    
    
    
    
    // 아이디가 비어있는지 확인
    if (!userid) {
        // 아이디가 비어 있으면 알림을 띄우고 함수 종료
        alert("아이디를 입력해주세요.");
        return false;
    }

    // AJAX 요청을 보내는 함수 (jQuery를 사용)
    $.ajax({
        url: "http://localhost:8081/checkId",  // 요청을 보낼 URL
        type: "POST",  // HTTP 메소드 (POST 방식)
        contentType: "application/json",  // 요청 타입 (JSON 형식)
        data: JSON.stringify({ "userid": userid }),  // 서버로 전송할 데이터 (JSON 형태로 아이디 값 전달)
        
        // 요청이 성공적으로 처리되면 실행될 콜백 함수
        success: function(response) {
            // 서버 응답에서 'available' 값을 확인
            if (response.available) {
                // 아이디가 사용 가능하면 알림
                alert(response);
                storeid= userid;
                 condition = true;
                
            } else {
                // 아이디가 이미 사용 중이면 알림
                alert(response);
                 storeid= "";
                  condition = false;
                
            }
        },
        
        // 요청이 실패했을 경우 실행될 콜백 함수
        error: function(xhr, status, error) {
            // 요청 실패 시 오류를 콘솔에 출력하고 알림
            console.error("Error: " + error);
            alert("아이디 확인 중 오류가 발생했습니다.");
        }
    });
}

//export function compareValue () {		//회원가입을 누를 시 마지막으로 중복확인 재점검
//		if(storeid !== userid){
//			condition = false;
//			
//		}else{
//			condition  = true;
//		}
//	
//	return condition;
//}
