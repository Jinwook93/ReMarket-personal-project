document.addEventListener('DOMContentLoaded', function() {
    let memberUserid = document.getElementById("userid").value;

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
                     <div style="display: flex; align-items: center;">
                        <img src="${comment.memberEntity.profileImage}" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">
                        <span>${comment.memberEntity.name}</span>
                     </div>
                     <div style="display: flex;">
                        <button type="button" class ="commentlike" id="commentlike-${comment.id}" name="commentlike">❤️ ${comment.totalLike}</button>
                        <button type="button" class ="commentdislike" id="commentdislike-${comment.id}" name="commentdislike">🖤 ${comment.totalDislike}</button>
                     </div>
                     <p class="comment-text">${comment.content}</p>
                     <p class="comment-createTime">${comment.createTime}</p>
                    `;

                    // 수정 및 삭제 버튼 추가
                    if ((memberUserid === principalDetails) || (comment.memberEntity.userid === principalDetails)) {
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

                    // 좋아요 및 싫어요 버튼 이벤트 리스너 등록 (동적으로 추가)
                    const commentlikeButton = document.getElementById(`commentlike-${comment.id}`);
                    const commentdislikeButton = document.getElementById(`commentdislike-${comment.id}`);
                    
  					commentButtonEnable(`${comment.id}`,commentlikeButton,commentdislikeButton);
  					
                    commentlikeButton.addEventListener("click", () => {
                        if (commentlikeButton.classList.contains("active")) {
                            commenthandleUnlike(comment.id);
                            commentlikeButton.classList.remove("active");
                        } else {
                            commenthandleLike(comment.id);
                            commentlikeButton.classList.add("active");
                            commentdislikeButton.classList.remove("active"); // 싫어요 취소
                        }
                    });

                    commentdislikeButton.addEventListener("click", () => {
                        if (commentdislikeButton.classList.contains("active")) {
                            commenthandleUndislike(comment.id);
                            commentdislikeButton.classList.remove("active");
                        } else {
                            commenthandleDislike(comment.id);
                            commentdislikeButton.classList.add("active");
                            commentlikeButton.classList.remove("active"); // 좋아요 취소
                        }
                    });
                });
            })
            .catch(error => {
                console.error("Error:", error);
                alert("댓글을 불러오는 데 실패했습니다. 서버 관리자에게 문의해 주세요.");
            });
    }

    // 좋아요 요청 함수
    const commenthandleLike = async (commentId) => {
        try {
            const response = await fetch(`/comment/${commentId}/like`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const result = await response.json();
                updatecommentLikeDislikeUI(commentId, result[0], result[1]);
            } else {
                console.error("좋아요 요청 실패");
            }
        } catch (error) {
            console.error("좋아요 요청 중 오류 발생:", error);
        }
    };

    // 좋아요 취소 요청 함수
    const commenthandleUnlike = async (commentId) => {
        try {
            const response = await fetch(`/comment/${commentId}/like`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const result = await response.json();
                updatecommentLikeDislikeUI(commentId, result[0], result[1]);
            } else {
                console.error("좋아요 취소 요청 실패");
            }
        } catch (error) {
            console.error("좋아요 취소 요청 중 오류 발생:", error);
        }
    };

    // 싫어요 요청 함수
    const commenthandleDislike = async (commentId) => {
        try {
            const response = await fetch(`/comment/${commentId}/dislike`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const result = await response.json();
                updatecommentLikeDislikeUI(commentId, result[0], result[1]);
            } else {
                console.error("싫어요 요청 실패");
            }
        } catch (error) {
            console.error("싫어요 요청 중 오류 발생:", error);
        }
    };

    // 싫어요 취소 요청 함수
    const commenthandleUndislike = async (commentId) => {
        try {
            const response = await fetch(`/comment/${commentId}/dislike`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const result = await response.json();
                updatecommentLikeDislikeUI(commentId, result[0], result[1]);
            } else {
                console.error("싫어요 취소 요청 실패");
            }
        } catch (error) {
            console.error("싫어요 취소 요청 중 오류 발생:", error);
        }
    };





//버튼 활성화 확인 함수
	
	    const commentButtonEnable = async (commentId,commentlikeButton,commentdislikeButton) => {
        try {
            const response = await fetch(`/comment/${commentId}/buttonenable`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("버튼 활성화 확인 성공");
                const result = await response.text();
                console.log(result);
                
                if(result === "ENABLE_LIKE"){
					commentlikeButton.classList.add("active");
					commentdislikeButton.classList.remove("active");
				}else if (result === "ENABLE_DISLIKE"){
					commentlikeButton.classList.remove("active");
					commentdislikeButton.classList.add("active");
				}else{
					commentlikeButton.classList.remove("active");
					commentdislikeButton.classList.remove("active");
					
				}
                
//                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("버튼 활성화 실패");
            }
        } catch (error) {
            console.error("싫어요 요청 중 오류 발생:", error);
        }
    };






    // UI 업데이트 함수
    const updatecommentLikeDislikeUI = (commentId, totalLike, totalDislike) => {
        const commentlikeButton = document.getElementById(`commentlike-${commentId}`);
        const commentdislikeButton = document.getElementById(`commentdislike-${commentId}`);

        commentlikeButton.textContent = `❤️ ${totalLike}`;
        commentdislikeButton.textContent = `🖤 ${totalDislike}`;
    };













    // 게시글 ID와 현재 사용자 정보 가져오기
    const boardId = document.getElementById('id').value;
    const principalDetails = document.getElementById('principalDetails').value;

    // 댓글 불러오기 함수 호출
    loadComments(boardId, principalDetails);
  
});
