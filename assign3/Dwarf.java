package braedenstewart.assign3;

/**
 * class to define the data fields and methods of the Dwarf object
 */
public class Dwarf implements Comparable<Dwarf>{
    String data; // element of the dwarf object
    int which; // records which number of dwarf it is
    static  int ct = 0; // iterates number of dwarf that the object is when constructed

    // constructor
    public Dwarf(String x){
        data = x;
        which= ct++;
    }

    @Override
    // allows us to compare Dwarven objects to each other
    public int compareTo(Dwarf b2){
        return (this.data.compareTo( b2.data));
    }
    // overridden method to display Dwarf object as a string
    public String toString(){
        return  data + which;
    }
}

