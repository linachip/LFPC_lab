package com.lab1;


public class Main {

    public static void main(String[] args){
        String regularGrammar = "Vn={S, A, B}, Vt={a, b, c, d},\n"+
                "S->bS\n"+
                "S->dA\n"+
                "A->aA\n"+
                "A->dB\n"+
                "B->cB\n"+
                "A->b\n"+
                "B->a}";

        combination("bdadc");
    }

    public static boolean calculations(String input) {
        char state = 'S';
        for (char c : input.toCharArray()) {
            switch (c) {
                case 'a':
                    if (state == 'A') state = 'A';
                    else return false;
                    break;
                case 'b':
                    if (state == 'S') state = 'S';
                    else return false;
                    break;
                case 'c':
                    if (state == 'B') state = 'B';
                    else return false;
                    break;
                case 'd':
                    if (state == 'S') state = 'A';
                    else if (state == 'A') state = 'B';
                    else return false;
                    break;
                default:
                    return false;

            }
        }
        return state == 'A' || state == 'B';
    }

    public static void combination (String s){
        System.out.printf("%s: %s", s, calculations(s) ? "Accepted" : "Rejected");
    }
}
