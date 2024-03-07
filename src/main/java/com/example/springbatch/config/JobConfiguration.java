package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobRepository jobRepository; //
    private final PlatformTransactionManager platformTransactionManager; //
    @Bean
    public Job job(){
        // Job 객체 생성
        /**
         * Job
         * -> SimplJob(step들을 가지고 실행)
         * -> BatchAutoConfiguration
         */
        return new JobBuilder("job", jobRepository)
                .start(step1()) // 최소 1개 이상의 step 구성
                .next(step2()) // step 호출
                .build();
    }
    @Bean
    public Step step1(){
        // Step 객체 생성
        /**
         * Job
         * -> SimplJob(step들을 가지고 실행)
         * -> AbstracJob
         * -> stepHandler 에서 step 실행
         */
        return new StepBuilder("step1", jobRepository)
                // tasklet 방식 호출
                // Tasklet 객체 생성
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                    log.info("name = {}", jobParameters.getString("name"));
                    log.info("seq = {}", jobParameters.getLong("seq"));
                    log.info("date = {}", jobParameters.getDate("date"));
                    log.info("age = {}", jobParameters.getDouble("age"));

                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED; // step 종료
                }, platformTransactionManager).build();
    }

    @Bean
    public Step step2(){
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();

                    log.info("jobParameters = {}", jobParameters.toString());
                    log.info("step2 is executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }
}
