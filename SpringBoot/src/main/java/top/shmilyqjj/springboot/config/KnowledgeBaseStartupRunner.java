package top.shmilyqjj.springboot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.shmilyqjj.springboot.services.knowledge.KnowledgeBaseService;

@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class KnowledgeBaseStartupRunner implements ApplicationRunner {

    private final KnowledgeBaseService knowledgeBaseService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            knowledgeBaseService.reconcileVectorStoreOnStartup();
        } catch (Exception e) {
            log.error("Knowledge base vector store startup failed (check DB table test.kb_document and embedding API): {}", e.getMessage());
        }
    }
}
