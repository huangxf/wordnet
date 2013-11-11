
public class SAP {
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!(0 <= v && v < digraph.V() && 0 <= w && w < digraph.V()))
            throw new IndexOutOfBoundsException();

        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);

        int length = Integer.MAX_VALUE;
        for (int i = 0; i < digraph.V(); i++) {
            if (searchFromV.hasPathTo(i) && searchFromW.hasPathTo(i)) {
                int dist = searchFromV.distTo(i) + searchFromW.distTo(i);
                if (length > dist)
                    length = dist;
            }
        }

        if (length == Integer.MAX_VALUE)
            return -1;
        else
            return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!(0 <= v && v < digraph.V() && 0 <= w && w < digraph.V()))
            throw new IndexOutOfBoundsException();

        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);

        int minDist = Integer.MAX_VALUE, ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (searchFromV.hasPathTo(i) && searchFromW.hasPathTo(i)) {
                int dist = searchFromV.distTo(i) + searchFromW.distTo(i);
                if (minDist > dist) {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }

        if (minDist == Integer.MAX_VALUE)
            return -1;
        else
            return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < digraph.V(); i++) {
            if (searchFromV.hasPathTo(i) && searchFromW.hasPathTo(i)) {
                int dist = searchFromV.distTo(i) + searchFromW.distTo(i);
                if (minDist > dist)
                    minDist = dist;
            }
        }
        if (minDist == Integer.MAX_VALUE)
            return -1;
        else
            return minDist;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths searchFromV = new BreadthFirstDirectedPaths(
                digraph, v);
        BreadthFirstDirectedPaths searchFromW = new BreadthFirstDirectedPaths(
                digraph, w);
        int minDist = Integer.MAX_VALUE, ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (searchFromV.hasPathTo(i) && searchFromW.hasPathTo(i)) {
                int dist = searchFromV.distTo(i) + searchFromW.distTo(i);
                if (minDist > dist) {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In("./wordnet-testing/wordnet/digraph1.txt");
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
