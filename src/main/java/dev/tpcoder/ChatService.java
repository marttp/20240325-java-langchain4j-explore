package dev.tpcoder;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ChatService implements UserStreamCommunication, ModelCommunication {

    private final StreamingChatLanguageModel languageModel;
    private final ModelCommunication assistant;

    public ChatService(String modelUrl, String modelName) {
        this.languageModel = connectModel(modelUrl, modelName);
        // Memorize for 10 messages continuously
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(ModelCommunication.class)
                // Alternative of .chatLanguageModel() which support streaming response
                .streamingChatLanguageModel(this.languageModel)
                .chatMemory(chatMemory)
                .build();
    }

    // Could you give me the way to exercise for office worker, please?
    // Based on previous answer, What if I don't have much place outside?
    public CompletableFuture<Void> ask(String userPrompt) {
        TokenStream tokenStream = chatWithModel(userPrompt);
        CompletableFuture<Void> future = new CompletableFuture<>();
        tokenStream.onNext(System.out::print)
                .onComplete(_ -> {
                    System.out.println();
                    future.complete(null);
                })
                .onError(Throwable::printStackTrace)
                .start();
        return future;
    }

    @Override
    public TokenStream chatWithModel(String message) {
        return assistant.chatWithModel(message);
    }

    private static StreamingChatLanguageModel connectModel(String url, String modelName) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(url)
                .modelName(modelName)
                .timeout(Duration.ofHours(1))
                .build();
    }
}
