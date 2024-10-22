package com.example.springbatch.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;

import java.util.List;

@Slf4j
public class CustomItemStreamReader implements ItemStreamReader<String> {
    /**
     * 생성자에서 실제 아이템들을 가지고 옴
     */
    private final List<String> items;
    private int index = -1;
    private boolean restart = false; // 재시작 여부

    public CustomItemStreamReader(List<String> items){
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String item = null;

        if(this.index < this.items.size()){
            item = this.items.get(this.index);
            this.index++;
        }

        /**
         * reader 과정에서 실패 할 예정임
         * 6번째 item을 읽을때 실패
         * 만약 예외가 터지고나서 다시 서버를 실행한다면
         * index == 6 && restart == true 이므로 예외가 터지지 않음
         */
        if(this.index == 6 && !restart){
            throw new RuntimeException("Restart is required.");
        }

        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // 초기화 하는 작업
        // 이미 실행된 작업 내역에서 (executionContext) index라는 키값이 있다면
        // 해당 값을 꺼내오고 재시작하도록 초기화 -> restart = true
        if(executionContext.containsKey("index")){
            this.index = executionContext.getInt("index");
            this.restart = true;
        }
        // 맨 처음 시작할 경우는 재시작이 아니라
        // index를 초기화 해야함
        else{ // 초기에는 당연히 저장된 index key 값이 없으므로 초기화
            this.index = 0;
            executionContext.put("index", this.index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // 매번 chunksize 만큼의 데이터를 읽어서 itemwriter에게 전달
        // 현재 상태 정보를 저장 -> Job이 재시작 될 때, 가장 마지막으로 재적된 값을 가지고 옴
        executionContext.put("index", this.index);
    }

    @Override
    public void close() throws ItemStreamException {
        // 예외가 발생해서 Job이 실패
        // resource를 해제하거나 초기 작업들을 해제
        log.info("close");
    }

}
