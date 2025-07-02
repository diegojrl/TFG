FROM node:24 as builder

WORKDIR /app

# Install dependencies
COPY package.json .
COPY package-lock.json .
RUN npm install

# Build web
COPY . .
RUN npm run build

ARG hostname
ENV hostname=$hostname
RUN openssl req -x509 -nodes -days 365 -newkey rsa:4096 -keyout server.key -out server.crt -subj "/CN=$hostname"

FROM httpd:2-alpine

COPY --from=builder /app/build /usr/local/apache2/htdocs/

COPY --from=builder /app/server.* /usr/local/apache2/conf/

COPY apache.conf /usr/local/apache2/conf/httpd.conf

ARG hostname
ENV HOST=$hostname