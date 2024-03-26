package dev.tpcoder;

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
        this.assistant = AiServices.create(ModelCommunication.class, this.languageModel);
    }

    // Could you give me the way to exercise for office worker, please?
    // Based on previous answer, What if I don't have much place outside?
    public CompletableFuture<Void> ask(String userPrompt) {
        TokenStream tokenStream = chatWithModel(userPrompt);
        CompletableFuture<Void> future = new CompletableFuture<>();
        tokenStream.onNext(System.out::print)
                .onComplete(token -> {
                    System.out.println(token);
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
