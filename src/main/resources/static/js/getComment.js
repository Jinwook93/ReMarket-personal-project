import { loadComments } from "./loadComments.js";

document.addEventListener('DOMContentLoaded', function() {


	// 게시글 ID와 현재 사용자 정보 가져오기
	const boardId = document.getElementById('id').value;
	const principalDetails = document.getElementById('principalDetails').value;

	// 댓글 불러오기 함수 호출
	loadComments(boardId, principalDetails);

});
