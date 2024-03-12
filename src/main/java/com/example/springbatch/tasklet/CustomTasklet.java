package com.example.springbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
public class CustomTasklet implements Tasklet {
    /**
     * 다른 객체를 DI 하거나, Bean 생성가능
     * BUT, Bean의 기능을 사용하지 않을 것같으면, 그냥 일반 객체로 사용가능
     *
     * @param contribution mutable state to be passed back to update the current step
     * execution
     * @param chunkContext attributes shared between invocations but not between restarts
     * @return
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Step 실행 -> StepExecution 생성
        // StepExecution이 모두 정상적으로 완료 => JobExecution 정상 완료
        // JobExecution : StepExecution = 1 : M

        log.info("step1 was executed.");

        JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
        log.info("name = {}", jobParameters.getString("name"));
        log.info("seq = {}", jobParameters.getLong("seq"));
        log.info("date = {}", jobParameters.getDate("date"));
        log.info("age = {}", jobParameters.getDouble("age"));


        return RepeatStatus.FINISHED;

    }
}
