package com.example.springbatch.config;

import com.example.springbatch.model.Customer;
import com.example.springbatch.processor.CustomItemProcessor;
import com.example.springbatch.reader.CustomItemReader;
import com.example.springbatch.writer.CustomItermWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChunkItemConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job chunkItemJob(){
        return new JobBuilder("chunkItemJob", jobRepository)
                .start(chunkItemStep1())
                .next(chunkItemStep2())
               .build();
    }

    @Bean
    public Step chunkItemStep1() {
        return new StepBuilder("chunkItemStep1", jobRepository)
                .<Customer, Customer>chunk(3, platformTransactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWiter())
               .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new CustomItemReader(Arrays.asList(new Customer("user1"),
                new Customer("user2"),
                new Customer("user3"),
                new Customer("user4"),
                new Customer("user5")));
    }

    @Bean
    public ItemProcessor<? super Customer, ? extends Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<? super Customer> itemWiter() {
        return new CustomItermWriter();
    }

    @Bean
    public Step chunkItemStep2() {
        return new StepBuilder("chunkItemStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("chunkItemStep2 has executed.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
