/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Shear;
import javafx.util.Duration;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class Bird extends Group {

    protected final Circle birdBody, birdEyeMuscle, birdEyeBall;

    protected final Group birdEye;

    protected final Path birdBeak, birdWing;

    protected final Shear wingShearing;

    protected final Timeline soarTimeline;

    public Bird() {
        birdBody = new Circle(14);
        birdEyeMuscle = new Circle(5);
        birdEyeBall = new Circle(3);
        birdEye = new Group();
        birdBeak = new Path();
        birdWing = new Path();
        wingShearing = new Shear();
        soarTimeline = new Timeline();
        init();
    }

    @PostConstruct
    private void init() {
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        
        birdBody.setFill(Color.CRIMSON);
        birdBeak.setFill(Color.DARKRED);
        birdBeak.setStroke(Color.DARKRED);
        birdEyeMuscle.setFill(Color.YELLOW);
        birdEyeBall.setFill(Color.BLACK);
        birdWing.setFill(Color.PINK);
        birdWing.setStroke(Color.WHEAT);

        birdBeak.getElements().addAll(
                new MoveTo(0, 0),
                new LineTo(6, 3.5),
                new LineTo(0, 7),
                new MoveTo(0, 0));

        birdWing.getElements().addAll(
                new MoveTo(0, 2),
                new QuadCurveTo(1, 0, 0, -2),
                new QuadCurveTo(8, 2, 2, 1),
                new QuadCurveTo(2, 3, 0, 2));
        birdWing.setRotate(5);
        birdWing.setScaleX(0.8);
        birdWing.setScaleY(0.8);
        birdWing.setScaleZ(0.8);

        birdBeak.translateXProperty().bind(birdBody.radiusProperty());
        birdBeak.translateYProperty().bind(birdBody.translateYProperty().subtract(3.5));

        birdEye.translateXProperty().bind(birdBody.translateXProperty()
                .add(birdBody.radiusProperty()
                        .multiply(0.25)));
        birdEye.translateYProperty().bind(birdBody.translateYProperty()
                .subtract(birdBody.radiusProperty()
                        .subtract(birdEyeMuscle.radiusProperty()
                                .multiply(1.5))));

        wingShearing.setX(-3);

        BooleanProperty wingMoveUp = new SimpleBooleanProperty(true);
        soarTimeline.getKeyFrames().addAll(new KeyFrame(
                        Duration.millis(10),
                        event -> {
                            double min = -2.6;
                            double currentY = wingShearing.getY();
                            if (wingMoveUp.get() && currentY > min) {
                                wingShearing.setY(currentY - 0.3);
                            } else if (currentY <= min) {
                                wingMoveUp.set(false);
                            }
                        }),
                new KeyFrame(
                        Duration.millis(10),
                        event -> {
                            double max = 1;
                            double currentY = wingShearing.getY();
                            if (wingMoveUp.get() == false && currentY < max) {
                                wingShearing.setY(currentY + 0.3);
                            } else if (currentY >= max) {
                                wingMoveUp.set(true);
                            }
                        }));
        soarTimeline.setCycleCount(Animation.INDEFINITE);

        birdWing.translateXProperty().bind(birdBody.translateYProperty()
                .subtract(birdBody.radiusProperty().divide(1.5)));
        birdWing.translateYProperty().bind(birdBody.translateYProperty()
                .add(birdBody.radiusProperty().multiply(0.2)));
        birdWing.getTransforms().add(wingShearing);

        birdEye.getChildren().addAll(birdEyeMuscle, birdEyeBall);

        getChildren().addAll(birdBeak, birdBody, birdEye, birdWing);
    }

    public Circle getBirdBody() {
        return birdBody;
    }

    public Path getBirdBeak() {
        return birdBeak;
    }

    public boolean hasCollidedWith(Shape shape) {
        Shape intersect = Shape.intersect(birdBody, shape);
        return intersect.getBoundsInLocal().getWidth() != -1;
    }
    
    public void fly() {
        soarTimeline.play();
    }
    
    public void fall() {
        soarTimeline.stop();
    }

}
