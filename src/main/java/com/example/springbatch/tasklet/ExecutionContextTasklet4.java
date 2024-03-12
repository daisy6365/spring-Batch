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
public class ExecutionContextTasklet4 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        log.info("jobName : {}", executionContext.get("jobName")); // job
        log.info("stepName : {}", executionContext.get("stepName")); // name
        // 예외 터뜨리지 않는다면 -> user1
        // 예외 터뜨린다면 -> step4 실행되지 않고 종료
        log.info("name : {}", executionContext.get("name"));

        return RepeatStatus.FINISHED;
    }
}
