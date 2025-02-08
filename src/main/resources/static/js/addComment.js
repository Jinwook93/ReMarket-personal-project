import { loadComments } from "./loadComments.js";

let boardId = document.getElementById("id").value;
const principalDetails = document.getElementById('principalDetails').value;

$('#submitComment').click(function() {
	// 댓글 내용 가져오기
	const commentText = $('#commentText').val();

	if (!commentText.trim()) {
		alert("댓글 내용을 입력해주세요.");
		return;
	}

	// 댓글 데이터 전송
	fetch(`http://localhost:8081/comments/board/${boardId}`, {
		headers: {
			'Content-Type': 'application/json',
		},
		method: 'POST',
		body: JSON.stringify({
			content: commentText,
		}),
	})
		.then(response => {
			if (!response.ok) {
				throw new Error("댓글 작성 중 오류가 발생했습니다.");
			}
			return response.json();
		})
		.then(data => {
			alert("댓글이 추가되었습니다.");
			  $('#commentText').val('');
			// 댓글 불러오기 함수 호출
			loadComments(boardId, principalDetails);
		
		})
		.catch(error => {
			console.error("Error:", error);
			alert("댓글 추가에 실패했습니다.");
		});
});
