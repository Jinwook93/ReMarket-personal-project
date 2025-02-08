
document.getElementById("updateMember").addEventListener("click", function () {
    const memberId = document.getElementById("id").value; // 숨겨진 input에서 boardId 가져오기
    window.location.href = `/updatemypage/${memberId}`;
});









//export function goUpdateForm(){
//	
//	const boardId = document.getElementById("id").value;
//	window.location.href = `/board/updateboard/${boardId}`;
//	
//	
//	
//	
//}







// 전역 스코프로 등록하는 방법
//
//
//function goUpdateForm() {
//    console.log("게시글 수정 폼으로 이동");
//    window.location.href = "/board/update"; // 수정 페이지로 이동
//}
//
//window.goUpdateForm = goUpdateForm; // 전역 스코프에 등록
