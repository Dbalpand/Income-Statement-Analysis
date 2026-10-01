package com.divyesh.incomestatementanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IncomeStatementAnalysisApplication {

	public static void main(String[] args) {

        SpringApplication.run(IncomeStatementAnalysisApplication.class, args);
	}

}
