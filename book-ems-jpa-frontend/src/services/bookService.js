
import axios from "axios";

const API_URL = "http://localhost:8080/api/books";

export const getAllBooks = () => axios.get(API_URL);

export const getBookById=(id)=>axios.get(`${API_URL}/${id}`)

export const createBook = (data) => axios.post(API_URL, data);

export const updateBook = (id, data) => axios.put(`${API_URL}/${id}`, data);

export const deleteBook = (id) => axios.delete(`${API_URL}/${id}`);

export const searchBook = (text) => axios.get(`${API_URL}/search?text=${text}`);  
// (錯誤： export const searchBook=(text)=>axios.get(`${API_URL}/${text}`);