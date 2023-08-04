package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Job(일) 구동 -> Step(단계) 실행 -> Tasklet(작업 내용) 수행
 */
@Slf4j
@Configuration // 하나의 배치 Job을 정의하고 빈 설정
@RequiredArgsConstructor // 생성자 인자에 의존성 주입이 됨
public class HelloJobConfiguration extends DefaultBatchConfiguration {
    @Bean
    public Job helloJob(JobRepository jobRepository, Step helloStep1, Step helloStep2){
        // Job을 생성하는 빌더
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep1)
                .next(helloStep2)
                .build();
    }

    @Bean
    public Step helloStep1(JobRepository jobRepository, PlatformTransactionManager pt){
        // Step을 생성하는 빌더
        // tasklet : Step 안에 단일 태스크로 수행하는 로직 구현
        return new StepBuilder("helloStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=======================");
                    log.info(">>>> Step1 was executed");
                    log.info("=======================");
                    return RepeatStatus.FINISHED;
                },pt)
                .build();
    }

    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager pt){
        return new StepBuilder("helloStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=======================");
                    log.info(">>>> Step2 was executed");
                    log.info("=======================");
                    return RepeatStatus.FINISHED;
                }, pt)
                .build();
    }
}
