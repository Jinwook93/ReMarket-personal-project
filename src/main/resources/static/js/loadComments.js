import { deleteComment } from "./deleteComment.js";
import { blindComment } from "./blindComment.js";
import { updateComment } from "./updateComment.js";
import * as commentLike from "./commentLike.js";
import { addComment } from "./addComment.js";
import { formatDate } from "./formatDate.js";


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


			loadChildComments(comments, boardId, boardUserId, principalDetails, -20);


		})
		.catch(error => {
			console.error("Error:", error);
			alert("댓글을 불러오는 데 실패했습니다. 서버 관리자에게 문의해 주세요.");
		});







	//자식 댓글 불러오기

	function loadChildComments(childComments, boardId, boardUserId, principalDetails, marginLeft) {


		childComments.sort((a, b) => b.id - a.id);
		childComments.forEach(child => {

			//자식 댓글
			//			const parentCommentResponse = await fetch(`/comments/parent/${child.id}`);
			//			const parentComment = await parentCommentResponse.json();
			//			console.log("부모 호출 확인" + parentComment);
const childCommentElement = document.createElement('div');
childCommentElement.classList.add('comment');
childCommentElement.id = `comment-${child.id}`;

const childMarginLeft = marginLeft + 20; // 개별 댓글마다 marginLeft 증가
childCommentElement.style.marginLeft = `${childMarginLeft}px`;

// 대댓글 입력창의 너비 조정 (marginLeft에 따른 길이 변경)
const replyInputElement = document.getElementById(`reply-input-${child.id}`);
if (replyInputElement) {
    const replyInputWidth = replyInputElement.offsetWidth; // 실제 너비 구하기
    const childReplyInputWidth = replyInputWidth - 20; // marginLeft에 따른 길이 조정
    replyInputElement.style.width = `${childReplyInputWidth}px`; // width 수정
}

// 수정 입력창의 너비 조정 (marginLeft에 따른 길이 변경)
const updateInputElement = document.getElementById(`updateInputField-${child.id}`);
if (updateInputElement) {
    const updateInputWidth = updateInputElement.offsetWidth; // 실제 너비 구하기
    const childUpdateInputWidth = updateInputWidth - 20; // marginLeft에 따른 길이 조정
    updateInputElement.style.width = `${childUpdateInputWidth}px`; // width 수정
}


							
							
			childCommentElement.innerHTML = `
<div style="display: flex; justify-content: space-between; align-items: center;">
    <!-- 프로필 (왼쪽 정렬) -->
    <div class="profile" style="display: flex; align-items: center;">
        ${marginLeft >= 0 ? `<img src="/icon/childComment.png" loading="lazy" alt="Profile Image Preview" class="profile-img">` : ""}
        ${child.memberEntity.profileImage != null
					? `<img src="${child.memberEntity.profileImage}" loading="lazy" alt="Profile Image Preview" class="profile-img">`
					: `<img src="/boardimage/nullimage.jpg" loading="lazy" alt="Profile Image Preview" class="profile-img">`
				}
        <span>${child.memberEntity.userid}</span>
    </div>

    <!-- 댓글 버튼과 시간 (오른쪽 정렬) -->
    <div class="comment-right">
       ${child.memberEntity.userid !== principalDetails
					? `<div class="comment-actions">
        <button type="button" class="commentlike" id="commentlike-${child.id}" name="commentlike">❤️ ${child.totalLike}</button>
        <button type="button" class="commentdislike" id="commentdislike-${child.id}" name="commentdislike">🖤 ${child.totalDislike}</button>
    </div>`
					: `<div class="comment-actions"> ❤️ ${child.totalLike} / 🖤 ${child.totalDislike}</div>`
				}

        <p class="comment-createTime">${formatDate(child.createTime)}</p>
    </div>
</div>

<p class="comment-text">
    <!-- 비밀 댓글을 확인할 수 있는 조건 추가 -->
    ${child.blind ?
					'<b>⚠️블라인드 처리된 글입니다</b>' :
					(child.private === true &&
						(principalDetails != boardUserId &&
							principalDetails != child.memberEntity.userid &&
							principalDetails != child.parentComment?.memberEntity?.userid)) ?
						'<b>🔐비밀글입니다</b>' :
						child.content
				}
</p>

		<!--<p>부모정보 : ${child.parentComment ? child.parentComment.memberEntity.userid : ""}</p>
			
			<p>비밀글 여부 :${child.private}</p>
			<p>블라인드 : ${child.blind}</p>-->
            
            
            
        `;


			//			if (child.parentComment !== null) {
			//				console.log(child.parentComment);
			//			}

			const buttonContainer = document.createElement('div');
			buttonContainer.classList.add('comment-buttons');


			// 🟢 대댓글 버튼 추가
			const replyButton = document.createElement('button');
			replyButton.textContent = '댓글';
			replyButton.classList.add('reply-button');
			replyButton.addEventListener('click', () => toggleReplyInput(child.id));
			if (principalDetails && principalDetails !== 'anonymousUser') {
				// 로그인한 상태 → 댓글 버튼 생성
				buttonContainer.appendChild(replyButton);
				childCommentElement.appendChild(buttonContainer);
			}

			//수정 및 삭제버튼 추가
			if (child.memberEntity.userid === principalDetails) {

				const updateButton = document.createElement('button');
				updateButton.textContent = '수정';
				updateButton.classList.add('update-button');
				updateButton.addEventListener('click', () => updateComment(child.id, child.content, child.blind));

				if (child.blind) {
					updateButton.style.display = "none";
				}

				const deleteButton = document.createElement('button');
				deleteButton.textContent = '삭제';
				deleteButton.classList.add('delete-button');
				deleteButton.addEventListener('click', () => deleteComment(child.id));

				buttonContainer.appendChild(updateButton);
				buttonContainer.appendChild(deleteButton);
				childCommentElement.appendChild(buttonContainer);
			} else if (child.memberEntity.userid !== principalDetails && boardUserId === principalDetails) {
				//   const buttonContainer = document.createElement('div');
				//    buttonContainer.classList.add('comment-buttons');

				if (child.memberEntity.userid !== boardUserId && boardUserId === principalDetails) {
					const blindButton = document.createElement('button');
					blindButton.id = `blindButton-${child.id}`;
					blindButton.textContent = child.blind === false ? '블라인드' : '블라인드 취소';
					blindButton.style.backgroundColor = child.blind === false ? 'green' : 'orange';
					blindButton.classList.add('blind-button');
					blindButton.addEventListener('click', () => blindComment(child.id, child.blind));

					buttonContainer.appendChild(blindButton);
				}

				const deleteButton = document.createElement('button');
				deleteButton.textContent = '삭제';
				deleteButton.classList.add('delete-button');
				deleteButton.addEventListener('click', () => deleteComment(child.id));

				buttonContainer.appendChild(deleteButton);
				childCommentElement.appendChild(buttonContainer);
			}


			//			// 🟢 대댓글 버튼 추가
			//			const replyButton = document.createElement('button');
			//			replyButton.textContent = '댓글';
			//			replyButton.classList.add('reply-button');
			//			replyButton.addEventListener('click', () => toggleReplyInput(child.id));
			//			buttonContainer.appendChild(replyButton);
			//			childCommentElement.appendChild(replyButton);

			// 🟢 대댓글 입력창 추가 (초기에는 숨김)
const replyContainer = document.createElement('div');
replyContainer.id = `reply-container-${child.id}`;
replyContainer.style.display = 'none'; // 입력창과 버튼을 가로로 배치하기 위한 스타일
replyContainer.style.alignItems = 'center'; // 세로 정렬을 맞추기 위한 스타일
replyContainer.style.gap = '10px'; // 입력창과 버튼 사이에 간격 추가
replyContainer.style.margin = '10px'; // 좌우 및 위/아래 공백 추가

replyContainer.innerHTML = `
    <label style="display: flex; align-items: center; gap: 5px;">
        <input type="checkbox" id="isPrivate-${child.id}">비밀글
    </label>
    <input type="text" id="reply-input-${child.id}" placeholder="답글을 입력하세요" style="width: 65%; margin-right: 10px;" />
`;

// 버튼 생성
const submitButton = document.createElement('button');
submitButton.id = `submit-${child.id}`;
submitButton.textContent = '등록';
submitButton.classList.add("submitComment");
submitButton.addEventListener('click', () => addComment(boardId, child.id, principalDetails));

// 입력창과 버튼을 같은 div에 추가
replyContainer.appendChild(submitButton);

// 댓글 요소에 replyContainer 추가
childCommentElement.appendChild(replyContainer);

// 댓글 리스트에 추가
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


function toggleReplyInput(commentId) {
	const replyContainer = document.getElementById(`reply-container-${commentId}`);
		const replyInput = document.getElementById(`reply-input-${commentId}`);
	const submitVisible = document.getElementById(`submit-${commentId}`);

	if (replyContainer.style.display === 'none') {
		replyContainer.style.display = 'flex';
		submitVisible.style.display = 'flex';
		replyInput.value = '';
	} else {
		replyContainer.style.display = 'none';
		submitVisible.style.display = 'none';
	}

}

