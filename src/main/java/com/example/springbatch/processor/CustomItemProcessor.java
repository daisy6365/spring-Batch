package com.example.springbatch.processor;

import com.example.springbatch.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        // 대문자로 변경하여 전달
        customer.setName(customer.getName().toUpperCase());
        return customer;
    }
}
