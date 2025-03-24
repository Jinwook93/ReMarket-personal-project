 window.onload = function() {
    // 모든 금액에 대해 포맷팅
    document.querySelectorAll('td[id^="price-"]').forEach(function(cell) {
      const price = parseFloat(cell.innerText);
      cell.innerText = formatCurrency(price) + '원';
    });
    
     document.querySelectorAll('div[id^="price-"]').forEach(function(cell) {
      const price = parseFloat(cell.innerText);
      cell.innerText = formatCurrency(price) + '원';
    });
    
     document.querySelectorAll('h2[id^="price-"]').forEach(function(cell) {
      const price = parseFloat(cell.innerText);
      cell.innerText = formatCurrency(price) + '원';
    });
  };


   
  // 금액 포맷 함수
 export function formatCurrency(amount) {
    return amount.toLocaleString(); // 천 단위 구분 기호 추가
  }