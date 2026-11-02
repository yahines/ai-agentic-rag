package nesrine_dev.aiagenticrag.agents;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Service
public class AIAgent {
    private ChatClient chatClient;


    public AIAgent(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public String onQuery(String query,
                          @RequestParam(defaultValue = "default") String conversationId) {
        return chatClient.prompt().user(query).advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)).call().content();
    }

    public Flux<String> onStreamQuery(String query,
                                      @RequestParam(defaultValue = "default") String conversationId) {
        return chatClient.prompt().user(query).advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)).stream().content();
    }
}
