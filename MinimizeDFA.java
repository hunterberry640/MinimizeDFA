import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/* 
Author: Hunter Berry
Date: April 1st, 2021
Desc: Program that reads in a DFA from a text file and minimizes it.
*/

public class MinimizeDFA {

    static ArrayList<ArrayList<Integer>> DFAGraph = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> minDFAGraph = new ArrayList<>();
    static ArrayList<Integer> finStates = new ArrayList<>();
    static ArrayList<Integer> minfinStates = new ArrayList<>();
    static ArrayList<String> testStrings = new ArrayList<>();
    static int numStates;
    static int numTransitions;
    static List<String> transitionList;
    static int initState;
    static int minInitState;
    static int[][] table;
    public static void main(String[] args) {
        String graphfile = args[0];
        Scanner lineScanner = null;
        StringTokenizer st;
        try 
        {
        lineScanner = new Scanner(new File(graphfile)); // scanner that reads file line by line
        } 
        catch (FileNotFoundException e) 
        {
        System.out.println("file not found " + graphfile + "\n " + e);
        }

        String line = lineScanner.nextLine(); // read in num of states

        numStates = Integer.parseInt(line);

        line = lineScanner.nextLine(); // read in transitions
      
        String[] transitions = line.split(" ");
      
        //remove Sigma: at beginning of list
        transitionList = new ArrayList<String>(Arrays.asList(transitions));
        transitionList.removeAll(Arrays.asList("", " ", "\t"));
        transitionList.remove(0);
        
        numTransitions = transitionList.size();

        line = lineScanner.nextLine();
        int count = 0;
        while (count < numStates) {
            ArrayList<Integer> stateList = new ArrayList<>();
            line = lineScanner.nextLine();
            st = new StringTokenizer(line);
            String token = st.nextToken();
            
            while(st.hasMoreTokens()){
                token = st.nextToken();
                
                stateList.add(Integer.parseInt(token));
            }
            DFAGraph.add(stateList);
            count++;
        }
        lineScanner.nextLine();
        initState = Integer.parseInt(lineScanner.nextLine().split(":")[0]);
        
        st = new StringTokenizer(lineScanner.nextLine(), ",: ", true);
        String token;
        while (st.hasMoreTokens()){ 
            token = st.nextToken();
            if(token.equals(" ")){
                break;
            }
            int tok = Integer.parseInt(token);
            finStates.add(tok);
            st.nextToken();
        }
        
        count =0;
        boolean accepted;
        int yes = 0;
        int no = 0;
        System.out.println("\nParsing results of " + graphfile + " on strings attached in "+ graphfile +":");
        while (lineScanner.hasNextLine()){
            if(count == 15){
                System.out.println();
            }
            line = lineScanner.nextLine();
            accepted = checkDFASolution(line);
            testStrings.add(line);
            if(accepted)
                yes++;
            else
                no++;
            
            count++;
            
        }
        lineScanner.close();
        System.out.println("\n\nYes:" + yes + " No:" + no);
        System.out.println();
        generateTable();
        createMinDFA();
        printMinDFA();

        System.out.println("\nParsing results of minimized" + graphfile + " on strings attached in "+ graphfile +":");
        count = 0;
        yes = 0;
        no = 0;
        for (String string : testStrings) {
            if(count == 15){
                System.out.println();
            }
            accepted = checkMinDFASolution(string);
            if(accepted)
                yes++;
            else
                no++;
            
            count++;
        }
        System.out.println("\n\nYes:" + yes + " No:" + no);
        System.out.println();
    }

    public static void printMinDFA(){
        System.out.print("\n\nSigma:\t");
        for (int i = 0; i < numTransitions; i++) {
            System.out.print((char)(97+i) + "\t");
        }
        System.out.println("\n----------------------------------");
        int  count = 0;
        for (ArrayList<Integer> arrayList : minDFAGraph) {
            System.out.print(count + ":\t");
            for (Integer integer : arrayList) {
                System.out.print(integer + "\t");
            }
            System.out.println();
            count++;
        }
        System.out.println("----------------------------------\n");
        System.out.println("initial State: " + minInitState);
        System.out.println("accepting states: " + minfinStates.toString());
    }

