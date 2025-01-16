import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = () => {
    return (
        <div>
           <nav>
        <ul>
          <li><Link to="/">홈</Link></li>
          <li><Link to="/about">게시글 보기</Link></li>
          <li><Link to="/login">로그인</Link></li>
        </ul>
      </nav> 
        </div>
    );
};

export default Navigation;