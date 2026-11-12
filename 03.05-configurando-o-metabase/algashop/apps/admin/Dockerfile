# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar arquivos de dependências
COPY package.json package-lock.json ./

# Instalar dependências
RUN npm ci

# Copiar código-fonte
COPY . .

# Build do projeto Angular
RUN npm run build

# Stage 2: Servir com Nginx
FROM nginx:1.27-alpine

# Copiar arquivo de configuração do Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Remover conteúdo padrão do Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiar artefatos do build para o diretório do Nginx
COPY --from=builder /app/dist/admin /usr/share/nginx/html

# Expor porta 80
EXPOSE 80

# Comando para iniciar o Nginx
CMD ["nginx", "-g", "daemon off;"]
