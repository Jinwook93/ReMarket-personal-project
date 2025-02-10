import { loadComments } from "./loadComments.js";

export function addComment(boardId, parentCommentId, principalDetails) {
    let commentText = null;
    let replyInput = null;
    let replyContent = null;

    if (parentCommentId == null) {
        commentText = document.getElementById("commentText").value;

        if (!commentText.trim()) {
            alert("댓글 내용을 입력해주세요.");
            return;
        }
    } else {
        replyInput = document.getElementById(`reply-input-${parentCommentId}`);
        replyContent = replyInput.value.trim();

        if (!replyContent) {
            alert("답글을 입력해주세요.");
            return;
        }
    }

    fetch(`http://localhost:8081/comments/board/${boardId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            content: parentCommentId == null ? commentText : replyContent,
            parentCommentId: parentCommentId,
            boardId: boardId
        }),
    })
    .then(response => {
        if (!response.ok) throw new Error("답글 저장 실패");
        return response.json();
    })
    .then(data => {
        console.log(data);
        alert("답글이 등록되었습니다.");

        // 댓글 입력 후 텍스트 초기화
        if (replyInput) {
            replyInput.value = "";
        } else if (commentText) {
            document.getElementById("commentText").value = ""; 
        }

        // 댓글 새로고침
        loadComments(String(boardId), data.memberEntity.userid, principalDetails);
    })
    .catch(error => {
        console.error("Error:", error);
        alert("답글을 등록하는 중 오류가 발생했습니다.");

        // 오류 발생 시 텍스트 초기화
        if (replyInput) {
            replyInput.value = "";
        } else if (commentText) {
            document.getElementById("commentText").value = "";
        }
    });
}
