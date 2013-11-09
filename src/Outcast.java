import java.io.IOException;
import java.util.Arrays;

public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int outcastDegree = -1;
        int index = -1;
        int N = nouns.length;
        int[][] distMatrix = new int[N][N];
        for (int i = 0; i < N; i++)
            Arrays.fill(distMatrix[i], -1);

        for (int i = 0; i < nouns.length; i++) {
            int dist = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    if (distMatrix[j][i] != -1)
                        dist += distMatrix[j][i];
                    else {
                        int tmp = wordnet.distance(nouns[i], nouns[j]);
                        distMatrix[i][j] = tmp;
                        dist += tmp;
                    }
                }
            }
            if (outcastDegree < dist) {
                outcastDegree = dist;
                index = i;
            }
        }

        if (index == -1)
            System.out.println();
        return nouns[index];
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) throws IOException {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            @SuppressWarnings("deprecation")
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
