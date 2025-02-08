// 좋아요 요청 함수
	export const commenthandleLike = async (commentId) => {
		try {
			const response = await fetch(`/comment/${commentId}/like`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
			});

			if (response.ok) {
				const result = await response.json();
				updatecommentLikeDislikeUI(commentId, result[0], result[1]);
			} else {
				console.error("좋아요 요청 실패");
			}
		} catch (error) {
			console.error("좋아요 요청 중 오류 발생:", error);
		}
	};

	// 좋아요 취소 요청 함수
	export const commenthandleUnlike = async (commentId) => {
		try {
			const response = await fetch(`/comment/${commentId}/like`, {
				method: "DELETE",
				headers: {
					"Content-Type": "application/json",
				},
			});

			if (response.ok) {
				const result = await response.json();
				updatecommentLikeDislikeUI(commentId, result[0], result[1]);
			} else {
				console.error("좋아요 취소 요청 실패");
			}
		} catch (error) {
			console.error("좋아요 취소 요청 중 오류 발생:", error);
		}
	};

	// 싫어요 요청 함수
	export const commenthandleDislike = async (commentId) => {
		try {
			const response = await fetch(`/comment/${commentId}/dislike`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
			});

			if (response.ok) {
				const result = await response.json();
				updatecommentLikeDislikeUI(commentId, result[0], result[1]);
			} else {
				console.error("싫어요 요청 실패");
			}
		} catch (error) {
			console.error("싫어요 요청 중 오류 발생:", error);
		}
	};

	// 싫어요 취소 요청 함수
	export const commenthandleUndislike = async (commentId) => {
		try {
			const response = await fetch(`/comment/${commentId}/dislike`, {
				method: "DELETE",
				headers: {
					"Content-Type": "application/json",
				},
			});

			if (response.ok) {
				const result = await response.json();
				updatecommentLikeDislikeUI(commentId, result[0], result[1]);
			} else {
				console.error("싫어요 취소 요청 실패");
			}
		} catch (error) {
			console.error("싫어요 취소 요청 중 오류 발생:", error);
		}
	};





	//버튼 활성화 확인 함수

	export const commentButtonEnable = async (commentId, commentlikeButton, commentdislikeButton) => {
		try {
			const response = await fetch(`/comment/${commentId}/buttonenable`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
			});

			if (response.ok) {
				//                alert("버튼 활성화 확인 성공");
				const result = await response.text();
				console.log(result);

				if (result === "ENABLE_LIKE") {
					commentlikeButton.classList.add("active");
					commentdislikeButton.classList.remove("active");
				} else if (result === "ENABLE_DISLIKE") {
					commentlikeButton.classList.remove("active");
					commentdislikeButton.classList.add("active");
				} else {
					commentlikeButton.classList.remove("active");
					commentdislikeButton.classList.remove("active");

				}

				//                updateLikeDislikeUI(result[0], result[1]);
			} else {
				//                alert("버튼 활성화 실패");
			}
		} catch (error) {
			console.error("싫어요 요청 중 오류 발생:", error);
		}
	};






	// UI 업데이트 함수
	export const updatecommentLikeDislikeUI = (commentId, totalLike, totalDislike) => {
		const commentlikeButton = document.getElementById(`commentlike-${commentId}`);
		const commentdislikeButton = document.getElementById(`commentdislike-${commentId}`);

		commentlikeButton.textContent = `❤️ ${totalLike}`;
		commentdislikeButton.textContent = `🖤 ${totalDislike}`;
	};