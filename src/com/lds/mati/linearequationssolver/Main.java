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
        // TODO code application logic here
        String input = "2*1a + b = 3 \n a + b = 1 / 1";
        String regex = "(?:(?:(?:[ ]*[+-]?[ ]*(?:(?:\\d+(?:.\\d+)?)|\\d+(?:.\\d+)?[ ]*[*/][ ]*\\d+(?:.\\d+)?)?)[ ]*[*]?[ ]*[a-zA-Z](?!(?:[ ]+\\*)))+[ ]*=[ ]*[-]?[ ]*(?:\\d+(?:.\\d+)?)[ ]*(?:[*/][ ]*[-]?\\d+(?:\\.\\d+)?)?\\s*)+";
        String regex2 = "([+-]?)(?:([ ]*\\d+(?:\\.\\d+)?)(?:(/|\\*)[ ]*([+-]?)[ ]*(\\d+(?:\\.\\d+)?))?)?[ ]*[*]?[ ]*([a-zA-Z])|=[ ]*([-]?)[ ]*(\\d+(?:\\.\\d+)?)[ ]*(?:([*/])[ ]*([-])?(\\d+(?:\\.\\d+)?))?";

        Pattern pat = Pattern.compile(regex);
        Pattern pat2 = Pattern.compile(regex2);

        Matcher mat = pat.matcher(input);
        System.out.println(mat.matches());
        if (!mat.matches()) {
            return;
        }
        Matcher mat2 = pat2.matcher(input);
        while (mat2.find()) {
            System.out.println("---------");
            for (int i = 0; i <= mat2.groupCount(); ++i) {
                System.out.printf("group(%d) = %s\n", i, mat2.group(i));
            }
            System.out.println("---------");
        }
        mat2.reset();
        ArrayList<Map<String, Double>> env = new ArrayList<>();
        ArrayList<Double> results = new ArrayList<>();
        double d1, d2, result, result2;
        String var;
        Map<String, Double> locEnv = new HashMap<>();
        TreeSet<String> variables = new TreeSet<>();
        mat2.reset();

        while (mat2.find()) {
            if (mat2.group(6) != null) {
                var = mat2.group(6);
                variables.add(var);
                if (mat2.group(2) != null) {
                    d1 = Double.parseDouble(mat2.group(2));
                    d1 = mat2.group(1).equals("-") ? -d1 : d1;
                    if (mat2.group(3) != null) {
                        d2 = Double.parseDouble(mat2.group(5));
                        d2 = mat2.group(4).equals("-") ? -d2 : d2;
                        if (mat2.group(3).equals("*")) {
                            if (!locEnv.containsKey(var)) {
                                locEnv.put(var, d1 * d2);
                            } else {
                                locEnv.put(var, locEnv.get(var) + d1 * d2);
                            }
                        } else {
                            if (!locEnv.containsKey(var)) {
                                locEnv.put(var, d1 / d2);
                            } else {
                                locEnv.put(var, locEnv.get(var) + d1 / d2);
                            }
                        }
                    } else {
                        if (!locEnv.containsKey(var)) {
                            locEnv.put(var, d1);
                        } else {
                            locEnv.put(var, locEnv.get(var) + d1);
                        }
                    }
                } else {
                    if (mat2.group(1).equals("-")) {
                        if (!locEnv.containsKey(var)) {
                            locEnv.put(var, -1.);
                        } else {
                            locEnv.put(var, locEnv.get(var) - 1);
                        }
                    } else {
                        if (!locEnv.containsKey(var)) {
                            locEnv.put(var, 1.);
                        } else {
                            locEnv.put(var, locEnv.get(var) + 1);
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
                        results.add(result * result2);
                    } else {
                        results.add(result / result2);
                    }
                } else {
                    results.add(result);
                }
                env.add(locEnv);
                locEnv = new HashMap<>();
            }
        }
        ArrayList<String> vars = new ArrayList<>(variables);
        Matrix m = new Matrix(results.size(), results.size());
        for (int i = 0; i < env.size(); ++i) {
            
            for (int j = 0; j < vars.size() ; ++j) {
                String key = vars.get(j);
                if (env.get(i).containsKey(key)) {
                    m.set(i, j, env.get(i).get(key));
                } else {
                    m.set(i, j, 0);
                }
            }
        }

        m = m.inverse();
        
        Matrix res = new Matrix( results.size(),1);
        for (int i = 0; i < results.size(); ++i) {
            res.set(i, 0, results.get(i));
        }

        res = m.times(res);
        
        for(int i = 0 ; i < res.getRowDimension() ; ++i){
            System.out.printf("%s = %.4f\n",vars.get(i),res.get(i, 0));
        }

        System.out.println("");
    }
}
