	import { deleteComment } from "./deleteComment.js";
import { updateComment } from "./updateComment.js";
import * as commentLike from "./commentLike.js";


	
	
	// 댓글 불러오기 함수 정의
	export function loadComments(boardId, principalDetails) {
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
                   ${comment.memberEntity.profileImage != null
							? `<img src="${comment.memberEntity.profileImage}" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">`
							: `<img src="/boardimage/nullimage.jpg" loading="lazy" alt="Profile Image Preview" style="width: 50px; height: 50px; margin-right: 10px;">`
						}

                        <span>${comment.memberEntity.userid}</span>
                     </div>
                    ${comment.memberEntity.userid !== principalDetails ?
							`<div style="display: flex;">
      								 <button type="button" class="commentlike" id="commentlike-${comment.id}" name="commentlike">❤️ ${comment.totalLike}</button>
      								 <button type="button" class="commentdislike" id="commentdislike-${comment.id}" name="commentdislike">🖤 ${comment.totalDislike}</button>
									   </div>` : ""
						}
                     <p class="comment-text">${comment.content}</p>
                     <p class="comment-createTime">${comment.createTime}</p>
                    `;

					// 수정 및 삭제 버튼 추가
					
					
					if (comment.memberEntity.userid === principalDetails) {
						const divLikeDisLike = document.createElement('div');
						divLikeDisLike.style.display = "flex";
						commentElement.appendChild(divLikeDisLike);
						const likeText = document.createElement('p');
						likeText.innerText = `좋아요 수 : ${comment.totalLike}   / `;
						divLikeDisLike.appendChild(likeText);
//							commentElement.appendChild(likeText);
						const dislikeText = document.createElement('p');
					      dislikeText.innerText= `싫어요 수 : ${comment.totalDislike}`;
					      		divLikeDisLike.appendChild(dislikeText);
//					      commentElement.appendChild(dislikeText);
					const updateButton = document.createElement('button');
						updateButton.textContent = '수정';
						updateButton.addEventListener('click', () => updateComment(comment.id, comment.content));

						const deleteButton = document.createElement('button');
						deleteButton.textContent = '삭제';
						deleteButton.style.backgroundColor = 'red';
						deleteButton.addEventListener('click', () => deleteComment(comment.id));

						commentElement.appendChild(updateButton);
						commentElement.appendChild(deleteButton);
					}	else if (comment.memberEntity.userid  === principalDetails) {
						const deleteButton = document.createElement('button');
						deleteButton.textContent = '삭제';
						deleteButton.style.backgroundColor = 'red';
						deleteButton.addEventListener('click', () => deleteComment(comment.id));
							commentElement.appendChild(deleteButton);
					}
				
			




					commentsList.appendChild(commentElement);

					if (comment.memberEntity.userid !== principalDetails) {

						// 좋아요 및 싫어요 버튼 이벤트 리스너 등록 (동적으로 추가)
						const commentlikeButton = document.getElementById(`commentlike-${comment.id}`);
						const commentdislikeButton = document.getElementById(`commentdislike-${comment.id}`);

						commentLike.commentButtonEnable(`${comment.id}`, commentlikeButton, commentdislikeButton);

						commentlikeButton.addEventListener("click", () => {
							if (commentlikeButton.classList.contains("active")) {
								commentLike.commenthandleUnlike(comment.id);
								commentlikeButton.classList.remove("active");
							} else {
								commentLike.commenthandleLike(comment.id);
								commentlikeButton.classList.add("active");
								commentdislikeButton.classList.remove("active"); // 싫어요 취소
							}
						});

						commentdislikeButton.addEventListener("click", () => {
							if (commentdislikeButton.classList.contains("active")) {
								commentLike.commenthandleUndislike(comment.id);
								commentdislikeButton.classList.remove("active");
							} else {
								commentLike.commenthandleDislike(comment.id);
								commentdislikeButton.classList.add("active");
								commentlikeButton.classList.remove("active"); // 좋아요 취소
							}
						});
					}
				});
			})
			.catch(error => {
				console.error("Error:", error);
				alert("댓글을 불러오는 데 실패했습니다. 서버 관리자에게 문의해 주세요.");
			});
	}