package braedenstewart.assign5;

public class Pair implements Comparable<Pair> {

    public long freq;
    public String word;
    public char charArray[];
    public Pair left;
    public Pair right;

    public Pair(String word, long freq){
        this.word = word;
        this.freq = freq;
        this.charArray = word.toCharArray();
    }
    public String toString() {
        return "Wt: " + freq + "\t " + word + "\n";
    }
    public int compareTo(Pair t2){
        if (this.freq==t2.freq) return 0;
        else if (this.freq < t2.freq) return -1;
        return 1;
    }
    public int contains(String term){
        char termChar[] = term.toCharArray();
        int count = 0;
        int end = term.length();

        if (term.length() > word.length()){
            end = word.length();
        }

        for (int i = 0; i < end; i++){
           if (this.charArray[i] != termChar[i]){
               return count;
           }
           count++;
        }
        return count;
    }
}