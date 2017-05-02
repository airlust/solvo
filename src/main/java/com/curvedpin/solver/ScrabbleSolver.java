package com.curvedpin.solver;


import com.curvedpin.solver.wordgraph.WordGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScrabbleSolver {

    static List<Move> generateCrossSets(WWFClassicBoard board, WordGraph.Node initialNode) {

        //FIXME: We don't need to reset all the crosssets, only those that had tiles placed near them on the last round
        if(board.hasCalculatedCrossSets()) {
            board.resetAllCrossSets();
        }
        List<Move> retVal = new ArrayList<>();
        String letters = "abcdefghijklmnopqrst";

        for(String singleLetterRack: letters.split("")) {

            List<Move> moves = _wordSearch(board, singleLetterRack, initialNode, false);
            for(Move m: moves) {
                for (Move.MoveElement e : m.getMoveElements()) {

                    if(e.tile.isAnchorTile()) {
                        switch (m.getDirection()) {
                            case RIGHT:
                                e.tile.addAccrossCrossSet(e.letter,m.getScore(false));
                                break;
                            case DOWN:
                                e.tile.addDownCrossSet(e.letter,m.getScore(false));
                                break;
                        }
                    }
                }
            }
            retVal.addAll(moves);
        }
        board.setCalculatedCrossSets();
        return retVal;
    }

    public static List<Move> wordSearch(WWFClassicBoard board, String rackLetters, WordGraph.Node initialNode) {
        generateCrossSets(board, initialNode);
        return _wordSearch(board,rackLetters, initialNode, true);
    }

    static List<Move> _wordSearch(WWFClassicBoard board, String rackLetters, WordGraph.Node initialNode, boolean crossSetFiltering) {

        Collection<TileCell> anchorCells = board.getAnchorSquares().values();
        rackLetters = rackLetters.toLowerCase();
        List<Move> candidateMoves = new ArrayList<>();

        for(TileCell currentAnchor: anchorCells) {
            if (currentAnchor.isEmpty() != true) {
                throw new IllegalStateException("Current cell isn't empty!");
            }

            //Go across and left first if we can.
            if (currentAnchor.getLeftCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialNode, rackLetters, null, WWFClassicBoard.Direction.LEFT, candidateMoves, crossSetFiltering);
            } else if (currentAnchor.getRightCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialNode, rackLetters, null, WWFClassicBoard.Direction.RIGHT, candidateMoves,crossSetFiltering);
            }

            //Go up first if we can down if not (top of the board)
            if (currentAnchor.getUpCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialNode, rackLetters, null, WWFClassicBoard.Direction.UP, candidateMoves,crossSetFiltering);
            } else if (currentAnchor.getDownCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialNode, rackLetters, null, WWFClassicBoard.Direction.DOWN, candidateMoves,crossSetFiltering);
            }
        }

        return candidateMoves;
    }

    static void findWordForAnchor(TileCell anchor, TileCell currentCell, WordGraph.Node node, String rackLetters, List<Move.MoveElement> currentMoves, WWFClassicBoard.Direction direction, List<Move> candidateMoves, boolean crossSetFiltering) {

        if (currentCell == null) {
            //We're off the board; could short circuit this before recursing into this function.
            return;
        } else if (node == null) {
            //We have a word.
            Move move = new Move(currentMoves, anchor, direction, rackLetters);
            candidateMoves.add(move);

        } else if(currentCell.isEmpty()) {
            //For each letter left on the rack to which we can navigate (from this node) AND meets the cross set constraints (if we're checking)
            //remove that letter from the rack and place it on the board.
            node.getChildren().keySet().stream().filter(s ->  rackLetters.contains(s) || s.equals(WordGraph.Node.EOW) || s.equals(WordGraph.Node.BREAK)).filter(s -> {
                if(crossSetFiltering) {
                    if (direction.equals(WWFClassicBoard.Direction.UP) || direction.equals(WWFClassicBoard.Direction.DOWN)) {
                        return currentCell.checkAcrossCrossSet(s);
                    } else {
                        return currentCell.checkDownCrossSet(s);
                    }
                }
                return true;
            }).forEach(s -> {

                //Remove letter from rack if it's not a BREAK or EOW
                String newRackLetters = (s.equals(WordGraph.Node.BREAK) || s.equals(WordGraph.Node.EOW)) ? rackLetters :  rackLetters.replaceFirst(s,"");

                WWFClassicBoard.Direction newDirection = direction;
                TileCell newCell = currentCell;

                if(s.equals(WordGraph.Node.BREAK)) {
                    //we need to switch direction (in theory we don't switch from RIGHT or DOWN so we could remove those.
                    newCell = anchor;
                    switch (direction) {
                        case LEFT:
                            newDirection = WWFClassicBoard.Direction.RIGHT;
                            break;
                        case RIGHT:
                            newDirection = WWFClassicBoard.Direction.LEFT;
                            break;
                        case UP:
                            newDirection = WWFClassicBoard.Direction.DOWN;
                            break;
                        case DOWN:
                            newDirection = WWFClassicBoard.Direction.UP;
                            break;
                    }
                }

                //Get the next tile for the current direction
                newCell = newCell.getCellForDirection(newDirection);

                //Build a new list holding the moves.
                List<Move.MoveElement> newMoves = new ArrayList<>();
                if(currentMoves != null) {newMoves.addAll(currentMoves);}

                //pop the current letter into our list of current moves (if we're not the root node)
                if(!s.equals(WordGraph.Node.EOW)) { newMoves.add(new Move.MoveElement(s,currentCell,true));}

                //recurse, also getting the new node (from the current node).
                findWordForAnchor(anchor,newCell, node.getChildNode(s),newRackLetters, newMoves,newDirection,candidateMoves,crossSetFiltering);
            });

        } else {
            // current cell isn't empty, must be something on the board already - can we navigate to it from our current node?
            WordGraph.Node nextNode = node.getChildNode(currentCell.getLetter());
            if(nextNode != null) {
                List<Move.MoveElement> newMoves = new ArrayList<>();
                if(currentMoves != null) {newMoves.addAll(currentMoves);}
                newMoves.add(new Move.MoveElement(currentCell.getLetter(),currentCell, false));
                findWordForAnchor(anchor,currentCell.getCellForDirection(direction), node.getChildNode(currentCell.getLetter()),rackLetters,newMoves,direction,candidateMoves,crossSetFiltering);
            }
        }
    }

}
