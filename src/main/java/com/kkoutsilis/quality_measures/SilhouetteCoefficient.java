package com.kkoutsilis.quality_measures;

import com.kkoutsilis.graphs.Graph;
import com.kkoutsilis.graphs.Vertex;

import java.util.*;

public class SilhouetteCoefficient extends QualityMeasure {

    public SilhouetteCoefficient(List<Set<Vertex>> clusteringResult, Graph graph) {
        super(clusteringResult, graph);
    }

    public float calculate() {
        List<Float> sMean = new ArrayList<>();
        for (Set<Vertex> c : this.clusteringResult) {
            float sum = 0;
            for (Vertex v : c) {
                float a = this.a(v, c);
                float b = this.b(v, c);
                float s = this.s(a, b);
                sum += s;
            }
            sMean.add(sum / c.size());
        }
        return Collections.max(sMean);
    }

    private float a(Vertex i, Set<Vertex> cluster) {
        float sum = 0;
        for (Vertex j : cluster) {
            if (i != j) {
                sum += this.dist(i.getLabel(), j.getLabel());
            }
        }
        return (1f / (cluster.size() - 1) * sum);
    }

    private float b(Vertex i, Set<Vertex> cluster) {
        float minB = Float.MAX_VALUE;
        for (Set<Vertex> c : this.clusteringResult) {
            if (c != cluster) {
                float sum = 0;
                for (Vertex j : c) {
                    sum += this.dist(i.getLabel(), j.getLabel());
                }
                float res = (1f / c.size()) * sum;
                if (res < minB) {
                    minB = res;
                }
            }
        }
        return minB;
    }

    private float s(float a, float b) {
        return (b - a) / Math.max(a, b);
    }

    private int dist(int source, int dest) {
        int nOfVertices = this.graph.getVertices().size() + 1;
        PriorityQueue<Vertex> minHeap;
        minHeap = new PriorityQueue<>(Comparator.comparingInt(Vertex::getLabel));
        Vertex sourceVertex = this.graph.getVertices().keySet().stream().filter(v -> v.getLabel() == source).findFirst().orElse(null);
        minHeap.add(sourceVertex);

        List<Integer> dist;
        dist = new ArrayList<>(Collections.nCopies(nOfVertices, Integer.MAX_VALUE));

        dist.set(source, 0);

        boolean[] done = new boolean[nOfVertices];
        done[source] = true;

        int[] prev = new int[nOfVertices];
        prev[source] = -1;

        while (!minHeap.isEmpty()) {
            Vertex vertex = minHeap.poll();
            int u = vertex.getLabel();
            for (Vertex edge : graph.getEdges(vertex)) {
                int v = edge.getLabel();
                if (!done[v] && (dist.get(u)) < dist.get(v)) {
                    dist.set(v, dist.get(u));
                    prev[v] = u;
                    minHeap.add(edge);
                }
            }
            done[u] = true;
        }
        return dist.get(dest);
    }
}
