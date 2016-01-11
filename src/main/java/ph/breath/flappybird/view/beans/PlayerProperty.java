/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.view.beans;

import java.beans.PropertyChangeListener;
import java8.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ph.breath.flappybird.model.Player;

/**
 *
 * @author Warren Nocos
 */
public class PlayerProperty extends SimpleObjectProperty<Player> {
    
    protected final StringProperty personName;
    
    protected final IntegerProperty score;
    
    protected final PropertyChangeListener propertyChangeListener;
    
    public PlayerProperty(Player player) {
        super(player);
        personName = new SimpleStringProperty(this, "personName");
        personName.addListener((observable, oldValue, newValue) -> {
            if (! Objects.equals(oldValue, newValue)) {
                get().setPersonName(newValue);
            }
        });
        score = new SimpleIntegerProperty(this, "score");
        score.addListener((observable, oldValue, newValue) -> {
            if (! Objects.equals(oldValue, newValue)) {
                get().setScore(newValue.intValue());
            }
        });
        propertyChangeListener = event -> {
            if (event.getPropertyName().equals("personName")) {
                if (Objects.equals(get().getPersonName(), event.getNewValue())) {
                    personName.set((String) event.getNewValue());
                }
            } else if (event.getPropertyName().equals("score")) {
                if (Objects.equals(get().getPersonName(), event.getNewValue())) {
                    score.set((int) event.getNewValue());
                }
            }
        };
    }

    @Override
    public void set(Player player) {
        super.set(player);
        if (Objects.nonNull(player)) {
            player.addPropertyChangeListener(propertyChangeListener);
        }
    }

    public void setPersonName(String value) {
        personName.set(value);
    }

    public String getPersonName() {
        return personName.get();
    }

    public StringProperty personNameProperty() {
        return personName;
    }

    public void setScore(Integer value) {
        score.set(value);
    }

    public Integer getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
    
}
