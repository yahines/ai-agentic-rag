package nesrine_dev.aiagenticrag.tools;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgentTools {

    private VectorStore  vectorStore;

    public AgentTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Get informations about an employee (name, salary, seniority)")
    public EmployeeInfo getEmployeeInfo(
            @ToolParam(description = "Employee Name") String employeeName){
        return new EmployeeInfo(
                employeeName, 65000, 10
        );
    }

    @Tool(description = "chercher des informations sur le cv d'une peronne")
    public List<String> searchContext(String query) {
        List<Document> documents= vectorStore.similaritySearch(SearchRequest.builder()
                                                .query(query)
                                                .topK(4)
                                                .build());
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }
}

record EmployeeInfo(String name, double salary, int seniority) {}