package com.example.springbatch.reader;

import com.example.springbatch.model.Customer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class CustomItemReader implements ItemReader<Customer> {

    private List<Customer> customerList;

    public CustomItemReader(List<Customer> customerList) {
        this.customerList = new ArrayList<>(customerList);
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (!customerList.isEmpty()) {
            // 하나씩 읽을 때마다 데이터가 줄도록
            return customerList.remove(0);
        }

        return null;
    }
}
