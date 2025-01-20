  // 댓글 수정하기
  export  function updateComment(id, currentContent) {
        const newContent = prompt("수정할 내용을 입력하세요:", currentContent);
        if (!newContent) {
            alert("내용을 입력해야 합니다.");
            return;
        }

        fetch(`http://localhost:8081/comments/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
            body: JSON.stringify({ content: newContent }),
        })
            .then(response => {
				console.log(response);
                if (!response.ok) {
                    throw new Error("댓글 수정 중 오류가 발생했습니다.");
                }
                return response.json();
            })
            .then(result => {
                alert("댓글이 성공적으로 수정되었습니다.");
                const commentText = document.querySelector(`#comment-${id} .comment-text`);
                if (commentText) commentText.textContent = newContent;
            })
            .catch(error => {
                console.error("Error:", error);
                alert("댓글 수정에 실패했습니다.");
            });
    }