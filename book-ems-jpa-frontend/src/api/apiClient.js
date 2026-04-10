import axios from "axios";

// 1. 創建一個自訂的 Axios 實體 (設定共用的 Base URL)
const apiClient = axios.create({
  baseURL: "https://ems-api.jensen-store.online/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// 2. 請求攔截器 (Request Interceptor) - 出門前攔截
apiClient.interceptors.request.use(
  (config) => {
    // 從 localStorage 抓取 Token
    const token = localStorage.getItem("jwtToken");
    
    // 如果有 Token，就自動把它塞進 Header 裡面
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 3. 回應攔截器 (Response Interceptor) - 回來時攔截
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 如果後端回傳 401 Unauthorized (未授權)
    if (error.response && error.response.status === 401) {
      console.error("Token 已過期或無效，請重新登入！");
      
      // 清除壞掉的 Token
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("user");
      
     
      window.location.href = "/login"; 
    }
    return Promise.reject(error);
  }
);

export default apiClient;