package com.curvedpin.services;

import com.curvedpin.solver.Move;
import com.curvedpin.solver.ScrabbleSolver;
import com.curvedpin.solver.WWFClassicBoard;
import com.curvedpin.solver.wordgraph.WordGraph;
import com.curvedpin.solver.image.BoardImageUtils;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class SolvoServicesController {

    private final WordGraph wordGraph = new WordGraph();

    @PostMapping(value = "/chopshop/board", produces = "application/json")
    public Map<Integer,String> getBoard(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        return BoardImageUtils.getBoardLetters(bf);
    }

    @PostMapping(value = "/chopshop/rack", produces = "application/json")
    public Map<Integer,String> getRack(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        return BoardImageUtils.getRackLetters(bf);
    }

    @PostMapping(value = "/chopshop", produces = "application/json")
    public ArrayList<Map<Integer,String>> getRackAndBoard(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        Map<Integer, String> boardLetters = BoardImageUtils.getBoardLetters(bf);
        Map<Integer, String> rackLetters = BoardImageUtils.getRackLetters(bf);
        return new ArrayList<>(Arrays.asList(boardLetters,rackLetters));
    }

    @PostMapping(value = "/wordshop", produces = "text/plain")
    public String getBestMoves(@RequestParam("file") MultipartFile file) throws IOException {
        StringBuffer result = new StringBuffer();
        BufferedImage bf = ImageIO.read(file.getInputStream());
        Map<Integer, String> boardLetters = BoardImageUtils.getBoardLetters(bf);
        Map<Integer, String> rackLetters = BoardImageUtils.getRackLetters(bf);

        WWFClassicBoard wordBoard = new WWFClassicBoard(boardLetters);
        List<Move> moves = ScrabbleSolver.wordSearch(wordBoard, rackLetters.values().stream().collect(Collectors.joining()), wordGraph.getRootNode());
        moves.sort(Comparator.<Move>comparingInt(Move::getScore).reversed());
        for(Move m: moves) {
            System.out.println(m);
            result.append(m.toString());
            result.append("\n");
        }
        return result.toString();
    }

}