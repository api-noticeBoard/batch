package com.portfolio.batch.core.listener;

import com.portfolio.batch.core.domain.IdentifiableBatchItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
public class StepResultListener<T extends IdentifiableBatchItem> implements StepExecutionListener {
    // 생성자를 통해 제네릭 타입이 맞는 CustomSkipListener를 주입받음
    private final CustomSkipListener<T> customSkipListener;

    public StepResultListener(CustomSkipListener<T> customSkipListener) {
        this.customSkipListener = customSkipListener;
    }

    public void beforeStep(StepExecution stepExecution) {
        // Step 시작 시, 이전 실행에 남을 수 있는 실패 기록 초기화
        customSkipListener.getSkippedItems().clear();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String stepName = stepExecution.getStepName();
//        log.info("======================================================");
//        log.info("                  STEP RESULT SUMMARY ('{}')", stepName);
//        log.info("======================================================");

        long readCount = stepExecution.getReadCount();
        long writeCount = stepExecution.getWriteCount();
        long skipCount = stepExecution.getSkipCount();

//        log.info("Total Read Items: {}", readCount);
//        log.info("Total Success (Write) Items: {}", writeCount);
//        log.info("Total Failed (Skip) Items: {}", skipCount);

        ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
        stepExecutionContext.put(stepName + ".successCount", writeCount);
        stepExecutionContext.put(stepName + ".failCount", skipCount);

        if (skipCount > 0) {
            String failedItemsStr = customSkipListener.getSkippedItemsAsString();
            stepExecutionContext.put(stepName + ".failedItems", failedItemsStr);

//            log.warn("---------- Failed Items List for Step '{}' ----------", stepName);
            // CustomSkipListener에 저장된 실패 아이템 정보(문자열)를 그대로 출력합니다.
            customSkipListener.getSkippedItems().forEach(info -> log.warn("Failed Item: {}", info));
//            log.warn("-------------------------------------------------------");
        }

//        log.info("======================================================");
        return stepExecution.getExitStatus();
    }
}
