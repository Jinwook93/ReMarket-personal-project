// 문자열을 원하는 길이로 자르고 초과 시 ... 처리하는 함수
export function truncateText(text, maxLength) {
  if (typeof text !== 'string') return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}