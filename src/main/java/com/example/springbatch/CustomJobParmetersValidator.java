package com.example.springbatch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParmetersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if(parameters.getString("name") == null){
            // job 실행 중 parameter를 검증 하다가 예외
            // job을 생성하지도 못했기 때문에 META_DB에 저장이 안됨
            throw new JobParametersInvalidException("name parameters is not found");
        }
    }
}
