package dev.tpcoder;

import java.util.Scanner;

public class Main {

    // Ollama serve locally on port 11434
    private static final String OLLAMA_HOST = "http://localhost:11434";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatService chatService = new ChatService(OLLAMA_HOST, "llama2");
        while (true) {
            System.out.print("""
                    Type 'exit' to quit the program.
                    Enter your prompt:\s""");
            String userPrompt = scanner.nextLine();
            if (userPrompt.equals("exit")) {
                break;
            }
            // Change to streaming model
            chatService.ask(userPrompt)
                    .join();
        }
    }
}