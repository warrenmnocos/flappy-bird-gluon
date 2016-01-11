/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java.util.ArrayList;
import java8.util.Objects;
import java8.util.Spliterators;
import java8.util.function.Supplier;
import java8.util.stream.StreamSupport;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
public class CloudPane extends StackPane {

    protected final TwoPipePane twoPipePane;
    
    protected final Supplier<Cloud> cloudSupplier;

    protected final Supplier<Cloud> localCloudSupplier;

    protected final ObjectProperty<Cloud> latestCloudProperty;

    protected final ObservableList<Cloud> cloudList;

    protected final DoubleProperty spacingProperty, cloudTransitionSpeed;

    @Inject
    public CloudPane(
            TwoPipePane twoPipePane,
            @Named("cloudSupplier") Supplier<Cloud> cloudSupplier) {
        this.twoPipePane = twoPipePane;
        this.cloudSupplier = cloudSupplier;
        spacingProperty = new SimpleDoubleProperty(80D);
        cloudTransitionSpeed = new SimpleDoubleProperty(0.7D);
        cloudList = FXCollections.observableList(new ArrayList<>());
        localCloudSupplier = () -> {
            Cloud newCloud = StreamSupport.parallelStream(cloudList)
                    .filter(cloud -> !getChildren().contains(cloud))
                    .findAny()
                    .orElseGet(() -> {
                        Cloud cloud = cloudSupplier.get();
                        cloudList.add(cloud);
                        return cloud;
                    });
            newCloud.setTranslateX(newCloud.getWidth());
            return newCloud;
        };
        latestCloudProperty = new SimpleObjectProperty<>();
        init();
    }

    @PostConstruct
    private void init() {
        setAlignment(Pos.TOP_RIGHT);
        setPadding(new Insets(0, -50, 0, -50));
    }

    public void moveClouds() {
        if (latestCloudProperty.get() == null) {
            Cloud cloud = localCloudSupplier.get();
            getChildren().add(cloud);
            cloud.relocateRandomly();
            latestCloudProperty.set(cloud);
        }
        if (spacingProperty.get()
                <= Math.abs(latestCloudProperty.get().getTranslateX())) {
            Cloud cloud = localCloudSupplier.get();
            getChildren().add(cloud);
            cloud.relocateRandomly();
            latestCloudProperty.set(cloud);
        }
        StreamSupport.stream(cloudList)
                .filter(getChildren()::contains)
                .peek(cloud -> cloud.setTranslateX(
                        cloud.getTranslateX() - cloudTransitionSpeed.get()))
                .filter(cloud -> Objects.equals(
                        Math.abs(cloud.getTranslateX()),
                        getBoundsInLocal().getMaxX()))
                .forEach(getChildren()::remove);
    }
    
    public DoubleProperty cloudTransitionSpeedProperty() {
        return cloudTransitionSpeed;
    }
    
    public double getCloudTransitionSpeed() {
        return cloudTransitionSpeed.get();
    }
    
    public void setCloudTransitionSpeed(double cloudTransitionSpeed) {
        this.cloudTransitionSpeed.set(cloudTransitionSpeed);
    }

}
