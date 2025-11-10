## 🛠️ Tecnologias Utilizadas

- **Java 17**  
- **Spring Boot 3.x**  
- **Spring Data JPA**  
- **H2 Database** (em memória)    
- **Lombok** (redução de boilerplate)  
- **JUnit & Mockito** (testes unitários e de integração)  
- **Swagger/OpenAPI** (documentação interativa)  
- **Docker** (para containerização e execução do projeto)

## 🧱 Estrutura do Projeto

```text
rc/main/java/org/fadesp/pagamentos_api/
├─ controller/       # 🌐 Endpoints REST e manipulação de requisições.
├─ dto/              # 🔄 Objetos de transferência de dados.
├─ enums/            # 🏷️ Enumerações (StatusPagamento, MetodoPagamento).
├─ model/            # 📦 Entidades JPA e mapeamento do banco de dados (Pagamento).
├─ repository/       # 💾 Acesso a dados com Spring Data JPA.
├─ service/          # 🧠 Camada de Regras de Negócio e Lógica.
└─ configuration/    # ⚙️ Configurações do Spring, Swagger, etc.
```
## ▶️ Rodando o Projeto

### Pré-requisitos
- Java 17 instalado  
- Docker e Docker Compose instalados
  
### Passos

1. Clone o projeto e navegue para o diretório:
```bash
git clone https://github.com/RafaelaBicalho/pagamentos-api.git
cd pagamentos-api
```

2. Construa os containers do Docker:
```bash
docker-compose build
```

3. Execute a aplicação:
```bash
docker-compose run
```
Acesso e Documentação

Após a inicialização (http://localhost:8080/pagamentos):
```bash
Swagger UI:   http://localhost:8080/swagger-ui.html
Console H2:   http://localhost:8080/h2-console
```

Configuração do Console H2
Ao acessar o console H2, preencha os campos com a seguinte configuração para se conectar ao banco de dados no container:
```bash
JDBC URL: jdbc:h2:file:/data/testdb
User Name: sa
Password: (Deixe em branco)
```

## 💾 Exemplo de payload para criação de pagamento (POST /pagamentos):
```bash
{
  "codigoDebito": 123,
  "cpfCnpj": "12345678910",
  "metodoPagamento": "CARTAO_CREDITO",
  "numeroCartao": "123456789",
  "valor": 375.50
}
```

## ✅ Cobertura de Testes
O projeto utiliza JUnit para garantir a qualidade e a conformidade das regras de negócio.

- A classe PagamentoServiceTest cobre detalhadamente:
- Regras de criação de pagamento e validação de cpfCnpj.
- Regras de transição de status (ex: não alterar de PROCESSADO_SUCESSO).
- Lógica de exclusão/inativação (somente se PENDENTE).
- Testes para os filtros dinâmicos, incluindo o novo filtro por ID.

## ▶️ Rodando o teste:
```bash
mvn test  
```


