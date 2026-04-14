FROM node:20-alpine AS builder

WORKDIR /app

# 复制 package 文件并安装依赖
COPY frontend/package*.json ./
RUN npm install

# 复制源码并构建
COPY frontend/ ./
RUN npm run build

# 运行阶段
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
