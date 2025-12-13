# Sistema de Autenticação e Autorização

## Visão Geral

Este documento descreve como funciona o sistema de autenticação e autorização implementado na aplicação IFPark. O sistema utiliza tokens JWT (JSON Web Tokens) para autenticação stateless e controle de acesso baseado em papéis (RBAC).

## Arquitetura de Segurança

### Componentes Principais

1. **JWT (JSON Web Tokens)**: Utilizado para autenticação stateless
2. **Spring Security**: Framework de segurança do Spring
3. **Filtro JWT**: Intercepta requisições para validar tokens
4. **UserDetails**: Interface implementada pela entidade Usuario
5. **UserDetailsService**: Serviço para carregar usuários do banco de dados
6. **RBAC (Role-Based Access Control)**: Controle de acesso baseado em papéis

### Fluxo de Autenticação

1. Usuário envia credenciais para `/auth/login`
2. Credenciais são validadas pelo `AuthenticationManager`
3. Se válidas, um token JWT é gerado pelo `JwtUtil`
4. Token é retornado ao cliente
5. Cliente envia token no header `Authorization: Bearer <token>` nas requisições subsequentes
6. `JwtFilter` intercepta a requisição e valida o token
7. Se válido, usuário é autenticado e autorizado conforme seu papel

## Papéis de Usuário

Existem três papéis definidos no sistema:

- `ROLE_ADMIN`: Administrador com acesso total
- `ROLE_VIGIA`: Vigia com acesso limitado
- `ROLE_COMUM`: Usuário comum com acesso básico

## Endpoints de Autenticação

### Registro de Usuário
```
POST /auth/register
```
Registra um novo usuário com papel padrão `ROLE_COMUM`.

Corpo da requisição:
```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@example.com",
  "senha": "senha123",
  "tipo": "ALUNO",
  "telefone": "(11) 99999-9999"
}
```

### Login
```
POST /auth/login
```
Autentica um usuário e retorna um token JWT.

Corpo da requisição:
```json
{
  "email": "joao@example.com",
  "senha": "senha123"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresIn": 86400000
}
```

## Controle de Acesso (Autorização)

### Regras de Acesso

- **Endpoints públicos**: `/auth/**` (registro e login)
- **Endpoints protegidos**: Todos os demais requerem autenticação
- **Controle por papel**: Alguns endpoints requerem papéis específicos

### Exemplos de Controle de Acesso

#### UsuarioController
- `GET /usuarios`: Acessível por `ADMIN` e `VIGIA`
- `GET /usuarios/{id}`: Acessível por `ADMIN` e `VIGIA`
- `GET /usuarios/email/{email}`: Acessível por `ADMIN` e `VIGIA`
- `POST /usuarios`: Acessível apenas por `ADMIN`
- `PUT /usuarios/{id}`: Acessível apenas por `ADMIN`
- `DELETE /usuarios/{id}`: Acessível apenas por `ADMIN`

## Como Testar

### 1. Registro de Usuário

Envie uma requisição POST para `/auth/register`:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Administrador",
    "cpf": "123.456.789-00",
    "email": "admin@ifpark.com",
    "senha": "admin123",
    "tipo": "SERVIDOR",
    "telefone": "(11) 99999-9999"
  }'
```

### 2. Login

Envie uma requisição POST para `/auth/login`:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@ifpark.com",
    "senha": "admin123"
  }'
```

Guarde o token retornado na resposta.

### 3. Acesso a Endpoints Protegidos

Use o token para acessar endpoints protegidos:

```bash
curl -X GET http://localhost:8080/usuarios \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### 4. Testando Controle de Acesso por Papel

Para testar o controle de acesso por papel, você pode:

1. Registrar um usuário comum (recebe `ROLE_COMUM` por padrão)
2. Fazer login com esse usuário
3. Tentar acessar um endpoint que requer `ADMIN` (deve retornar 403 Forbidden)

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "email": "outro@example.com",
    "senha": "senha123",
    "papel": "ROLE_COMUM",
    "pessoaId": "ID_DA_PESSOA"
  }'
```

## Configurações

### Propriedades do JWT

As seguintes propriedades podem ser configuradas no `application.properties`:

```properties
# Chave secreta para assinatura dos tokens (deve ser alterada em produção)
jwt.secret=minhaChaveSecretaQueDeveSerAlteradaEmProducao

# Tempo de expiração do token em milissegundos (padrão: 24 horas)
jwt.expiration=86400000
```

## Classes Principais

### Usuario (Entity)
Implementa `UserDetails` para integração com Spring Security.

### UserDetailsServiceImpl
Implementa `UserDetailsService` para carregar usuários do banco de dados.

### JwtUtil
Utilitário para geração e validação de tokens JWT.

### JwtFilter
Filtro que intercepta requisições e valida tokens JWT.

### SecurityConfig
Configuração de segurança do Spring Security.

## Tratamento de Erros

O sistema trata os seguintes erros de autenticação/autorização:

- Credenciais inválidas (401 Unauthorized)
- Token ausente ou inválido (401 Unauthorized)
- Acesso negado (403 Forbidden)
- Usuário não encontrado (404 Not Found)

## Melhorias Futuras

1. **Refresh Tokens**: Implementar refresh tokens para renovar tokens expirados
2. **Logout**: Implementar mecanismo de logout para invalidar tokens
3. **Auditoria**: Adicionar logs de acesso e auditoria de segurança
4. **Rate Limiting**: Implementar limitação de taxa para prevenir ataques
5. **Segurança Adicional**: Adicionar verificação de IP, user agent, etc.
