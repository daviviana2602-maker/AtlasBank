package org.atlas.common.scheduler;



import org.atlas.common.messaging.RecoveryDlqMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class RecoveryDlqMessageScheduler {


    private final RecoveryDlqMessageService recoveryDlqMessageService;


    public RecoveryDlqMessageScheduler(RecoveryDlqMessageService recoveryDlqMessageService) {
        this.recoveryDlqMessageService = recoveryDlqMessageService;
    }


    @Scheduled(fixedDelay = 90 * 60 * 1000)
    public void recoverMessages() {
        recoveryDlqMessageService.recoverMessages();
    }

}