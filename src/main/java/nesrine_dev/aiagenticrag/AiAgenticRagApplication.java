package nesrine_dev.aiagenticrag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiAgenticRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgenticRagApplication.class, args);
    }

    @Bean
    public SimpleVectorStore simpleVectorStore(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        return vectorStore;
    }
}

