package com.example.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class PassCheckingListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution){

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution){
        String exitCode = stepExecution.getExitStatus().getExitCode();

        // AbstactStep -> TaskletStep의 부모
        // 스텝이 모두 실행이 되고 난 후, exitStatus를 다시 설정
        // COMPLETE -> PASS로 설정
        if(!exitCode.equals(ExitStatus.FAILED.getExitCode())){
            return new ExitStatus("PASS");
        }
        return null;

    }
}
