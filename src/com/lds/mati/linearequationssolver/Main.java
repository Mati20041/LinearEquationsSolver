/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lds.mati.linearequationssolver;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mati
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String input = "a+b=0 \n 2a+b+c+d=0 \n a+b+2d=0 \n b+c+d=1";
        if(LinearEquationsSolver.isValidEquations(input)){
            ArrayList<Map<String, Double>> equations = LinearEquationsSolver.parseEquations(input);
            Map<String, Double> result = LinearEquationsSolver.solveLinearEquations(equations);
            
            for(Map.Entry<String,Double> entry : result.entrySet()){
                System.out.printf("%s = %.4f\n",entry.getKey(),entry.getValue());
            }
        }
        else 
            System.err.println("Wprowadzone wyrażenie nie jest układem równań liniowych");
    }
}
