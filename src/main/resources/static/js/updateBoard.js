document.getElementById('updateBoard').addEventListener('click', async () => {
    const formData = new FormData();

    // 기존 파일 추가
    const existingFiles = document.querySelectorAll("#existingFiles a");
    
    // 삭제된 파일을 제외한 기존 파일을 formData에 추가
  //  let fileUrls = []; // 기존 파일 URL 목록을 저장
    for (const existingFile of existingFiles) {
        // imagePreview 요소가 삭제되지 않았다면
        if (existingFile.closest('#imagePreview') !== null) {
            formData.append('existingFiles', existingFile.href);
    //        fileUrls.push(existingFile.href); // URL 목록에 추가
        }else{
			continue;
		}
        
    }

    // 기본 데이터 추가
    const boardData = {
        id: document.getElementById('id').value,
        title: document.getElementById('title').value,
        contents: document.getElementById('contents').value,
//        boardFiles: JSON.stringify(fileUrls)  // 파일 URL 목록을 보냄
boardFiles: JSON.stringify(formData.getAll(existingFiles))  // 파일 URL 목록을 보냄
    };
    formData.append('boardData', JSON.stringify(boardData));

    // 파일 추가
    const files = document.getElementById('boardFiles').files;
    for (const file of files) {
        formData.append('boardFiles', file);
    }

    try {
        const response = await fetch(`/board/updateboard/${boardData.id}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json', // JSON 응답 요청
            },
            body: formData,
        });

        if (response.ok) {
            alert('게시글이 수정되었습니다!');
            window.location.href = "/";
        } else {
            alert(`수정 실패: ${response.status}`);
            console.error('서버 응답:', response);
        }
    } catch (error) {
        console.error('요청 중 오류:', error);
        alert('요청 처리 중 문제가 발생했습니다.');
    }
});

// "X" 버튼 클릭 시 imagePreview 요소 제거
document.addEventListener('click', function(event) {
    if (event.target && event.target.id === 'displayButton') {
        // imagePreview 요소를 DOM에서 제거
        const imagePreview = event.target.closest('#imagePreview');
        if (imagePreview) {
            imagePreview.remove();
        }
    }
});
