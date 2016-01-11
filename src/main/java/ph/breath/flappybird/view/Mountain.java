/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class Mountain extends Polygon {
    
    protected final DoubleProperty mountainHeight, mountainWidth;
    
    protected final LinearGradient mountainPaint;
    
    public Mountain() {
        mountainPaint = new LinearGradient(0, 0, 1, 0, true, 
                CycleMethod.NO_CYCLE, 
                new Stop(0, Color.CADETBLUE),
                new Stop(1, Color.LIGHTBLUE));
        mountainHeight = new SimpleDoubleProperty(300);
        mountainWidth = new SimpleDoubleProperty();
        init();
    }
    
    @PostConstruct
    private void init() {
        setFill(mountainPaint);
        setRotate(180);
    }
    
    public DoubleProperty mountainHeightProperty() {
        return mountainHeight;
    }
    
    public double getMountainHeight() {
        return mountainHeight.get();
    }
    
    public void setMountainHeight(double mountainHeight) {
        this.mountainHeight.set(mountainHeight);
    }
    
    public DoubleProperty mountainWidthProperty() {
        return mountainWidth;
    }
    
    public double getMountainWidth() {
        return mountainWidth.get();
    }
    
    public void setMountainWidth(double mountainWidth) {
        this.mountainWidth.set(mountainWidth);
    }
    
    public void drawMount(int mountGroup) {
        double width = mountainWidth.get();
        double height = mountainHeight.get();
        for (int index = 1; index <= mountGroup; index++) {
            getPoints().addAll(
                    width, 0D,
                    width, height,
                    width + 300, height / 2,
                    width + (300 * 2), height,
                    width + (300 * 2), 0D,
                    width, 0D);
            width = width + 600;
            mountainWidth.set(width);
        }
    }

    public void drawMount(double width) {
        if (mountainWidth.get() < width) {
            Mountain.this.drawMount((int) width / 600 + 1);
        }
    }
    
}
