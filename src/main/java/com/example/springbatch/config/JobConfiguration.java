package com.example.springbatch.config;

import com.example.springbatch.tasklet.*;
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

    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

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
                .next(step3())
                .next(step4())
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
                .tasklet(executionContextTasklet1, platformTransactionManager).build();
    }

    @Bean
    public Step step2(){
        return new StepBuilder("step2", jobRepository)
                .tasklet(executionContextTasklet2,platformTransactionManager)
                .build();
//                .tasklet((contribution, chunkContext) -> {
//                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//
//                    log.info("jobParameters = {}", jobParameters.toString());
//                    // jobParameters = {date=Mon Mar 11 14:57:41 KST 2024, name=user1, seq=2, age=16.5}
//                    log.info("step2 is executed.");
//                    contribution.setExitStatus(ExitStatus.COMPLETED);
//
////                    throw new RuntimeException("step2 has failed"); // FAILED를 위해 일부러 throw
//                    return RepeatStatus.FINISHED;
//
//                    /**
//                     * step이 모두 정상적으로 종료 -> JOB status : [COMPLETED]
//                     * [COMPLETE] : JobInstance 실행 불가 → JobExecution 생성 X => 재실행 불가
//                     * [FAILED] : JobInstance 실행 가능 → JobExecution 생성 O => 재실행 가능
//                     */
//                }, platformTransactionManager).build();
    }

    @Bean
    public Step step3(){
        return new StepBuilder("step3", jobRepository)
                .tasklet(executionContextTasklet3, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step4(){
        return new StepBuilder("step4", jobRepository)
                .tasklet(executionContextTasklet4, platformTransactionManager)
                .build();
    }
}
