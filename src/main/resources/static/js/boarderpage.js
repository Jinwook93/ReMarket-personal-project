//document.addEventListener('DOMContentLoaded', () => {  // 수정: 따옴표 수정
    document.querySelectorAll("[id^='authorDiv-']").forEach(function(authorDiv) {
        authorDiv.addEventListener("click", function() {
            // 클릭한 요소에서 board.id 가져오기
            let boardId = this.id.split('-')[1];  // 'authorDiv-123'에서 123을 추출


            // Ajax 요청 (게시자 정보 가져오기)
            fetch(`/boarderpage/${boardId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            })
            .then(response => response.json())
            .then(data => {
                // 모달에 데이터 표시
                console.log(data);
                document.getElementById("memberBoardNickname").textContent = data.nickname;
//                document.getElementById("memberName").textContent = data.name;
//                document.getElementById("memberAge").textContent = data.age;
                document.getElementById("memberGender").textContent = data.gender;
                document.getElementById("memberAddress").textContent = data.address;
                document.getElementById("memberPhone").textContent = data.phone;
                document.getElementById("memberProfileImage").src = data.profileImage;

                // 모달 열기
                document.getElementById("boarderpage").style.display = "block";
                      bringToFront(  document.getElementById("boarderpage"));  // 모달을 최상위로 올리기
            })
            .catch(error => {
                console.error("Error fetching member data:", error);
            });
        });
    });
//});







// 모달 닫기 버튼 클릭 시
document.getElementById("closeBoarderPage").addEventListener("click", function() {
    document.getElementById("boarderpage").style.display = "none";
});
