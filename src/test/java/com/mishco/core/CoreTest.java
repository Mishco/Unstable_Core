package com.mishco.core;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class CoreTest {
    private final BlockingQueue<Double> temperatureQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Double> powerQueue = new ArrayBlockingQueue<>(1);

    @Test
    void runWithNoChangingTemperature() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        var tempBefore = core.getTemperatureInCore();

        // run the core for one moment
        Runnable runnable = core::run;
        core.setForceStop(true);

        assertEquals(50.0, core.getReactivity());
        assertEquals(tempBefore, core.getTemperatureInCore());
    }

    @Test
    void simulationIncreasingTemperature() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        core.setReactivity(60.0); // to higher
        var tempBefore = core.getTemperatureInCore();

        core.runOneStep();

        assertEquals(60.0, core.getReactivity());
        assertNotEquals(tempBefore, core.getTemperatureInCore());
    }

    @Test
    void simulationDecreasingTemperature() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        core.setReactivity(40.0); // to higher
        var tempBefore = core.getTemperatureInCore();

        core.runOneStep();

        assertEquals(40.0, core.getReactivity());
        assertNotEquals(tempBefore, core.getTemperatureInCore());
    }

    /**
     * For sure, because of random value
     */
    @RepeatedTest(20)
    void testIncreasingInSmallSteps() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        core.setReactivity((51.0));
        core.setTemperatureInCore((double) 400);

        core.runOneStep();

        assertEquals(51.0, core.getReactivity());
        assertTrue(core.getTemperatureInCore() > 400 && core.getTemperatureInCore() <= 401.0149);
    }

    @Test
    void testDecresingInSmallSteps() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        core.setReactivity((49.0));
        core.setTemperatureInCore((double) 400);

        core.runOneStep();

        assertEquals(49.0, core.getReactivity());
        //System.err.println(core.getTemperatureInCore());
        var actTemp = core.getTemperatureInCore();
        assertTrue(actTemp > 399 && actTemp <= 400);

    }

}














