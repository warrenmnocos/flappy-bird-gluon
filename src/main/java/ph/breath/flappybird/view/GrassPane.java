/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.ArrayList;
import java8.util.Objects;
import java8.util.Spliterators;
import java8.util.function.Consumer;
import java8.util.function.Supplier;
import java8.util.stream.StreamSupport;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class GrassPane extends StackPane {

    protected final Supplier<Grass> grassSupplier;

    protected final ObservableList<Grass> grassList;

    protected final Supplier<Grass> localGassSupplier;

    protected final DoubleProperty grassTransitionSpeed;

    protected final ObjectProperty<Grass> latestGrass;

    @Inject
    public GrassPane(
            @Named("grassSupplier") Supplier<Grass> grassSupplier) {
        this.grassSupplier = grassSupplier;
        grassList = FXCollections.observableList(new ArrayList<>());
        grassTransitionSpeed = new SimpleDoubleProperty(0.75D);
        localGassSupplier = () -> {
            Grass newGrass = StreamSupport.parallelStream(grassList)
                    .filter(grass -> !getChildren().contains(grass))
                    .findAny()
                    .orElseGet(() -> {
                        Grass grass = grassSupplier.get();
                        grass.addLeaf(1);
                        grassList.add(grass);
                        return grass;
                    });
            return newGrass;
        };
        latestGrass = new SimpleObjectProperty<>();
        init();
    }

    @PostConstruct
    private void init() {
        setAlignment(Pos.BOTTOM_RIGHT);
        setPadding(new Insets(0, -80, 0, 0));

        /**
         * Other events
         */
        Consumer<Number> autoSize = newWidthValue -> {
            double sceneWidth = newWidthValue.doubleValue();
            if (sceneWidth > 0) {
                do {
                    Grass newGrass = localGassSupplier.get();
                    newGrass.setTranslateX(newGrass.getTranslateX()
                            + newGrass.getGrassWidth());
                    getChildren().add(newGrass);
                    latestGrass.set(newGrass);
                    StreamSupport.stream(grassList)
                            .filter(getChildren()::contains)
                            .forEach(grass -> {
                                grass.setTranslateX(grass.getTranslateX()
                                        - grass.getGrassWidth());
                            });
                } while (StreamSupport.parallelStream(grassList)
                        .filter(getChildren()::contains)
                        .map(grass -> grass.getGrassWidth())
                        .reduce(0.0, (addend, anotherAddend) -> 
                                addend + anotherAddend) <= sceneWidth);
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
    }

    public void moveGrasses() {
        /**
         * Populate pane with grasses
         */
        if (StreamSupport.parallelStream(getChildren())
                .noneMatch(node
                        -> Objects.equals(node.getClass(), Grass.class))) {
            Grass newGrass = localGassSupplier.get();
            newGrass.setTranslateX(newGrass.getTranslateX()
                    + newGrass.getGrassWidth());
            getChildren().add(newGrass);
            latestGrass.set(newGrass);
        }
        Grass theLatestGrass = latestGrass.get();
        if (theLatestGrass.getGrassWidth() <= Math.abs(
                theLatestGrass.getTranslateX())) {
            Grass newGrass = localGassSupplier.get();
            getChildren().add(newGrass);
            latestGrass.set(newGrass);
        }

        /**
         * Relocate grasses
         */
        StreamSupport.stream(grassList)
                .filter(getChildren()::contains)
                .peek(grass -> grass.setTranslateX(
                        grass.getTranslateX() - grassTransitionSpeed.get()))
                .filter(grass -> Objects.equals(
                        Math.abs(grass.getTranslateX()),
                        getBoundsInLocal().getMaxX()))
                .forEach(getChildren()::remove);
    }

    public DoubleProperty grassTransitionSpeedProperty() {
        return grassTransitionSpeed;
    }

    public double getGrassTransitionSpeed() {
        return grassTransitionSpeed.get();
    }

    public void setGrassTransitionSpeed(double grassTransitionSpeed) {
        this.grassTransitionSpeed.set(grassTransitionSpeed);
    }

}
