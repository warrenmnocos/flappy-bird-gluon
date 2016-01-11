/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class ScoreBoard extends Group {
    
    protected final IntegerProperty score;
    
    protected final Text scoreText;
    
    public ScoreBoard() {
        score = new SimpleIntegerProperty(-1);
        scoreText = new Text();
        init();
    }
    
    @PostConstruct
    private void init() {
        scoreText.setFill(Color.GREEN);
        scoreText.setFont(Font.font(scoreText.getFont().getFamily(), 
                FontWeight.BOLD, 30));
        score.addListener((observable, oldValue, newValue) -> {
            scoreText.setText(newValue.toString());
        });
        score.set(0);
        getChildren().addAll(scoreText);
    }
    
    public IntegerProperty scoreProperty() {
        return score;
    }
    
    public int getScore() {
        return score.get();
    }
    
    public void setScore(int score) {
        this.score.set(score);
    }
    
}
