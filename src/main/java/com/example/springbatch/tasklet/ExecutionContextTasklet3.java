package com.example.springbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet3 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");

        log.info("name : {}", name); // null

        // JobExecution의 ExecutionContext name -> null
        // name 저장
        if(name == null){
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name", "user1");
            // 저장후 예외를 터뜨린다면? -> 재시도 후 통과
//            throw new RuntimeException("step3 was failed.");
        }

        return RepeatStatus.FINISHED;
    }
}
