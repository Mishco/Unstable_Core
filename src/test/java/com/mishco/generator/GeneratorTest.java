package com.mishco.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {

    private static double CAPACITY_CORE = 1000.0;
    private Generator generator;
    private Double inputPower = 2000.0; // kW


    @BeforeEach
    public void setupGeneratorToBasicParameters() {
        generator = Generator.builder()
                .amountOfWaterPerSecond(11.8)
                .capacityReactor(CAPACITY_CORE)
                .capacityWater(10000.0)
                .temperatureIncomingWater(12.0)
                .temperatureOutComingWater(100.0)
                .build();
    }

    @Test
    public void test_createSimpleGenerator() {
        assertEquals(generator.getAmountOfWaterPerSecond(), 11.8);
        assertEquals(generator.getCapacityReactor(), CAPACITY_CORE);
        assertTrue(generator.getCapacityWater() >= CAPACITY_CORE);
        assertEquals(generator.getTemperatureIncomingWater(), 12.0);
        assertEquals(generator.getTemperatureOutComingWater(), 100.0);
    }

    @Test
    public void testConnectionBetweenCoreAndGenerator() {

    }

}