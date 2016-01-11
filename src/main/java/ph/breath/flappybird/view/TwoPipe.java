/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.Random;
import java8.util.function.Consumer;
import java8.util.function.Supplier;
import java8.util.stream.RefStreams;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Warren Nocos
 */
public class TwoPipe extends Group {

    protected final Random random;

    protected final LinearGradient pipePaint;

    protected final Consumer<Number> resize;

    protected final Rectangle topHead, bottomHead, topBody, bottomBody;

    protected final DoubleProperty pipeWidth, pipeHeadWidth,
            pipeHeadHeight, pipeGap, heightPercentage;

    protected final Supplier<Double> heightPercentageSupplier;

    @Inject
    public TwoPipe(
            @Named("pipePaint") LinearGradient pipePaint,
            Random random) {
        this.random = random;
        this.pipePaint = pipePaint;
        pipeWidth = new SimpleDoubleProperty(60);
        pipeHeadWidth = new SimpleDoubleProperty(70);
        pipeHeadHeight = new SimpleDoubleProperty(25);
        pipeGap = new SimpleDoubleProperty(130);
        heightPercentageSupplier = () -> {
            double value = 0;
            while (value < 0.1 || value > 0.9) {
                value = random.nextDouble();
            }
            return value;
        };
        heightPercentage = new SimpleDoubleProperty();
        topBody = new Rectangle();
        topHead = new Rectangle();
        bottomHead = new Rectangle();
        bottomBody = new Rectangle();
        resize = newHeightValue -> {
            double sceneHeight = newHeightValue.doubleValue();
            double pipeGapHeight = pipeGap.doubleValue();
            double pipeHeadHeightCombined = pipeHeadHeight.doubleValue();
            double usableHeight = sceneHeight - (pipeGapHeight + pipeHeadHeightCombined);
            double topBodyHeight = usableHeight * heightPercentage.get();
            double bottomBodyHeight = usableHeight - topBodyHeight;

            topBody.setHeight(topBodyHeight);
            bottomBody.setHeight(bottomBodyHeight);
        };
        init();
    }

    @PostConstruct
    private void init() {
        setCache(true);
        setCacheHint(CacheHint.SPEED);

        topBody.widthProperty().bind(pipeWidth);
        topBody.setFill(pipePaint);
        topBody.setStroke(pipePaint);
        topBody.setStrokeWidth(1);

        topHead.widthProperty().bind(pipeHeadWidth);
        topHead.heightProperty().bind(pipeHeadHeight);
        topHead.setArcHeight(10);
        topHead.setArcWidth(10);
        topHead.setFill(pipePaint);
        topHead.setStroke(pipePaint);
        topHead.setStrokeWidth(1);
        topHead.translateXProperty().bind(topBody.translateXProperty()
                .subtract(pipeHeadWidth.subtract(pipeWidth)).divide(2));
        topHead.translateYProperty().bind(topBody.heightProperty());

        bottomHead.widthProperty().bind(pipeHeadWidth);
        bottomHead.heightProperty().bind(pipeHeadHeight);
        bottomHead.setArcHeight(10);
        bottomHead.setArcWidth(10);
        bottomHead.setFill(pipePaint);
        bottomHead.setStroke(pipePaint);
        bottomHead.setStrokeWidth(1);
        bottomHead.translateXProperty().bind(topHead.translateXProperty());
        bottomHead.translateYProperty().bind(topHead.translateYProperty()
                .add(pipeGap).add(bottomHead.heightProperty()));

        bottomBody.widthProperty().bind(pipeWidth);
        bottomBody.setFill(pipePaint);
        bottomBody.setStroke(pipePaint);
        bottomBody.setStrokeWidth(1);
        bottomBody.translateXProperty().bind(topBody.translateXProperty());
        bottomBody.translateYProperty().bind(bottomHead.translateYProperty());

        getChildren().addAll(topBody, topHead, bottomHead, bottomBody);

        heightPercentage.set(heightPercentageSupplier.get());
        ChangeListener<Number> sceneHeightListener = (observable, oldValue, newValue)
                -> resize.accept(newValue);
        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.heightProperty().addListener(sceneHeightListener);
                resize.accept(newValue.getHeight());
                if (oldValue != null) {
                    oldValue.heightProperty().removeListener(sceneHeightListener);
                }
            }
        });
    }

    public Rectangle getTopHead() {
        return topHead;
    }

    public Rectangle getBottomHead() {
        return bottomHead;
    }

    public Rectangle getTopBody() {
        return topBody;
    }

    public Rectangle getBottomBody() {
        return bottomBody;
    }

    public double getPipeHeadWidth() {
        return pipeHeadWidth.get();
    }

    public void resizeRandomly() {
        heightPercentage.set(heightPercentageSupplier.get());
        resize.accept(getScene().getHeight());
    }

    public boolean hasCollidedWith(Shape shape) {
        return RefStreams.of(topBody, topHead, bottomBody, bottomHead)
                .filter(pipePart -> {
                    Shape intersect = Shape.intersect(pipePart, shape);
                    return intersect.getBoundsInLocal().getWidth() != -1;
                })
                .findAny()
                .isPresent();
    }

}
