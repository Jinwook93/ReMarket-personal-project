import { deleteComment } from "./deleteComment.js";
import { updateComment } from "./updateComment.js";


let boardId = document.getElementById("id").value;
const principalDetails = document.getElementById('principalDetails').value;

$('#submitComment').click(function () {
    // 댓글 내용 가져오기
    const commentText = $('#commentText').val();

    if (!commentText.trim()) {
        alert("댓글 내용을 입력해주세요.");
        return;
    }

    // 댓글 데이터 전송
    fetch(`http://localhost:8081/comments/board/${boardId}`, {
        headers: {
            'Content-Type': 'application/json',
        },
        method: 'POST',
        body: JSON.stringify({
            content: commentText,
        }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("댓글 작성 중 오류가 발생했습니다.");
            }
            return response.json();
        })
        .then(data => {
            alert("댓글이 추가되었습니다.");

            // 댓글 목록에 새 댓글 추가
            const commentElement = document.createElement('div');
            commentElement.classList.add('comment');
            commentElement.id = `comment-${data.id}`;
            commentElement.innerHTML = `
                <p class="comment-author">${data.memberEntity.name}</p>
                <p class="comment-text">${data.content}</p>
                <p class="comment-createTime">${data.createTime}</p>
            `;

            // 수정 및 삭제 버튼 추가
            if (data.memberEntity.userid === principalDetails) {
                const updateButton = document.createElement('button');
                updateButton.textContent = '수정하기';
                updateButton.addEventListener('click', () => updateComment(data.id, data.content));

                const deleteButton = document.createElement('button');
                deleteButton.textContent = '삭제하기';
                deleteButton.addEventListener('click', () => deleteComment(data.id));

                commentElement.appendChild(updateButton);
                commentElement.appendChild(deleteButton);
            }

            // Append the new comment to the list
            document.getElementById('commentsList').appendChild(commentElement);

            // 댓글 입력 필드 초기화
            $('#commentText').val('');
        })
        .catch(error => {
            console.error("Error:", error);
            alert("댓글 추가에 실패했습니다.");
        });
});
