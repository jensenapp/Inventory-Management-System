// file: src/components/Login.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';
import { useAuth } from '../store/auth-context';

export default function Login() {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [errorMsg, setErrorMsg] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { loginSuccess } = useAuth(); // 從 Context 取得 loginSuccess 函數
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMsg('');

    try {
      // 1. 呼叫後端登入 API
      const response = await login(credentials);
      const data = response.data;

      // 2. 登入成功，將資料存入 Context (包含 Token 與 User 資訊)
      loginSuccess(data.jwtToken, { id: data.id, username: data.username, roles: data.roles });

      // 3. 導向首頁 (書本列表)
      navigate('/books');
    } catch (error) {
      // 如果是 401，顯示帳密錯誤
      if (error.response && error.response.status === 401) {
        setErrorMsg('帳號或密碼錯誤，請重新輸入！');
      } else {
        setErrorMsg('系統發生錯誤，請稍後再試。');
      }
      console.error('Login failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: '400px' }}>
      <div className="card shadow-sm">
        <div className="card-body">
          <h2 className="text-center mb-4">系統登入</h2>
          
          {/* 顯示錯誤訊息 */}
          {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">帳號 (Username)</label>
              <input
                type="text"
                className="form-control"
                name="username"
                value={credentials.username}
                onChange={handleChange}
                required
                placeholder="輸入 admin 或 user1"
              />
            </div>
            
            <div className="mb-3">
              <label className="form-label">密碼 (Password)</label>
              <input
                type="password"
                className="form-control"
                name="password"
                value={credentials.password}
                onChange={handleChange}
                required
                placeholder="輸入密碼"
              />
            </div>

            <button type="submit" className="btn btn-primary w-100" disabled={isLoading}>
              {isLoading ? '登入中...' : '登入'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}