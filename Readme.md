# ChatBot Project

## Overview
This project is an AI-powered chatbot that uses the `langchain4j` library to interact with the local Ollama API. The chatbot is designed to provide intelligent responses and maintain conversation history using in-memory storage.

## Installation and Launching Instructions

### Prerequisites
- Docker
- Java 21
- Maven

### Step 1: Launch Docker Compose
To start the Ollama service using Docker Compose, navigate to the project directory and run the following command:

```sh
docker-compose up -d
```

### Step 2: Pull the Mistral Model
To pull the Mistral model, use the following command:

```sh
docker exec -it ollama ollama pull mistral
```

### Step 3: Run the Mistral Model
To run the Mistral model, use the following command:

```sh
docker exec -it ollama ollama run mistral
```

### Step 4: Launch the Chatbot
To launch the chatbot, follow these steps:

1. Navigate to the project directory.
2. Build the project using Maven:

    ```sh
    mvn clean install
    ```

3. Run the chatbot application:

    ```sh
    java -jar target/chatBot-1.0-SNAPSHOT.jar
    ```

This will start the chatbot, and you can interact with it via the console. Type 'exit' to quit the application.

### Step 5: Run the Chatbot in the IDE
To run the chatbot in IntelliJ IDEA, follow these steps:

1. Open IntelliJ IDEA.
2. Open the project by selecting `File > Open` and navigating to the project directory.
3. Wait for IntelliJ IDEA to index the project and download dependencies.
4. Navigate to the `src/main/java/net/wickedshell/ai/chatbot/OllamaChat.java` file.
5. Right-click on the `OllamaChat` class and select `Run 'OllamaChat.main()'`.

This will start the chatbot, and you can interact with it via the console in the IDE. Type 'exit' to quit the application.