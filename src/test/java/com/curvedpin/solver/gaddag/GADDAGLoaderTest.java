package com.curvedpin.solver.gaddag;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ben on 3/17/17.
 */
public class GADDAGLoaderTest {

    GADDAGLoader myGADDAG = new GADDAGLoader();

//    @Test
//    public void gaddagLoader() {
//        new GADDAGLoader();
//    }


    @Test
    public void searchForWords() {
        System.out.println(myGADDAG.findWordsForHook("H", "ELL"));
    }

}