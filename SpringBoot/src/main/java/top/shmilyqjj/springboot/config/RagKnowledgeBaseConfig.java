package top.shmilyqjj.springboot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KnowledgeBaseProperties.class)
public class RagKnowledgeBaseConfig {

    @Bean(name = "customerServiceChatClient")
    public ChatClient customerServiceChatClient(ChatModel chatModel, VectorStore vectorStore) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.0)
                .build();
        QuestionAnswerAdvisor ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(searchRequest)
                .build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(ragAdvisor)
                .defaultSystem("""
                        你是专业在线客服。请严格依据对话中由系统检索并附带的「上下文知识」回答用户问题。
                        若上下文不足以回答，请明确说明知识库中暂无相关信息，不要编造事实。""")
                .build();
    }
}
