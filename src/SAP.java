import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SAP {
    private Digraph digraph;
    private Graph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G);
        graph = new Graph(digraph.V());
        for (int i = 0; i < digraph.V(); i++) {
            Iterator<Integer> neighbors = digraph.adj(i).iterator();
            while (neighbors.hasNext()) {
                int neighbor = neighbors.next();
                graph.addEdge(i, neighbor);
                graph.addEdge(neighbor, i);
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int ancestor = ancestor(v, w);
        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);
        return searchFromV.distTo(ancestor) + searchFromW.distTo(ancestor);
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!(0 <= v && v < digraph.V() && 0 <= w && w < digraph.V()))
            throw new IndexOutOfBoundsException();

        BreadthFirstPaths search = new BreadthFirstPaths(graph, v);
        if (!search.hasPathTo(w))
            return -1;

        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);

        int ancestor = -1, minDist = Integer.MAX_VALUE;
        for (Iterator<Integer> paths = search.pathTo(w).iterator(); paths
                .hasNext();) {
            int innerVertex = paths.next();
            if (searchFromV.hasPathTo(innerVertex)
                    && searchFromW.hasPathTo(innerVertex)) {
                int dist = searchFromV.distTo(innerVertex)
                        + searchFromW.distTo(innerVertex);
                if (minDist > dist) {
                    minDist = dist;
                    ancestor = innerVertex;
                }
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int min = Integer.MAX_VALUE;

        for (int iw : w) {
            for (int iv : v) {
                int len = length(iv, iw);
                if (len < min)
                    min = len;
            }
        }

        return min;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int iv : v)
            queue.add(iv);
        for (int iw : w)
            queue.add(iw);

        while (queue.size() > 1) {
            int a = queue.poll();
            int b = queue.poll();
            queue.add(ancestor(a, b));
        }

        return queue.poll();
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In("./wordnet-testing/wordnet/digraph5.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
