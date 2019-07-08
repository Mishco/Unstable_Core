package com.mishco.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import static com.mishco.helper.Util.FULL_REACTIVITY;
import static com.mishco.helper.Util.MAX_CORE_POWER;

@Slf4j
public class Core implements Runnable {

    private final BlockingQueue<Double> temperatureQueue;
    private final BlockingQueue<Double> powerQueue;
    private Random random;

    private Double temperatureInCore;
    private Double reactivity;
    private Double actualPower;
    private Boolean running = true;
    private Boolean forceStop;
    private Set<ControlRods> controlRodsInCore;

    private Double inputPower;
    private Double outputPower; // should be the same as actualPower


    private boolean accumulation;
    private boolean reducing;

    public Core(BlockingQueue<Double> temperatureQueue, BlockingQueue<Double> powerQueue) {
        this.temperatureQueue = temperatureQueue;
        this.powerQueue = powerQueue;
        this.temperatureInCore = (double) new Random().nextInt(MAX_CORE_POWER - 1) + 1;
        this.reactivity = FULL_REACTIVITY / 2.0;   // reactivity from 0 to 100 %, due to set the rods
        this.actualPower = (double) new Random().nextInt(MAX_CORE_POWER - 1) + 1;
        this.accumulation = true;
        this.reducing = false;
        this.forceStop = false;
        this.random = new Random();
        this.controlRodsInCore = new HashSet<>();
    }

    public void runOneStep() {
        try {
            reaction();
        } catch (InterruptedException e) {
            //log.warn();
            System.err.println("Temperatur producer interrupted: " + e.getLocalizedMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void run() {
        while (running) {
            try {
                reaction();
            } catch (InterruptedException e) {
                running = false;
                System.err.println("Temperatur producer interrupted: " + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Simulation of reaction in Core
     *
     * @throws InterruptedException
     */
    private void reaction() throws InterruptedException {
        Double increase = (1) * random.nextDouble();
        //Double increase = (double) (new Random().nextInt(1)) + 1; // between 0 and 1
        if (reactivity == FULL_REACTIVITY / 2) {
            increase = 0.0;
            accumulation = false;
            reducing = false;
        } else if (reactivity > FULL_REACTIVITY / 2) {
            accumulation = true;
            reducing = false;
            increase++;
        } else if (reactivity < FULL_REACTIVITY / 2) {
            accumulation = false;
            reducing = true;
        } else {
            throw new IllegalStateException("Unexpected value: " + reactivity);
        }

        if (accumulation) {
            temperatureInCore += increase * (reactivity / 100);
        }

        if (reducing) {
            temperatureInCore -= increase * (reactivity / 100);
        }

        if (controlRodsInCore.isEmpty()) {
            reactivity *= 1.5; // very fast increasing
        }
        // TODO reaktiviy due to number and depth of control rods


        temperatureQueue.put(temperatureInCore);

        actualPower += increase;
        powerQueue.put(actualPower);
    }

    public Double getTemperatureInCore() {
        return temperatureInCore;
    }

    public Double getReactivity() {
        return reactivity;
    }

    public void addToSetOfControlRods(ControlRods controlRods) {
        controlRodsInCore.add(controlRods);
    }

    public void removeFromSetOfControlRods(int hashCodeOfControlRod) {
        for (ControlRods actControlRods : controlRodsInCore) {
            if (actControlRods.getId() == hashCodeOfControlRod) {
                controlRodsInCore.remove(actControlRods);
                if (controlRodsInCore.isEmpty()) {
                    System.err.println("Last item was removed");
                }
                break;
            }
        }
    }

    public Double getActualPower() {
        return actualPower;
    }

    public void setTemperatureInCore(Double temperatureInCore) {
        this.temperatureInCore = temperatureInCore;
    }

    public void setReactivity(Double reactivity) {
        if (reactivity > 0.0) {
            this.reactivity = reactivity;
        } else {
            this.reactivity = 0.0;
        }
    }


    public void setActualPower(Double actualPower) {
        this.actualPower = actualPower;
    }

    public Boolean getForceStop() {
        return forceStop;
    }

    public void setForceStop(Boolean forceStop) {
        this.forceStop = forceStop;
    }


    public Double getInputPower() {
        return inputPower;
    }

    public void setInputPower(Double inputPower) {
        this.inputPower = inputPower;
    }

    public Double getOutputPower() {
        return outputPower;
    }

    public void setOutputPower(Double outputPower) {
        this.outputPower = outputPower;
    }

    public boolean hasControlRods() {
        return !controlRodsInCore.isEmpty();
    }
}
