# 🚀 Nome do Projeto: Desafio CAST

> Projeto Backend desenvolvido com a stack mais moderna do ecossistema Java, focado em boas práticas.

---

## 🛠️ Tecnologias e Ferramentas

* **Java 21 (LTS)** - Utilizando as últimas features de performance e sintaxe.
* **Spring Boot 3.4.3** - Framework base.
* **PostgreSQL** - Banco de dados relacional.
* **Docker & Docker Compose** - Conteinerização para ambiente de desenvolvimento.
* **Swagger (OpenAPI 3)** - Documentação da API.
* **JUnit 5 & Mockito** - Garantia de qualidade através de testes unitários.

---

## 🌟 Diferenciais Técnicos

Este projeto é um case para apresentação. Ele foi construído seguindo princípios que o mercado exige:

* **Arquitetura em Camadas:** Separação clara entre Controller, Service e Repository.
* **Tratamento de Erros:** Implementação de um handler global para respostas HTTP padronizadas.
* **Dockerizado:** Setup do ambiente, sem necessidade de instalar o banco localmente.
* **Records & Pattern Matching:** Uso de recursos modernos do Java 21 para um código mais limpo e imutável.
---

## 📖 Documentação da API

Com a aplicação rodando, você pode explorar e testar todos os endpoints através do Swagger:

🔗 http://localhost:8520/swagger-ui.html

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Docker e Docker Compose instalados.
* Java 21 ou superior.
* Maven

### Passo a Passo

1. **Clone o repositório:**
    git clone https://github.com/welsson/desafio-cast.git

2. **Navege até o diretório do projeto:**
    cd cast

3. **Execute com Docker Compose:**
   docker compose up -d
   
4. **Execute a aplicação com o maven:** (Desenvolvimento)
   ./mvnw spring-boot:run
