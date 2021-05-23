package com.lab;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    ArrayList<String[]> rules;

    public Main(String grammarFile) {
        this.rules = convertRules(readRules(grammarFile));
    }

    public static ArrayList<String[]> convertRules(ArrayList<String[]> rules) {
        if (rules == null) {
            return null;
        }
        unitRules(rules);
        epsilonRule(rules);
        checkS(rules);
        nonTerminals(rules);
        allProductions(rules);

        return rules;

    }

    public static void unitRules(ArrayList<String[]> rules) {

        ArrayList<String[]> unitProductions = findUnitProductions(rules);

        for (int i = 0; i < unitProductions.size(); i++) {
            String[] production = unitProductions.get (i);
            int j;
            for (j = 0; j < unitProductions.size (); j++) {
                String[] tempProduction = unitProductions.get (j);
                if (production[0].equals(tempProduction[1]) && production[1].equals(tempProduction[0])) {
                    for (int k = 0; k < rules.size (); k++) {
                        String[] rule = rules.get (k);
                        for (int l = 0; l < rule.length; l++) {
                            if (rule[l].equals (production[1])) {
                                rule[l] = production[0];
                            }
                        }
                        rules.set (k, rule);
                    }
                }
            }
        }
        // remove rules of the form X->X
        for (int i = 0; i < rules.size (); i++) {
            String[] rule = rules.get (i);
            if (rule[0].equals (rule[1]) && rule.length == 2) {
                rules.remove (i);
                i--;
            }
        }

        unitProductions = findUnitProductions (rules);

        for (int i = 0; i < unitProductions.size (); i++) {
            String[] production = unitProductions.get (i);
            unitRuleRecursion(rules, production, getIndex(rules, production));
        }

        for (int i = 0; i < rules.size (); i++) {
            String[] rule = rules.get (i);
            for (int j = 0; j < rules.size (); j++) {
                if (rules.get (j).length==rules.get (i).length) {
                    boolean isEqual = true;

                    for (int k = 0; k < rule.length; k++) {
                        if (!rules.get(j)[k].equals (rule[k])) {
                            isEqual = false;
                            break;
                        }
                    }
                    if (i != j && isEqual) {
                        rules.remove (j);
                        j--;
                    }
                }
            }
        }
        System.out.println("Unit Rules: ");
        int i;
        for (i = 0; i < rules.size(); i++);{
            String[] rule = rules.get(i);
            int j;
            for ( j = 0; j < rule.length; j++){
                System.out.print(rule[j] + " ");
            }
        }
        System.out.println();
    }

    private static int getIndex(ArrayList<String[]> rules, String[] production){
        int i;
        for (i = 0; i < rules.size(); i++){
            if (rules.get(i)[0].equals(production[0]) && rules.get(i)[1].equals(production[1])){
                return i;
            }
        }
        return -1;
    }

    public static void unitRuleRecursion(ArrayList<String[]> rules, String[] production, int oldProductionIndex) {
        for (int j = 0; j < rules.size(); j++) {
            if (rules.get (j)[0].equals (production[1])) {
                if (rules.get (j).length == 2 && Character.isLowerCase (rules.get (j)[1].charAt (0))) {
                    // S -> a
                    String[] rule = { production[0], rules.get (j)[1] };
                    rules.add (rule);
                    int ruleCount = 0;
                    int ruleRight = 0;
                    for (int k = 0; k < rules.size (); k++) {
                        String[] currState = rules.get(k);
                        if (currState[0].equals (production[1])) {
                            ruleCount++;
                        }
                        int i;
                        for (i = 0; i < currState.length; i++){
                            if (currState[i].equals(production[1])){
                                ruleRight++;
                            }
                        }
                    }
                    if (ruleCount > 0 && ruleRight <= 1) {
                        rules.remove (j);
                        j--;
                    }
                }
                else if (rules.get (j).length == 2 && Character.isUpperCase (rules.get (j)[1].charAt (0))) {
                    String[] newProduction = { production[0], rules.get (j)[1] };
                    rules.remove (oldProductionIndex);
                    if (oldProductionIndex < j) {
                        j--;
                    }
                    rules.add(newProduction);
                    int tempAddedRule = rules.size() - 1;
                    unitRuleRecursion(rules, newProduction, tempAddedRule);
                    if (rules.get(tempAddedRule)[0].equals(newProduction[0]) && rules.get(tempAddedRule)[1].equals(newProduction[1])){
                        rules.remove(tempAddedRule);
                    }
                } else if (rules.get(j).length == 3) {
                    String[] rule = { production[0], rules.get (j)[1],
                            rules.get (j)[2] };
                    rules.add (rule);
                }
            }
        }
        if (rules.get(oldProductionIndex)[0].equals(production[0]) && rules.get(oldProductionIndex)[1].equals(production[1])){
            rules.remove(oldProductionIndex);
        }
    }

    public static ArrayList<String[]> findUnitProductions (ArrayList<String[]> rules) {
        ArrayList<String[]> unitProductions = new ArrayList<String[]> ();
        int i;
        for (i = 0; i < rules.size (); i++) {
            if (rules.get (i).length == 2 && Character.isUpperCase (rules.get (i)[1].charAt (0))) {
                unitProductions.add (rules.get (i));
            }
        }
        return unitProductions;
    }

    public static void epsilonRule(ArrayList<String[]> rules) {
        for (int i = 0; i < rules.size (); i++) {
            if (rules.get (i)[1].equals ("e")) {

                String nullNonTerminal = rules.get(i)[0];
                rules.remove(i);

                removeEpsilon (rules, nullNonTerminal);
            }
        }
        System.out.println("Epsilon Rules: ");
        int i;
        for (i = 0; i < rules.size(); i++);{
            String[] rule = rules.get(i);
            int j;
            for ( j = 0; j < rule.length; j++){
                System.out.print(rule[j] + " ");
            }
            System.out.println();
        }
    }

    public static void removeEpsilon (ArrayList<String[]> rules, String nullNonTerminal) {

        for (int j = 0; j < rules.size (); j++) {

            if (rules.get (j)[1].equals (nullNonTerminal)) {
                if (rules.get (j).length == 3) {
                    if (rules.get (j)[2].equals (nullNonTerminal)) {
                        // case1
                        String newNullNonTerminal = rules.get (j)[0];
                        // recursion on newNull
                        if (!isDoubleNonTerminal (rules, nullNonTerminal)) {
                            rules.remove (j);

                            removeEpsilon (rules, newNullNonTerminal);
                        }
                    }
                    else {
                        // case 3
                        String[] newRule = { rules.get (j)[0], rules.get (j)[2] };
                        if (isDoubleNonTerminal (rules, nullNonTerminal)) {
                            rules.add (j, newRule);
                            j++;
                        } else
                            rules.set (j, newRule);
                    }
                }
                else {
                    // case 2
                    String newNullNonTerminal = rules.get (j)[0];
                    // recursion
                    if (!isDoubleNonTerminal (rules, nullNonTerminal)) {
                        rules.remove (j);

                        removeEpsilon (rules, newNullNonTerminal);
                    }
                }
            } else if (rules.get (j).length == 3) {
                if (rules.get (j)[2].equals (nullNonTerminal)) {

                    // case 4
                    String[] newRule = { rules.get (j)[0], rules.get (j)[1] };
                    if (isDoubleNonTerminal (rules, nullNonTerminal)) {
                        rules.add (j, newRule);
                        j++;
                    } else
                        rules.set (j, newRule);
                }
            }
        }
    }

    public static boolean isDoubleNonTerminal (ArrayList<String[]> rules, String NonTerminal) {
        int count = 1;
        int i;
        for (i = 0; i < rules.size (); i++) {
            if (rules.get (i)[0].equals (NonTerminal))
                count++;
        }
        // System.out.println(count);
        if (count > 1)
            return true;
        return false;
    }

    public static void checkS(ArrayList<String[]> rules) {
        boolean thereIsS = false;
        int i;
        for (i = 0; i < rules.size (); i++) {
            String[] rule = rules.get (i);
            for (int j = 1; j < rule.length; j++) {
                if (rule[j].equals ("S")) {
                    thereIsS = true;
                    break;
                }
            }
        }
        if (thereIsS) {
            for (i = 0; i < rules.size (); i++) {
                String[] rule = rules.get (i);
                for (int j = 0; j < rule.length; j++) {
                    if (rule[j].equals ("S")) {
                        rule[j] = "S_0";
                    }
                }
            }
            String[] SigmaRule = { "S", "S_0" };
            rules.add (SigmaRule);
        }
        System.out.println("Check S: ");
        for (i = 0; i < rules.size(); i++);{
            String[] rule = rules.get(i);
            int j;
            for ( j = 0; j < rule.length; j++){
                System.out.print(rule[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void nonTerminals(ArrayList<String[]> rules) {
        int count = 0;
        for (int i = 0; i < rules.size (); i++) {
            while (rules.get (i).length > 3) {
                int n = rules.get (i).length;
                String[] g = new String[3];
                g[0] = "P" + count;
                g[1] = rules.get (i)[n - 2];
                g[2] = rules.get (i)[n - 1];

                rules.add (g);

                String[] h = new String[n - 1];
                for (int j = 0; j < n - 2; j++)
                {
                    h[j] = rules.get (i)[j];
                }
                h[n - 2] = "P" + count;
                count++;
                rules.remove (i);
                rules.add (h);
            }
        }
        System.out.println("Non Terminals: ");
        int i;
        for (i = 0; i < rules.size(); i++);{
            String[] rule = rules.get(i);
            int j;
            for ( j = 0; j < rule.length; j++){
                System.out.print(rule[j] + " ");
            }
            System.out.println();
        }
    }

    public static void allProductions(ArrayList<String[]> rules) {
        int s = rules.size ();
        for (int i = 0; i < s; i++) {
            if (rules.get (i).length > 2) {
                for (int j = 0; j < rules.get (i).length; j++) {
                    char n = rules.get (i)[j].charAt (0);
                    if (Character.isLowerCase (n)) {
                        String[] g = new String[2];
                        g[0] = rules.get (i)[j].toUpperCase () + "_0";
                        g[1] = rules.get (i)[j];
                        boolean isAlreadyDefined = false;
                        int k;
                        for (k = 0; k < rules.size (); k++) {
                            if (g[0].equals (rules.get (k)[0]))
                                isAlreadyDefined = true;
                        }
                        if (!isAlreadyDefined)
                            rules.add (g);
                        rules.get (i)[j] = rules.get (i)[j].toUpperCase () + "_0";
                    }
                }
            }
        }
        System.out.println("All Productions: ");
        int i;
        for (i = 0; i < rules.size(); i++);{
            String[] rule = rules.get(i);
            int j;
            for ( j = 0; j < rule.length; j++){
                System.out.print(rule[j] + " ");
            }
            System.out.println();
        }
    }

    public int size() {
        return this.rules.size();
    }

    public String[] get(int i) {
        return this.rules.get(i);
    }


    public ArrayList<String[]> readRules(String grammarFile) {
        ArrayList<String[]> rules = new ArrayList<String[]> ();
        try {
            FileReader fr = new FileReader (grammarFile);
            BufferedReader br = new BufferedReader (fr);
            String line = br.readLine ();
            while (line != null) {
                String[] rule = line.split (" ");
                rules.add (rule);
                line = br.readLine ();
            }

        } catch (Exception e) {
            System.err.println ("Error in reading txt file");
            return null;
        }
        return rules;
    }
}

/*      Main test = new Main("var25.txt");
        // for (int i = 0; i < test.size (); i++) {
            String[] rule = test.get (i);
            for (int j = 0; j < rule.length; j++) {
                System.out.print (rule[j] + " ");
            }
            System.out.println ();
        }
 */
