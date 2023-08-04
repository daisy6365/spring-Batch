package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * 스프링 배치가 작동하기 위해 선언
 * 스프링 부트 배치의 자동 설정 클래스 실행
 * -> Bean으로 등록된 모든 Job을 검색해서 초기화 및 수행
 */
@EnableBatchProcessing
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}
