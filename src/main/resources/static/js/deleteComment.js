import { loadComments } from "./loadComments.js";
// 댓글 삭제하기
export function deleteComment(id) {
		// 게시글 ID와 현재 사용자 정보 가져오기
	const boardId = document.getElementById('id').value;
	let boardUserId = document.getElementById("userid").value;
	const principalDetails = document.getElementById('principalDetails').value;
	
	if (!confirm("정말로 댓글을 삭제하시겠습니까?")) {
		return;
	}

	fetch(`http://localhost:8081/comments/${id}`, {
		method: 'DELETE',
	})
		.then(response => {
			if (!response.ok) {
				throw new Error("댓글 삭제 중 오류가 발생했습니다.");
			}
			return response.json(); // 서버에서 List<Long> 반환
		})
		.then(result => {
			alert("댓글이 성공적으로 삭제되었습니다.");
			// 댓글 불러오기 함수 호출
			loadComments(boardId,  boardUserId, principalDetails);
		})
		.catch(error => {
			console.error("Error:", error);
			alert("댓글 삭제에 실패했습니다.");
		});
}
