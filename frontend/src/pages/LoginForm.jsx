import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

function LoginForm() {
  const navigate = useNavigate();
    
//   const [loginRequest, setLoginRequest] = useState({
//     userid: '',
//     password: '',
//   });

    const [userid, setUserid] =useState('');
    const [password, setPassword] =useState('');




  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios({
        url: 'http://localhost:8081/login',         //스프링 시큐리티 기본 로그인 (formLogin)이 아니므로 loginProcessingUrl과 다르게 지정
        method: 'POST',
        // data: {                                          //JSON 데이터 타입 (application/json)
        //     userid : userid,
        //     password : password

        // },



        data: new URLSearchParams({                                         
            userid: userid,
            password: password,
          }).toString(),
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },







        withCredentials: true,
      });
      if (response.status === 200) {
        alert('로그인 성공! ');
        console.log('유저 아이디: ' + response.data);
        console.log('권한: ' + response.data.authorities);
        navigate('/', { state: { userData: response.data } });
      }
    } catch (error) {
      alert('로그인에 실패하였습니다 ');
      console.log('로그인 에러: ', error);
    }
  };

  return (
    <div>
      <h3>로그인</h3>
      <form onSubmit={handleSubmit}>
        <input type="text" name="userid" placeholder="아이디" value={userid} onChange={(e) => setUserid(e.target.value)} />
        <input type="password" name="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)}  />
        <button type="submit">로그인</button>
      </form>
      <Link to="/join">
        <button>회원가입</button>
      </Link>
    </div>
  );
}

export default LoginForm;