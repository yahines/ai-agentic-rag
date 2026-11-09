package nesrine_dev.aiagenticrag.controllers;

import nesrine_dev.aiagenticrag.agents.AIAgent;
import nesrine_dev.aiagenticrag.rag.DocumentIndexer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@CrossOrigin("*")
public class AgentController {
    private AIAgent aiAgent;
    private DocumentIndexer indexer;

    public AgentController(AIAgent aiAgent, DocumentIndexer indexer) {
        this.aiAgent = aiAgent;
        this.indexer = indexer;
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

    @PostMapping(value = "/loadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadFile(@RequestPart("file") MultipartFile file) throws IOException {
        indexer.loadFile(file);
    }
}
