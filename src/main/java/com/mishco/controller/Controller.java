package com.mishco.controller;

import com.mishco.core.ControlRods;
import com.mishco.core.Core;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.mishco.helper.Util.MAX_CORE_TEMPERATUR;


public class Controller {
    private static final String RED_BAR = "red-bar";
    private static final String YELLOW_BAR = "yellow-bar";
    private static final String ORANGE_BAR = "orange-bar";
    private static final String GREEN_BAR = "green-bar";
    private static final String[] barColorStyleClasses = {RED_BAR, ORANGE_BAR, YELLOW_BAR, GREEN_BAR};
    private static final double CHANGE = 2.0;


    @FXML
    private Label labelValTemperature;

    @FXML
    private Label labelValPower;

    @FXML
    private Label labelValControlRods;

    @FXML
    private TextArea console = new TextArea();

    @FXML
    private ProgressBar progressBarTemperature;

    @FXML
    private Rectangle controlRod1;

    @FXML
    private Rectangle controlRod2;

    @FXML
    private Rectangle controlRod3;

    @FXML
    private Rectangle fuelUraniumID2;

    @FXML
    private Rectangle fuelUraniumID7;

    @FXML
    private Rectangle fuelUraniumID12;

    @FXML
    private LineChart<Number, Number> lineChart;

    private final BlockingQueue<Double> temperatureQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Double> powerQueue = new ArrayBlockingQueue<>(1);
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private Core core = new Core(temperatureQueue, powerQueue);

    private ControlRods firstControlRod = new ControlRods(1, 0.0);
    private ControlRods secondControlRod = new ControlRods(2, 0.0);
    private ControlRods thirdControlRod = new ControlRods(3, 0.0);

    @FXML
    public void initialize() {
        setProgressBarForTemperature();

        Thread thread = new Thread(core);
        thread.setDaemon(true);
        thread.start();

        labelValPower.setText(core.getGenerator().getOutputPower().toString());
        labelValTemperature.setText(core.getTemperatureInCore().toString());
        labelValControlRods.setText(core.getReactivity().toString());

        // Control Rods region
        animateControlRodsInCore();

        // Line Chart region
        lineChart.setTitle("Temperature");
        series.setName("Temperature in core");

        // Animation region
        animationTimerTemperature(temperatureQueue);
        animationTimerPower(powerQueue);
    }

