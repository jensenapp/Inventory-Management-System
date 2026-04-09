
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../services/authService';

export default function Register() {
  // 對應後端的 SignupRequest DTO，加上前端專用的 confirmPassword
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    realName: '',
    password: '',
    confirmPassword: ''
  });

  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    // 1. 前端基本防呆：檢查密碼是否一致
    if (formData.password !== formData.confirmPassword) {
      setErrorMsg('兩次輸入的密碼不一致，請重新確認！');
      return;
    }

    setIsLoading(true);

    try {
      // 2. 準備傳給後端的 Payload (不需要把 confirmPassword 傳過去)
      const payload = {
        username: formData.username,
        email: formData.email,
        realName: formData.realName,
        password: formData.password
      };

      // 3. 呼叫註冊 API
      const response = await register(payload);
      
      // 4. 註冊成功處理
      setSuccessMsg(response.data.message || '註冊成功！將為您導向登入頁面...');
      
      // 延遲 2 秒後自動跳轉到登入頁
      setTimeout(() => {
        navigate('/login');
      }, 2000);

    } catch (error) {
      // 5. 錯誤處理：擷取後端傳回的具體錯誤訊息 (例如帳號重複、信箱重複)
      if (error.response && error.response.data && error.response.data.message) {
        setErrorMsg(error.response.data.message);
      } else {
        setErrorMsg('系統發生未知的錯誤，請稍後再試。');
      }
      console.error('Registration failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: '500px' }}>
      <div className="card shadow-sm border-0">
        <div className="card-body p-4">
          <h2 className="text-center mb-4 fw-bold">建立新帳號</h2>
          
          {/* 顯示訊息區塊 */}
          {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
          {successMsg && <div className="alert alert-success">{successMsg}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label text-muted">真實姓名 (Real Name)</label>
              <input
                type="text"
                className="form-control"
                name="realName"
                value={formData.realName}
                onChange={handleChange}
                required
                minLength="3"
                maxLength="100"
                placeholder="請輸入您的真實姓名"
              />
            </div>

            <div className="mb-3">
              <label className="form-label text-muted">帳號 (Username)</label>
              <input
                type="text"
                className="form-control"
                name="username"
                value={formData.username}
                onChange={handleChange}
                required
                minLength="3"
                maxLength="20"
                placeholder="設定登入帳號 (3~20字元)"
              />
            </div>

            <div className="mb-3">
              <label className="form-label text-muted">電子信箱 (Email)</label>
              <input
                type="email"
                className="form-control"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                placeholder="example@email.com"
              />
            </div>
            
            <div className="mb-3">
              <label className="form-label text-muted">密碼 (Password)</label>
              <input
                type="password"
                className="form-control"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                minLength="6"
                placeholder="設定密碼 (至少 6 個字元)"
              />
            </div>

            <div className="mb-4">
              <label className="form-label text-muted">確認密碼 (Confirm Password)</label>
              <input
                type="password"
                className="form-control"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
                minLength="6"
                placeholder="請再次輸入密碼"
              />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary w-100 py-2 fw-bold" 
              disabled={isLoading || successMsg !== ''}
            >
              {isLoading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  處理中...
                </>
              ) : '立即註冊'}
            </button>

            <div className="text-center mt-3">
              <span className="text-muted">已經有帳號了嗎？ </span>
              <Link to="/login" className="text-decoration-none">
                點此登入
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}