## 📁 Estrutura do Projeto

- **`coordinator/`**: Contém a classe principal `Coordinator.java`, responsável por orquestrar todo o processo MapReduce.
- **`generator/`**: Inclui `TextGenerator.java` para gerar o arquivo de dados e `ChunkSplitter.java` para dividir esse arquivo em partes menores (*chunks*).
- **`workers/`**: Abriga `MapperWorker.java` e `ReducerWorker.java`, que executam as fases de mapeamento e redução, respectivamente.
- **`pom.xml`**: Arquivo de configuração do Maven, gerenciando dependências e compilação do projeto.

---

## 🔄 Ordem de Execução

### 1. Geração dos Dados

Execute o `TextGenerator` para criar o arquivo `data.txt` com dados de exemplo:

run as > java aplication

### 2. Divisão do Arquivo em Chunks

Execute a classe `ChunkSplitter` para dividir o arquivo `data.txt` em partes menores (chunks), por exemplo em 4 partes:

run as > java aplication
(ele vem definido para dividir em 10 arquivos)

Isso criará arquivos como:

```text
chunk0.txt
chunk1.txt
chunk2.txt
chunk3.txt
```

---

### 3. Execução do Coordenador

Execute a classe `Coordinator` para iniciar o processo completo de MapReduce:

```bash
java coordinator.Coordinator
```

A classe `Coordinator` executa as seguintes etapas:

1. ✅ Enfileira os arquivos `chunk*.txt` no Redis como tarefas de mapeamento (`map-tasks`).
(em progresso)

---

## ✅ Resultado Final

O arquivo `final_result.txt` conterá as palavras encontradas no texto e o número de ocorrências de cada uma delas.

---

## ⚙️ Requisitos

- Java 8+
- Redis em execução local na porta padrão `6379`
- Maven (para compilar o projeto com `mvn package`)

---

## 📌 Observações

- O Redis é utilizado apenas como **sistema de fila simples** para distribuir tarefas.
- Os workers são executados em **processos independentes** gerados pela classe `Coordinator` via `ProcessBuilder`.

---

## 👨‍💻 Autor Daniel Artur
