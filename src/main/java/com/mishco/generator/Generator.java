package com.mishco.generator;

import lombok.Builder;
import lombok.Data;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.mishco.helper.Util.*;

@Builder
@Data
public class Generator {
    private final BlockingQueue<Double> powerQueue;

    private Double amountOfWaterPerSecond;
    private Double outputPower; // outputPower
    private Double inputPower;

    public Generator() {
        this.powerQueue = new ArrayBlockingQueue<>(1);
    }

    public Generator(BlockingQueue<Double> powerQueue) {
        this.outputPower = (double) new Random().nextInt(MAX_CORE_POWER - 1) + 1;
        this.powerQueue = powerQueue;
    }

    public Generator(BlockingQueue<Double> powerQueue, Double amountOfWaterPerSecond, Double actualPower, Double inputPower) {
        this.powerQueue = powerQueue;
        this.amountOfWaterPerSecond = amountOfWaterPerSecond;
        this.outputPower = actualPower;
        this.inputPower = inputPower;
    }


    public void recalculatePower(Double increase) throws InterruptedException {
        // how long take the heat the water
        /* Měrná tepelná kapacita vody - vzorec

           Jednotkové odvození přepočtu měrné tepelné kapacity z J na Wh
           Jednotkové odvození přepočtu měrné tepelné kapacity z J na Wh - vzorec
         */

        // https://vytapeni.tzb-info.cz/tabulky-a-vypocty/97-vypocet-doby-ohrevu-teple-vody
        // hmotnost vody * merna tepelna kapacita * (teplota vystupnej vody - teplota
        double efficiency = 0.97;
        double timeForHeat = 0.2; // in hour
        double massOfWater = CAPACITY_REACTOR; // but it should be exactly the number from amountOfWaterPerSecond * time
        double specificHeatCapacityOfWater = 4186 / 3600;// fixme create constant in Util
        /*
         * Energy for heat [Watt per hour]
         */
        double needEnergy = massOfWater * specificHeatCapacityOfWater * (TEMPERATURE_OUT_COMING_WATER - TEMPERATURE_INCOMING_WATER);

        /**
         * Prikon ohrevu
         */
        this.inputPower = (1 / efficiency) * (needEnergy / timeForHeat);
        this.setOutputPower(this.getOutputPower() + increase);
        this.getPowerQueue().put(this.getOutputPower());
    }
}
