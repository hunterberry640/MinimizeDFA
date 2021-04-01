import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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
    static int numStates;
    static int numTransitions;
    static List<String> transitionList;
    static int initState;
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

        line = lineScanner.nextLine();
      
        String[] transitions = line.split(" ");
      
        transitionList = new ArrayList<String>(Arrays.asList(transitions));
        transitionList.removeAll(Arrays.asList("", " ", "\t"));
        transitionList.remove(0);
        
        numTransitions = transitionList.size();

        System.out.println("numStates in DFA: " + numStates);
        System.out.println("transitionList for DFA: " + transitionList);
        System.out.println("numTransitions in DFA: " + numTransitions);

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
            if(accepted)
                yes++;
            else
                no++;
            
            count++;
            
        }
        System.out.println("\n\nYes:" + yes + " No:" + no);
        System.out.println();
    }


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
}