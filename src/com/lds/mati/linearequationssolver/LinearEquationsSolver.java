/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lds.mati.linearequationssolver;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mati
 */
public class LinearEquationsSolver {

    final private static String validationRegex = "(?:(?:(?:[ ]*[+-]?[ ]*(?:(?:\\d+(?:.\\d+)?)|\\d+(?:.\\d+)?[ ]*[*/][ ]*\\d+(?:.\\d+)?)?)[ ]*[*]?[ ]*[a-zA-Z](?!(?:[ ]+\\*)))+[ ]*=[ ]*[-]?[ ]*(?:\\d+(?:.\\d+)?)[ ]*(?:[*/][ ]*[-]?[ ]*\\d+(?:\\.\\d+)?)?\\s*)+";
    final private static String dataMiningRegex = "([+-]?)(?:([ ]*\\d+(?:\\.\\d+)?)(?:(/|\\*)[ ]*([+-]?)[ ]*(\\d+(?:\\.\\d+)?))?)?[ ]*[*]?[ ]*([a-zA-Z])|=[ ]*([-]?)[ ]*(\\d+(?:\\.\\d+)?)[ ]*(?:([*/])[ ]*([-])?[ ]*(\\d+(?:\\.\\d+)?))?";

    public static Map<String, Double> solveLinearEquations(ArrayList<Map<String, Double>> equations) {
        TreeSet<String> variables = new TreeSet<>();
        Map<String, Double> results = new HashMap<>();
        Matrix core,equationsResults,solvedEquations;
        
        for (Map<String, Double> equation : equations) {
            variables.addAll(equation.keySet());
        }
        variables.remove("=");

        core = new Matrix(variables.size(), variables.size());
        equationsResults = new Matrix(equations.size(), 1);

        for (int i = 0; i < equations.size(); ++i) {
            Map<String, Double> equation = equations.get(i);
            int j = 0;
            for (String var : variables) {
                if (equation.containsKey(var)) {
                    core.set(i, j, equation.get(var));
                }
                ++j;
            }
            equationsResults.set(i, 0, equation.get("="));
        }
        solvedEquations = core.inverse().times(equationsResults);
        
        Iterator<String> iter = variables.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            results.put(iter.next(), solvedEquations.get(i, 0));
        }

        return results;
    }

    public static ArrayList<Map<String, Double>> parseEquations(String input) {
        if (!isValidEquations(input)) {
            return null;
        }

        Pattern data = Pattern.compile(dataMiningRegex);
        Matcher mat2 = data.matcher(input);
        return bigFatEquationAutomata(mat2);
        
    }

    private static ArrayList<Map<String, Double>> bigFatEquationAutomata(Matcher mat2) throws NumberFormatException {
        ArrayList<Map<String, Double>> equations = new ArrayList<>();
        double d1, d2, result, result2;
        String var;
        Map<String, Double> singleEquation = new HashMap<>();

        while (mat2.find()) {
            if (mat2.group(6) != null) {
                var = mat2.group(6);
                if (mat2.group(2) != null) {
                    d1 = Double.parseDouble(mat2.group(2));
                    d1 = mat2.group(1).equals("-") ? -d1 : d1;
                    if (mat2.group(3) != null) {
                        d2 = Double.parseDouble(mat2.group(5));
                        d2 = mat2.group(4).equals("-") ? -d2 : d2;
                        if (mat2.group(3).equals("*")) {
                            if (!singleEquation.containsKey(var)) {
                                singleEquation.put(var, d1 * d2);
                            } else {
                                singleEquation.put(var, singleEquation.get(var) + d1 * d2);
                            }
                        } else {
                            if (!singleEquation.containsKey(var)) {
                                singleEquation.put(var, d1 / d2);
                            } else {
                                singleEquation.put(var, singleEquation.get(var) + d1 / d2);
                            }
                        }
                    } else {
                        if (!singleEquation.containsKey(var)) {
                            singleEquation.put(var, d1);
                        } else {
                            singleEquation.put(var, singleEquation.get(var) + d1);
                        }
                    }
                } else {
                    if (mat2.group(1).equals("-")) {
                        if (!singleEquation.containsKey(var)) {
                            singleEquation.put(var, -1.);
                        } else {
                            singleEquation.put(var, singleEquation.get(var) - 1);
                        }
                    } else {
                        if (!singleEquation.containsKey(var)) {
                            singleEquation.put(var, 1.);
                        } else {
                            singleEquation.put(var, singleEquation.get(var) + 1);
                        }
                    }
                }
            } else {
                result = Double.parseDouble(mat2.group(8));
                result = mat2.group(7).equals("-") ? -result : result;
                if (mat2.group(9) != null) {
                    result2 = Double.parseDouble(mat2.group(11));
                    result2 = mat2.group(7).equals("-") ? -result2 : result2;
                    if (mat2.group(9).equals("/")) {
                        singleEquation.put("=", result * result2);
                    } else {
                        singleEquation.put("=", result / result2);
                    }
                } else {
                    singleEquation.put("=", result);
                }
                equations.add(singleEquation);
                singleEquation = new HashMap<>();
            }
        }
        return equations;
    }

    public static boolean isValidEquations(String input) {
        Pattern val = Pattern.compile(validationRegex);
        Matcher validator = val.matcher(input);
        if (validator.matches()) {
            return true;
        }
        return false;
    }
}
