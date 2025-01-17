 document.getElementById("deleteBoard").addEventListener("click", function () {
        const boardId = document.getElementById("id").value; // 숨겨진 input에서 boardId 가져오기

        $.ajax({
            url: `/board/deleteboard/${boardId}`, // 삭제 API 경로
            type: "DELETE", // HTTP 메서드
            contentType: "application/json", // 요청 Content-Type
            success: function (response) {
                alert("게시글이 삭제되었습니다.");
                window.location.href = "/"; // 성공 시 리다이렉트 경로 설정
            },
            error: function (xhr, status, error) {
                alert("게시글이 삭제되지 않았습니다. 다시 시도해주세요.");
                console.error("Error:", error);
            }
        });
    });