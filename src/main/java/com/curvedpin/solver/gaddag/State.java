package com.curvedpin.solver.gaddag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ben on 3/17/17.
 */
public class State {
    public static final String BREAK = "^";
    public static final String EOW = "$";
    public static final String ROOT = " ";
    private final String letter;
    Map<String, State> children = new HashMap<>();

    public State(String ch) {
        this.letter = ch;
    }

    /**
     * Add a child state unless we already have a mapping for this letter to state.
     * Return either the state that was just added, the state that already existed, or null if we're at the end of
     * the word
     *
     * @param ch
     * @return
     */
    public State addChild(String ch) {

        State retVal = null;
        if(children.keySet().contains(ch)) {
            retVal = children.get(ch);
        } else {
            retVal = ch.equals(EOW) ? null : new State(ch);
            children.put(ch, retVal);
        }
        return retVal;
    }

    public State addChild(String ch, State state) {
        if(children.keySet().contains(ch)) {
            state = children.get(ch);
        } else {
            children.put(ch, state);
        }
        return state;
    }

    public String getLetter() {
        return letter;
    }

    public boolean containsChild(String ch) {
        return children.containsKey(ch);
    }

    public State getChildState(String ch) {
        return children.get(ch);
    }

    public Map<String, State> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return String.format("State{%s -> %s}", letter, children.keySet());
    }
}
