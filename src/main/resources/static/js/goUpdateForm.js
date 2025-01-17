document.getElementById("updateBoard").addEventListener("click", function () {
    const boardId = document.getElementById("id").value; // 숨겨진 input에서 boardId 가져오기
    window.location.href = `/board/updateboard/${boardId}`;
});
