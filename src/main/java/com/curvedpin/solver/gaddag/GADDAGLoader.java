package com.curvedpin.solver.gaddag;

import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Created by ben on 3/17/17.
 */
public class GADDAGLoader {

    State initialState = new State(State.ROOT);

    public GADDAGLoader() {
        this(s -> true);
    }

    public GADDAGLoader(Predicate<String> wordFilter) {

        Reader r = new InputStreamReader(ClassLoader.getSystemResourceAsStream("enable1.txt"), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(r);

        //bufferedReader.lines().filter(s -> s.equals("hm")).forEach(word -> {
        bufferedReader.lines().filter(wordFilter).forEach(word -> {
//        bufferedReader.lines().forEach(word -> {
            //System.out.println(word);

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

    public Set<String> findWordsForHook(String hook, String rack ) {

        Set<String> retVal = new TreeSet<>();
        hook = new StringBuffer(hook).reverse().toString();

        searchGADDAG(initialState, retVal, "", rack.toLowerCase(), hook.toLowerCase());
        return retVal;
    }

    private void searchGADDAG(State state, Set<String> retVal, String letters, String rack, String hook) {
        if (state == null) {
            retVal.add(reconstituteWord(letters));
        } else if (!hook.isEmpty()) {
            StringBuffer sb = new StringBuffer(letters);
            sb.append(state.getLetter().equals(State.ROOT) ? "" : state.getLetter());
            State childState = state.getChildState(hook.substring(0,1));
            if(childState != null) {
                String newHook = hook.substring(1,hook.length());
                searchGADDAG(childState,retVal,sb.toString(),rack,newHook);
            }
        } else {
            final StringBuffer sb = new StringBuffer(letters);
            sb.append(state.getLetter().equals(State.ROOT) ? "" : state.getLetter());

            //FIXME - this needs chidren to be visible which is a bit broken; just being lazy
            state.children.keySet().stream().filter(s -> rack.contains(s) || s.equals(State.EOW) || s.equals(State.BREAK)).forEach(s -> {

                String newRack = (s.equals(State.BREAK) || s.equals(State.EOW)) ? rack : rack.replaceFirst(s,"");
                searchGADDAG(state.getChildState(s),retVal, sb.toString(),newRack,hook);
            } );

        }

    }

    private String reconstituteWord(String letters) {
        StringBuffer sb = new StringBuffer();
        for(int i = letters.indexOf(State.BREAK) - 1; i >= 0; i-- ) {
            sb.append(letters.charAt(i));
        }
        for(int i = letters.indexOf(State.BREAK) + 1; i < letters.length(); i++) {
            sb.append(letters.charAt(i));
        }
        return sb.toString();
    }

    public State getRootState() {
        return initialState;
    }
}
