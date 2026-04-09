import { createContext, useContext, useReducer, useEffect } from "react";

// ==========================================
// 1. 初始化 Context 與 Hook
// ==========================================

// 建立 Context 物件，這是資料的「容器」
export const AuthContext = createContext();

// 建立 Custom Hook (自定義 Hook)
// 好處：讓其他組件不用每次都 import useContext 和 AuthContext，直接用 useAuth() 即可
export const useAuth = () => useContext(AuthContext);

// ==========================================
// 2. 定義 Action Types 與 Reducer
// ==========================================

// 定義 Action 字串常數，避免打錯字導致 bug
const LOGIN_SUCCESS = "LOGIN_SUCCESS";
const LOGOUT = "LOGOUT";

/**
 * Auth Reducer: 純函數，負責根據 Action 更新 State
 * @param {Object} prevState - 目前的狀態
 * @param {Object} action - 包含 type (操作類型) 和 payload (資料)
 */
const authReducer = (prevState, action) => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...prevState, // 保留之前的狀態 (雖然這裡幾乎都會被覆蓋)
        jwtToken: action.payload.jwtToken, // 更新 Token
        user: action.payload.user,         // 更新使用者資訊
        isAuthenticated: true,             // 重要：標記狀態為「已登入」
      };
    case LOGOUT:
      return {
        ...prevState,
        jwtToken: null,        // 清空 Token
        user: null,            // 清空使用者資訊
        isAuthenticated: false, // 標記狀態為「未登入」
      };
    default:
      return prevState; // 若 Action 不匹配，回傳原狀態
  }
};

// ==========================================
// 3. 建立 Provider (供應者組件)
// ==========================================

export const AuthProvider = ({ children }) => {
  
  // --- A. 初始化狀態邏輯 (IIFE 立即執行函式) ---
  // 目的是在組件初次渲染時，先去 Local Storage 檢查有沒有舊的登入資料
  const initialAuthState = (() => {
    try {
      // 嘗試從瀏覽器 Local Storage 讀取資料
      const jwtToken = localStorage.getItem("jwtToken");
      const user = localStorage.getItem("user");
      
      // 如果 Token 和 User 都存在，代表使用者之前登入過且未過期
      if (jwtToken && user) {
        return {
          jwtToken,
          user: JSON.parse(user), // 將 JSON 字串轉回物件
          isAuthenticated: true,  // 恢復為已登入狀態
        };
      }
    } catch (error) {
      console.error("Error loading auth state", error);
    }
    
    // 若找不到資料或發生錯誤，回傳預設狀態 (未登入)
    return {
      jwtToken: null,
      user: null,
      isAuthenticated: false,
    };
  })();

  // --- B. 綁定 useReducer ---
  // authState: 當前的狀態
  // dispatch: 用來發送 Action 的函數
  const [authState, dispatch] = useReducer(authReducer, initialAuthState);

  // --- C. 監聽狀態變化 (副作用) ---
  // 當 authState 改變時，自動同步到 Local Storage
  useEffect(() => {
    try {
      if (authState.isAuthenticated) {
        // 若是登入狀態：將資料寫入 Local Storage (持久化保存)
        localStorage.setItem("jwtToken", authState.jwtToken);
        localStorage.setItem("user", JSON.stringify(authState.user));
      } else {
        // 若是登出狀態：清除 Local Storage 中的資料
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("user");
      }
    } catch (error) {
      console.error("Error saving auth state", error);
    }
  }, [authState]); // 依賴陣列：只有當 authState 變更時才執行此 useEffect

  // --- D. 定義對外公開的 Helper Functions ---

  // 登入成功時呼叫此函數
  const loginSuccess = (jwtToken, user) => {
    dispatch({ type: LOGIN_SUCCESS, payload: { jwtToken, user } });
  };

  // 登出時呼叫此函數
  const logout = () => {
    dispatch({ type: LOGOUT });
  };

  // --- E. 回傳 Provider ---
  // value 屬性包含了所有子組件需要用到的 狀態資料 和 操作函數
  return (
    <AuthContext.Provider
      value={{
        jwtToken: authState.jwtToken,
        user: authState.user,
        isAuthenticated: authState.isAuthenticated,
        loginSuccess, // 讓組件可以呼叫登入
        logout,       // 讓組件可以呼叫登出
      }}
    >
      {children} {/* 渲染被包裹的子組件 (通常是 <App />) */}
    </AuthContext.Provider>
  );
};