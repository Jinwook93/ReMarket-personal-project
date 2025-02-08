export function addBoard (){			//게시글 추가
	
	document.getElementById("boardForm").addEventListener("submit", (e) => {
		    e.preventDefault();  // 기본 동작을 막음
		    alert("게시글이 등록되었습니다");
		    e.target.submit();  // 폼을 실제로 제출
		});

	
}


