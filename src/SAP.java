import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SAP {
    private Digraph G;
    private int root;

    // private int v, w; // used for caching

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);

        // calculate the root's index
        Outer: for (int i = 0; i < G.V(); i++) {
            Iterator<Integer> iter = G.adj(i).iterator();
            while (!iter.hasNext()) {
                root = i;
                break Outer;
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!(0 <= v && v < G.V() && 0 <= w && w < G.V()))
            throw new IndexOutOfBoundsException();

        // find length of direct path
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);
        int directLen = -1;
        if (pathV.hasPathTo(w))
            directLen = pathV.distTo(w);
        if (pathW.hasPathTo(v))
            directLen = pathV.distTo(v);

        // find length of indirect path
        int indirectLen = -1, indirectAncestor = -1;
        BreadthFirstDirectedPaths rootNode = new BreadthFirstDirectedPaths(
                G.reverse(), root);
        if (rootNode.hasPathTo(v) && rootNode.hasPathTo(w)) {
            Iterator<Integer> iterRoot2V = rootNode.pathTo(v).iterator();
            Iterator<Integer> iterRoot2W = rootNode.pathTo(w).iterator();
            while (iterRoot2V.hasNext() && iterRoot2W.hasNext()) {
                Integer iv = iterRoot2V.next();
                Integer iw = iterRoot2W.next();
                if (!iv.equals(iw))
                    break;
                indirectAncestor = iv;
            }
            assert indirectAncestor != -1;
            indirectLen = pathV.distTo(indirectAncestor)
                    + pathW.distTo(indirectAncestor);
        }

        if (directLen != -1 && directLen < indirectLen)
            return directLen;
        else
            return indirectLen;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!(0 <= v && v < G.V() && 0 <= w && w < G.V()))
            throw new IndexOutOfBoundsException();

        // find length of direct path
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);
        int directLen = -1, directAncestor = -1;
        if (pathV.hasPathTo(w)) {
            directLen = pathV.distTo(w);
            directAncestor = w;
        }
        if (pathW.hasPathTo(v)) {
            directLen = pathV.distTo(v);
            directAncestor = v;
        }

        // find length of indirect path
        int indirectLen = -1, indirectAncestor = -1;
        BreadthFirstDirectedPaths rootNode = new BreadthFirstDirectedPaths(
                G.reverse(), root);
        if (rootNode.hasPathTo(v) && rootNode.hasPathTo(w)) {
            Iterator<Integer> iterRoot2V = rootNode.pathTo(v).iterator();
            Iterator<Integer> iterRoot2W = rootNode.pathTo(w).iterator();
            while (iterRoot2V.hasNext() && iterRoot2W.hasNext()) {
                Integer iv = iterRoot2V.next();
                Integer iw = iterRoot2W.next();
                if (!iv.equals(iw))
                    break;
                indirectAncestor = iv;
            }
            assert indirectAncestor != -1;
            indirectLen = pathV.distTo(indirectAncestor)
                    + pathW.distTo(indirectAncestor);
        }

        if (directLen != -1 && directLen < indirectLen)
            return directAncestor;
        else
            return indirectAncestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int min = Integer.MAX_VALUE;

        for (Integer iw : w) {
            for (Integer iv : v) {
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
        for (Integer iv : v)
            queue.add(iv);
        for (Integer iw : w)
            queue.add(iw);

        while (queue.size() > 1) {
            Integer a = queue.poll();
            Integer b = queue.poll();
            queue.add(ancestor(a, b));
        }

        return queue.poll();
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In("./wordnet-testing/wordnet/digraph6.txt");
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
