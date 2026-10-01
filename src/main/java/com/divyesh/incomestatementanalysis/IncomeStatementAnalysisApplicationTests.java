package com.divyesh.incomestatementanalysis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class IncomeStatementAnalysisApplicationTests {

    /**
     * Verifies that the Spring Boot application context
     * loads successfully.
     */
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // If the Spring context starts successfully,
            // this test passes.
        });
    }

}