package com.mishco.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class CoreObjectTest {
    private final BlockingQueue<Double> temperatureQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Double> powerQueue = new ArrayBlockingQueue<>(1);

    @Test
    void addToSetOfControlRods() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        core.removeFromSetOfControlRods(111);
        assertFalse(core.hasControlRods());
    }

    @Test
    void remoteFromSetOfControlRods() {
        var core = new Core(temperatureQueue, powerQueue);
        core.addToSetOfControlRods(new ControlRods(111, 1.0));
        assertTrue(core.hasControlRods());
    }
}