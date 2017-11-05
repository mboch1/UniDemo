package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.GraphGenerator;

public class WattsStrogatzGraphGenerator<V,E> implements GraphGenerator<V,E,V>{
    private static final boolean DEFAULT_ADD_INSTEAD_OF_REWIRE = false;

    private final Random rng;
    private final int n;
    private final int k;
    private final double p;
	private final boolean addInsteadOfRewire;
	
    public WattsStrogatzGraphGenerator(int n, int k, double p){
        this(n, k, p, DEFAULT_ADD_INSTEAD_OF_REWIRE, new Random());
    }
    
    public WattsStrogatzGraphGenerator(int n, int k, double p, long seed){
        this(n, k, p, DEFAULT_ADD_INSTEAD_OF_REWIRE, new Random(seed));
    }

    public WattsStrogatzGraphGenerator(int n, int k, double p, boolean addInsteadOfRewire, Random rng){
        if (n < 3) {
            throw new IllegalArgumentException("number of vertices must be at least 3");
        }
        this.n = n;
        if (k < 1) {
            throw new IllegalArgumentException("number of k-nearest neighbors must be positive");
        }
        if (k % 2 == 1) {
            throw new IllegalArgumentException("number of k-nearest neighbors must be even");
        }
        if (k > n - 2 + (n % 2)) {
            throw new IllegalArgumentException("invalid k-nearest neighbors");
        }
        this.k = k;

        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("invalid probability");
        }
        
        this.p = p;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
        this.addInsteadOfRewire = addInsteadOfRewire;
    }

    /**
     * Generates a small-world graph based on the Watts-Strogatz model.
     * 
     * @param target the target graph
     * @param vertexFactory the vertex factory
     * @param resultMap not used by this generator, can be null
     */
    @Override
    public void generateGraph(Graph<V, E> target, VertexFactory<V> vertexFactory, Map<String, V> resultMap){
        // special cases
        if (n == 0) {
            return;
        } else if (n == 1) {
            target.addVertex(vertexFactory.createVertex());
            return;
        }

        // create ring lattice
        List<V> ring = new ArrayList<>(n);
        Map<V, List<E>> adj = new LinkedHashMap<>(n);

        for (int i = 0; i < n; i++) {
            V v = vertexFactory.createVertex();
            if (!target.addVertex(v)) {
                throw new IllegalArgumentException("Invalid vertex factory");
            }
            ring.add(v);
            adj.put(v, new ArrayList<>(k));
        }

        for (int i = 0; i < n; i++) {
            V vi = ring.get(i);
            List<E> viAdj = adj.get(i);

            for (int j = 1; j <= k/2; j++) {
                viAdj.add(target.addEdge(vi, ring.get((i+j)%n)));
            }
        }

        // re-wire edges
        for (int r = 0; r < k / 2; r++) {
            for (int i = 0; i < n; i++) {
                V v = ring.get(i);
             //   E e = adj.get(i).get(r);

                if (rng.nextDouble() < p) {
                    V other = ring.get(rng.nextInt(n));
                    if (!other.equals(v) && !target.containsEdge(v, other)) {
                        if (!addInsteadOfRewire) {
                           // target.removeEdge(e);
                        }
                        target.addEdge(v, other);
                    }
                }
            }
        }
    }
}
