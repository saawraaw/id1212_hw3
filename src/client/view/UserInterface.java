/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;


import java.util.Scanner;


/**
 *
 * @author Sarah
 */
public class UserInterface {
    private Command cmd;
    private static final String PARAM_DELIMETER = " ";
    private static final String PROMPT = "> ";
    private static final Scanner console = new Scanner(System.in);
   

    public Command getCmd(String enteredLine) {
        parseCmd(enteredLine);
        return cmd;
    }
    
    private void parseCmd(String enteredLine) {
        int cmdNameIndex = 0;
        try {
            String[] enteredTokens = removeExtraSpaces(enteredLine).split(PARAM_DELIMETER);
            cmd = Command.valueOf(enteredTokens[cmdNameIndex].toUpperCase());
        } catch (Throwable failedToReadCmd) {
            cmd = Command.ILLEGAL_COMMAND;
        }
    }
    
    private String removeExtraSpaces(String source) {
        if (source == null) {
            return source;
        }
        String oneOrMoreOccurences = "+";
        return source.trim().replaceAll(PARAM_DELIMETER + oneOrMoreOccurences, PARAM_DELIMETER);
    }


    public static String readNextLine() {
        print(PROMPT);
        return console.nextLine();
    }
    
    public static void print(String output) {
        System.out.print(output);
    }
    
    public static void println(String output) {
        System.out.println(output);
    }

  
}