    private void animateControlRodsInCore() {
        var startingReactivity = core.getReactivity();
        var singleElementReactivity = startingReactivity / 3;

        controlRod1.setY(controlRod1.getY() + singleElementReactivity);
        controlRod2.setY(controlRod2.getY() + singleElementReactivity);
        controlRod3.setY(controlRod3.getY() + singleElementReactivity);
        core.addToSetOfControlRods(firstControlRod);
        core.addToSetOfControlRods(secondControlRod);
        core.addToSetOfControlRods(thirdControlRod);

        // If force stop, push all rods down
        final LongProperty lastUpdate = new SimpleLongProperty();
        final long minUpdateInterval = 100000000L; // nanoseconds. Set to higher number to slow output.
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    if (core.getForceStop()) {
                        onActionDownControlRods1();
                        onActionDownControlRods2();
                        onActionDownControlRods3();
                        //  if all rods are full in core, stop this thread !
                        if (firstControlRod.getPercentInCore() == 100.0 && secondControlRod.getPercentInCore() == 100.0 && thirdControlRod.getPercentInCore() == 100.0) {
                            this.stop();
                            console.appendText("All control rods are in core");
                        }
                    }

                    lastUpdate.set(now);
                }
            }
        };
        timer.start();
    }


    @FXML
    public void onActionDownControlRods1() {
        if (controlRod1.getY() + CHANGE > fuelUraniumID2.getHeight()) {
            console.appendText("Rod 1 is full in core\n");
            firstControlRod.setPercentInCore(100.0);
        } else {
            controlRod1.setY(controlRod1.getY() + CHANGE);
            Shape intersect = Shape.intersect(controlRod1, fuelUraniumID2);
            updateReactivityInCore(intersect, fuelUraniumID2, firstControlRod);
        }
    }

    @FXML
    public void onActionUpControlRods1() {
        controlRod1.setY(controlRod1.getY() - CHANGE);
        Shape intersect = Shape.intersect(controlRod1, fuelUraniumID2);
        updateReactivityInCore(intersect, fuelUraniumID2, firstControlRod);
    }

    @FXML
    public void onActionDownControlRods2() {
        if (controlRod2.getY() + CHANGE > fuelUraniumID7.getHeight()) {
            console.appendText("Rod 2 is full in core\n");
            secondControlRod.setPercentInCore(100.0);
        } else {
            controlRod2.setY(controlRod2.getY() + CHANGE);
            Shape intersect = Shape.intersect(controlRod2, fuelUraniumID7);
            updateReactivityInCore(intersect, fuelUraniumID7, secondControlRod);
        }
    }

    @FXML
    public void onActionUpControlRods2() {
        controlRod2.setY(controlRod2.getY() - CHANGE);
        Shape intersect = Shape.intersect(controlRod2, fuelUraniumID7);
        updateReactivityInCore(intersect, fuelUraniumID7, secondControlRod);
    }

    @FXML
    public void onActionUpControlRods3() {
        controlRod3.setY(controlRod3.getY() - CHANGE);
        Shape intersect = Shape.intersect(controlRod3, fuelUraniumID12);
        updateReactivityInCore(intersect, fuelUraniumID12, thirdControlRod);
    }

    @FXML
    public void onActionDownControlRods3() {
        if (controlRod3.getY() + CHANGE > fuelUraniumID12.getHeight()) {
            console.appendText("Rod 3 is full in core\n");
            thirdControlRod.setPercentInCore(100.0);
        } else {
            controlRod3.setY(controlRod3.getY() + CHANGE);
            Shape intersect = Shape.intersect(controlRod3, fuelUraniumID12);
            updateReactivityInCore(intersect, fuelUraniumID12, thirdControlRod);
        }
    }

    private void updateReactivityInCore(Shape intersect, Rectangle fuel, ControlRods controlRods) {
        if (intersect.getBoundsInLocal().getHeight() != -1) {
            double percentRodInFuel = (intersect.getBoundsInLocal().getHeight() / fuel.getHeight());
            core.setReactivity(core.getReactivity() - percentRodInFuel);
            core.addToSetOfControlRods(controlRods);
            console.appendText(String.format("Control rods {%.2f} %% in core and reactivity {%.2f} %% %n", percentRodInFuel, core.getReactivity()));
        } else {
            core.removeFromSetOfControlRods(controlRods.getId());
            console.appendText(String.format("No Control rods in core, reactivity {%.2f} %% %n", core.getReactivity()));
        }
    }


    @FXML
    public void onForceStopClicked() {
        core.setForceStop(true);
        console.appendText("Force stop core");
    }

    private void animationTimerPower(BlockingQueue<Double> powerQueue) {
        final LongProperty lastUpdate = new SimpleLongProperty();
        final long minUpdateInterval = 2000000000L; // nanoseconds. Set to higher number to slow output.
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    final Double actPower = powerQueue.poll();
                    if (actPower != null) {
                        labelValPower.setText(actPower.toString());
                    }
                    lastUpdate.set(now);
                }
            }
        };
        timer.start();
    }

    private void animationTimerTemperature(BlockingQueue<Double> temperatureQueue) {
        final LongProperty lastUpdate = new SimpleLongProperty();
        final long[] id = {0};
        final long minUpdateInterval = 1000000000L; // nanoseconds. Set to higher number to slow output.
        lineChart.getData().add(series);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    final Double actTemp = temperatureQueue.poll();
                    final Double actPower = powerQueue.poll();
                    final Double actReactivity = core.getReactivity();
                    if (actTemp != null && actPower != null) {
                        console.appendText(String.format("Temperature: {%.2f} Power: {%.2f} Reactivity: {%.2f} %% %n ", actTemp, actPower, actReactivity));
                        labelValTemperature.setText(actTemp.toString());
                        labelValPower.setText(actPower.toString());
                        progressBarTemperature.setProgress(actTemp / MAX_CORE_TEMPERATUR);

                        Platform.runLater(() -> series.getData().add(new XYChart.Data<>(id[0]++, actTemp)));
                    }
                    lastUpdate.set(now);
                }
            }
        };
        timer.start();
    }

    private void setProgressBarForTemperature() {
        progressBarTemperature.progressProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double progress = newValue == null ? 0 : newValue.doubleValue();
                if (progress < 0.3) {
                    setBarStyleClass(progressBarTemperature, GREEN_BAR);
                } else if (progress < 0.5) {
                    setBarStyleClass(progressBarTemperature, YELLOW_BAR);
                } else if (progress < 0.85) {
                    setBarStyleClass(progressBarTemperature, ORANGE_BAR);
                } else {
                    setBarStyleClass(progressBarTemperature, RED_BAR);
                }
            }

            private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
                bar.getStyleClass().removeAll(barColorStyleClasses);
                bar.getStyleClass().add(barStyleClass);
            }
        });
        progressBarTemperature.setProgress(0);
    }


}
