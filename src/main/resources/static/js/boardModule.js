export function addBoard (){			//게시글 추가
	
	document.getElementById("boardForm").addEventListener("submit", (e) => {
		    e.preventDefault();  // 기본 동작을 막음
		    alert("게시글이 등록되었습니다");
		    e.target.submit();  // 폼을 실제로 제출
		});

	
}


export async function getBoardMainFile(boardId) {
	// DB에서 roomId 내의 loggedUserId, userid 간의 메일 크기를 실시간으로 조회
	return fetch(`/board/getBoardMainFile/${boardId}`, {
		method: "GET",
		headers: { 'Content-Type': "application/json;charset=utf-8;" }
	})
		.then(response => {
			return response.text();
		})
		.then(data => {
			//        console.log(data);  // Print the received data
			return data;  // Return the data
		})
		.catch(error => {
			console.error("Error fetching message count:", error);  // Print any error that occurs
		});
}