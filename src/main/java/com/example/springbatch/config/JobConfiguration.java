package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobRepository jobRepository; //
    private final PlatformTransactionManager platformTransactionManager; //
    @Bean
    public Job job(){
        // Job 객체 생성
        return new JobBuilder("job", jobRepository)
                .start(step1()) // 최소 1개 이상의 step 구성
                .next(step2()) // step 호출
                .build();
    }
    @Bean

    public Step step1(){
        // Step 객체 생성
        return new StepBuilder("step1", jobRepository)
                // tasklet 방식 호출
                // Tasklet 객체 생성
                .tasklet((contribution, chunkContext) -> {
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    log.info("step1 is executed.");
                    return RepeatStatus.FINISHED; // step 종료
                }, platformTransactionManager).build();
    }

    @Bean
    public Step step2(){
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    log.info("step2 is executed.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }
}
