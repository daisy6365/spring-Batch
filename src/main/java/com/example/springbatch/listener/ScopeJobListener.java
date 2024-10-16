package com.example.springbatch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ScopeJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // job이 실행되기 전, 리스너를 통해 name1에 해당하는 parmeter값을 설정
        jobExecution.getExecutionContext().putString("name1", "user1");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}
