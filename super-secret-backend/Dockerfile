FROM node:lts AS build

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install
COPY . .
# RUN npm run build

FROM node:lts-slim

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/dist ./dist
COPY --from=build /usr/src/app/package*.json ./
COPY --from=build /usr/src/app/start.sh ./start.sh

RUN apt-get update && apt-get install -y --no-install-recommends curl socat && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    npm install --production && \
    npm cache clean --force && \
    # Create a non-root user
    groupadd -r nodejs && \
    useradd -r -g nodejs -s /bin/bash -d /usr/src/app nodejs && \
    chmod +x ./start.sh && \
    chown -R nodejs:nodejs /usr/src/app

USER nodejs

EXPOSE 8090 8080

ENTRYPOINT ["/usr/src/app/start.sh"]
