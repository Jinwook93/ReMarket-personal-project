import { deleteComment } from "./deleteComment.js";
import { updateComment } from "./updateComment.js";


document.addEventListener('DOMContentLoaded', function () {
    // 댓글 불러오기 함수 정의
    function loadComments(boardId, principalDetails) {
        fetch(`http://localhost:8081/comments/board/${boardId}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'GET',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("댓글을 불러오는 중 오류가 발생했습니다.");
                }
                return response.json();
            })
            .then(comments => {
                const commentsList = document.getElementById('commentsList');
                commentsList.innerHTML = ''; // 기존 댓글 초기화

                if (comments.length === 0) {
                    commentsList.innerHTML = '<p>댓글이 없습니다.</p>';
                    return;
                }

                comments.forEach(comment => {
                    const commentElement = document.createElement('div');
                    commentElement.classList.add('comment');
                    commentElement.id = `comment-${comment.id}`;
                    commentElement.innerHTML = `
                        <p class="comment-author">${comment.memberEntity.name}</p>
                        <p class="comment-text">${comment.content}</p>
                        <p class="comment-createTime">${comment.createTime}</p>
                    `;

                    // 수정 및 삭제 버튼 추가
                    if (comment.memberEntity.userid === principalDetails) {
                        const updateButton = document.createElement('button');
                        updateButton.textContent = '수정하기';
                        updateButton.addEventListener('click', () => updateComment(comment.id, comment.content));

                        const deleteButton = document.createElement('button');
                        deleteButton.textContent = '삭제하기';
                        deleteButton.addEventListener('click', () => deleteComment(comment.id));

                        commentElement.appendChild(updateButton);
                        commentElement.appendChild(deleteButton);
                    }

                    commentsList.appendChild(commentElement);
                });
            })
            .catch(error => {
                console.error("Error:", error);
                alert("댓글을 불러오는 데 실패했습니다. 서버 관리자에게 문의해 주세요.");
            });
    }
    
    
    
    
    
    
    
    
    
    
    
//      // 댓글 수정하기
//function updateComment(id, currentContent) {
//        const newContent = prompt("수정할 내용을 입력하세요:", currentContent);
//        if (!newContent) {
//            alert("내용을 입력해야 합니다.");
//            return;
//        }
//
//        fetch(`http://localhost:8081/comments/${id}`, {
//            method: 'PUT',
//            headers: {
//                'Content-Type': 'application/json;charset=UTF-8',
//            },
//            body: JSON.stringify({ content: newContent }),
//        })
//            .then(response => {
//				console.log(response);
//                if (!response.ok) {
//                    throw new Error("댓글 수정 중 오류가 발생했습니다.");
//                }
//                return response.json();
//            })
//            .then(result => {
//                alert("댓글이 성공적으로 수정되었습니다.");
//                const commentText = document.querySelector(`#comment-${id} .comment-text`);
//                if (commentText) commentText.textContent = newContent;
//            })
//            .catch(error => {
//                console.error("Error:", error);
//                alert("댓글 수정에 실패했습니다.");
//            });
//    }
//    
    
    
    
    
//     // 댓글 삭제하기
// function deleteComment(id) {
//        if (!confirm("정말로 댓글을 삭제하시겠습니까?")) {
//            return;
//        }
//
//        fetch(`http://localhost:8081/comments/${id}`, {
//            method: 'DELETE',
//        })
//            .then(response => {
//                if (!response.ok) {
//                    throw new Error("댓글 삭제 중 오류가 발생했습니다.");
//                }
//                return response.json();
//            })
//            .then(result => {
//                alert("댓글이 성공적으로 삭제되었습니다.");
//                const commentElement = document.getElementById(`comment-${id}`);
//                if (commentElement) commentElement.remove();
//            })
//            .catch(error => {
//                console.error("Error:", error);
//                alert("댓글 삭제에 실패했습니다.");
//            });
//    }
//    
    
    
    
    
    
    
    
    
    

    // 게시글 ID와 현재 사용자 정보 가져오기
    const boardId = document.getElementById('id').value;
    const principalDetails = document.getElementById('principalDetails').value;

    // 댓글 불러오기 함수 호출
    loadComments(boardId, principalDetails);
});
