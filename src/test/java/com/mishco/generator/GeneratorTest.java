package com.mishco.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {


    private Generator generator;
    private Double inputPower = 2000.0; // kW


    @BeforeEach
    public void setupGeneratorToBasicParameters() {
        generator = Generator.builder()
                .amountOfWaterPerSecond(11.8)

                .inputPower(inputPower)
                .outputPower(0.0)
                .build();
    }

    @Test
    public void test_createSimpleGenerator() {
        assertEquals(generator.getAmountOfWaterPerSecond(), 11.8);
        assertEquals(generator.getInputPower(), inputPower);
        assertEquals(generator.getOutputPower(), 0.0);
    }

    @Test
    public void testConnectionBetweenCoreAndGenerator() {

    }

}