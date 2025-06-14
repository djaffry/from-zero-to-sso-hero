FROM --platform=linux node:22-alpine AS build
WORKDIR /app

RUN apk add --no-cache coreutils && npm install -g npm@10.2.4

COPY package.json ./
RUN timeout 180 npm install || (echo "npm install timed out" && exit 1)

COPY . .
RUN npm run build-only

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/public /usr/share/nginx/html/public
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
