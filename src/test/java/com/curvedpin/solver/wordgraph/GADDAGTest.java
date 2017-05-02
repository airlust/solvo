package com.curvedpin.solver.wordgraph;

import org.junit.Test;

/**
 * Created by ben on 3/17/17.
 */
public class GADDAGTest {

    @Test
    public void gaddagLoader() {
        WordGraph filteredGADDAG = new WordGraph(s -> s.equals("help"));
        printWordGraph(filteredGADDAG.getRootNode(), 0);
    }

    void printWordGraph(WordGraph.Node node, int level) {

        if(node == null) { return;}
        String letter = (node.letter == null) ? "$" : node.letter;
        System.out.println(String.format("%" + (level+1) + "s",letter));
        for(WordGraph.Node child : node.getChildren().values()) {
            printWordGraph(child, level + 1);
        }

    }

}