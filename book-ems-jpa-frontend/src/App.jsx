import { BrowserRouter, Route, Routes } from "react-router-dom";
import BookForm from "./components/BookForm";
import BookList from "./components/BookList";

export default function App() {


  return (
   <BrowserRouter>
   <Routes>
    <Route path="/" element={<BookList/>}/>
    <Route path="/books" element={<BookList/>}/>
    <Route path="/add-books" element={<BookForm/>}/>
    <Route path="/update-books/:id" element={<BookForm/>}/>
   </Routes>
   </BrowserRouter>
  )
}
