/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.controller;

import java8.util.Objects;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import ph.breath.flappybird.view.Bird;
import ph.breath.flappybird.view.ControlPane;
import ph.breath.flappybird.view.ScoreBoard;
import ph.breath.flappybird.view.TwoPipePane;

/**
 *
 * @author Warren Nocos
 */
@Singleton
public class RootPaneController {

    protected final ControlPane controlPane;
    
    protected final ScoreBoard scoreBoard;

    protected final TwoPipePane twoPipePane;

    protected final Bird bird;

    @Inject
    public RootPaneController(
            ControlPane controlPane,
            ScoreBoard scoreBoard,
            TwoPipePane twoPipePane,
            Bird bird) {
        this.controlPane = controlPane;
        this.scoreBoard = scoreBoard;
        this.twoPipePane = twoPipePane;
        this.bird = bird;
        init();
    }

    @PostConstruct
    private void init() {
        controlPane.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (Objects.equals(event.getButton() , MouseButton.PRIMARY)) {
                        play();
                    }
                });
        controlPane.addEventHandler(
                KeyEvent.KEY_TYPED,
                event -> {
                    if (Objects.equals(event.getCode(), KeyCode.UP)) {
                        play();
                    }
                });
        twoPipePane.approachingTwoPipeProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (Objects.nonNull(newValue) && Objects.nonNull(oldValue)) {
                        scoreBoard.setScore(scoreBoard.getScore() + 1);
                    }
                });
        scoreBoard.scoreProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() % 5 == 0 && newValue.intValue() <= 250) {
                twoPipePane.setPipeTransitionSpeed(
                        twoPipePane.getPipeTransitionSpeed() + 0.1);
                twoPipePane.setBySoar(twoPipePane.getBySoar() - 0.65);
                twoPipePane.setByFall(twoPipePane.getByFall() + 0.65);
            }
        });
        twoPipePane.playableProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                scoreBoard.setScore(0);
            } else {
                twoPipePane.setPipeTransitionSpeed(1);
                twoPipePane.setBySoar(-65);
                twoPipePane.setByFall(twoPipePane.getScene().getHeight());
            }
        });
    }

    public void play() {
        if (!twoPipePane.isPlayable()) {
            twoPipePane.replay();
        }
        twoPipePane.soarBird();
    }

}
