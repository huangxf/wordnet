import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class WordNet {
    private Map<String, List<Integer>> noun2indexes;

    private Map<Integer, String> index2synset;
    private SAP sap;
    private Set<String> nouns;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) throws IOException {
        nouns = new HashSet<String>();
        noun2indexes = new HashMap<String, List<Integer>>();
        index2synset = new HashMap<Integer, String>();
        int max = -1;
        Scanner vertexScanner = new Scanner(new File(synsets));

        while (vertexScanner.hasNext()) {
            String[] components = vertexScanner.nextLine().split(",");
            Integer index = Integer.parseInt(components[0]);
            String synset = components[1];
            for (String noun : synset.split(" ")) {
                nouns.add(noun);
                if (noun2indexes.containsKey(noun))
                    noun2indexes.get(noun).add(index);
                else {
                    List<Integer> tmp = new LinkedList<Integer>();
                    tmp.add(index);
                    noun2indexes.put(noun, tmp);
                }
            }
            if (index > max)
                max = index;
        }
        vertexScanner.close();

        Digraph G = new Digraph(max + 1);
        Scanner edgesScanner = new Scanner(new File(hypernyms));
        while (edgesScanner.hasNext()) {
            String[] vertexes = edgesScanner.nextLine().split(",");
            for (int i = 1; i < vertexes.length; i++)
                G.addEdge(Integer.parseInt(vertexes[0]),
                        Integer.parseInt(vertexes[i]));

        }
        edgesScanner.close();

        sap = new SAP(G);
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nouns.contains(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        List<Integer> v = noun2indexes.get(nounA), w = noun2indexes.get(nounB);
        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        List<Integer> v = noun2indexes.get(nounA), w = noun2indexes.get(nounB);
        int ancestor = sap.ancestor(v, w);
        return index2synset.get(ancestor);
    }

    // for unit testing of this class
    public static void main(String[] args) throws IOException {
        WordNet wordnet = new WordNet("./data/synsets.txt",
                "./data/hypernyms.txt");
        System.out.println(wordnet.distance("municipality", "region"));
    }
}
