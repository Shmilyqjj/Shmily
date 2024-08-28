package top.shmilyqjj.springboot.services.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Shmily
 * @Description: 调度任务
 * @CreateTime: 2024/8/28 下午5:26
 */

@Component
public class SchedulerTask {
    @Scheduled(fixedRate = 10000)
    public void func() {
        System.out.println("Application is running");
    }
}
