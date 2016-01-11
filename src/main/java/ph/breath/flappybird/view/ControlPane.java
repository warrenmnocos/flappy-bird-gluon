/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import java8.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class ControlPane extends StackPane {
    
    protected final ScoreBoard scoreBoard;
    
    protected Text scoreText, playText, gameOverText;
    
    protected final Rectangle playButton;
    
    @Inject
    public ControlPane(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
        playButton = new Rectangle();
        init();
    }
    
    @PostConstruct
    private void init() {
        ControlPane.setAlignment(scoreBoard, Pos.TOP_RIGHT);
        
        setPadding(new Insets(15));
        
        getChildren().addAll(scoreBoard);
        
        Consumer<Number> adjustControlHeights = newSceneHeight -> {
            double sceneHeight = newSceneHeight.doubleValue();
            if (sceneHeight > 0) {
                
            }
        }; 
        ChangeListener<Number> sceneHeightListener = (observable, oldValue, newValue) -> {
            adjustControlHeights.accept(newValue);
        };
        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.heightProperty().addListener(sceneHeightListener);
                adjustControlHeights.accept(newValue.getHeight());
                if (oldValue != null) {
                    oldValue.heightProperty().removeListener(sceneHeightListener);
                }
            }
        });
    }
    
}
