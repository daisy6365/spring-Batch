package com.example.springbatch.config;

import com.example.springbatch.listener.ScopeJobListener;
import com.example.springbatch.listener.ScopeStepListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobScopeAndStepScopeConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job scopeJob(){
        return new JobBuilder("scopeJob", jobRepository)
                .start(scopeStep1(null))
                .next(scopeStep2())
                .listener(new ScopeJobListener())
                .build();
    }

    @Bean
    @JobScope
    public Step scopeStep1(@Value("#{jobParameters['message']}") String message) {
        // step1이 생성되는 시점에 해당하는 parameter값이 설정됨
        log.info("scopeStep1 message = {}", message);
        return new StepBuilder("scopeStep1", jobRepository)
                .tasklet(scopeTasklet1(null), platformTransactionManager)
               .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeTasklet1(@Value("#{jobExecutionContext['name1']}") String name1) {
        // 초기화 시점에 리스너에서 설정한
        // jobEecution에 존재하는 name1의 값을 참조함
        log.info("scopeTasklet1 name1 = {}", name1);
        return (contribution, chunkContext) -> {
            log.info("scopeTasklet1 executed.");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step scopeStep2() {
        return new StepBuilder("scopeStep2", jobRepository)
                .tasklet(scopeTasklet2(null), platformTransactionManager)
                .listener(new ScopeStepListener())
               .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeTasklet2(@Value("#{stepExecutionContext['name2']}") String name2) {
        // @StepScope : Bena의 실행시점에서 표현식을 통한 값을 참조할수 있도록 제공함
        log.info("scopeTasklet2 name2 = {}", name2);
        return (contribution, chunkContext) -> {
            log.info("scopeTasklet2 executed.");
            return RepeatStatus.FINISHED;
        };
    }


}
