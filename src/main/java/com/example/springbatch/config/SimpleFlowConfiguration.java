package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SimpleFlowConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    /**
     * Jsr Flow Java 표준 배치에 정의된 내용
     * 스프링 배치를 지원하기 위해 만들어진 것
     *
     * Simple Flow 흐름
     * - FlowJob : SimpleFlow가 생성 & 저장
     * - SimpleFlow : flow or step 저장
     * - flow : state가 존재. 내부적으로 가지고 있는 flow나 step을 실행 시킴
     * - FlowBuilder : state를 생성하고 step or flow를 저장
     * - SimpleFlow : 모든 Flow들을 제어
     *
     * Transition
     * List<StateTransition>으로 생성 됨 : 각각의 Transition 정보를 담고 있음
     * 1) state : 현재 State
     * 2) pattern : on() Transition
     * 3) next : 다음 State
     * * FlowBuilder가 각각의 타입에 따라 State 캡쳐를 정상화
     *
     */

    @Bean
    public Job SimpleFlowJob(){
        return new JobBuilder("simpleFlowJob", jobRepository)
                .start(simpleFlowstep1())
                    .on("COMPLETED")
                    .to(simpleFlowstep2())
                .from(simpleFlowstep1())
                    .on("FAILED")
                    .to(flow())
                .end() // Simple Flow 객체를 생성함
                .build();
    }

    @Bean
    public Flow flow(){
        return new FlowBuilder<Flow>("flow")
                .start(simpleFlowstep2())
                .on("*")
                .to(simpleFlowstep3())
                .build();
    }

    @Bean
    public Flow flow1(){
        return new FlowBuilder<Flow>("flow1")
                .start(simpleFlowstep1())
                .next(simpleFlowstep2())
                .build();
    }

    @Bean
    public Flow flow2(){
        return new FlowBuilder<Flow>("flow2")
                .start(flow3())
                .next(simpleFlowstep5())
                .next(simpleFlowstep6())
                .build();
    }

    @Bean
    public Flow flow3(){
        return new FlowBuilder<Flow>("flow3")
                .start(simpleFlowstep3())
                .on("*").to(simpleFlowstep4())
                .build();
    }

    @Bean
    public Step simpleFlowstep1(){
        return new StepBuilder("simpleFlowstep1",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1 has executed.");
//                    throw new RuntimeException("An error has occurred.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowstep2(){
        return new StepBuilder("simpleFlowstep2",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step2 has executed.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowstep3(){
        return new StepBuilder("simpleFlowstep3",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step3 has executed.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowstep4(){
        return new StepBuilder("simpleFlowstep4",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step4 has executed.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowstep5(){
        return new StepBuilder("simpleFlowstep5",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step5 has executed.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowstep6(){
        return new StepBuilder("simpleFlowstep6",jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step6 has executed.");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

}
