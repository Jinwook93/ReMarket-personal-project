document.getElementById('updateBoard').addEventListener('click', async () => {
	
	
	
	
	
	
	//validation
        const title = document.getElementById('title');
        const product = document.getElementById('product');
        const price = document.getElementById('price');
        const address = document.getElementById('address');
        const address2 = document.getElementById('address2');
//        const contents = document.getElementById('contents');
		const files = document.getElementById('boardFiles').files;
		
		
        // 필드별 최대 글자 수 (원하는 기준으로 조절)
        const maxLength = {
            title: 30,
            product: 20,
            price: 15,
            address: 30,
            address2: 30,
//            contents: 1000
        };

        // 공백, null, 길이 검사 함수
        function validateField(field, fieldName, maxLen) {
            const value = field.value.trim();
            if (value === "" || value.length === 0) {
                alert(`${fieldName}을(를) 입력하세요.`);
                field.focus();
                return false;
            }
            if (value.length > maxLen) {
                alert(`${fieldName}은(는) 최대 ${maxLen}자까지 입력 가능합니다.`);
                field.focus();
                return false;
            }
            return true;
        }

        // 순서대로 검사
        if (!validateField(title, "제목", maxLength.title)) return;
        if (!validateField(product, "물품명", maxLength.product)) return;
        if (!validateField(price, "가격", maxLength.price)) return;
        if (!validateField(address, "주소", maxLength.address)) return;
        if (!validateField(address2, "세부 주소", maxLength.address2)) return;
//        if (!validateField(contents, "내용", maxLength.contents)) return;

		if(files.length >3){
			alert("파일 첨부는 3개까지 가능합니다");
			return;
		}


        // 모든 검사 통과 시 AJAX 또는 form 전송 실행 (예시)
     
//        alert("검증 성공! 수정 작업 진행합니다.");
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	const formData = new FormData();

	// 기존 파일 추가
const existingFiles = document.querySelectorAll("#existingFiles a");

for (const existingFile of existingFiles) {
    // imagePreview 요소가 삭제되지 않았다면
    if (existingFile.closest('#imagePreview') !== null) {
        formData.append('existingFiles', existingFile.href); // 기존 파일 URL 추가
        console.log(existingFile.href);
    } else {
        continue;
    }
}

if(existingFiles.length + files.length >3){
		alert("파일 첨부는 기존 첨부 파일까지 합해서 3개까지 가능합니다");
		return;
}


	// 기본 데이터 추가
	const boardData = {
		id: document.getElementById('id').value,
		title: document.getElementById('title').value,
		contents: document.getElementById('contents').value,
		//        boardFiles: JSON.stringify(fileUrls)  // 파일 URL 목록을 보냄
		buy_Sell: document.getElementById('buy_Sell').value,
		category: document.getElementById('category').value,
		price:document.getElementById("price").value,
		product:document.getElementById("product").value,
		boardFiles: JSON.stringify(formData.getAll(existingFiles)),  // 파일 URL 목록을 보냄,
		address: document.getElementById("address").value + " " + document.getElementById("address2").value
	};
	formData.append('boardData', JSON.stringify(boardData));

	// 파일 추가	)
//	const files = document.getElementById('boardFiles').files;	(위로 이동시킴)
	for (const file of files) {
		formData.append('boardFiles', file);
	}

	formData.append('nullimageButton', document.getElementById("nullimageButton").checked);
	
		console.log(formData.get('nullimageButton'));

//		document.getElementById("nullimageButton").checked


	try {
		const response = await fetch(`/board/updateboard/${boardData.id}`, {
			method: 'POST',
//			headers: {
//				'Accept': 'application/json', // JSON 응답 요청
//			},
			body: formData,
		});

		if (response.ok) {
			alert('게시글이 수정되었습니다!');
//			url = await response.text();
//			window.location.href =  url;

			window.location.href = `/board/view/${boardData.id}`;
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
