// 날짜 포맷 함수
export function formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false, // 24시간 형식
    });
}

//// 예제 사용법
//const formattedTime = formatDate(child.createTime);
//console.log(formattedTime); // "2025.02.12. 15:30:00"
