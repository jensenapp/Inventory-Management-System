// file: src/App.jsx
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import BookForm from "./components/BookForm";
import BookList from "./components/BookList";
import Login from "./components/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Register from "./components/register";

export default function App() {
  return (
    <BrowserRouter>
      {/* 這層 div 是關鍵！
        d-flex: 啟用 flex 排版
        flex-column: 由上到下垂直排列
        min-vh-100: 最小高度為整個螢幕高度 (100 Viewport Height)
      */}
      <div className="d-flex flex-column min-vh-100">
        
        <Header />

        {/* 中間主要內容區塊 
          flex-grow-1: 這行最重要！它會自動把「螢幕剩下的空間」全部撐開，
                       這樣就會強迫把 Footer 推到螢幕最底下。
          container: 讓內容有左右邊距
          mt-4: 距離上面的 Header 一點點距離
        */}
        <main className="flex-grow-1 container mt-4">
          <Routes>
            {/* 建議加上這個，讓首頁自動導向 /books */}
            <Route path="/" element={<Navigate to="/books" replace />} />
            <Route path="/books" element={<ProtectedRoute><BookList/></ProtectedRoute>}/>
            <Route path="/add-books" element={<ProtectedRoute allowedRoles={["ROLE_ADMIN"]}><BookForm/></ProtectedRoute>}/>
            <Route path="/update-books/:id" element={<ProtectedRoute allowedRoles={["ROLE_ADMIN"]}><BookForm/></ProtectedRoute>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
          </Routes>
        </main>

        <Footer />
        
      </div>
    </BrowserRouter>
  );
}