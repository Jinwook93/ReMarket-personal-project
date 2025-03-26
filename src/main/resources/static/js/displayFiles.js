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