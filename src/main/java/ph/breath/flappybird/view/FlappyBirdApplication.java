/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.stage.Stage;
import javax.inject.Inject;
import org.codejargon.feather.Feather;
import ph.breath.flappybird.configuration.ApplicationConfiguration;
import ph.breath.flappybird.controller.RootPaneController;

/**
 *
 * @author Warren Nocos
 */
public class FlappyBirdApplication extends Application {

    @Inject
    protected Scene scene;

    @Inject
    protected RootPaneController rootPaneController;
    
    @Inject
    protected Bird bird;

    protected Feather feather;

    @Override
    public void init() throws Exception {
        ApplicationConfiguration applicationConfiguration;
        applicationConfiguration = new ApplicationConfiguration();
        feather = Feather.with(applicationConfiguration);
        applicationConfiguration.setFeather(feather);
        feather.injectFields(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setTitle("Just Another Flappy Bird Game");
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(bird.snapshot(new SnapshotParameters(), null));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String... args) {
        Application.launch(FlappyBirdApplication.class, args);
    }

}
