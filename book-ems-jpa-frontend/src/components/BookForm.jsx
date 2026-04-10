// file: src/components/BookForm.jsx
import React, { useEffect, useState } from 'react'
import { createBook, updateBook } from '../services/bookService';
import { useNavigate, useParams } from 'react-router-dom';
import { getBookById } from '../services/bookService';

export default function BookForm() {

  const [formData, setFormData] = useState({
    "title": "",
    "author": "",
    "price": 0,
    "publishDate": "",
    "publisherId": 1
  });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const { id } = useParams();
  const navigate = useNavigate();

  const fetchBook = async () => {
    try {
      const response = await getBookById(id);
      const datas = response.data;
      console.log(datas);
      setFormData({
        "title": datas.title, 
        "author": datas.author, 
        "price": datas.price, 
        "publishDate": datas.publishDate, 
        "publisherId": datas.publisherId 
      });
    } catch (error) {
      console.error("載入書籍失敗", error);
    }
  }

  useEffect(() => {
    if (id) {
      fetchBook();
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      const payLoad = { ...formData, price: Number(formData.price) }

      if (id) {
        await updateBook(id, payLoad);
        navigate("/books"); // 更新完通常會回到列表頁
      } else {
        await createBook(payLoad);
        navigate("/books"); // 新增完通常也會回到列表頁
      }

      setFormData({
        "title": "",
        "author": "",
        "price": 0,
        "publishDate": "",
        "publisherId": 1
      });
    } catch (error) {
      console.error(error);
    } finally {
      setIsSubmitting(false);
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  }

  const handleCancel = () => {
    // 取消時通常會回到列表頁
    navigate("/books");
  }

  const title = id ? "更新書籍資料" : "新增書籍";

  return (
    <div className="container mt-5" style={{ maxWidth: '600px' }}>
      <div className="card shadow-sm border-0">
        <div className="card-body p-4">
          <h2 className="text-center mb-4 fw-bold">{title}</h2>
          
          <form onSubmit={handleSubmit}>
            
            <div className="mb-3">
              <label className="form-label text-muted fw-bold">書名 (Title)</label>
              <input 
                type="text" 
                className="form-control"
                name="title" 
                value={formData.title} 
                onChange={handleChange} 
                required 
                placeholder="請輸入書名" 
              /> 
            </div>

            <div className="mb-3">
              <label className="form-label text-muted fw-bold">作者 (Author)</label>
              <input 
                type="text" 
                className="form-control"
                name="author" 
                value={formData.author} 
                onChange={handleChange} 
                required 
                placeholder="請輸入作者姓名" 
              /> 
            </div>

            <div className="row mb-3">
              <div className="col-md-6">
                <label className="form-label text-muted fw-bold">價格 (Price)</label>
                <div className="input-group">
                  <span className="input-group-text">$</span>
                  <input 
                    type="number" 
                    className="form-control"
                    name="price" 
                    value={formData.price} 
                    onChange={handleChange} 
                    required 
                    min="0"
                    placeholder="0" 
                  /> 
                </div>
              </div>
              
              <div className="col-md-6">
                <label className="form-label text-muted fw-bold">出版日期 (Publish Date)</label>
                <input 
                  type="date" 
                  className="form-control"
                  name="publishDate" 
                  value={formData.publishDate} 
                  onChange={handleChange} 
                  required 
                /> 
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label text-muted fw-bold">出版商 (Publisher)</label>
              <select 
                className="form-select"
                name="publisherId" 
                value={formData.publisherId} 
                onChange={handleChange}
              >
                <option value="1">碁峰資訊</option>
                <option value="2">歐萊禮 (O'Reilly)</option>
              </select>
            </div>
      
            {/* 按鈕區塊 */}
            <div className="d-flex gap-3 mt-4">
              <button 
                type="submit" 
                className="btn btn-primary w-100 py-2 fw-bold" 
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    儲存中...
                  </>
                ) : (
                  id ? "更新書籍" : "新增書籍"
                )}
              </button>
              
              {id && (
                <button 
                  type="button" 
                  className="btn btn-outline-secondary w-100 py-2 fw-bold" 
                  onClick={handleCancel}
                >
                  取消更新
                </button>
              )}
            </div>
            
          </form>
        </div>
      </div>
    </div>
  )
}