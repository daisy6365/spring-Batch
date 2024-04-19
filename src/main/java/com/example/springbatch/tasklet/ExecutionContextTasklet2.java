package com.example.springbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet2 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        log.info("jobName : {}", jobExecutionContext.get("jobName")); // job
        log.info("stepName : {}", stepExecutionContext.get("stepName")); // null

        String stepName =  chunkContext.getStepContext().getStepExecution().getStepName();

        // StepExecution의 ExecutionContext stepName 존재 X -> 새로운 stepName 저장
        if(stepExecutionContext.get("stepName") == null){
            stepExecutionContext.put("stepName", stepName);
        }

        // step의 exit code = FAILED
//        chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED);
        // job의 exit code = STOPPED
//        contribution.setExitStatus(ExitStatus.STOPPED);

        return RepeatStatus.FINISHED;
    }
}
