package com.example.springbatch.config;

import com.example.springbatch.reader.CustomItemStreamReader;
import com.example.springbatch.writer.CustomItemStreamWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * [Chunk Processor 아키텍처]
 * TaskletStep : RepeatTemplate을 이용해서 ChunkOrientedTasklet 반복 실행
 * SimpleChunkProvider : RepeatTemplate을 이용해서 chunksize 만큼 반복하면서 ItemReader 실행
 * Transaction 시작
 * i) ItemReader.read() == null : 모든 반복분 및  chunk. 프로세스 종료
 * ii) ItemReader.read() != null : Chunk<I>에 Item 추가
 *
 * i) Chunk<I> Items가  == chunksize : ItemReader.read()
 * ii) Chunk<I> Items가 != SimpleChunkProcess에 Chunk<I> 전달
 *
 * ItemProcessor.process(item) 실행 -> Chunk<O>에 Item 추가
 * ItemProcessor.write(List<Item>) 실행
 * Transaction commit
 * Chunk 단위 프로세스 재실행
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemStreamConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OrderedFormContentFilter formContentFilter;

    @Bean
    public Job ItemStreamJob(){
        return new JobBuilder("itemStreamJob", jobRepository)
                .start(itemStreamStep1())
                .next(itemStreamStep2())
                .build();
    }

    @Bean
    public Step itemStreamStep1(){
        return new StepBuilder("itemStreamStep1", jobRepository)
                .<String, String >chunk(5, platformTransactionManager)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }


    public CustomItemStreamReader itemReader(){
        // Implement your item reader logic here
        List<String> items = new ArrayList<>(10);
        // Add items
        for (int i = 0; i <= 10; i++) {
            items.add(String.valueOf(i));
        }

        return new CustomItemStreamReader(items);
    }


    @Bean
    public ItemWriter<? super String> itemWriter(){
        return new CustomItemStreamWriter();
    }

    @Bean
    public Step itemStreamStep2(){
        return new StepBuilder("itemStreamStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("itemStreamStep2 has executed.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
