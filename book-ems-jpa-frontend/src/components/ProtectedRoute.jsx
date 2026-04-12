import React from 'react'
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../store/auth-context";



export default function ProtectedRoute({children,allowedRoles}) {

  const {isAuthenticated,user}=useAuth();


 if(!isAuthenticated){
 return <Navigate to="/login" replace/>
}

if(allowedRoles && allowedRoles.length>0){
  const hasPermission=user?.roles?.some(role=>allowedRoles.includes(role));
  if(!hasPermission){
    alert("無權限訪問此頁面");
   return <Navigate to="/books" replace/>;
  }
}

  
  return children;
}
