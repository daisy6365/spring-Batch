package com.example.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

/**
 * ChunkOrientedTasklet
 * Tasklet의 구현체
 * TaskletStep에 의해 반복적으로 수행
 * Spring Batch가 chunk 기반의 프로세스를 처리할 수 있도록 구현함
 * chunksize 만큼 Transition 안에서 처리
 *
 * [ItemReader]
 * 1. provider() 호출
 * 2. read()에서 input data를 읽음
 * 3. chunksize를 기준으로 하나의 item 단위를 생성
 * 4. 하나의 item을 Chunk<I>에 add()
 * 5. Chunk<I>를 반환
 * ChunkProvider.provide() : ItemReader.read()를 Chunk Size 만큼 반복
 *
 * [ItemProcessor]
 * 1. transform() 호출
 * 2. doProcess()에서 item 가공
 * 3. 가공이 완료된 item을 Chunk<O>에 add()
 * 4. Chunk<O> 반환
 * ItemProcessor.process() : ItemReader가 전달한 Item 개수 만큼 반복
 *
 * [ItemWriter]
 * 1. write() 호출
 * 2. doWrite에서 최종적으로 DB에 데이터를 처리
 * ItemWriter.write(items) : 가공된 Items를 일괄 처리
 *
 * [Transition]
 * chunk단위로 맨 마지막에 itemWriter에게 전달 되면, Chunk단위로 commit & rollback.
 * -> 하나의 chunk마다 새롭게 생성되어 처리
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChunkConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job chunkJob(){
        return new JobBuilder("chunkJob", jobRepository)
                .start(chunkStep1())
                .next(chunkStep2())
                .build();
    }

    @Bean
    public Step chunkStep1(){
        /**
         * Chunk기반의 프로세싱을 Step에서 구성
         * chunk() api 사용
         */
        return new StepBuilder("chunkStep1", jobRepository)
                // input, output, generic Type 을 설정함
                // chunksize는 commit 간격을 뜻함. chunksize기준으로 reader~writer 반복
                .<String, String> chunk(2, platformTransactionManager)
                // 3단계의 Sync 기반 프로세싱 처리
                // reader, processor, writer를 설정
                /**
                 * ChunkOrientedTasklet
                 * Buffer를 통해 input 데이터를 담아놓음 -> 이후에 삭제함
                 * Chunk<I> inputs = (Chunk)chunkContext.getAttribute("INPUTS");
                 * chunkContext.removeAttribute("INPUTS");
                 *
                 * 예외가 발생한다면 ?
                 * 다시 데이터를 읽지 않고, 버퍼에 담아 놓았던 데이터를 가지고옴
                 */
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        // 하나의 String 타입 아이템이 처리되도록 parameter가 전달 됨
                        Thread.sleep(300); // 300 밀리초 주기로 아이템 전달 받음
                        log.info("ItemProcessor item = " + item);

                        // String 타입의 아이템을 최종적으로 반환해야 함
                        return "my_" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    /**
                     * @param chunk
                     * ItemWriter는 ItemProcessor와 달리 하나의 item이 아닌 item list가 전달 됨
                     */
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        Thread.sleep(300);
                        log.info("ItemWriter items = " + chunk);
                    }
                })
               .build();
    }

    @Bean
    public Step chunkStep2(){
        return new StepBuilder("chunkStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Your chunk processing logic goes here
                    return null;
                }, platformTransactionManager)
               .build();
    }
}
