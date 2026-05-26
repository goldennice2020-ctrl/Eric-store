FROM node:22-alpine

WORKDIR /app

ENV NODE_ENV=production
ENV HOSTNAME=0.0.0.0
ENV DATABASE_URL=file:/data/dev.db
ENV UPLOAD_ROOT=/data/uploads

COPY package.json package-lock.json ./
COPY prisma ./prisma
RUN npm ci --include=dev

COPY . .
RUN npm run build
RUN npm prune --omit=dev
RUN mkdir -p /data/uploads/apks /data/uploads/icons

EXPOSE 3002

CMD ["npm", "start"]
