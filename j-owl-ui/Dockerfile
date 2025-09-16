# Multi-stage build for Angular frontend served by nginx

FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Build Angular app for production
RUN npm run build -- --configuration production

FROM nginx:alpine

# Copy built app to nginx html directory
COPY --from=build /app/dist/j-owl-ui/browser/ /usr/share/nginx/html/

# Provide a default nginx config that can proxy API requests to backend
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]


