package com.portfolio.batch.app.mapper;

import com.portfolio.batch.app.domain.ExamUser;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User 데이터에 접근하기 위한 MyBatis Mapper 인터페이스입니다.
 * @Mapper 어노테이션을 통해 Spring이 MyBatis Mapper로 인식하고 빈으로 등록합니다.
 */
@Mapper
public interface ExamUserMapper {

    /**
     * 특정 날짜 이전에 마지막으로 로그인한 활성(ACTIVE) 사용자 목록을 조회합니다.
     * ItemReader에서 페이징 처리를 위해 사용됩니다.
     * @param targetDate 기준 날짜
     * @return 사용자 목록
     */
    List<ExamUser> findDormantUsers(LocalDateTime targetDate);

    /**
     * 사용자의 상태를 업데이트합니다.
     * ItemWriter에서 사용됩니다.
     * @param user 업데이트할 사용자 정보 (id와 status 필드 사용)
     * @return 업데이트된 row 수
     */
    int updateUserStatus(ExamUser user);
}
