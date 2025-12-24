# Coupon Service

Este repositório contém um serviço REST simples para gerenciamento de cupons (Spring Boot).

## Visão geral

- Aplicação: API para criar, listar e excluir (soft delete) cupons.
- Porta padrão: 8080
- Formato de data: ISO-8601 (ex: 2026-01-31T23:59:59)

## Rodando com Docker

Abaixo estão instruções rápidas para executar a aplicação usando Docker (imagem) ou `docker-compose`.

Pré-requisitos

- Docker instalado (Docker Engine).
- (Opcional) Docker Compose instalado (ou use o `docker compose` integrado ao Docker).

1) Buildando a imagem Docker

Para construir a imagem a partir do `Dockerfile` no diretório do projeto:

```sh
docker build -t coupon:latest .
```

- Isso executa o build do projeto dentro do container (o `Dockerfile` já roda o Gradle e gera o jar).
- O processo pode baixar dependências e levar alguns minutos na primeira execução.

2) Executando a imagem gerada

Para rodar um container a partir da imagem e mapear a porta 8080 para o host:

```sh
docker run --rm -p 8080:8080 \
  -e SERVER_PORT=8080 \
  --name coupon coupon:latest
```

- A opção `--rm` remove o container automaticamente quando ele parar.
- Use `-e` para passar variáveis de ambiente (opcional).

3) Usando docker-compose

O repositório já traz um arquivo `docker-compose.yml`. Para subir o serviço com ele:

```sh
docker-compose up --build -d
```

ou, se você usa a versão integrada do Docker (v2+):

```sh
docker compose up --build -d
```

Para parar e remover os containers criados pelo compose:

```sh
docker-compose down
```

4) Verificando logs e status

- Ver logs do serviço:

```sh
docker-compose logs -f coupon
```

- Listar containers ativos:

```sh
docker ps
```

5) Acessando a API

Depois que o container estiver em execução, a API fica disponível em:

- http://localhost:8080/

Exemplo rápido com curl (listar cupons):

```sh
curl http://localhost:8080/coupon
```

6) Observações e dicas

- O `Dockerfile` do projeto expõe a porta `8080` (se quiser mudar a porta, altere o mapeamento `-p` ou a variável `SERVER_PORT`).
- O build interno usa Gradle para criar o `bootJar`; caso já tenha o jar local você também pode usar uma imagem base mínima para rodar apenas o JAR.
- Se tiver problemas com permissões no container, verifique variáveis de ambiente e o usuário configurado no `Dockerfile`.

---

## Endpoints principais

Abaixo seguem exemplos de requisições curl e os retornos esperados.

### Criar um cupom (POST /coupon)

Request:

```
curl --location 'http://localhost:8080/coupon' \
--header 'Content-Type: application/json' \
--data '{
    "code": "123458",
    "description": "CUPOM 3",
    "discountValue": 0.65,
    "expirationDate": "2026-01-31T23:59:59",
    "published": false
}'
```

Exemplo de retorno HTTP 201:

```
{
    "id": "45f94487-8e08-44e9-a891-5dea6e81c3fe",
    "code": "123458",
    "description": "CUPOM 3",
    "discountValue": 0.65,
    "expirationDate": "2026-01-31T23:59:59",
    "published": false
}
```

### Listar todos os cupons (GET /coupon)

Request:

```
curl --location 'http://localhost:8080/coupon'
```

Exemplo de retorno (array de cupons):

```
[
    {
        "id": "f69cfe1b-292b-442b-95bc-6faeeab49a29",
        "code": "123456",
        "description": "CUPOM 1",
        "discountValue": 0.65,
        "expirationDate": "2026-01-31T23:59:59",
        "published": false
    },
    {
        "id": "45f94487-8e08-44e9-a891-5dea6e81c3fe",
        "code": "123458",
        "description": "CUPOM 3",
        "discountValue": 0.65,
        "expirationDate": "2026-01-31T23:59:59",
        "published": false
    }
]
```

### Deletar um cupom (soft delete) (DELETE /coupon/{id})

Request:

```
curl --location --request DELETE 'http://localhost:8080/coupon/c0464f65-442e-48f0-9d14-472dec96a32d'
```

Exemplo de retorno (booleano indicando sucesso ou falha):

```
true
```

- `true` indica que o cupom foi marcado como deletado (soft delete).

## Observações importantes

- O campo `expirationDate` deve estar em formato ISO-8601 com timezone/sem timezone conforme aceito pela API (exemplo fornecido sem timezone: `2026-01-31T23:59:59`).
- `discountValue` é um número decimal (ex: 0.65 representa 65% ou dependendo da interpretação da aplicação — confirme a unidade no código).
- A exclusão é do tipo "soft delete" (campo `deleted` marcado como `true`) — registros não são removidos fisicamente.

## Swagger / OpenAPI

A documentação interativa das APIs é gerada automaticamente pelo Springdoc OpenAPI e está disponível quando a aplicação está em execução.

- Swagger UI (interface web): http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON (spec): http://localhost:8080/v3/api-docs