import React, {useEffect, useState } from 'react'
import { createBook, updateBook  } from '../services/bookService';
import { useNavigate, useParams } from 'react-router-dom';
import { getBookById } from '../services/bookService';

export default function BookForm() {

     const [formData,setFormData]=useState({
      "title": "",
      "author": "",
      "price": 0,
      "publishDate": "",
      "publisherId": 1
    });

      const [isSubmitting,setIsSubmitting]=useState(false);

      const {id}=useParams();

      const navigate=useNavigate();

         const fetchBook=async()=>{
            const response=await getBookById(id);
            const datas=response.data;
            console.log(datas);
            setFormData({"title": datas.title,"author":datas.author,"price":datas.price,"publishDate": datas.publishDate,"publisherId":datas.publisherId});
          }

      useEffect(()=>{
        if (id) {
            fetchBook();
         }         
      },[id]);


      const handleSubmit=async (e)=>{
          e.preventDefault();
          setIsSubmitting(true);
    try {
      const payLoad={...formData,price:Number(formData.price)}

      if(id){
       await updateBook(id,payLoad);
         navigate("/");
      }else{
         await createBook(payLoad);
          navigate("/");
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
    }finally{
    
 setIsSubmitting(false);
 
    }
   }

     const handleChange=(e)=>{
       const {name,value}=e.target;  
      setFormData(prev=>({...prev,[name]:value}));
   }



const handleCancel=()=>{
    navigate("/add-books");
   }

  const title=id ? "更新書籍" :"新增書籍";

  return (
    <div>
        <h1>{title}</h1>
    <form onSubmit={handleSubmit}>
      <div>
 <input 
 type="text" 
 name="title" 
 value={formData.title} 
 onChange={handleChange} 
 required 
 placeholder="title" 
 /> 
      </div>


           <div>
 <input 
 type="text" 
 name="author" 
 value={formData.author} 
 onChange={handleChange} 
 required 
 placeholder="author" 
 /> 
      </div>


           <div>
 <input 
 type="number" 
 name="price" 
 value={formData.price} 
 onChange={handleChange} 
 required 
 placeholder="price" 
 /> 
      </div>


           <div>
 <input 
 type="date" 
 name="publishDate" 
 value={formData.publishDate} 
 onChange={handleChange} 
 required 
 placeholder="publishDate" 
 /> 
      </div>


      <div>
        <select name="publisherId" value={formData.publisherId} onChange={handleChange}>
        <option value="1">碁峰資訊</option>
        <option value="2">歐萊禮 (O''Reilly)</option>
        </select>
      </div>
  
      <button type="submit" disabled={isSubmitting}>{id ? "更新書籍" : "新增書籍"}</button>
      {id && <button type="button" onClick={handleCancel}>取消更新</button>}
    
     </form>
    
    </div>
  )
}
