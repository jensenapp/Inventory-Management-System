import apiClient from "../api/apiClient";

const AUTH_URL="/auth/public"

export const login=(data)=>apiClient.post(`${AUTH_URL}/signin`,data);

export const register=(data)=>apiClient.post(`${AUTH_URL}/signup`,data);