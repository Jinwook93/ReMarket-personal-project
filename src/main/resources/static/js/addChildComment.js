//import { loadComments } from "./loadComments.js";
//
//
//export async function submitReply(parentCommentId, principalDetails) {
//    const replyInput = document.getElementById(`reply-input-${parentCommentId}`);
//    const replyContent = replyInput.value.trim();
//
//    if (!replyContent) {
//        alert("답글을 입력해주세요.");
//        return;
//    }
//
//   await fetch(`http://localhost:8081/comments/comment/${parentCommentId}`, {
//        method: "POST",
//        headers: { "Content-Type": "application/json" },
//        body: JSON.stringify({
//            content: replyContent,
//            parentCommentId: parentCommentId
//        }),
//    })
//    .then(response => {
//        if (!response.ok) throw new Error("답글 저장 실패");
//        return response.json();
//    })
//    .then(data => {
//        alert("답글이 등록되었습니다.");
//        loadComments(data.boardId, data.boardUserId, principalDetails); // 댓글 새로고침
//    })
//    .catch(error => {
//        console.error("Error:", error);
//        alert("답글을 등록하는 중 오류가 발생했습니다.");
//    });
//
//}