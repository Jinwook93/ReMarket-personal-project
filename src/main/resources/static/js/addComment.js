import { loadComments } from "./loadComments.js";



export function addComment(boardId, parentCommentId, principalDetails) {
	let commentText = null;
	let replyInput = null;
	let replyContent = null;

	let parentIsPrivate = null;
	let childIsPrivate = null;


	if (parentCommentId == null) { // 부모 전송
		commentText = document.getElementById("commentText").value;
		parentIsPrivate = document.getElementById("isPrivate")?.checked ?? false;
		// 체크박스가 있을 경우만 값을 읽기
		//        parentIsPrivateChecked = parentIsPrivate ? parentIsPrivate.checked : false;
		//        console.log("parentIsPrivate checked:", parentIsPrivateChecked);

		if (!commentText.trim()) {
			alert("댓글 내용을 입력해주세요.");
			return;
		}
	} else { // 자식 전송
		replyInput = document.getElementById(`reply-input-${parentCommentId}`);
		replyContent = replyInput.value.trim();
		childIsPrivate = document.getElementById(`isPrivate-${parentCommentId}`)?.checked ?? false;
		if (!replyContent) {													
			alert("답글을 입력해주세요.");
			return;
		}
	}

//		console.log("childIsPrivate 요소:", document.getElementById(`isPrivate-${parentCommentId}`));
//		console.log("체크되었는가???" +childIsPrivate);


	fetch(`http://localhost:8081/comments/board/${boardId}`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({
			content: parentCommentId == null ? commentText : replyContent,
			parentCommentId: parentCommentId,
			boardId: boardId,
			isPrivate: parentCommentId == null ? parentIsPrivate  : childIsPrivate,
			isBlind: false
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
