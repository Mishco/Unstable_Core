package com.mishco.core;

import java.util.*;
import java.util.concurrent.BlockingQueue;

import static com.mishco.helper.Util.FULL_REACTIVITY;
import static com.mishco.helper.Util.MAX_CORE_POWER;

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

    private boolean accumulation;
    private boolean reducing;

    public Core(BlockingQueue<Double> temperatureQueue, BlockingQueue<Double> powerQueue) {
        this.temperatureQueue = temperatureQueue;
        this.powerQueue = powerQueue;
        this.temperatureInCore = (double) new Random().nextInt(MAX_CORE_POWER - 1) + 1;
        this.reactivity = Double.valueOf(FULL_REACTIVITY / 2);   //(double) new Random().nextInt(FULL_REACTIVITY) + 1; // reactivity from 0 to 100 %, due to set the rods
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
            System.err.println("Temperatur producer interrupted: " + e.getLocalizedMessage());
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
            //increase = -increase;
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
        //Optional<ControlRods> cr = controlRodsInCore.stream().filter(controlRods1 -> controlRods1.getId() == hashCodeOfControlRod).findAny();

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


    public boolean hasControlRods() {
        return !controlRodsInCore.isEmpty();
    }
}
