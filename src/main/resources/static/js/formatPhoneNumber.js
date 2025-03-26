//function formatPhoneNumber(event) {
//    let phone = event.target;  // 전화번호 입력 필드
//    let phoneValue = phone.value.replace(/[^0-9]/g, ''); // 숫자만 남기기
//
//    // 최대 길이 제한 (전화번호 길이: 11자리)
//    if (phoneValue.length > 11) {
//        phoneValue = phoneValue.slice(0, 11);
//    }
//
//    // 3자리마다 "-" 추가
//    if (phoneValue.length >= 4 && phoneValue.length < 8) {
//        phoneValue = phoneValue.slice(0, 3) + "-" + phoneValue.slice(3);
//    } else if (phoneValue.length >= 8) {
//        phoneValue = phoneValue.slice(0, 3) + "-" + phoneValue.slice(3, 7) + "-" + phoneValue.slice(7, 11);
//    }
//
//    phone.value = phoneValue;
//}
//
//// 전화번호 입력 필드에 이벤트 리스너 추가
//document.get
//document.getElementById('phone').addEventListener('input', formatPhoneNumber);







document.addEventListener('DOMContentLoaded', function () {
    // 전화번호 자동 완성 함수
    function formatPhoneNumber(event) {
        let phone = event.target;  // 전화번호 입력 필드
        let phoneValue = phone.value.replace(/[^0-9]/g, ''); // 숫자만 남기기

        // 최대 길이 제한 (전화번호 길이: 11자리)
        if (phoneValue.length > 11) {
            phoneValue = phoneValue.slice(0, 11);
        }

        // 3자리마다 "-" 추가
        if (phoneValue.length >= 4 && phoneValue.length < 8) {
            phoneValue = phoneValue.slice(0, 3) + "-" + phoneValue.slice(3);
        } else if (phoneValue.length >= 8) {
            phoneValue = phoneValue.slice(0, 3) + "-" + phoneValue.slice(3, 7) + "-" + phoneValue.slice(7, 11);
        }

        phone.value = phoneValue;
    }

    // DOM이 완전히 로드된 후에 전화번호 입력 필드에 이벤트 리스너 추가
     const phoneInput = document.getElementById('phone');
    if (phoneInput) {
    document.getElementById('phone').addEventListener('input', formatPhoneNumber);
    }
});