    public static void createMinDFA(){
        ArrayList<Set<Integer>> arr = new ArrayList<>();
        ArrayList<Integer> skip = new ArrayList<>();
        Set<Integer> arrlist;
        //find states with zeroes and combine them and add them to arrlist.
        for (int i = 1; i < table.length; i++) {
            arrlist = new HashSet<>();
            for (int j = 0; j < table.length+i-table.length; j++) {
                if(table[i][j] == 0 && !skip.contains(i)){
                    skip.add(i);
                    skip.add(j);
                    arrlist.add(j);
                    arrlist.add(i);
                    arr.add(arrlist);
                }
            }
        }
        //add individual states that didn't haave zeroes in them in the table
        for (int i = 0; i < table.length; i++) {
            arrlist = new HashSet<>();
            if(!skip.contains(i)){
                arrlist.add(i);
                arr.add(arrlist);
            }
        }
        // find sets that have common elemnts and combine them
        for (int i = 0; i < arr.size(); i++) {
            for (int j = 0; j < arr.size(); j++){
                if(arr.get(i) != arr.get(j) && !Collections.disjoint(arr.get(i), arr.get(j))){
                    arr.get(i).addAll(arr.get(j));
                    arr.remove(arr.get(j));
                    j-=1;
                }
            }
        }

        
        ArrayList<Integer> minDFAlist;
        int index = 0;
        for (Set<Integer> elem : arr) {
            if(elem.contains(initState)){
                minInitState = index;
            }
            for (Integer integer : finStates) {
                if(elem.contains(integer)){
                    minfinStates.add(index);
                    break;
                }
            }
            index++;
            minDFAlist = new ArrayList<>();
            int item= elem.iterator().next(); // first int in set of 
            for(int i =0; i < numTransitions; i++){
                int state = DFAGraph.get(item).get(i);
                int count = 0;
                for (Set<Integer> element : arr){
                    if (element.contains(state)){
                        minDFAlist.add(count);
                    }
                    count++;
                }    
                
            }
            minDFAGraph.add(minDFAlist);
        }
    }

    public static void generateTable(){
        table = new int[numStates][numStates];
        for (int i = 1; i < table.length; i++) {
            for (int j = 0; j < table.length+i-table.length; j++) {
                if(finStates.contains(i) && !finStates.contains(j)){
                    table[i][j] = 1;
                }else if(finStates.contains(j) && !finStates.contains(i)){
                    table[i][j] = 1;
                }
            }
        }
        boolean change = true;
        while(change == true){
            change = false;
        for (int i = 1; i < table.length; i++) {
            for (int j = 0; j < table.length+i-table.length; j++) {
                if(table[i][j] == 0){
                    for (int j2 = 0; j2 < numTransitions; j2++) {
                        if(table[DFAGraph.get(i).get(j2)][DFAGraph.get(j).get(j2)] ==1 || table[DFAGraph.get(j).get(j2)][DFAGraph.get(i).get(j2)] ==1 ){
                            table[i][j] = 1;
                            change = true;
                        }
                    }
                }
            }
        }
    }


    }
    // test a string of inputs on dfa will return true if accepted false otherwise.
    public static boolean checkDFASolution(String testString){
        
        ArrayList<String> inputs= new ArrayList<>();
        inputs.add("a");
        inputs.add("b");
        inputs.add("c");
        inputs.add("d");
        inputs.add("e");
        inputs.add("f");
        
            
        String[] testArr = testString.split("");
        int currentState = 0;
        int lastInput = 0;

        for (String str : testArr) {
            if(str.equals("")){
                break;
            }
            if(inputs.indexOf(str) >= numTransitions){
                lastInput = inputs.indexOf(str);
                break;
            }
            
            currentState = DFAGraph.get(currentState).get(inputs.indexOf(str));

            lastInput = inputs.indexOf(str);
        }
        
        if(lastInput >= numTransitions){
            System.out.printf("%-5s ", " No ");
            return false;
        }else if (finStates.contains(currentState)){ // if dfa ends in accepting state, accept
            System.out.printf("%-5s "," Yes " );
            return true;
        }else{
            System.out.printf("%-5s ", " No ");
            return false;
        }
    }

    // test a string of inputs on minimized dfa will return true if accepted false otherwise.
    public static boolean checkMinDFASolution(String testString){
        
        ArrayList<String> inputs= new ArrayList<>();
        inputs.add("a");
        inputs.add("b");
        inputs.add("c");
        inputs.add("d");
        inputs.add("e");
        inputs.add("f");
        
            
        String[] testArr = testString.split("");
        int currentState = minInitState;
        int lastInput = 0;

        for (String str : testArr) {
            if(str.equals("")){
                break;
            }
            if(inputs.indexOf(str) >= numTransitions){
                lastInput = inputs.indexOf(str);
                break;
            }
            
            currentState = minDFAGraph.get(currentState).get(inputs.indexOf(str));

            lastInput = inputs.indexOf(str);
        }
        
        if(lastInput >= numTransitions){
            System.out.printf("%-5s ", " No ");
            return false;
        }else if (minfinStates.contains(currentState)){ // if dfa ends in accepting state, accept
            System.out.printf("%-5s "," Yes " );
            return true;
        }else{
            System.out.printf("%-5s ", " No ");
            return false;
        }
    }
}