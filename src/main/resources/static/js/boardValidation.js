// boardValidation.js
export function boardValidation() {
    const title = document.getElementById('title');
    const product = document.getElementById('product');
    const price = document.getElementById('price');
    const address = document.getElementById('address');
    const address2 = document.getElementById('address2');
    const files = document.getElementById('boardFiles').files;

    const maxLength = {
        title: 30,
        product: 20,
        price: 15,
        address: 30,
        address2: 30,
    };

    function validateField(field, fieldName, maxLen) {
        const value = field.value.trim();
        if (value === "" || value.length === 0) {
            alert(`${fieldName}을(를) 입력하세요.`);
            field.focus();
            return false;
        }
        if (value.length > maxLen) {
            alert(`${fieldName}은(는) 최대 ${maxLen}자까지 입력 가능합니다.`);
            field.focus();
            return false;
        }
        return true;
    }

    // 각 필드를 검증
    if (!validateField(title, "제목", maxLength.title)) return false;
    if (!validateField(product, "물품명", maxLength.product)) return false;
    if (!validateField(price, "가격", maxLength.price)) return false;
    if (!validateField(address, "주소", maxLength.address)) return false;
    if (!validateField(address2, "세부 주소", maxLength.address2)) return false;

    // 파일 개수 검사
    if (files.length > 3) {
        alert("파일 첨부는 3개까지 가능합니다.");
        return false;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    return true; // 유효성 검사 통과
}
