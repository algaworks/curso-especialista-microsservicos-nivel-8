# Docker - Guia de Uso

Este guia descreve como construir e executar a aplicação Angular em um container Docker com Nginx.

## Pré-requisitos

- Docker instalado
- Docker Compose instalado (opcional, mas recomendado)

## Construir a Imagem Docker

```bash
docker build -t sample-auth-guards:latest .
```

## Executar o Container

### Opção 1: Usar Docker Compose (Recomendado)

```bash
docker-compose up -d
```

A aplicação estará disponível em: `http://localhost`

Para parar o container:
```bash
docker-compose down
```

### Opção 2: Usar Docker Diretamente

```bash
docker run -d -p 4200:80 --name oauth2-client-frontend-angular-app:latest
```

A aplicação estará disponível em: `http://localhost`

Para parar o container:
```bash
docker stop sample-auth-guards
docker rm sample-auth-guards
```

## Executar com Porta Customizada

Se você quiser usar uma porta diferente da 80:

```bash
docker run -d -p 8080:80 --name sample-auth-guards sample-auth-guards:latest
```

A aplicação estará disponível em: `http://localhost:8080`

## Ver Logs

```bash
docker logs sample-auth-guards
```

Para seguir os logs em tempo real:
```bash
docker logs -f sample-auth-guards
```

## Sobre o Dockerfile

O Dockerfile usa um build **multi-stage**:

1. **Stage 1 (Builder)**: 
   - Usa imagem `node:20-alpine`
   - Instala dependências com `npm ci`
   - Executa o build do Angular
   - Resultado: artefatos em `dist/sample-auth-guards/browser`

2. **Stage 2 (Runtime)**:
   - Usa imagem `nginx:1.27-alpine` (menor e mais rápida)
   - Copia apenas os artefatos do build
   - Configura Nginx para servir a SPA

**Vantagem**: A imagem final contém apenas o Nginx e os artefatos compilados, sem Node.js ou código-fonte.

## Sobre o nginx.conf

O arquivo `nginx.conf` é otimizado para servir Single Page Applications (SPAs):

- **SPA Routing**: Usa `try_files` para redirecionar rotas desconhecidas para `index.html`
- **Cache**: 
  - Arquivos com hash (.js, .css) têm cache de 1 ano
  - index.html não é cacheado
- **Compressão Gzip**: Ativada automaticamente para reduzir tamanho
- **Security Headers**: Adiciona headers de segurança ao HTML
- **Suporte a Large Files**: Permite até 20MB em upload

## Troubleshooting

### Erro de Build

Se receber erro durante o build docker, tente:

```bash
docker build --no-cache -t sample-auth-guards:latest .
```

### Porta já em uso

Se a porta 80 estiver em uso, use uma porta diferente:

```bash
docker run -d -p 8080:80 --name sample-auth-guards sample-auth-guards:latest
```

### Container parou

Verifique os logs:
```bash
docker logs sample-auth-guards
```

## Desenvolvimento vs Produção

- **Este Dockerfile é otimizado para PRODUÇÃO**
- Para desenvolvimento, continue usando `npm start`
- O Dockerfile não inclui source maps em produção (para segurança)

## Customizações

### Alterar Porta

Edite `docker-compose.yml` e altere a porta em `ports`:
```yaml
ports:
  - "8080:80"  # Nova porta
```

### Alterar Versão do Nginx

Edite o Dockerfile, linha `FROM nginx:...`:
```dockerfile
FROM nginx:1.27-alpine  # Altere a versão aqui
```

### Adicionar Variáveis de Ambiente

Edite `docker-compose.yml` e adicione em `environment`:
```yaml
environment:
  - NODE_ENV=production
  - CUSTOM_VAR=value
```

## Push para Registro Docker

Para publicar em Docker Hub:

```bash
docker tag sample-auth-guards:latest seu-usuario/sample-auth-guards:latest
docker push seu-usuario/sample-auth-guards:latest
```
