import { loadComments } from "./loadComments.js";
const boardId = document.getElementById('id').value;
	let boardUserId = document.getElementById("userid").value;
	const principalDetails = document.getElementById('principalDetails').value;
// 댓글 삭제하기
export async function blindComment(commentId, isBlind) {
		// 게시글 ID와 현재 사용자 정보 가져오기
	
	
	if(!isBlind){
	if (!confirm("해당 댓글을 블라인드하시겠습니까?")) {
		return;
	}
	}else{
		if (!confirm("해당 댓글을 블라인드 취소하시겠습니까?")) {
		return;
	}
		
	}
	

fetch(`http://localhost:8081/comments/blind/${commentId}`, {
    method: 'PUT',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        isBlind: isBlind  // 이 값은 true 또는 false여야 함
    })
})
		.then(response => {
			if (!response.ok) {
				throw new Error("댓글 블라인드 중 오류가 발생했습니다.");
			}
			return response.json(); // 서버에서 List<Long> 반환
		})
		.then(result => {
			
//			const blindButtonId = document.getElementById(`blindButton-${id}`);
			if(result === true){
				alert("댓글이 성공적으로 블라인드 되었습니다.");
				}else{
					alert(" 블라인드가 취소되었습니다.");
					
				}
			// 댓글 불러오기 함수 호출
			loadComments(boardId,  boardUserId, principalDetails);
		})
		.catch(error => {
			console.error("Error:", error);
			alert("댓글 블라인드에 실패했습니다.");
		});
}

