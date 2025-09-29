package com.portfolio.batch.core.listener;

import com.portfolio.batch.core.domain.IdentifiableBatchItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class CustomSkipListener<T extends IdentifiableBatchItem> implements SkipListener<T, T> {
    // 실패한 아이템을 제네릭 타입 T로 저장
    private final List<String> skippedItems = new ArrayList<>();

    /**
     * ItemReader에서 예외가 발생하여 스킵될 때 호출됩니다.
     */
    @Override
    public void onSkipInRead(Throwable t) {
        log.error("조회 중 예외 발생.(스킵 처리) : {}", t.getMessage());
    }

    /**
     * ItemWriter에서 예외가 발생하여 스킵될 때 호출됩니다.
     * @param item 실패한 아이템
     * @param t 발생한 예외
     */
    @Override
    public void onSkipInProcess(T item, Throwable t) {
        String itemInfo = item.getItemId().toString();
        log.error("처리 중 예외 발생.(스킵 처리) Failed Item : {}, Error : {}", itemInfo, t.getMessage());
        this.skippedItems.add(itemInfo);
    }

    /**
     * ItemProcessor에서 예외가 발생하여 스킵될 때 호출됩니다.
     * @param item 실패한 아이템
     * @param t 발생한 예외
     */
    @Override
    public void onSkipInWrite(T item, Throwable t) {
        String itemInfo = item.getItemId().toString();
        log.error("등록 및 수정 중 예외 발생.(스킵 처리) Failed Item : {}, Error : {}", itemInfo, t.getMessage());
        this.skippedItems.add(itemInfo);
    }
    public String getSkippedItemsAsString(){
        if (skippedItems.isEmpty()) return "";
        return skippedItems.stream().collect(Collectors.joining("\n"));
    }
}
