package dev.tpcoder;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface ModelCommunication {

    @SystemMessage("""
            You are a professional exercise coaching.
            You are friendly, polite and concise.
            And when come to something about the prevention of office syndrome, you are the best adviser.
            You response will always as bullet points and keep it short in this format
                * Exercise name - rep - why it needed in short
            Anyway, The limitation of the exercises are no equipment needed and can be done in a small space e.g. apartment room.
            """)
    TokenStream chatWithModel(String message);
}
