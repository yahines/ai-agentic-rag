package nesrine_dev.aiagenticrag.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class AgentTools {

    @Tool(description = "Get infos about an employee")
    public EmployeeInfo getEmployeeInfo(
            @ToolParam(description = "Employee Name") String employeeName){
        return new EmployeeInfo(
                employeeName, 65000, 10
        );
    }
}

record EmployeeInfo(String name, double salary, int seniority) {}