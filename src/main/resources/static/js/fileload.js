document.getElementById("boardFiles").addEventListener("change", function (event) {
    // 미리보기 이미지를 표시할 컨테이너 요소 가져오기
    const previewContainer = document.getElementById("previewContainer");
    
    // 기존에 표시된 미리보기 초기화 (새로운 파일을 선택할 때 기존 이미지 제거)
    previewContainer.innerHTML = "";

    // 사용자가 선택한 파일 목록 가져오기
    const files = event.target.files;

    // 파일이 하나 이상 선택되었을 경우 실행
    if (files.length > 0) {
        // 파일 목록을 배열로 변환 후 순회
        Array.from(files).forEach(file => {
            // 파일 타입이 이미지인지 확인 (이미지가 아닐 경우 미리보기에 추가하지 않음)
            if (file.type.startsWith("image/")) {
                // FileReader 객체 생성 (파일을 읽어서 데이터 URL로 변환하기 위해 사용)
                const reader = new FileReader();

                // 파일을 성공적으로 읽었을 때 실행되는 이벤트 핸들러
                reader.onload = function (e) {
                    // 이미지 요소 생성
                    const img = document.createElement("img");
                    img.src = e.target.result; // 읽어온 이미지 데이터 URL을 src로 설정
                    img.style.width = "100px"; // 미리보기 이미지 너비 설정
                    img.style.height = "100px"; // 미리보기 이미지 높이 설정
                    img.style.marginRight = "10px"; // 이미지 간격 설정
                    img.style.objectFit = "cover"; // 이미지 비율 유지하면서 크기 조절
                    img.style.border = "1px solid #ddd"; // 테두리 스타일 설정

                    // 미리보기 컨테이너에 이미지 추가
                    previewContainer.appendChild(img);
                };

                // 파일을 읽어서 데이터 URL로 변환 (이미지 미리보기를 위해 사용)
                reader.readAsDataURL(file);
            }
        });
    }
});