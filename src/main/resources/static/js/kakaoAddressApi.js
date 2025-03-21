// 모달 열기
document.getElementById("addressButton").addEventListener("click", function() {
  document.getElementById("addressModal").style.display = "block";
});

// 모달 닫기
document.getElementById("closeModal").addEventListener("click", function() {
  document.getElementById("addressModal").style.display = "none";
});

// 외부 클릭 시 모달 닫기
window.onclick = function(event) {
  if (event.target == document.getElementById("addressModal")) {
    document.getElementById("addressModal").style.display = "none";
  }
};

export function searchAddress() {
  const query = document.getElementById("addressKeywordModal").value; // 검색할 주소
  
  fetch(`https://dapi.kakao.com/v2/local/search/address.json?query=${encodeURIComponent(query)}`, {
    method: 'GET',
    headers: { 'Authorization': "KakaoAK 1b3675999e224828ea71304f8d2cfc87" }
  })
  .then(response => response.json())
  .then(data => {
    console.log(data);
    const addressResult = document.getElementById("addressResult");
    addressResult.innerHTML = ""; // 기존 결과 초기화 (중복 방지)

    data.documents.forEach((address, index) => {
      const addresslist = document.createElement("ul");
      const add = document.createElement("li");
      const href = document.createElement("a");

      href.id = "addresslist-" + index;
      href.innerHTML = address.address_name;
      href.href = "#";  // a 태그니까 href 추가해주는 게 좋음

      add.appendChild(href);
      addresslist.appendChild(add);
      addressResult.appendChild(addresslist);

      // a 태그에 클릭 이벤트 추가
      document.getElementById(href.id).addEventListener("click", function(e) {
        selectAddress(e, address.address_name); // 주소 전달
      });
    });
  })
  .catch(error => console.error('Error:', error));  // 에러 처리
}

// 주소 선택 시 input에 값 넣기
export function selectAddress(e, address_name) {
  e.preventDefault(); // a 태그 기본 이벤트 막기
  const address = document.getElementById("address");
  address.value = address_name; // 클릭한 주소를 input에 넣기

  // 모달 닫기
  document.getElementById("addressModal").style.display = "none";
}

// 주소 검색 버튼 클릭 시
document.getElementById("addressButtonModal").addEventListener("click", searchAddress);




//// 주소에서 '/'를 공백으로 바꾸는 함수
//function updateAddress1() {
//    // 입력받은 주소 (예: 사용자가 입력한 값)
//    const address = document.getElementById("address").value;
//    
//    // '/'를 공백으로 바꿈
//    const updatedAddress = address.replace(/\//g, " "); // 모든 '/'를 공백으로 바꿈
//    
//    // 변경된 주소를 다시 input에 넣기
//    document.getElementById("address").value = updatedAddress;
//
//    console.log("변경된 주소: ", updatedAddress); // 콘솔에 출력 (디버깅용)
//}
//
//// 버튼 클릭 시 updateAddress 함수 호출
//document.addEventListener("DOMContentLoaded", updateAddress1);











export async function updateAddress2() {
    // 입력받은 주소 (예: 사용자가 입력한 값)
    
    if(!window.location.href.includes("/board")){
    const address = document.getElementById("loggedAddress").value;
    // '/'를 기준으로 주소를 분리
    const addressParts = address.split("/");

    // 주소 분리된 배열을 로그로 출력
    console.log("주소 분리 결과: ", addressParts);

    // 분리된 주소를 각각 input 필드에 반영 (예시로 3개의 input 필드 사용)
    document.getElementById("address").value = addressParts[0] || ''; // 첫 번째 부분
    document.getElementById("address2").value = addressParts[1] || ''; // 두 번째 부분

    console.log("변경된 주소: ", addressParts); // 콘솔에 출력 (디버깅용)
    
    
    
    
    
    
    }else if (window.location.href.includes("/board")){
 const address = document.getElementById("boardAddress").value;
    // '/'를 기준으로 주소를 분리
    const addressParts = address.split("/");

    // 주소 분리된 배열을 로그로 출력
    console.log("주소 분리 결과: ", addressParts);

    // 분리된 주소를 각각 input 필드에 반영 (예시로 3개의 input 필드 사용)
    document.getElementById("address").value = addressParts[0] || ''; // 첫 번째 부분
    document.getElementById("address2").value = addressParts[1] || ''; // 두 번째 부분

    console.log("변경된 주소: ", addressParts); // 콘솔에 출력 (디버깅용)
 
 }
    
}

document.addEventListener("DOMContentLoaded",updateAddress2);