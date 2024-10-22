package com.example.springbatch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

/**
 * Spring Batch가 각각의 itemStream 타입의 구현체를 동시에 호출해 주는 지 확인하기 위한 로그
 */
@Slf4j
public class CustomItemStreamWriter implements ItemStreamWriter<String> {

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(item -> log.info(item));
    }

    // ItemStreamReader와 동시에 ItemStreamWriter도 open
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter open");
    }

    /**
     * chunksize만큼 실행되고 나서 계속 update실행
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter update");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("CustomItemStreamWriter close");
    }
}
