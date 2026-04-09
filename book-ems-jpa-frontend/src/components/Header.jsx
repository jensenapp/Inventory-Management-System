
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../store/auth-context';

export default function Header() {
  const { isAuthenticated, user, logout } = useAuth();

  const isAdmin=user?.roles.includes("ROLE_ADMIN");

  const navigate = useNavigate();

  const handleLogout = () => {
    logout(); // 清除 Context 與 LocalStorage
    navigate('/login'); // 導向登入頁
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4 shadow-sm">
      <div className="container">
        {/* 左側 Logo / 品牌名稱 */}
        <Link className="navbar-brand fw-bold" to="/">
          📚 Book EMS
        </Link>

      
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>

        {/* 導覽列內容 */}
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            {/* 只有在登入狀態下，才顯示管理連結 */}
            {isAuthenticated && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/books">書本列表</Link>
                </li>
               {isAdmin && (
                 <li className="nav-item">
                  <Link className="nav-link" to="/add-books">新增書籍</Link>
                </li>
               )}
              </>
            )}
          </ul>

          {/* 右側使用者專區 */}
          <div className="d-flex align-items-center">
            {isAuthenticated ? (
              <>
                <span className="text-white me-3">
                  歡迎, <strong>{user?.username}</strong>
                </span>
                <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                  登出
                </button>
              </>
            ) : (
              <>
                <Link className="btn btn-outline-light btn-sm me-2" to="/login">登入</Link>
                <Link className="btn btn-primary btn-sm" to="/register">註冊</Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}