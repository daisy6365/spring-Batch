package com.example.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class ScopeStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        // scopeStep2가 실행되기 전, 리스터를 통해 name2에 해당하는 parmeter값을 설정
        stepExecution.getExecutionContext().putString("name2", "user2");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // After step execution
        return null;
    }
}
