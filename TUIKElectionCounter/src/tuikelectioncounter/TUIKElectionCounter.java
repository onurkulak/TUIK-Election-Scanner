/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tuikelectioncounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * this program only works for a certain text format 
 * that TUIK used in their election result PDFs
 * file name is fixed and its in the project folder
 * @author onur
 */
public class TUIKElectionCounter {

    public static void main(String[] args) throws FileNotFoundException {
        HashMap<String, int[]> provinces = new HashMap(150);
        HashMap<String, Boolean> previousElection = new HashMap(130);
        HashMap<String, Boolean> currentElection = new HashMap(130);
        //current election holds the provinces that were in the current election
        //previous election holds the provinces that were in the current election
        //these are used to find if a province stopped joining elections or
        //it changed name, or there is a new province in elections
        Scanner scan = new Scanner(new File("iller ve sonuçlar.txt"));
        
        
        //in this loop, since in the file every election is splitted by an empty new line; 
        //all elections are splitted and election calculater method is called on them
        //also handles the cur pre election maps and their replacement
        for(int election=1; scan.hasNextLine() ; election++ ){
            String electionResults = scan.nextLine() + "\n" + scan.nextLine() + "\n";
            while(scan.hasNextLine()){
                String temp = scan.nextLine();
                if(temp.equals(""))
                    break;
                electionResults += temp + " ";
            }
            calcElection(provinces, previousElection, currentElection, election, electionResults);
            previousElection = currentElection;
            currentElection = new HashMap(130);
        }
        
        printElections (provinces);
        
    }
    
    //calculates a whole election
    private static void calcElection(HashMap<String, int[]> provinces, HashMap<String, Boolean> previousElection, HashMap<String, Boolean> currentElection, int election, String results) {
        int orderOfTheFirstParty = 0;
        int maximumSeats = -1;
        Scanner scan = new Scanner(results);
        scan.nextLine();
        scan.next(); scan.next();
        for(int i=0; scan.hasNextInt() ; i++){
            int seats = scan.nextInt();
            if(seats>maximumSeats){
                maximumSeats=seats;
                orderOfTheFirstParty=i;
            }
            else if( seats==maximumSeats )
                System.out.println( " There might be a tie in election: " + election);
        }
        while(scan.hasNext()){
            String province =scan.next(); scan.next();
            if(previousElection.containsKey(province))
                previousElection.put(province, Boolean.TRUE);
            else
                System.out.println(province + " Entered the elections first time in, election : " + election);
            currentElection.put(province, Boolean.FALSE);
            while (scan.hasNextInt())
                province+=" " + scan.nextInt();
            calcProvinceElection(province, orderOfTheFirstParty, provinces, election);
        }
        checkLeavers(previousElection, election);
        System.out.println("\n\n\nELECTION : " + election);
        printElections(provinces);
    }
    
    //does the calculations for a single province
    private static void calcProvinceElection(String province, int orderOfTheFirstParty, HashMap<String, int[]> provinces, int election) {
        String[] divided = province.split("\\s+");
        boolean won = true; boolean tie = false;
        int wonByFirstParty = Integer.parseInt(divided[orderOfTheFirstParty+1]);
        for(int i=1; i<divided.length; i++){
            if(orderOfTheFirstParty+1 == i)
                continue;
            int count = Integer.parseInt(divided[i]);
            if(count>wonByFirstParty){
                won = false;
                tie = false;
                break;
            }
            else if ( count == wonByFirstParty)
                tie = true; 
        }
        String provinceName = divided[0];
        if(provinces.containsKey(provinceName))
            provinces.get(provinceName)[1]++;
        else provinces.put(provinceName, new int[]{0,1});
        if(won)
             provinces.get(provinceName)[0]++;
        else if(tie)
            System.out.println("There might be a tie in election: " + election +  divided[1]);
    }

    //controls if a province in the previousElection did not enter this election
    private static void checkLeavers(HashMap<String, Boolean> previousElection, int election) {
        String[] temp = {}; Boolean[] tempb = {};
        String[] keyset = previousElection.keySet().toArray(temp);
        Boolean[] valueCollection = previousElection.values().toArray(tempb);
        for(int i=0; i<valueCollection.length ; i++)
            if(!valueCollection[i])
                System.out.println("This province: " + keyset[i] + " did not enter the election: " + election);
    }

    //prints the collected results for provinces
    private static void printElections(HashMap<String, int[]> provinces) {
        String[] temp = {}; int[][] tempb = {};
        String[] keyset = provinces.keySet().toArray(temp);
        int[][] valueCollection = provinces.values().toArray(tempb);
        System.out.println();
        for(int i=0; i<keyset.length; i++)
            System.out.println(keyset[i] + "\t\t" + valueCollection[i][0] + "\t" + valueCollection[i][1] + "\t" +  (((double)valueCollection[i][0]) / valueCollection[i][1]));
        System.out.println();
    }
    
    
}
    

