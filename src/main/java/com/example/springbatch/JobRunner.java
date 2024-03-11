package com.example.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component // JobRunner 자체가 bean으로 등록되어야 함 -> run() 메소드 호출됨
/**
 * ApplicationRunner : Application 실행 시첨에 run() 실행
 */
public class JobRunner implements ApplicationRunner {

    // Autowired : 스프링배치가 초기화 될때 이미 bean으로 생성되어있음
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // jobLauncher는 job과 jobParameter를 필요로함 -> Job 실행

        // 1 : M = Job : JonInstance
        // 처음 시작 `Job` + `JobParameters` :  새로운 `JobInstance` 생성
        // 동일한 `Job` + `JobParameters`  :  기존 `JobInstance` 반환

        // 1 : 1 = JobParameter : JobInstance
        // 하나의 Job에 존재 가능한 여러개의 JobInstance를 구분하기 위한 용도
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user1")
                .addLong("seq", 2L)
                .addDate("date", new Date())
                .addDouble("age", 16.5)
                .toJobParameters();

        // spring boot가 자동으로 배치를 실행하지 않게끔 해야함
        // 내가 원하는 대로 Job execution control
        jobLauncher.run(job, jobParameters);

    }
}
