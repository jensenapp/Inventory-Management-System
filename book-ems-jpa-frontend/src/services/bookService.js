import apiClient from "../api/apiClient"; 

const BOOK_URL = "/books";

// export const getAllBooks = () => apiClient.get(BOOK_URL);

export const getAllBooks = (pageNo = 0, pageSize = 5, sortBy = "bookId", sortDir = "desc") => {
  return apiClient.get(`${BOOK_URL}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`);
};
export const getBookById = (id) => apiClient.get(`${BOOK_URL}/${id}`);

export const createBook = (data) => apiClient.post(BOOK_URL, data);

export const updateBook = (id, data) => apiClient.put(`${BOOK_URL}/${id}`, data);

export const deleteBook = (id) => apiClient.delete(`${BOOK_URL}/${id}`);

export const searchBook = (text, pageNo = 0, pageSize = 5, sortBy = "bookId", sortDir = "desc") => {
  return apiClient.get(`${BOOK_URL}/search?text=${text}&pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`);
};

// export const searchBook = (text) => apiClient.get(`${BOOK_URL}/search?text=${text}`);
// (錯誤： export const searchBook=(text)=>axios.get(`${API_URL}/${text}`);