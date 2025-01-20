 // 댓글 삭제하기
   export function deleteComment(id) {
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
                return response.json();
            })
            .then(result => {
                alert("댓글이 성공적으로 삭제되었습니다.");
                const commentElement = document.getElementById(`comment-${id}`);
                if (commentElement) commentElement.remove();
            })
            .catch(error => {
                console.error("Error:", error);
                alert("댓글 삭제에 실패했습니다.");
            });
    }