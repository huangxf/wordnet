import java.util.Iterator;

public class SAP {
    private Digraph G;
    private int root;
    private int v, w; // used for caching

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);

        // calculate the root's index
        for (int i = 0; i < G.V(); i++) {
            Iterator<Integer> iter = G.adj(i).iterator();
            while (!iter.hasNext()) {
                root = i;
                break;
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!(0 <= v && v < G.V() && 0 <= w && w < G.V()))
            throw new IndexOutOfBoundsException();

        // find length of direct path
        BreadthFirstDirectedPaths bfdpv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdpw = new BreadthFirstDirectedPaths(G, w);
        int len = -1;
        if (bfdpv.hasPathTo(w))
            len = bfdpv.distTo(w);
        else if (bfdpw.hasPathTo(v))
            len = bfdpv.distTo(v);
        else
            assert false;

        // find length of indirect path
        int ancestor = -1;
        Stack<Integer> stack = new Stack<Integer>();
        for (Integer iw : bfdpv.pathTo(root))
            stack.push(iw);
        for (Integer iv : bfdpw.pathTo(root))
            if (iv.equals(stack.peek())) {
                ancestor = stack.pop();
            }
        assert ancestor != -1;

        int temp = bfdpv.distTo(ancestor) + bfdpw.distTo(ancestor);
        if (temp < len)
            return temp;
        else
            return len;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!(0 <= v && v < G.V() && 0 <= w && w < G.V()))
            throw new IndexOutOfBoundsException();

        // find length of direct path
        BreadthFirstDirectedPaths bfdpv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdpw = new BreadthFirstDirectedPaths(G, w);
        int len = -1, ancestor = -1;
        if (bfdpv.hasPathTo(w)) {
            len = bfdpv.distTo(w);
            ancestor = w;
        } else if (bfdpw.hasPathTo(v)) {
            len = bfdpv.distTo(v);
            ancestor = v;
        } else
            assert false;

        // find length of indirect path
        int tempAncestor = -1;
        Stack<Integer> stack = new Stack<Integer>();
        for (Integer iw : bfdpv.pathTo(root))
            stack.push(iw);
        for (Integer iv : bfdpw.pathTo(root))
            if (iv.equals(stack.peek())) {
                tempAncestor = stack.pop();
            }
        assert tempAncestor != -1;

        int tempLen = bfdpv.distTo(tempAncestor) + bfdpw.distTo(tempAncestor);
        if (tempLen < len)
            return tempAncestor;
        else
            return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return 0;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int ancester = -1;
        Stack<Integer> stack = new Stack<Integer>();

        for (Integer iw : w)
            stack.push(iw);
        for (Integer iv : v)
            if (iv.equals(stack.peek())) {
                ancester = stack.pop();
            }

        return ancester;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
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
