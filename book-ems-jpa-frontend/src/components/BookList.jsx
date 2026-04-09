
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { deleteBook, getAllBooks, searchBook } from '../services/bookService';
import { useAuth } from "../store/auth-context";

export default function BookList() {
  const [books, setBooks] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // 分頁專用的 State
  const [pageNo, setPageNo] = useState(0);
  const [pageSize, setPageSize] = useState(5); 
  const [totalPages, setTotalPages] = useState(0);
  const [isLast, setIsLast] = useState(false);

  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");

  //整合抓取資料的邏輯
  const fetchBooks = async () => {
    setIsLoading(true);
    try {
      let response;
      if (searchTerm.trim() !== "") {
        // 情境 A：有搜尋關鍵字，呼叫分頁版 searchBook
        response = await searchBook(searchTerm, pageNo, pageSize, 'bookId', 'desc');
      } else {
        // 情境 B：無關鍵字，呼叫分頁版 getAllBooks
        response = await getAllBooks(pageNo, pageSize, 'bookId', 'desc');
      }
      
      // 兩種情況回傳的都是 PageResponseDto，所以處理方式一模一樣！
      setBooks(response.data.content);
      setTotalPages(response.data.totalPages);
      setIsLast(response.data.last);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  }

  // 1. 當 pageNo 改變時，自動去抓資料
  useEffect(() => {
    fetchBooks();
  }, [pageNo]);

  // 2. 監聽搜尋文字 (Debounce 防抖)
  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      // 只要使用者打字，就強制回到第 1 頁重新搜尋
      if (pageNo !== 0) {
        setPageNo(0); 
        // 不呼叫 fetchBooks，因為 setPageNo(0) 會觸發上面的 useEffect
      } else {
        fetchBooks(); // 如果本來就在第一頁，就直接發請求
      }
    }, 500); 

    return () => clearTimeout(delayDebounceFn);
  }, [searchTerm]); // 監聽 searchTerm

  const handleUpdate = (id) => {
    navigate(`/update-books/${id}`);
  }

  const handleDelete = async (id) => {
    if (!isAdmin) return alert("無權限");
    if (window.confirm("確定要刪除這本書嗎？")) {
      await deleteBook(id);
      fetchBooks(); 
    }
  }

  const handleAddBook = () => {
    navigate("/add-books");
  }

  const handlePrevPage = () => {
    if (pageNo > 0) setPageNo(prev => prev - 1);
  }

  const handleNextPage = () => {
    if (!isLast) setPageNo(prev => prev + 1);
  }

  const totalPrice = books.reduce((acc, book) => {
    const price = Number(book.price) || 0
    return acc + price
  }, 0)

  return (
    <div>
      <h2 className="mb-4">書本管理系統 (Book EMS)</h2>

      <div className="d-flex justify-content-between mb-3">
        <div>
          <input 
            type="text" 
            className="form-control"
            value={searchTerm} 
            onChange={e => setSearchTerm(e.target.value)} 
            placeholder="搜尋書名或作者..."
            style={{ width: '250px' }}
          />
        </div>
        
        {isAdmin && (
          <div>
            <button className="btn btn-primary" onClick={handleAddBook}>+ 新增書籍</button>
          </div>
        )}
      </div>
      
      {isLoading ? (
        <div className="text-center my-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      ) : (
        <>
          <table className="table table-hover table-bordered shadow-sm">
            <thead className="table-dark">
              <tr>
                <th>Id</th>
                <th>書名</th>
                <th>作者</th>
                <th>價格</th>
                <th>出版日期</th>
                <th>出版商</th>
                {isAdmin && <th>操作</th>}
              </tr>
            </thead>
            <tbody>
              {books.length > 0 ? (
                books.map(item => (
                  <tr key={item.bookId}>
                    <td>{item.bookId}</td>
                    <td>{item.title}</td>
                    <td>{item.author}</td>
                    <td>$ {item.price}</td>
                    <td>{item.publishDate}</td>
                    <td>{item.publisherName}</td>
                    {isAdmin && (
                      <td>
                        <button className="btn btn-sm btn-outline-secondary me-2" onClick={() => handleUpdate(item.bookId)}>編輯</button>
                        <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(item.bookId)}>刪除</button>
                      </td>
                    )}
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={isAdmin ? "7" : "6"} className="text-center py-3">目前沒有書籍資料</td>
                </tr>
              )}
            </tbody>
            <tfoot>
              <tr className="table-light">
                <td colSpan="3" className="text-end fw-bold">本頁總計金額：</td>
                <td className="text-danger fw-bold">$ {totalPrice}</td>
                <td colSpan={isAdmin ? "3" : "2"}></td>
              </tr>
            </tfoot>
          </table>

          <div className="d-flex justify-content-between align-items-center mt-3">
            <span className="text-muted">
              第 <strong>{pageNo + 1}</strong> 頁，共 <strong>{totalPages === 0 ? 1 : totalPages}</strong> 頁
            </span>
            <div>
              <button 
                className="btn btn-outline-primary btn-sm me-2" 
                onClick={handlePrevPage} 
                disabled={pageNo === 0}
              >
                &laquo; 上一頁
              </button>
              <button 
                className="btn btn-outline-primary btn-sm" 
                onClick={handleNextPage} 
                disabled={isLast}
              >
                下一頁 &raquo;
              </button>
            </div>
          </div>
        </>
      )}     
    </div>
  )
}