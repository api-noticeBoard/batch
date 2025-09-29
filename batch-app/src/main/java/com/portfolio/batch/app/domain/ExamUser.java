package com.portfolio.batch.app.domain;

import com.portfolio.batch.core.domain.IdentifiableBatchItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 사용자 정보를 담는 도메인 클래스입니다.
 */
@Getter @Setter
@ToString
public class ExamUser implements IdentifiableBatchItem {
    private Long id;
    private String username;
    private String status;  // ACTIVE, DORMANT
    private LocalDateTime lastLoginAt;


    @Override
    public Object getItemId() {
        return this.id;
    }

    @Override
    public String getItemName() {
        return this.username;
    }
}
