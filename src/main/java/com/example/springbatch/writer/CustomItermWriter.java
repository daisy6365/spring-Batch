package com.example.springbatch.writer;

import com.example.springbatch.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomItermWriter implements ItemWriter<Customer> {
    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        chunk.forEach(customer -> log.info(String.valueOf(customer)));
    }
}
