package com.example.springbatch.config;

import com.example.springbatch.simplejob.CustomJobParametersIncrementer;
import com.example.springbatch.tasklet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Map;


/**
 * BatchAutoConfigurationd
 * -> BatchProperties에 Job의 Bean 이름 갖고있음
 * -> JobLauncherApplicationRunner를 생성
 * -> ApplicationArguments에 우리가 실행하라고 설정한 Job의 이름 담겨 있음 (JobArguments)
 *      -> Arguments에 따라 Job 동시에 여러개 실행 가능
 * -> JobLauncherApplicationRunner에서 Job Launch
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    /**
     * Batch 작업 중의 정보를 저장하는 저장소 역할
     * Job 실행 및 결과에 관련된 모든 meta data를 저장 -> 기본 : SimpleJobRepository
     * JobLauncher, Job, Step 구현체 내부에서 CRUD기능 처리
     * 내부적으로 Transaction처리 해줌 -> @Transaction 불필요
     */
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager; //
    private final JobRepositoryListener jobRepositoryListener;

    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

    @Bean
    public Job job(){
        // Job 객체 생성
        /**
         * Job
         * -> SimplJob(step들을 가지고 실행)
         * -> BatchAutoConfiguration
         */
        return new JobBuilder("job", jobRepository)
                // DB를 초기화 하지 않고 재시작 할 수있도록 Job Param의 id를 증가 시킴
                // 기존의 JobParameter 변경 없이 Job을 여러번 시작하고자 할 때
                // run.id : incrementer api를 호출하면서 생긴 run.id
//                .incrementer(new CustomJobParametersIncrementer())
                .incrementer(new RunIdIncrementer())
                // SimpleJobBuilder 생성 후 반환
                .start(step1()) // 최소 1개 이상의 step 구성 // 실패시 next Step은 실행되지 않음
                // 순차적으로 연결하도록 설정. 여러번 설정이 가능함
                .next(step2()) // step 호출
                // job 실행에 꼭 필요한 Parameter를 검증하는 용도
//                .validator(new CustomJobParmetersValidator())
                // requiredKeys (필수값), optionalKeys(선택값) - 자동검증
//                .validator(new DefaultJobParametersValidator(new String[]{"name", "date"}, new String[]{"count"}))
                // job의 재시작 여부를 설정
                // default : false -> 재시작 불가능
//                .preventRestart()
//                .next(step3())
                .build();
    }


    @Bean
    public Job job2(){
        return new JobBuilder("job2", jobRepository)
                .start(step3())
                .next(step4())
                .build();
    }

    @Bean Job parentJob(){
        // 1. parentJob 실행
        //    - param : date
        // 2. jobStep 실행
        // 3. childJob 실행 -> 독립적으로 Job 저장 됨
        //    - param : date, name (added)
        // 4. step3 실행
        // 5. step4 실행
        // 6. step2 실행

        // childJob이 실행 도중 실패한다면
        // 다음으로 넘어가지 못하고 parentJob의 결과도 실패
        return new JobBuilder(("parentJob"), jobRepository)
                .start(jobStep(null))
                .next(step2())
                .build();
    }




    @Bean
    public Step step1(){
        // Step 객체 생성
        /**
         * Job
         * -> SimplJob(step들을 가지고 실행)
         * -> AbstracJob
         * -> stepHandler 에서 step 실행
         */
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1 has executed");
//                    throw new RuntimeException("step1 was failed.");
                    // Batch status와 exit status가 통일하게 가지 않음
                    // SimplJob
                    // 마지막 step - ExitStatus -> Job 최종 ExitStatus로 변경
                    // FlowJob
                    // 마지막 flow - FlowExecutionStatus -> Job 최종 ExitStatus로 변경
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }),platformTransactionManager)
                // tasklet 방식 호출
                // Tasklet 객체 생성
//                .tasklet(executionContextTasklet1, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step2(){
        return new StepBuilder("step2", jobRepository)
                .<String, String>chunk(3, platformTransactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return item.toUpperCase();
                    }
                })
                .writer(new ItemStreamWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.forEach(log::info);
                    }
                })
                .build();
//                .tasklet((contribution, chunkContext) -> {
////                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//
////                    log.info("jobParameters = {}", jobParameters.toString());
//                    // jobParameters = {date=Mon Mar 11 14:57:41 KST 2024, name=user1, seq=2, age=16.5}
//                    log.info("step2 is executed.");
//                    contribution.setExitStatus(ExitStatus.COMPLETED);
//
////                    throw new RuntimeException("step2 has failed"); // FAILED를 위해 일부러 throw
//                    return RepeatStatus.FINISHED;
//
//                    /**
//                     * step이 모두 정상적으로 종료 -> JOB status : [COMPLETED]
//                     * [COMPLETE] : JobInstance 실행 불가 → JobExecution 생성 X => 재실행 불가
//                     * [FAILED] : JobInstance 실행 가능 → JobExecution 생성 O => 재실행 가능
//                     */
//                }, platformTransactionManager).build();
    }

    @Bean
    public Step step3(){
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step3 has executed");
                    // step3은 성공으로 끝났기 때문에, 재시작 하면
                    // 실행되지 않고, 데이터 재적이 되지 않음
//                    throw new RuntimeException("step3 was failed");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                // 성공 여부와 상관없이 항상 Step을 실행하기 위한 설정
//                .allowStartIfComplete(true)
//                .tasklet(executionContextTasklet3, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step4(){
        return new StepBuilder("step4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
//                    throw new RuntimeException("step4 was failed");
                    log.info("step4 has executed");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                // 시작 횟수를 초과한다면 ?
                // StartLimitExceededException: Maximum start limit exceeded for step: step4StartMax: 3 에러 발생
                .startLimit(3)
//                .tasklet(executionContextTasklet4, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step5(){
        return new StepBuilder("step5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("step5 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step6(){
        return new StepBuilder("step6", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("step6 was executed.");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    public Step jobStep(JobLauncher jobLauncher) {
        return new StepBuilder("jobStep",jobRepository)
                .job(childJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                        stepExecution.getExecutionContext().putString("name", "user1");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .build();
    }

    @Bean
    public Job childJob() {
        return new JobBuilder("childJob", jobRepository)
                .start(step3())
                .next(step4())
                .build();
    }

    private DefaultJobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        // sqlContext에 저장된 Key 중에서 name을 찾아서 가져옴
        extractor.setKeys(new String[]{"name"});

        return extractor;
    }

}
