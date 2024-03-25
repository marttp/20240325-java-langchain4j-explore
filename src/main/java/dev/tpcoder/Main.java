package dev.tpcoder;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    // Ollama serve locally on port 11434
    private static final String OLLAMA_HOST = "http://localhost:11434";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        var model = connectModel("llama2");
        while (true) {
            System.out.print("""
                    \n
                    Type 'exit' to quit the program.
                    Enter your prompt:\s""");
            String userPrompt = scanner.nextLine();
            if (userPrompt.equals("exit")) {
                break;
            }
            // Change to streaming model
            modelResponse(model, userPrompt);
        }
    }

    private static void modelResponse(StreamingChatLanguageModel model, String userPrompt) {
        CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
        model.generate(userPrompt, new StreamingResponseHandler<>() {

            @Override
            public void onNext(String token) {
                System.out.print(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureResponse.complete(response);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });
        futureResponse.join();
    }

    private static StreamingChatLanguageModel connectModel(String modelName) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(OLLAMA_HOST)
                .modelName(modelName)
                .timeout(Duration.ofHours(1))
                .build();
    }
}