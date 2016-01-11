/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java8.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class RootPane extends StackPane implements Runnable {

    protected final ControlPane controlPane;

    protected final TwoPipePane twoPipePane;

    protected final Bird bird;

    protected final CloudPane cloudPane;

    protected final Mountain mountain;

    protected final GrassPane grassPane;

    protected final LinearGradient skyPaint;

    protected final AnimationTimer gameLoop;

    @Inject
    public RootPane(
            ControlPane controlPane,
            TwoPipePane twoPipePane,
            Bird bird,
            CloudPane cloudPane,
            Mountain mountain,
            GrassPane grassPane) {
        this.controlPane = controlPane;
        this.twoPipePane = twoPipePane;
        this.bird = bird;
        this.cloudPane = cloudPane;
        this.mountain = mountain;
        this.grassPane = grassPane;
        this.skyPaint = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.LIGHTBLUE),
                new Stop(1, Color.WHITE));
        gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                run();
            }
        };
        init();
    }

    @PostConstruct
    private void init() {
        /**
         * View settings
         */
        RootPane.setAlignment(mountain, Pos.BOTTOM_LEFT);
        RootPane.setAlignment(grassPane, Pos.BOTTOM_LEFT);
        setBackground(new Background(new BackgroundFill(
                skyPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().addAll(cloudPane, mountain, twoPipePane,
                grassPane, controlPane);

        Consumer<Number> autoSize = newWidthValue -> {
            double sceneWidth = newWidthValue.doubleValue();
            if (sceneWidth > 0) {
                mountain.drawMount(sceneWidth);
            }
        };
        ChangeListener<Number> sceneWidthListener = (observable, oldValue, newValue)
                -> autoSize.accept(newValue);
        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.widthProperty().addListener(sceneWidthListener);
                autoSize.accept(newValue.getWidth());
                if (oldValue != null) {
                    oldValue.widthProperty().removeListener(sceneWidthListener);
                }
            }
        });

        /**
         * Synchronize speed
         */
        twoPipePane.pipeTransitionSpeedProperty()
                .bindBidirectional(grassPane.grassTransitionSpeed);
        twoPipePane.setPipeTransitionSpeed(1D);
        cloudPane.setCloudTransitionSpeed(0.05D);

        /**
         * Game loop, approximately 60 frames per second
         */
        gameLoop.start();
    }

    @Override
    public void run() {
        cloudPane.moveClouds();
        if (twoPipePane.isPlayable()) {
            twoPipePane.playGame();
            grassPane.moveGrasses();
        }
    }

}
