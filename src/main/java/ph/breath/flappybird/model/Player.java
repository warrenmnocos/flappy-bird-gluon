/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.breath.flappybird.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java8.util.Objects;

/**
 *
 * @author Warren Nocos
 */
public class Player implements Comparable<Player>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected final transient PropertyChangeSupport propertyChangeSupport;
    
    protected String personName;
    
    protected int score;
    
    public Player() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        propertyChangeSupport.firePropertyChange("personName", this.personName, personName);
        this.personName = personName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        propertyChangeSupport.firePropertyChange("score", this.score, score);
        this.score = score;
    }

    @Override
    public int compareTo(Player otherPlayer) {
        return personName.compareTo(otherPlayer.personName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.personName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        return Objects.equals(this.personName, other.personName);
    }
    
    @Override
    public String toString() {
        return personName;
    }
    
}
