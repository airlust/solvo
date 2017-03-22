package com.curvedpin.solver.gaddag;

/**
 * Created by ben on 3/17/17.
 */
public class Arc {

    private State startState;
    private State endState;
    private Character character;

    public Arc(State startState, Character character, State endState) {
        this.startState = startState;
        this.character = character;
        this.endState = endState;
    }
}
