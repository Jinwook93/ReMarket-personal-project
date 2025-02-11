import { loadComments } from "./loadComments.js";
import { addComment } from "./addComment.js";
document.addEventListener('DOMContentLoaded', function() {


	// 게시글 ID와 현재 사용자 정보 가져오기
	const boardId = document.getElementById('id').value;
	let boardUserId = document.getElementById("userid").value;
	const principalDetails = document.getElementById('principalDetails').value;
	document.getElementById("submitComment").addEventListener('click', () =>{
		addComment(boardId, null, principalDetails);
	})

//	document.getElementById(`submit-${child.id}`).addEventListener('click', () =>{
//		addComment(boardId, child.id, principalDetails);
//	})


// 댓글이 추가될 부모 컨테이너에 이벤트 위임
//    document.getElementById("comment-section").addEventListener('click', function (event) {
//        if (event.target.matches("[id^='submit-']")) { 
//            const childId = event.target.id.replace("submit-", ""); 
//            addComment(boardId, childId, principalDetails);
//        }
//    });
//	// 댓글 불러오기 함수 호출
	loadComments(boardId,  boardUserId, principalDetails);

});
