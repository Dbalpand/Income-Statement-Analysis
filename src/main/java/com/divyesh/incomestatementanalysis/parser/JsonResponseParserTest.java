package com.divyesh.incomestatementanalysis.parser;

import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.divyesh.incomestatementanalysis.model.FinancialItem;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonResponseParserTest {

    private JsonResponseParser jsonResponseParser;

    @BeforeEach
    void setUp() {

        ObjectMapper objectMapper = new ObjectMapper();

        jsonResponseParser = new JsonResponseParser(objectMapper);

    }

    @Test
    void parse_ShouldReturnIncomeStatement_WhenJsonIsValid() {

        String json = """
                {
                  "companyName":"ABC Pvt Ltd",
                  "financialYear":"2024",
                  "currency":"INR",
                  "items":[
                    {
                      "name":"Revenue",
                      "amount":100000
                    },
                    {
                      "name":"Net Profit",
                      "amount":25000
                    }
                  ]
                }
                """;

        IncomeStatement result =
                jsonResponseParser.parse(json);

        assertNotNull(result);

        assertEquals(
                "ABC Pvt Ltd",
                result.getCompanyName()
        );

        assertEquals(
                "2024",
                result.getFinancialYear()
        );

        assertEquals(
                "INR",
                result.getCurrency()
        );

        assertEquals(
                2,
                result.getItems().size()
        );

        FinancialItem revenue =
                result.getItems().get(0);

        assertEquals(
                "Revenue",
                revenue.getName()
        );

        assertEquals(
                new BigDecimal("100000"),
                revenue.getAmount()
        );

    }

    @Test
    void parse_ShouldThrowBedrockException_WhenJsonIsInvalid() {

        String invalidJson =
                "{ invalid json }";

        BedrockException exception =
                assertThrows(
                        BedrockException.class,
                        () -> jsonResponseParser.parse(invalidJson)
                );

        assertEquals(
                "Invalid JSON received from AWS Bedrock.",
                exception.getMessage()
        );

    }

    @Test
    void parse_ShouldHandleUnknownProperties() {

        String json = """
                {
                  "companyName":"ABC Pvt Ltd",
                  "financialYear":"2024",
                  "currency":"INR",
                  "unknownField":"Ignored",
                  "items":[]
                }
                """;

        IncomeStatement result =
                jsonResponseParser.parse(json);

        assertNotNull(result);

        assertEquals(
                "ABC Pvt Ltd",
                result.getCompanyName()
        );

        assertEquals(
                "2024",
                result.getFinancialYear()
        );

    }

    @Test
    void parse_ShouldReturnEmptyItems_WhenItemsArrayIsEmpty() {

        String json = """
                {
                  "companyName":"ABC Pvt Ltd",
                  "financialYear":"2024",
                  "currency":"INR",
                  "items":[]
                }
                """;

        IncomeStatement result =
                jsonResponseParser.parse(json);

        assertNotNull(result);

        assertTrue(
                result.getItems().isEmpty()
        );

    }

}