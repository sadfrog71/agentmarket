FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY nginx-site-routes.inc /etc/nginx/conf.d/nginx-site-routes.inc
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80 443
