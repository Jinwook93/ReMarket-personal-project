document.getElementById("updateBoard").addEventListener("click", function () {
    const boardId = document.getElementById("id").value; // 숨겨진 input에서 boardId 가져오기
    const title = document.getElementById("title").value;
//    const name = document.getElementById("name").value;
    const userid = document.getElementById("userid").value;
    const contents = document.getElementById("contents").value;

    let data = {
        id: Number(boardId), // 숫자 타입으로 변환
        title: title,
//        name: name,
//        userid: userid,
        contents: contents
    };

     fetch(`/board/updateboard/${boardId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        alert("게시글이 수정되었습니다.");
        window.location.href = `/board/view/${boardId}`;
    })
    .catch(error => {
        console.error("Error:", error);
        alert("게시글 수정에 실패했습니다.");
    });
});
