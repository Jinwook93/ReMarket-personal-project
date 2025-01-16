// 회원가입 폼 유효성 검사
function joinValidation(e) {
    // 아이디, 비밀번호, 비밀번호 확인, 이름, 나이, 성별, 연락처, 주소 필드 확인
    let userid = document.getElementById("userid").value;
    let password = document.getElementById("password").value;
    let password_check = document.getElementById("password_check").value;
    let name = document.getElementById("name").value;
    let age = document.getElementById("age").value;
    let gender = document.getElementById("gender").value;
    let phone = document.getElementById("phone").value;
    let address = document.getElementById("address").value;
    let password_admin = document.getElementById("password_admin").value;

	e.preventDefault();

    // 아이디가 비어있는지 체크
    if (userid == "") {
        alert("아이디를 입력해 주세요.");
        return false;
    }

    // 비밀번호가 비어있는지 체크
    if (password == "") {
        alert("비밀번호를 입력해 주세요.");
        return false;
    }

    // 비밀번호 확인이 비밀번호와 일치하는지 체크
    if (password !== password_check) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        return false;
    }

    // 이름이 비어있는지 체크
    if (name == "") {
        alert("이름을 입력해 주세요.");
        return false;
    }

    // 나이가 비어있거나 숫자가 아닌 경우 체크
    if (age == "" || isNaN(age)) {
        alert("유효한 나이를 입력해 주세요.");
        return false;
    }

    // 성별이 선택되지 않은 경우 체크
    if (gender == "") {
        alert("성별을 선택해 주세요.");
        return false;
    }

    // 연락처가 비어있는지 체크 (선택 사항이지만 빈 경우 메시지 표시)
    if (phone == "") {
        alert("연락처를 입력해 주세요.");
        return false;
    }

    // 주소가 비어있는지 체크
    if (address == "") {
        alert("주소를 입력해 주세요.");
        return false;
    }

    // 관리자 패스워드 입력이 비어있지 않으면 관리자 처리
    if (password_admin != "" && password !== password_admin) {
        alert("관리자 패스워드가 비밀번호와 일치하지 않습니다.");
        return false;
    }

    // 모든 유효성 검사가 통과되면 폼 제출
    return true;
}
