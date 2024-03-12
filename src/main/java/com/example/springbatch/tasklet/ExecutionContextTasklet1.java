package com.example.springbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet1 implements Tasklet {
    /**
     * step1 에서 JobName과 StepName을 저장
     * 타 step에서 영향을 미치는지 확인하기 위함
     *
     * @param contribution mutable state to be passed back to update the current step
     * execution
     * @param chunkContext attributes shared between invocations but not between restarts
     * @return
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // * ExeutionContext
        // framework에서 유지 및 관리하는 키/값으로 된 컬렉션
        // JobExecution & StepExecution의 상태를 저장하는 공유 객체
        // Job : JobExecution에 저장. step간 공유 가능
        // Step : StepExecution에 저장. step간 공유 불가능

        ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

        log.info("jobName : {}", jobExecutionContext.get("jobName")); // null
        log.info("stepName : {}", stepExecutionContext.get("stepName")); // null

        String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName();
        String stepName = chunkContext.getStepContext().getStepExecution().getStepName();

        // JobExecution의 ExecutionContext JobName 저장
        if((jobExecutionContext.get("jobName")) == null){
            jobExecutionContext.put("jobName", jobName);
        }

        // StepExecution의 ExecutionContext StepName 저장
        if(stepExecutionContext.get("stepName") == null){
            stepExecutionContext.put("stepName", stepName);
        }

        return RepeatStatus.FINISHED;
    }
}
