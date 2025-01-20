document.getElementById("updateMember").addEventListener("click", function () {
    let memberId = document.getElementById("id").value;
      let userid = document.getElementById("userid").value;
    let password = document.getElementById("password").value;
    let password_check = document.getElementById("password_check").value;
    let name = document.getElementById("name").value;
    let address = document.getElementById("address").value;
    let phone = document.getElementById("phone").value;
    let gender = document.getElementById("gender").value;
//    let roles = document.getElementById("roles").value;

    // Password mismatch check
    if (password != password_check) {
        alert("패스워드가 일치하지 않습니다");
        return;
    }

    // Prepare the data to send
    let data = {
//        "id": memberId,
		"userid" : userid,
        "password": password,
        "name": name,
        "address": address,
        "phone": phone,
        "gender": gender
//        "roles": roles
    };

    // Sending the data via PUT request
    fetch(`/updateuser/${memberId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json;charset=UTF-8"
        },
        body: JSON.stringify(data)  // Stringify the data before sending
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("데이터 전송에 실패하였습니다");
        }
//        console.log(response.json());
//        return response.json();
 alert("데이터 전송에 성공하였습니다");
        window.location.href="/"
    })
  //  .then(data => {
//        alert("데이터 전송에 성공하였습니다");
//        window.location.href="/"
 //   })
    .catch(error => {
        console.error(error);  // Log the error for debugging
        alert("에러가 발생했습니다: " + error.message);
    });
});
