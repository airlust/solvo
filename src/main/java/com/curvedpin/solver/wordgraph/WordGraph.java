package com.curvedpin.solver.wordgraph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ben on 3/17/17.
 */
public class WordGraph {

    Node initialNode = new Node(Node.ROOT);

    public WordGraph() {
        this(s -> true);
    }

    public WordGraph(Predicate<String> wordFilter) {

        Reader r = new InputStreamReader(getClass().getResourceAsStream("/enable1.txt"), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(r);

        bufferedReader.lines().filter(wordFilter).forEach(word -> {

            List<Node> prevNode = new ArrayList<>();
            for(int i = 1 ; i <= word.length(); i++) {

                String prefix = word.substring(0,i);
                String suffix = "";

                if(i != word.length()) suffix = word.substring(i, word.length());

                String addWord = new StringBuffer(prefix).reverse().toString() + Node.BREAK + suffix + Node.EOW;

                Node currentNode = initialNode;
                boolean breakFound = false;
                int j = 0;

                for (String ch : addWord.split("")) {

                    if (breakFound && prevNode.size() > j) {
                        currentNode.addChild(ch, prevNode.get(j));
                        break;
                    }

                    currentNode = currentNode.addChild(ch);

                    if (prevNode.size() == j) {
                        prevNode.add(currentNode);
                    }

                    if (ch.equals(Node.BREAK)) {
                        breakFound = true;
                    }
                    j++;
                }
            }
        });
    }
    public Node getRootNode() {
        return initialNode;
    }

    public static class Node {
        public static final String BREAK = "^";
        public static final String EOW = "$";
        public static final String ROOT = " ";
        private final String letter;
        Map<String, Node> children = new HashMap<>();

        public Node(String ch) {
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
        public Node addChild(String ch) {
            Node retVal;
            if(children.keySet().contains(ch)) {
                retVal = children.get(ch);
            } else {
                retVal = ch.equals(EOW) ? null : new Node(ch);
                children.put(ch, retVal);
            }
            return retVal;
        }

        public Node addChild(String ch, Node node) {
            if(children.keySet().contains(ch)) {
                node = children.get(ch);
            } else {
                children.put(ch, node);
            }
            return node;
        }

        public Node getChildNode(String ch) {
            return children.get(ch);
        }

        public Map<String, Node> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            return String.format("Node{%s -> %s}", letter, children.keySet());
        }
    }
}
