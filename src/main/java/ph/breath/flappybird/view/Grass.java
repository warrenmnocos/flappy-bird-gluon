/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.Random;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.CacheHint;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Polygon;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Warren Nocos
 */
public class Grass extends Polygon {

    protected final LinearGradient grassPaint;

    protected final Random random;

    protected final DoubleProperty grassHeight, grassWidth, grassLength;
    
    protected final IntegerProperty grassLeaf;
    
    @Inject
    public Grass(
            @Named("grassPaint") LinearGradient grassPaint,
            Random random) {
        this.random = random;
        this.grassPaint = grassPaint;
        grassHeight = new SimpleDoubleProperty(30);
        grassWidth = new SimpleDoubleProperty();
        grassLength = new SimpleDoubleProperty(12);
        grassLeaf = new SimpleIntegerProperty();
        init();
    }

    @PostConstruct
    private void init() {
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setFill(grassPaint);
        setRotate(180);
    }

    public DoubleProperty grassHeightProperty() {
        return grassHeight;
    }

    public double getGrassHeight() {
        return grassHeight.get();
    }

    public void setGrassHeight(double height) {
        grassHeight.set(height);
    }

    public DoubleProperty grassWidthProperty() {
        return grassWidth;
    }

    public double getGrassWidth() {
        return grassWidth.get();
    }

    public void setGrassWidth(double grassWidth) {
        this.grassWidth.set(grassWidth);
    }
    
    public DoubleProperty grassLengthProperty() {
        return grassLength;
    }

    public double getGrassLength() {
        return grassLength.get();
    }

    public void setGrassLength(double grassLength) {
        this.grassLength.set(grassLength);
    }

    public void addLeaf(int leafCount) {
        double width = grassWidth.get();
        double height = grassHeight.get();
        double threeQuartersHeight = height * 1.75;
        for (int index = 1; index <= leafCount; index++) {
            getPoints().addAll(
                    width, 0D,
                    width, random.nextInt((int) threeQuartersHeight) * 1.0,
                    width + 6, random.nextInt((int) height) * 1.0,
                    width + (6 * 2), random.nextInt((int) threeQuartersHeight) * 1.0,
                    width + (6 * 2), 0D,
                    width, 0D);
            width = width + grassLength.get();
            grassWidth.set(width);
            grassLeaf.set(grassLeaf.get() + 1);
        }
    }

    public void addLeaf(double width) {
        if (grassWidth.get() < width) {
            addLeaf((int) (width / grassLength.get() + 1));
        }
    }

}
