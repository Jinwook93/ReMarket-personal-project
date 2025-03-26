// 모달 요소
const modal = document.getElementById('searchModal');
const openModalBtn = document.getElementById('openModalBtn');
const closeModalBtn = document.getElementById('closeModalBtn');
const searchForm = document.getElementById('searchForm');

// 모달 열기
openModalBtn.onclick = function() {
    modal.style.display = "block";
      bringToFront(modal);  // 모달을 최상위로 올리기
}

// 모달 닫기
closeModalBtn.onclick = function() {
    modal.style.display = "none";
}

// 모달 밖 클릭 시 닫기
window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

//// 폼 제출 시 서버로 요청 보내기
//searchForm.onsubmit = function(event) {
//    event.preventDefault(); // 기본 제출 동작을 막음
//
//    const formData = new FormData(searchForm);
//    const data = {};
//    formData.forEach((value, key) => {
//        data[key] = value;
//    });
//
//    // 서버에 POST 요청 보내기
//    fetch('/board/searchBoardManager', {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/json'
//        },
//        body: JSON.stringify(data)
//    })
//    .then(response => response.text())
//    .then(result => {
//        console.log('검색 결과:', result);
//        modal.style.display = "none"; // 검색 후 모달 닫기
//        event.target.submit();
//        // 결과를 처리하는 로직 추가 (예: 결과 페이지로 이동하거나 화면에 표시)
//    })
//    .catch(error => {
//        console.error('에러 발생:', error);
//    });
//}
