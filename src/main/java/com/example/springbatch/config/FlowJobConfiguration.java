package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job flowJob1(){
        /**
         * Transition
         * - Flow 내 Step의 "조건부 전환"을 정의함
         * - on() 메소드를 호출하면 TransitionBuilder가 반환되어 Transition Flow를 구성
         * - Step의 종료가 어떤 pattern과도 매칭되지 않으면 -> Exception, JOB FAILED
         * - transition은 구체적인 것 부터 그렇지 않은 순서로 적용
         */
        return new JobBuilder("flowJob", jobRepository)
                // FLOW JOB 예시
                .start(flowStep1())
                    // on()
                    // Step의 실행 결과로 돌려받는 종료상태(ExitStatus)와 매칭하는 패턴 스키마
                    // * : 0개 이상의 문자 (% 와 같은 개념)
                    // ? : 1개의 문자와 매칭 (_ 와 같은 개념)
                    .on("FAILED")
                    // to()
                    // 다음으로 실행할 단계를 지정
                    .to(flowStep2())
                    .on("FAILED")
                    .stop()
                // from()
                // 이전 단계에서 정의한 Transition을 새롭게 추가 정의
                .from(flowStep1())
                    .on("*")
                    .to(flowStep3())
                    .next(flowStep4())
                .from(flowStep2())
                    .on("*")
                    .to(flowStep5())
                // end 하는 순간 : Simple Flow 객체 생성
                .end().build();
    }

    @Bean
    public Flow flowA(){
        return new FlowBuilder<Flow>("flowA")
                .start(flowStep1())
                .next(flowStep2())
                .build();
    }

    @Bean
    public Step flowStep1(){
        return new StepBuilder("flowStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowStep1 was executed.");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowStep2(){
        return new StepBuilder("flowStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowStep2 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowStep3(){
        return new StepBuilder("flowStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowStep3 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowStep4(){
        return new StepBuilder("flowStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowStep4 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
    @Bean
    public Step flowStep5(){
        return new StepBuilder("flowStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowStep5 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

}
