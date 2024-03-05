package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@RequiredArgsConstructor
public class JobInstanceConfiguration {

//    @Bean
//    public Job job(){
//        return new JobBuilder("job")
//                .start(step1())
//                .next(step2())
//                .build();
//    }
//
//    @Bean
//    public Step step1(){
//        return new StepBuilder("step1")
//                .tasklet(((contribution, chunkContext) -> {
//                            contribution.setExitStatus(ExitStatus.COMPLETED);
//
//                            return RepeatStatus.FINISHED;
//                }
//                )).build();
//    }
//
//    @Bean
//    public Step step2(){
//        return new StepBuilder("step2")
//                .tasklet(((contribution, chunkContext) -> {
//                    contribution.setExitStatus(ExitStatus.COMPLETED);
//
//                    return RepeatStatus.FINISHED;
//                }
//                )).build();
//    }
}
