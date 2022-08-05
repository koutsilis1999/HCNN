package com.kkoutsilis;

import com.kkoutsilis.algorithms.ClusteringAlgorithm;
import com.kkoutsilis.algorithms.HCNN;
import com.kkoutsilis.graphs.Vertex;
import com.kkoutsilis.graphs.Graph;
import com.kkoutsilis.utilities.CsvHandler;
import com.kkoutsilis.utilities.NearestNeighbour;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws Exception {
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        int k = Integer.parseInt(args[2]);
        int fistNearestNeighbourAlgorithm = Integer.parseInt(args[3]);
        int secondNearestNeighbourAlgorithm = Integer.parseInt(args[4]);
        int n = Integer.parseInt(args[5]);

        long startTime = System.nanoTime();

        Map<Vertex, Set<Vertex>> inputVertices = CsvHandler.parseCSV(inputFilePath);

        Graph graph = new Graph(inputVertices);

        Map<Vertex, Set<Vertex>> knn = NearestNeighbour.knn(k, graph);

        Map<Vertex, Set<Vertex>> fistNnAlgorithm;
        if (fistNearestNeighbourAlgorithm == 1) {
            fistNnAlgorithm = knn;

        } else if (fistNearestNeighbourAlgorithm == 2) {
            Map<Vertex, Set<Vertex>> rKnnRes = NearestNeighbour.rknn(knn);
            fistNnAlgorithm = NearestNeighbour.mknn(knn, rKnnRes);

        } else {
            throw new IllegalArgumentException("Type 1 for KNN or 2 for mKNN");
        }
        Map<Vertex, Set<Vertex>> secondNnAglorithm;
        if (secondNearestNeighbourAlgorithm == 1) {
            secondNnAglorithm = knn;

        } else if (secondNearestNeighbourAlgorithm == 2) {
            Map<Vertex, Set<Vertex>> rKnnRes = NearestNeighbour.rknn(knn);
            secondNnAglorithm = NearestNeighbour.mknn(knn, rKnnRes);

        } else if (secondNearestNeighbourAlgorithm == 3) {
            secondNnAglorithm = NearestNeighbour.rknn(knn);

        } else {
            throw new IllegalArgumentException("Type 1 for KNN, 2 for mKNN or 3 for rKNN");
        }

        if (fistNearestNeighbourAlgorithm == 1 && secondNearestNeighbourAlgorithm == 3) {
            throw new IllegalArgumentException("KNN cannot be combined with rKNN");
        }

        ClusteringAlgorithm algo = new HCNN(graph, n, fistNnAlgorithm, secondNnAglorithm);
        List<Set<Vertex>> result = algo.fit();

        CsvHandler.dumpToCSV(outputFilePath, result);
        long elapsedTime = System.nanoTime() - startTime;
        int i = 0;
        for (Set<Vertex> s : result) {
            System.out.printf("------------------------------CLUSTER%d------------------------------%n", i++);
            System.out.println(s);
            System.out.println();
        }
        System.out.println("Total execution time in millis: " + elapsedTime / 1000000);

    }
}
