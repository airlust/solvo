package com.curvedpin.solver.gaddag;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ben on 3/17/17.
 */
public class GADDAG {

    State initialState = new State(State.ROOT);

    public GADDAG() {
        this(s -> true);
    }

    public GADDAG(Predicate<String> wordFilter) {

//        Reader r = new InputStreamReader(ClassLoader.getSystemResourceAsStream("enable1.txt"), StandardCharsets.UTF_8);
        Reader r = new InputStreamReader(getClass().getResourceAsStream("/enable1.txt"), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(r);

        bufferedReader.lines().filter(wordFilter).forEach(word -> {

            List<State> prevState = new ArrayList<>();
            for(int i = 1 ; i <= word.length(); i++) {

                String prefix = word.substring(0,i);
                String suffix = "";

                if(i != word.length()) suffix = word.substring(i, word.length());

                String addWord = new StringBuffer(prefix).reverse().toString() + State.BREAK + suffix + State.EOW;

                State currentState = initialState;
                boolean breakFound = false;
                int j = 0;

                for (String ch : addWord.split("")) {

                    if (breakFound && prevState.size() > j) {
                        currentState.addChild(ch, prevState.get(j));
                        break;
                    }

                    currentState = currentState.addChild(ch);

                    if (prevState.size() == j) {
                        prevState.add(currentState);
                    }

                    if (ch.equals(State.BREAK)) {
                        breakFound = true;
                    }
                    j++;
                }
            }
        });
    }
    public State getRootState() {
        return initialState;
    }

    /**
     * Created by ben on 3/17/17.
     */
    public static class State {
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
}
