// document.getElementById("deleteBoard").addEventListener("click", function () {
//        const boardId = document.getElementById("id").value; // 숨겨진 input에서 boardId 가져오기
//
//        $.ajax({
//            url: `/board/deleteboard/${boardId}`, // 삭제 API 경로
//            type: "DELETE", // HTTP 메서드
//            contentType: "application/json", // 요청 Content-Type
//            success: function (response) {
//                alert("게시글이 삭제되었습니다.");
//                window.location.href = "/"; // 성공 시 리다이렉트 경로 설정
//            },
//            error: function (xhr, status, error) {
//                alert("게시글이 삭제되지 않았습니다. 다시 시도해주세요.");
//                console.error("Error:", error);
//            }
//        });
//    });


const deleteButtons = document.querySelectorAll('[data-deleteBoardId]');
deleteButtons.forEach(button => {
	button.addEventListener('click', async function() {
		const boardId = this.getAttribute('data-deleteBoardId');

		if (!boardId) {
			alert('삭제할 게시글을 찾을 수 없습니다.');
			return;
		}

		const confirmation = confirm('정말로 게시글을 삭제하시겠습니까?');
		if (!confirmation) {
			return;
		}

		try {
			const response = await fetch(`/board/deleteboard/${boardId}`, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			});

			if (!response.ok) {
				const errorText = await response.text();
				throw new Error(`게시글 삭제 실패: ${errorText}`);
			}

			alert('게시글이 삭제되었습니다.');
			// 게시글 삭제 후, 페이지 새로고침


			if (window.location.href === "/board/list") {
				// 정확히 "/board/allboard" 경로에서만 실행
				window.location.reload();
			} else if (window.location.href.includes("/board/")) {
				// "/board/"를 포함하는 경로에서는 게시판 목록으로 리디렉션
				window.location.href = "/board/list";
			}

		} catch (error) {
			console.error('게시글 삭제 중 오류 발생:', error);
			alert('게시글을 삭제하는 도중 오류가 발생했습니다.');
		}
	});
});