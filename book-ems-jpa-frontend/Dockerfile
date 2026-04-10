# Stage 1: Build the React application
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the app with Nginx
FROM nginx:alpine
# 將編譯好的靜態檔案複製到 Nginx 的預設目錄
COPY --from=build /app/build /usr/share/nginx/html
# 暴露容器內的 80 port
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]