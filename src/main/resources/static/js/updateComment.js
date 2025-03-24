import { loadComments } from "./loadComments.js";

loadComments

// 댓글 수정하기
export function updateComment(id, currentContent, blind) {
		const boardId = document.getElementById('id').value;
	let boardUserId = document.getElementById("userid").value;
	const principalDetails = document.getElementById('principalDetails').value;
	
    const commentElement = document.getElementById(`comment-${id}`);
    if (!commentElement) return;

    // 기존 텍스트 숨기기
    const commentText = commentElement.querySelector(".comment-text");
    if (commentText) commentText.style.display = "none";

    // 기존 input이 있으면 중복 생성 방지
    if (commentElement.querySelector(".comment-edit-input")) return;

    // 입력 필드 추가
    const inputField = document.createElement("input");
    inputField.type = "text";
    inputField.value = blind ? "" : currentContent; // 블라인드 댓글이면 빈칸
    inputField.classList.add("comment-edit-input");
    inputField.style.marginRight = "5px";
    inputField.style.width= "450px";
       inputField.id= `updateInputField-${id}`;
	 inputField.placeholder="수정할 댓글 내용을 입력하세요";
    // 비공개 체크박스 추가
    const privateLabel = document.createElement("label");
    privateLabel.style.display = "flex";
    privateLabel.style.alignItems = "center";
    privateLabel.style.gap = "5px";

    const privateCheckbox = document.createElement("input");
    privateCheckbox.type = "checkbox";
    privateCheckbox.id = `isPrivate-${id}`;
    privateCheckbox.checked?true:false; // 기존 상태 반영

    privateLabel.appendChild(privateCheckbox);
    privateLabel.appendChild(document.createTextNode("비밀글"));

    // 수정 완료 버튼 추가
    const saveButton = document.createElement("button");
    saveButton.textContent = "완료";
    saveButton.classList.add("comment-save-btn");
    saveButton.style.marginLeft = "5px";

    // 취소 버튼 추가
    const cancelButton = document.createElement("button");
    cancelButton.textContent = "취소";
    cancelButton.classList.add("comment-cancel-btn");
    cancelButton.style.marginLeft = "5px";



// 컨테이너 추가
			const updateInputcontainer = document.createElement("div");
			updateInputcontainer.style.diplay="none";
			updateInputcontainer.style.alignItems="center";
		updateInputcontainer.style.gap = '10px'; // 입력창과 버튼 사이에 간격 추가
			updateInputcontainer.style.margin = '10px'; // 좌우 및 위/아래 공백 추가


    // 버튼 클릭 이벤트 처리
    saveButton.addEventListener("click", () => {
        const newContent = inputField.value.trim();
        const isPrivate = privateCheckbox.checked; // 비공개 여부 가져오기



			
			
        if (!newContent) {
            alert("내용을 입력해야 합니다.");
            return;
        }

        fetch(`http://localhost:8081/comments/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json;charset=UTF-8",
            },
            body: JSON.stringify({
                content: newContent,
                isPrivate: isPrivate,
            }),
        })
            .then(response => {
                if (!response.ok) throw new Error("댓글 수정 중 오류 발생");
                return response.json();
            })
            .then(() => {
                alert("댓글이 성공적으로 수정되었습니다.");
//                if (commentText) {
//                    commentText.textContent = newContent;
//                    commentText.style.display = "block"; // 다시 표시
//                }
//                inputField.remove();
//                privateLabel.remove();
//                saveButton.remove();
//                cancelButton.remove();
                		loadComments(boardId,  boardUserId, principalDetails);
            })
            .catch(error => {
                console.error("Error:", error);
                alert("댓글 수정에 실패했습니다.");
            });
    });

    // 취소 버튼 이벤트 처리
    cancelButton.addEventListener("click", () => {
        if (commentText) commentText.style.display = "block"; // 기존 텍스트 표시
        inputField.remove();
        privateLabel.remove();
        saveButton.remove();
        cancelButton.remove();
       updateInputcontainer.style.display ="none";
    });

    // 기존 요소 아래에 추가
    updateInputcontainer.style.display ="flex";
        updateInputcontainer.appendChild(privateLabel);
   updateInputcontainer.appendChild(inputField);
    updateInputcontainer.appendChild(saveButton);
    updateInputcontainer.appendChild(cancelButton);
        commentElement.appendChild(updateInputcontainer);
         
}








