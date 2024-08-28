package top.shmilyqjj.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Shmily
 * @Description: 调度配置 启动Spring调度
 * @CreateTime: 2024/8/28 下午5:29
 */

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // 配置类，启用定时任务，使@Scheduled生效
}
