/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.configuration;

import java.util.Random;
import java8.util.function.Supplier;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.codejargon.feather.Feather;
import org.codejargon.feather.Provides;
import ph.breath.flappybird.view.Cloud;
import ph.breath.flappybird.view.Grass;
import ph.breath.flappybird.view.RootPane;
import ph.breath.flappybird.view.TwoPipe;

/**
 *
 * @author Warren Nocos
 */
public class ApplicationConfiguration {

    @Inject
    protected Feather feather;

    public Feather getFeather() {
        return feather;
    }

    public void setFeather(Feather feather) {
        this.feather = feather;
    }

    @Provides
    @Singleton
    @Named("cloudSupplier")
    public Supplier<Cloud> getCloudSupplier() {
        return () -> feather.instance(Cloud.class);
    }

    @Provides
    @Singleton
    @Named("grassSupplier")
    public Supplier<Grass> getGrassSupplier() {
        return () -> feather.instance(Grass.class);
    }

    @Provides
    @Singleton
    @Named("twoPipeSupplier")
    public Supplier<TwoPipe> getTwoPipeSupplier() {
        return () -> feather.instance(TwoPipe.class);
    }

    @Provides
    @Singleton
    @Named("cloudPaint")
    public LinearGradient getCloudPaint() {
        return new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.5, Color.WHITESMOKE),
                new Stop(1, Color.LIGHTGRAY));
    }

    @Provides
    @Singleton
    @Named("grassPaint")
    public LinearGradient getGrassPaint() {
        return new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.GREEN),
                new Stop(1, Color.YELLOWGREEN));
    }

    @Provides
    @Singleton
    @Named("pipePaint")
    public LinearGradient getPipePaint() {
        return new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOWGREEN),
                new Stop(1, Color.GREEN));
    }

    @Provides
    @Singleton
    public Random getRandom() {
        return new Random();
    }

    @Provides
    @Singleton
    @Inject
    public Scene getScene(RootPane rootPane) {
        return new Scene(rootPane, 500, 660);
    }

}
