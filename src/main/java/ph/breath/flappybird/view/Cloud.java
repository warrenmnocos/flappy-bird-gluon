/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.Random;
import java8.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Warren Nocos
 */
public class Cloud extends Path {

    protected final Random random;

    protected final LinearGradient cloudPaint;

    protected final DoubleProperty width;

    @Inject
    public Cloud(
            @Named("cloudPaint") LinearGradient cloudPaint,
            Random random) {
        this.random = random;
        this.cloudPaint = cloudPaint;
        width = new SimpleDoubleProperty(60);
        init();
    }

    @PostConstruct
    private void init() {
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setFill(cloudPaint);
        getElements().addAll(
                new MoveTo(0, 0),
                new QuadCurveTo(-17, -5, 0, -15),
                new QuadCurveTo(-7.5, -37.5, 15, -30),
                new QuadCurveTo(30, -55, 45, -30),
                new QuadCurveTo(67.5, -37.5, 60, -15),
                new QuadCurveTo(77, -5, 60, 0),
                new ClosePath());
        setStroke(cloudPaint);
        setScaleX(1.6);
        setScaleY(1.6);

        scaleXProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, oldValue)) {
                width.set(79 * newValue.doubleValue());
            }
        });
    }

    public void relocateRandomly() {
        Scene scene = getScene();
        if (Objects.nonNull(scene)) {
            double sceneHeight = scene.getHeight();
            setTranslateY(random.nextDouble() * sceneHeight);
        }
    }

    public double getWidth() {
        return width.doubleValue();
    }

}
