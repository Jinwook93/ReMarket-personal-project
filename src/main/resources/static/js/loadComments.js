import { deleteComment } from "./deleteComment.js";
import { updateComment } from "./updateComment.js";
import * as commentLike from "./commentLike.js";
import { addComment } from "./addComment.js";

// 🟢 대댓글 입력창 토글 함수
function toggleReplyInput(commentId) {
	const replyContainer = document.getElementById(`reply-container-${commentId}`);
	const submitVisible = document.getElementById(`submit-${commentId}`);

	if (replyContainer.style.display === 'none') {
		replyContainer.style.display = 'block';
		submitVisible.style.display = 'block';
	} else {
		replyContainer.style.display = 'none';
		submitVisible.style.display = 'none';
	}

}


// 댓글 불러오기 함수 정의
export function loadComments(boardId, boardUserId, principalDetails) {
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

				loadChildComments(comments, boardId, boardUserId, principalDetails,-20);


		})
		.catch(error => {
			console.error("Error:", error);
			alert("댓글을 불러오는 데 실패했습니다. 서버 관리자에게 문의해 주세요.");
		});







	//자식 댓글 불러오기

	function loadChildComments(childComments,  boardId, boardUserId, principalDetails, marginLeft) {

	if(marginLeft >=0){
    childComments.sort((a, b) => b.id - a.id);
	}
    childComments.forEach(child => {

        const childCommentElement = document.createElement('div');
        childCommentElement.classList.add('comment');
        childCommentElement.id = `comment-${child.id}`;
        
        const childMarginLeft = marginLeft + 20; // 개별 댓글마다 marginLeft 증가
        childCommentElement.style.marginLeft = `${childMarginLeft}px`;

        childCommentElement.innerHTML = `
            <div style="display: flex; align-items: center;">
              ${marginLeft >= 0 ? `<img src="/icon/childComment.png" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">` : ""}
                ${child.memberEntity.profileImage != null
                    ? `<img src="${child.memberEntity.profileImage}" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">`
                    : `<img src="/boardimage/nullimage.jpg" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">`
                }
                <span>${child.memberEntity.userid}</span>
            </div>
            ${child.memberEntity.userid !== principalDetails ?
                `<div style="display: flex;">
                    <button type="button" class="commentlike" id="commentlike-${child.id}" name="commentlike">❤️ ${child.totalLike}</button>
                    <button type="button" class="commentdislike" id="commentdislike-${child.id}" name="commentdislike">🖤 ${child.totalDislike}</button>
                </div>` : ""
            }
            <p class="comment-text">${child.content}</p>
            <p class="comment-createTime">${child.createTime}</p>
        `;

        // 수정 및 삭제 버튼 추가
        if (child.memberEntity.userid === principalDetails) {
            const divLikeDisLike = document.createElement('div');
            divLikeDisLike.style.display = "flex";
            childCommentElement.appendChild(divLikeDisLike);
            
            const likeText = document.createElement('p');
            likeText.innerText = `좋아요 수 : ${child.totalLike}   / `;
            divLikeDisLike.appendChild(likeText);
            
            const dislikeText = document.createElement('p');
            dislikeText.innerText = `싫어요 수 : ${child.totalDislike}`;
            divLikeDisLike.appendChild(dislikeText);

            const updateButton = document.createElement('button');
            updateButton.textContent = '수정';
            updateButton.addEventListener('click', () => updateComment(child.id, child.content));

            const deleteButton = document.createElement('button');
            deleteButton.textContent = '삭제';
            deleteButton.style.backgroundColor = 'red';
            deleteButton.addEventListener('click', () => deleteComment(child.id));

            childCommentElement.appendChild(updateButton);
            childCommentElement.appendChild(deleteButton);
        } else if (child.memberEntity.userid !== principalDetails && boardUserId === principalDetails) {
            const deleteButton = document.createElement('button');
            deleteButton.textContent = '삭제';
            deleteButton.style.backgroundColor = 'red';
            deleteButton.addEventListener('click', () => deleteComment(child.id));
            childCommentElement.appendChild(deleteButton);
        }

        // 🟢 대댓글 버튼 추가
        const replyButton = document.createElement('button');
        replyButton.textContent = '댓글';
        replyButton.addEventListener('click', () => toggleReplyInput(child.id));
        childCommentElement.appendChild(replyButton);

        // 🟢 대댓글 입력창 추가 (초기에는 숨김)
        const replyContainer = document.createElement('div');
        replyContainer.id = `reply-container-${child.id}`;
        replyContainer.style.display = 'none'; // 기본적으로 숨김
        replyContainer.innerHTML = `
            <input type="text" id="reply-input-${child.id}" placeholder="답글을 입력하세요" style="width: 80%;" />
        `;
        
        const submitButton = document.createElement('button');
        submitButton.id = `submit-${child.id}`;
        submitButton.style.display = replyContainer.style.display;
        submitButton.textContent = '등록';
        submitButton.addEventListener('click', () => addComment(boardId, child.id, principalDetails));

        childCommentElement.appendChild(replyContainer);
        childCommentElement.appendChild(submitButton);

        commentsList.appendChild(childCommentElement);

        if (child.memberEntity.userid !== principalDetails) {
            // 좋아요 및 싫어요 버튼 이벤트 리스너 등록 (동적으로 추가)
            const commentlikeButton = document.getElementById(`commentlike-${child.id}`);
            const commentdislikeButton = document.getElementById(`commentdislike-${child.id}`);

            commentLike.commentButtonEnable(`${child.id}`, commentlikeButton, commentdislikeButton);

            commentlikeButton.addEventListener("click", () => {
                if (commentlikeButton.classList.contains("active")) {
                    commentLike.commenthandleUnlike(child.id);
                    commentlikeButton.classList.remove("active");
                } else {
                    commentLike.commenthandleLike(child.id);
                    commentlikeButton.classList.add("active");
                    commentdislikeButton.classList.remove("active"); // 싫어요 취소
                }
            });

            commentdislikeButton.addEventListener("click", () => {
                if (commentdislikeButton.classList.contains("active")) {
                    commentLike.commenthandleUndislike(child.id);
                    commentdislikeButton.classList.remove("active");
                } else {
                    commentLike.commenthandleDislike(child.id);
                    commentdislikeButton.classList.add("active");
                    commentlikeButton.classList.remove("active"); // 좋아요 취소
                }
            });
        }

        // 자식 댓글이 있는 경우 재귀적으로 처리
        if (child.childComments && child.childComments.length > 0) {
            loadChildComments(child.childComments, boardId, boardUserId, principalDetails, childMarginLeft);
        }
    });
}


}
