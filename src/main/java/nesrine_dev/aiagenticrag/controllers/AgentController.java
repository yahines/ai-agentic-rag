package nesrine_dev.aiagenticrag.controllers;

import nesrine_dev.aiagenticrag.agents.AIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
public class AgentController {
    private AIAgent aiAgent;
    public AgentController(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }
//    private ChatClient chatClient;
//    public AgentController(ChatClient.Builder builder) {
//        this.chatClient = builder.build();
//    }

    @GetMapping(value = "/askAgent", produces = MediaType.TEXT_PLAIN_VALUE)
    public String askAgent(@RequestParam(defaultValue = "Hello") String query,
                           @RequestParam(defaultValue = "default") String conversationId) {
//        return chatClient.prompt()
//                .user(query)
//                .call()
//                .content();
        return aiAgent.onQuery(query,conversationId);
    }

    @GetMapping(value = "/askStreamAgent", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> askStreamAgent(@RequestParam(defaultValue = "Hello") String query,
                                       @RequestParam(defaultValue = "default") String conversationId) {
        return aiAgent.onStreamQuery(query, conversationId);
    }
}
