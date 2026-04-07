import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { deleteBook,getAllBooks } from '../services/bookService';

export default function BookList() {

 
const[books,setBooks]=useState([]);

const [searchTerm,setSearchTerm]=useState("");

const [isLoading,setIsLoading]=useState(false);

const navigate=useNavigate();


const fetchBooks=async ()=>{
  setIsLoading(true);
      try {
        const response=await getAllBooks();
        setBooks(response.data);        
      } catch (error) {
        console.error(error);
      }finally{
        setIsLoading(false);
      }
    }

   useEffect(()=>{
    fetchBooks();
   },[]);

 


     const handleUpdate=(id)=>{
     navigate(`/update-books/${id}`);
    //  const item=books.find(book=>(book.bookId===id));
    //  setFormData(prev=>({...prev,title:item.title,author:item.author,price:item.price,publishDate:item.publishDate,publisherId:item.publisherId}));
   }

  const handleDelete= async (id)=>{
    await deleteBook((id));
    setBooks(prev=>prev.filter(item=>item.bookId !==id));
   }

   const handleAddBook=()=>{
       navigate("/add-books");
   }


    const filteredBooks=books.filter(book=>book.title.toLowerCase().includes(searchTerm.toLowerCase()) || book.author.toLowerCase().includes(searchTerm.toLowerCase()));
   
   const totalPrice=filteredBooks.reduce((acc,book)=>{
     const price=Number(book.price) || 0
    return acc+price
   },0)



  return (
    <div>
        <h1>書本管理系統 (Book EMS)</h1>

     <div>
     
        <input 
        type="text" 
        value={searchTerm} 
        onChange={e=>setSearchTerm(e.target.value)} 
        placeholder="search"
        />
     
     </div>

     <div>
        <button onClick={handleAddBook}>新增書籍</button>
     </div>
     
       {isLoading ? (
        <h2>is Loading...</h2>
       ) :(
        <table>
      <thead>
        <tr>
        <th>Id</th>
         <th>書名</th>
          <th>作者</th>
           <th>價格</th>
            <th>日期</th>
             <th>出版商</th>
             <th>操作</th>
             </tr>
      </thead>
      <tbody>
        {filteredBooks.map(item=>(
          <tr key={item.bookId}>
             <td>{item.bookId}</td>
             <td>{item.title}</td>
             <td>{item.author}</td>
             <td>{item.price}</td>
             <td>{item.publishDate}</td>
             <td>{item.publisherName}</td>
             <td>
              <button onClick={()=>handleUpdate(item.bookId)}>編輯</button>
              <button onClick={()=>handleDelete(item.bookId)}>刪除</button>
             </td>
          </tr>
        ))}
      </tbody>
          <tfoot>
            <tr>
              <td colSpan="3" style={{ textAlign: 'right', paddingRight: '10px' }}>總計金額：</td>
          <td style={{ color: 'red' }}>$ {totalPrice}</td>
          <td colSpan="3"></td>
            </tr>
          </tfoot>
    </table>
       )}     
    </div>
  )
}
