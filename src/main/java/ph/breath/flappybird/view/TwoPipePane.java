/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.ArrayList;
import java8.util.Objects;
import java8.util.function.Consumer;
import java8.util.function.Supplier;
import java8.util.stream.StreamSupport;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class TwoPipePane extends StackPane {

    protected final Grass grass;

    protected final Bird bird;

    protected final Supplier<TwoPipe> twoPipeSupplier;

    protected final Supplier<TwoPipe> pipeSupplier;

    protected final ObjectProperty<TwoPipe> latestPipesProperty;

    protected final ObservableList<TwoPipe> pipeList;

    protected final DoubleProperty spacingProperty;

    protected final TranslateTransition soarBirdTransition, fallBirdTransition;

    protected final RotateTransition soarBirdRotation, fallBirdRotation;

    protected final ReadOnlyBooleanWrapper playable;

    protected final DoubleProperty pipeTransitionSpeed;

    protected final ReadOnlyObjectWrapper<TwoPipe> approachingTwoPipe;

    @Inject
    public TwoPipePane(Grass grass, Bird bird,
            @Named("twoPipeSupplier") Supplier<TwoPipe> twoPipeSupplier) {
        this.grass = grass;
        this.bird = bird;
        this.twoPipeSupplier = twoPipeSupplier;
        playable = new ReadOnlyBooleanWrapper(false);
        spacingProperty = new SimpleDoubleProperty(150D);
        pipeTransitionSpeed = new SimpleDoubleProperty(6D);
        pipeList = FXCollections.observableList(new ArrayList<>());
        pipeSupplier = () -> {
            TwoPipe newPipe = StreamSupport.parallelStream(pipeList)
                    .filter(pipe -> !getChildren().contains(pipe))
                    .findAny()
                    .orElseGet(() -> {
                        TwoPipe pipe = twoPipeSupplier.get();
                        pipeList.add(pipe);
                        return pipe;
                    });
            newPipe.setTranslateX(newPipe.getPipeHeadWidth());
            return newPipe;
        };
        latestPipesProperty = new SimpleObjectProperty<>();
        soarBirdTransition = new TranslateTransition(Duration.millis(225));
        fallBirdTransition = new TranslateTransition(Duration.millis(300));
        soarBirdRotation = new RotateTransition(Duration.millis(225));
        fallBirdRotation = new RotateTransition(Duration.millis(500));
        approachingTwoPipe = new ReadOnlyObjectWrapper<>();
        init();
    }

    @PostConstruct
    private void init() {
        setAlignment(Pos.TOP_RIGHT);
        TwoPipePane.setAlignment(bird, Pos.CENTER_LEFT);
        TwoPipePane.setMargin(bird, new Insets(0, 0, 0, 50));
        getChildren().addAll(bird);

        /**
         * Let the bird fall if not playable
         */
        playable.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                fallBirdTransition.stop();
                fallBirdRotation.stop();
                soarBirdRotation.stop();
                soarBirdRotation.stop();
                fallBirdTransition.play();
                fallBirdRotation.play();
                latestPipesProperty.set(null);
                approachingTwoPipe.set(null);
            }
        });

        /**
         * Check collision on edges
         */
        bird.translateYProperty().addListener((observable, oldValue, newValue) -> {
            double birdTranslateY = newValue.doubleValue();
            double screenYBounds = getScene().getHeight() / 2;
            /**
             * Set playable to false if bird is at bottom Do not let the bird
             * fly higher than the scene height
             */
            if (birdTranslateY >= screenYBounds) {
                playable.set(false);
                approachingTwoPipe.set(null);
            } else if (birdTranslateY <= screenYBounds * -1) {
                soarBirdTransition.stop();
                bird.setTranslateY((screenYBounds * -1) + bird.getBirdBody().getRadius());
                fallBirdTransition.stop();
                fallBirdTransition.play();
            }
        });

        /**
         * Bird manipulation settings
         */
        fallBirdTransition.setNode(bird);
        fallBirdTransition.setInterpolator(Interpolator.EASE_IN);
        fallBirdTransition.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, Status.RUNNING)) {
                bird.fall();
            }
        });
        fallBirdRotation.setNode(bird);
        fallBirdRotation.setInterpolator(Interpolator.EASE_IN);
        fallBirdRotation.setToAngle(85);

        soarBirdTransition.setNode(bird);
        soarBirdTransition.setInterpolator(Interpolator.LINEAR);
        soarBirdTransition.setByY(-65);
        soarBirdTransition.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, Status.RUNNING)) {
                bird.fly();
            }
        });
        soarBirdTransition.setOnFinished(event -> {
            fallBirdTransition.stop();
            fallBirdTransition.play();
        });
        soarBirdRotation.setNode(bird);
        soarBirdRotation.setInterpolator(Interpolator.LINEAR);
        soarBirdRotation.setToAngle(-20);
        soarBirdRotation.setOnFinished(event -> {
            fallBirdRotation.stop();
            fallBirdRotation.play();
        });

        /**
         * Other events
         */
        Consumer<Number> autoSize = newWidthValue -> {
            double sceneWidth = newWidthValue.doubleValue();
            if (sceneWidth > 0) {
                grass.addLeaf(sceneWidth);
            }
        };
        ChangeListener<Number> sceneWidthListener = (observable, oldValue, newValue)
                -> autoSize.accept(newValue);
        ChangeListener<Number> sceneHeightListener = (observable, oldValue, newValue) -> {
            double sceneHeight = newValue.doubleValue();
            fallBirdTransition.setByY(sceneHeight);
            fallBirdTransition.setDuration(Duration.millis(sceneHeight * 3.25));
        };
        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.widthProperty().addListener(sceneWidthListener);
                newValue.heightProperty().addListener(sceneHeightListener);
                autoSize.accept(newValue.getWidth());
                if (oldValue != null) {
                    oldValue.widthProperty().removeListener(sceneWidthListener);
                }
            }
        });

    }

    public void playGame() {
        if (playable.get()) {
            /**
             * Check pipe collision
             */
            TwoPipe approachingTwoPipeValue = this.approachingTwoPipe.get();
            if (Objects.nonNull(approachingTwoPipeValue)
                    && approachingTwoPipeValue.hasCollidedWith(bird.getBirdBody())) {
                playable.set(false);
                return;
            }

            /**
             * Populate pane with pipes
             */
            if (Objects.isNull(latestPipesProperty.get())) {
                TwoPipe pipe = pipeSupplier.get();
                getChildren().add(pipe);
                pipe.resizeRandomly();
                latestPipesProperty.set(pipe);
                approachingTwoPipe.set(pipe);
            }
            if (spacingProperty.get() <= Math.abs(latestPipesProperty.get().getTranslateX())) {
                TwoPipe pipe = pipeSupplier.get();
                getChildren().add(pipe);
                pipe.resizeRandomly();
                latestPipesProperty.set(pipe);
            }

            /**
             * Relocate pipes
             */
            StreamSupport.stream(pipeList)
                    .filter(getChildren()::contains)
                    .forEach(pipe -> pipe.setTranslateX(
                            pipe.getTranslateX() - pipeTransitionSpeed.get()));

            /**
             * Remove pipes
             */
            if (Objects.nonNull(approachingTwoPipeValue)) {
                int indexOfApproachingTwoPipe = getChildren().indexOf(approachingTwoPipeValue);
                if (Objects.equals(Math.abs(approachingTwoPipeValue.getTranslateX()),
                        getBoundsInLocal().getMaxX())) {
                    getChildren().remove(approachingTwoPipeValue);
                } else if (Math.abs(approachingTwoPipeValue.getTranslateX())
                        >= getScene().getWidth() - bird.getLayoutX()) {
                    approachingTwoPipe.set((TwoPipe) getChildren().get(indexOfApproachingTwoPipe + 1));
                }
            }
        }
    }

    public void replay() {
        if (!playable.get()) {
            bird.setTranslateY(0);
            StreamSupport.stream(pipeList)
                    .forEach(getChildren()::remove);
            playable.set(true);
        }
    }

    public void soarBird() {
        if (playable.get()) {
            fallBirdTransition.stop();
            soarBirdTransition.stop();
            soarBirdTransition.play();
            soarBirdRotation.stop();
            soarBirdRotation.play();
        }
    }

    public void fallBird() {
        soarBirdTransition.stop();
        fallBirdTransition.play();
    }

    public DoubleProperty pipeTransitionSpeedProperty() {
        return pipeTransitionSpeed;
    }

    public double getPipeTransitionSpeed() {
        return pipeTransitionSpeed.get();
    }

    public void setPipeTransitionSpeed(double pipeTransitionSpeed) {
        this.pipeTransitionSpeed.set(pipeTransitionSpeed);
    }

    public ReadOnlyBooleanProperty playableProperty() {
        return playable.getReadOnlyProperty();
    }

    public boolean isPlayable() {
        return playable.get();
    }

    public ReadOnlyObjectProperty<TwoPipe> approachingTwoPipeProperty() {
        return approachingTwoPipe.getReadOnlyProperty();
    }

    public final DoubleProperty bySoarProperty() {
        return soarBirdTransition.byYProperty();
    }

    public double getBySoar() {
        return soarBirdTransition.byYProperty().get();
    }

    public void setBySoar(double bySoar) {
        soarBirdTransition.byYProperty().set(bySoar);
    }

    public final DoubleProperty byFallProperty() {
        return fallBirdTransition.byYProperty();
    }

    public double getByFall() {
        return fallBirdTransition.byYProperty().get();
    }

    public void setByFall(double byFall) {
        fallBirdTransition.byYProperty().set(byFall);
    }

}
