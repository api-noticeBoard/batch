package com.portfolio.batch.app.job;

import com.portfolio.batch.app.domain.ExamUser;
import com.portfolio.batch.core.listener.CustomSkipListener;
import com.portfolio.batch.core.listener.GlobalJobListener;
import com.portfolio.batch.core.listener.StepResultListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeactivateExamUsersJobConf {

    private final GlobalJobListener globalJobListener;
    private final SqlSessionFactory sqlSessionFactory;  // MyBatis 연동을 위한 주입
    private static final int CHUNK_SIZE = 100;  // 한번에 처리할 데이터 양

    /**
     * Job을 정의
     *
     * @param jobRepository
     * @param deactivateExamUsersStep
     * @return
     */
    @Bean
    public Job deactivateExamUsersJob(JobRepository jobRepository, Step deactivateExamUsersStep) {
        return new JobBuilder("deactivateExamUsersJob", jobRepository)
//                .preventRestart()               // 재시작 방지(같은 jobname, param으로 실행 불가)
                .listener(globalJobListener)    // 공통 리스너 등록
                .start(deactivateExamUsersStep) // 실행할 Step 지정
                .build();
    }

    /**
     * Step을 정의합니다.
     * @JobScope: Job 파라미터를 Step 레벨 컴포넌트에서 사용할 수 있게 해줌.
     * @param jobRepository
     * @param transactionManager
     * @param reader
     * @param processor
     * @param writer
     * @return
     */
    @Bean
    @JobScope
    public Step deactivateExamUsersStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        MyBatisPagingItemReader<ExamUser> reader,
                                        ItemProcessor<ExamUser, ExamUser> processor,
                                        MyBatisBatchItemWriter<ExamUser> writer,
                                        // 주입받는 리스너의 제네릭 타입을 ExamUser로 명확히 지정
                                        StepResultListener<ExamUser> stepResultListener,
                                        CustomSkipListener<ExamUser> customSkipListener) {
        return new StepBuilder("deactivateExamUsersStep", jobRepository)
                .<ExamUser, ExamUser>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepResultListener)
                .faultTolerant()
                .skip(Exception.class)
                .listener(customSkipListener)
                .build();
    }

    /**
     * ItemReader: DB에서 휴면 대상 사용자를 읽어옴.
     * @JobScope를 사용하여 JobParameter를 메서드 인자로 받을 수 있음.
     * @param targetDateStr
     * @return
     */
    @Bean
    @JobScope
    public MyBatisPagingItemReader<ExamUser> reader(@Value("#{jobParameters[targetDate]}") String targetDateStr) {
        Map<String, Object> parameters  = new HashMap<>();
        // Job Parameter로 받은 날짜 문자열을 LocalDateTime으로 변환하여 쿼리 파라미터로 설정
        parameters .put("targetDate", LocalDateTime.parse(targetDateStr));

        return new MyBatisPagingItemReaderBuilder<ExamUser>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.portfolio.batch.app.mapper.ExamUserMapper.findDormantUsers")  // 실행할 퀴리 ID
                .parameterValues(parameters)                                                // 쿼리에 전달할 파라미터
                .pageSize(CHUNK_SIZE)                                                       // 페이지 사이즈
                .build();
    }

    /**
     * ItemProcessor: 읽어온 사용자의 상태를 'DORMANT'로 변경.
     */
    @Bean
    public ItemProcessor<ExamUser, ExamUser> processor(){
        return user -> {
            log.info("Processing user id : {}, username : {}", user.getId(), user.getUsername());
            user.setStatus("DORMANT");
            return user;
        };
    }

    /**
     * ItemWriter: 가공된 사용자 데이터를 DB에 업데이트합니다.
     */
    @Bean
    public MyBatisBatchItemWriter<ExamUser> writer() {
        return new MyBatisBatchItemWriterBuilder<ExamUser>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.portfolio.batch.app.mapper.ExamUserMapper.updateUserStatus")  // 실행할 쿼리ID
                .build();
    }

    /**
     * 이 Step에서 사용할 CustomSkipListener Bean을 생성합니다.
     * 제네릭 타입 T가 <ExamUser>로 구체화됩니다.
     * ExamUser는 IdentifiableBatchItem을 구현했으므로 타입 제한 조건을 만족합니다.
     */
    @Bean
    @StepScope
    public CustomSkipListener<ExamUser> customSkipListener() {
        return new CustomSkipListener<>();
    }

    /**
     * 이 Step에서 사용할 StepResultListener Bean을 생성합니다.
     * 위에서 생성한 CustomSkipListener<ExamUser> Bean을 주입받아 생성합니다.
     */
    @Bean
    @StepScope
    public StepResultListener<ExamUser> stepResultListener(CustomSkipListener<ExamUser> customSkipListener) {
        // 제네릭 타입이 <ExamUser extends IdentifiableBatchItem>으로 일치하므로 정상적으로 생성됩니다.
        return new StepResultListener<>(customSkipListener);
    }
}
