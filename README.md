# 📋 Sistema de Registro de Ocorrências

Aplicação Java de linha de comando (CLI) para registrar e gerenciar ocorrências dentro de uma organização. O acesso é controlado por três níveis de permissão — **Diretor**, **Gerente** e **Funcionário** — e todas as informações são persistidas em um banco de dados MySQL.

---

## ✨ Funcionalidades

### 👔 Diretor
O perfil com maior nível de acesso. O Diretor tem visão total da organização e é responsável pela estrutura administrativa do sistema:
- Criar, editar e excluir **departamentos**
- Cadastrar, alterar e excluir **gerentes**
- Listar **todos os funcionários** cadastrados no sistema

### 🗂️ Gerente
O Gerente é o responsável operacional do seu departamento. Ele gerencia tanto a equipe quanto as ocorrências registradas:
- Registrar **novas ocorrências** para o seu departamento, definindo descrição, datas e o funcionário responsável pela resolução
- **Fechar definitivamente** uma ocorrência após análise
- Cadastrar, alterar, listar e excluir **funcionários** do seu próprio departamento

### 🔧 Funcionário
O Funcionário é quem recebe e executa as ocorrências. Seu acesso é focado nas tarefas que lhe foram atribuídas:
- Consultar as **ocorrências alocadas** para si
- Atualizar o status de uma ocorrência que foi resolvida (encerramento temporário)

### ⏰ Monitor automático de prazos
Ao iniciar a aplicação, uma **Thread de background** é disparada automaticamente. A cada minuto, ela verifica todas as ocorrências abertas e alerta no console caso alguma tenha ultrapassado o prazo limite de solução — tudo isso sem travar o menu principal.

---

## 🏗️ Padrões e recursos técnicos

Este projeto foi construído com foco em boas práticas de engenharia de software. Alguns destaques:

- **DAO Pattern (Data Access Object):** toda comunicação com o banco de dados é isolada em classes DAO (`DepartamentoDAOImpl`, `FuncionarioDAOImpl`, `OcorrenciaDAOImpl`), que implementam interfaces (`IDepartamentoDAO`, `IFuncionarioDAO`, `IOcorrenciaDAO`). Isso separa a lógica de persistência do restante da aplicação.

- **Service Layer:** as regras de negócio vivem em classes de serviço separadas (ex.: `OcorrenciaServiceImpl`), mantendo o `Main.java` apenas como orquestrador de interface — sem regras embutidas na UI.

- **Injeção de Dependência manual:** a classe `AppConfig` funciona como um *Service Locator* / *Composition Root*, instanciando os DAOs e injetando-os nos construtores dos serviços. Isso facilita a substituição de implementações sem alterar o restante do código.

- **Herança e Polimorfismo:** `Gerente` e `Diretor` herdam de `Funcionario`. O sistema usa `instanceof` para determinar o nível de permissão do usuário logado e exibir o menu correto, sem necessidade de campos de "role" no banco.

- **Exceção customizada:** `AuthException` é lançada exclusivamente em violações de permissão, permitindo tratar erros de autorização de forma diferenciada dos demais erros de negócio.

- **Sessão stateful com `SessionManager`:** o usuário autenticado fica armazenado em um campo estático da classe `SessionManager`, acessível globalmente durante a sessão — um padrão simples e eficaz para aplicações CLI de usuário único.

- **Multithreading com `Runnable`:** `VerificadorOcorrencias` implementa `Runnable` e é executado em uma thread separada via `new Thread(...).start()`. O loop usa `Thread.sleep()` e trata `InterruptedException` de forma segura, restaurando o flag de interrupção.

- **Java Time API (`java.time`):** datas são manipuladas com `LocalDate` e `DateTimeFormatter`, evitando os problemas clássicos do `java.util.Date`.

- **JDBC com `ConnectionFactory`:** a conexão com o banco é centralizada em uma única classe utilitária, usando `DriverManager` e carregando o driver via `Class.forName()`.

---

## 🛠️ Tecnologias

- Java 11+
- MySQL 8+
- JDBC (MySQL Connector/J)

---

## 📦 Pré-requisitos

- ☕ [Java 11+](https://adoptium.net/) instalado e configurado no PATH
- 🐬 [MySQL 8+](https://dev.mysql.com/downloads/mysql/) em execução
- 🔌 MySQL Connector/J — instruções abaixo

---

## 🔌 Obtendo o MySQL Connector/J

O driver JDBC não está incluso no repositório e precisa ser baixado separadamente:

1. Acesse: https://dev.mysql.com/downloads/connector/j/
2. Selecione **Platform Independent** e baixe o arquivo `.zip`
3. Extraia o `.zip` e localize o arquivo `mysql-connector-j-<versão>.jar`
4. Crie a pasta `lib/` na raiz do projeto e coloque o JAR dentro dela:

```
Registro-de-Ocorrencias-Java-CLI/
└── lib/
    └── mysql-connector-j-9.3.0.jar   ← exemplo
```

---

## 🗄️ Configurando o banco de dados

**1. Execute o script de criação:**

O repositório já inclui o arquivo `database_schema.sql` com tudo necessário — cria o banco, as tabelas e insere dados iniciais para você já conseguir entrar no sistema.

No terminal do MySQL:
```sql
SOURCE database_schema.sql;
```

Ou via linha de comando:
```bat
mysql -u root -p < database_schema.sql
```

O script já cria um **Diretor** com matrícula `1` para o primeiro acesso.

**2. Configure a conexão em [src/util/ConnectionFactory.java](src/util/ConnectionFactory.java):**

```java
private static final String URL      = "jdbc:mysql://localhost:3306/registro_ocorrencias?useSSL=false&serverTimezone=UTC";
private static final String USER     = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

---

## ▶️ Compilando e executando

**Via IDE (recomendado)**

Importe o projeto no IntelliJ IDEA ou Eclipse, adicione o JAR do Connector às bibliotecas do projeto e execute a classe `run.Main`.

**Via linha de comando (Windows)**

```bat
mkdir bin
javac -cp "lib\mysql-connector-j-9.3.0.jar" -d bin src\config\*.java src\dao\api\*.java src\dao\impl\*.java src\exception\*.java src\model\*.java src\service\api\*.java src\service\impl\*.java src\session\*.java src\util\*.java src\run\*.java
java -cp "bin;lib\mysql-connector-j-9.3.0.jar" run.Main
```

> Ajuste o nome do JAR conforme a versão que você baixou.

---

## 📁 Estrutura do projeto

```
src/
├── config/        — AppConfig (composição e injeção de dependências)
├── dao/
│   ├── api/       — interfaces de acesso a dados
│   └── impl/      — implementações JDBC
├── exception/     — AuthException (erros de permissão)
├── model/         — Departamento, Funcionario, Gerente, Diretor, Ocorrencia
├── run/           — Main (entrada) e VerificadorOcorrencias (thread)
├── service/
│   ├── api/       — interfaces de negócio
│   └── impl/      — regras de negócio e validações
├── session/       — SessionManager (usuário logado)
└── util/          — ConnectionFactory (JDBC)
```
