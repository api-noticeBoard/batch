package com.portfolio.batch.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 배치 애플리케이션의 시작점(Entry Point)이 되는 메인 클래스입니다.
 * @SpringBootApplication 어노테이션이 'com.portfolio.batch.app' 하위의 모든
 * @Configuration, @Component, @Mapper 등을 스캔하여 빈으로 등록합니다.
 *
 * 이 덕분에 `batch-core` 모듈의 `BatchCommonConfig`, `GlobalJobListener`와
 * `batch-app` 모듈의 Job 설정, Mapper 인터페이스 등이 모두 자동으로 등록됩니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.portfolio.batch"}) // core 패키지 포함
public class BatchApplication {
    public static void main(String[] args) {
        // 배치 실행 후 스프링 컨텍스트가 바로 종료되도록 System.exit()를 호출합니다.
        // 스케줄러를 통해 실행되는 독립적인 배치 애플리케이션에 적합한 방식입니다.
//        System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));

//        // build.gradle에 spring-boot-starter-web 추가해야 함
//        // 배치 작업이 끝난 후에도 JVM이 종료되는 것을 방지합니다.
//        SpringApplication application = new SpringApplication(BatchApplication.class);
//
//        // 1. 웹 환경을 SERVLET으로 명시하여 내장 톰캣 서버가 계속 실행되도록 합니다.
//        application.setWebApplicationType(WebApplicationType.SERVLET);
//
//        // 2. 애플리케이션을 실행합니다.
        SpringApplication.run(BatchApplication.class, args);
    }
}
