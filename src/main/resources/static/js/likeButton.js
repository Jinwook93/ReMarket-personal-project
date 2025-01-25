document.addEventListener("DOMContentLoaded", () => {
    const likeButton = document.getElementById("like");
    const dislikeButton = document.getElementById("dislike");
    const boardId = document.getElementById("id").value;



	//버튼 활성화 확인 함수
	
	    const ButtonEnable = async () => {
        try {
            const response = await fetch(`/board/${boardId}/buttonenable`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("버튼 활성화 확인 성공");
                const result = await response.text();
                console.log(result);
                
                if(result === "ENABLE_LIKE"){
					likeButton.classList.add("active");
					dislikeButton.classList.remove("active");
				}else if (result === "ENABLE_DISLIKE"){
					likeButton.classList.remove("active");
					dislikeButton.classList.add("active");
				}else{
					likeButton.classList.remove("active");
					dislikeButton.classList.remove("active");
					
				}
                
//                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("버튼 활성화 실패");
            }
        } catch (error) {
            console.error("싫어요 요청 중 오류 발생:", error);
        }
    };










    // 좋아요 요청 함수
    const handleLike = async () => {
        try {
            const response = await fetch(`/board/${boardId}/like`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("좋아요 요청 성공");
                const result = await response.json();
                console.log(result);
                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("좋아요 요청 실패");
            }
        } catch (error) {
            console.error("좋아요 요청 중 오류 발생:", error);
        }
    };

    // 좋아요 취소 요청 함수
    const handleUnlike = async () => {
        try {
            const response = await fetch(`/board/${boardId}/like`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("좋아요 취소 성공");
                const result = await response.json();
                console.log(result);
                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("좋아요 취소 요청 실패");
            }
        } catch (error) {
            console.error("좋아요 취소 요청 중 오류 발생:", error);
        }
    };

    // 싫어요 요청 함수
    const handleDislike = async () => {
        try {
            const response = await fetch(`/board/${boardId}/dislike`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("싫어요 요청 성공");
                const result = await response.json();
                console.log(result);
                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("싫어요 요청 실패");
            }
        } catch (error) {
            console.error("싫어요 요청 중 오류 발생:", error);
        }
    };

    // 싫어요 취소 요청 함수
    const handleUndislike = async () => {
        try {
            const response = await fetch(`/board/${boardId}/dislike`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
//                alert("싫어요 취소 성공");
                const result = await response.json();
                console.log(result);
                updateLikeDislikeUI(result[0], result[1]);
            } else {
//                alert("싫어요 취소 요청 실패");
            }
        } catch (error) {
            console.error("싫어요 취소 요청 중 오류 발생:", error);
        }
    };

    // UI 업데이트 함수
    const updateLikeDislikeUI = (totalLike, totalDislike) => {
        likeButton.textContent = `❤️ ${totalLike}`;
        dislikeButton.textContent = `🖤 ${totalDislike}`;
    };

    // 버튼 이벤트 리스너 등록
    likeButton.addEventListener("click", () => {
        if (likeButton.classList.contains("active")) {
            handleUnlike();
            likeButton.classList.remove("active");
        } else {
            handleLike();
            likeButton.classList.add("active");
            dislikeButton.classList.remove("active"); // 싫어요 취소
        }
    });

    dislikeButton.addEventListener("click", () => {
        if (dislikeButton.classList.contains("active")) {
            handleUndislike();
            dislikeButton.classList.remove("active");
        } else {
            handleDislike();
            dislikeButton.classList.add("active");
            likeButton.classList.remove("active"); // 좋아요 취소
        }
    });
    
    ButtonEnable();
});
