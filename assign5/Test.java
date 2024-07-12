package braedenstewart.assign5;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("SortedWords.txt");
        Scanner scanner = new Scanner(file);
        ArrayList<Pair> terms = new ArrayList<>();

        while(scanner.hasNextLine()){
            String temp[] = scanner.nextLine().trim().split("\\s+");
            if (temp[0].equals("4351")){
                continue;
            }
            terms.add(new Pair(temp[0], Long.parseLong(temp[1])));
        }

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the term: ");
        String term = in.nextLine();
        System.out.println("Enter the count: ");
        int count = in.nextInt();

        SkewHeap possibleTerms = getPossResults(terms, term);

        if (count > possibleTerms.getSize()){
            count = possibleTerms.getSize();
        }
        String finalString = "Top " + count + " for " + term + ": \n";

        while(count > 0){
            if (possibleTerms.getRoot() == null){
                break;
            }
            finalString += possibleTerms.deleteMax() + "\n";
            count--;
        }
        System.out.println(finalString);
    }
    private static SkewHeap getPossResults(ArrayList<Pair> terms, String term){
        int middle = findMid(terms, term);
        SkewHeap result = new SkewHeap();
        if (middle == -1){
            return result;
        }
        int start = findStart(terms, middle, term);
        int end = findEnd(terms, middle, term);
        for (int i = start; i <= end; i++){
            result.insert(terms.get(i));
        }
        return result;
    }
    private static int findMid(ArrayList<Pair> terms, String term){
        int left = 0;
        int right = terms.size() - 1;
        char termChar[] = term.toCharArray();

        while (left <= right){
            int mid = (left + (right - 1)) / 2;
            Pair temp = terms.get(mid);
            if (term.length() > temp.word.length()){
                right++;
                continue;
            }
            if (temp.contains(term) == term.length()){
                return mid;
            }
            if (temp.contains(term) > 0){
                if (temp.charArray[temp.contains(term)] > termChar[temp.contains(term)]){
                    right = mid - 1;
                }
                else{
                    left = mid + 1;
                }
            }
            else{
                if (temp.charArray[0] > termChar[0]){
                    right = mid -1;
                }
                else{
                    if (left == right - 1){
                        return right;
                    }
                    else{
                        left = mid + 1;
                    }
                }
            }
        }
        return -1;
    }
    private static int findStart(ArrayList<Pair> terms, int index, String term){
        while(terms.get(index).contains(term) == term.length()){
            if (index == 0 || terms.get(index - 1).contains(term) != term.length()){
                return index;
            }
            index--;
        }
        return index;
    }
    private static int findEnd(ArrayList<Pair> terms, int index, String term){
        while(terms.get(index).contains(term) == term.length()){
            if (index == terms.size() - 1 || terms.get(index + 1).contains(term) != term.length()){
                return index;
            }
            index++;
        }
        return index;
    }
}
