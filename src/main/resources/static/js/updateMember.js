// updateMember.js

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('updateMember').addEventListener('click', function () {
        // Create a FormData object to handle form data including file uploads
        const formData = new FormData();
        let id = document.getElementById('id').value;
//        formData.append('id', id);
        formData.append('userid', document.getElementById('userid').value);
        formData.append('password', document.getElementById('password').value);
        formData.append('password_check', document.getElementById('password_check').value);
        formData.append('name', document.getElementById('name').value);
        formData.append('age', document.getElementById('age').value);
        formData.append('gender', document.getElementById('gender').value);
        formData.append('phone', document.getElementById('phone').value);
        formData.append('address', document.getElementById('address').value);

        // If there is a profile image, append it to the form data
        const profileImage = document.getElementById('profileImage').files[0];
        if (profileImage) {
            formData.append('profileImage', profileImage);
        }

        // Use fetch to send the data to the backend
        fetch(`/updateMember/${id}`, {
            method: 'PUT',
            body: formData,
        })
        .then(response => {
			if(!response.ok){
				alert ("회원 업데이트 실패!!");
				return;
			}
			
   			alert("회원 정보가 성공적으로 업데이트 되었습니다");
			window.location.href="/";
//        return response.json();  // Parse the response as JSON
        })
//        .then(data => {
//            if (data.success) {
//                alert('회원 정보가 성공적으로 업데이트되었습니다.');
//                location.reload(); // Reload the page or redirect as needed
//            } else {
//                alert('회원 정보 업데이트 실패');
//            }
//        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버 오류가 발생했습니다.');
        });
    });
});
