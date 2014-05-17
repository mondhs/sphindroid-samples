package org.sphindroid.sample.command.executor;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgreibus on 14.3.23.
 */
public class CalculatorCommand implements GeneralCommand {

    static final String CALCULATOR = "suskaičiuok ";

    private static final String OP_PLUS = "plius";
    private static final String OP_MINUS = "minus";
    private static final String OP_MULTIPLE = "kart";
    private static final String OP_DEVIDE = "dalinti";

    private final static Map<String, Integer> DIGITS = new HashMap<String, Integer>();

    static {
        DIGITS.put("nulis",0);
        DIGITS.put("vienas",1);
        DIGITS.put("du",2);
        DIGITS.put("trys",3);
        DIGITS.put("keturi",4);
        DIGITS.put("penki",5);
        DIGITS.put("šeši",6);
        DIGITS.put("septyni",7);
        DIGITS.put("aštuoni",8);
        DIGITS.put("devyni",9);
    }


    @Override
    public String execute(String command, Context context) {
        String digitOperationDigit = command.replace("suskaičiuok ", "").replace("iš","").trim();
        String[] instruction = digitOperationDigit.split("\\s+");
        if(instruction.length != 3){
            return "Nesuprantau: " + command;
        }
        Integer digit1 = DIGITS.get(instruction[0]);
        Integer digit2 = DIGITS.get(instruction[2]);

        Integer resultInt = null;
        if(instruction[1].equals(OP_PLUS)){
            resultInt = digit1 + digit2;
        }else  if(instruction[1].equals(OP_MINUS)){
            resultInt = digit1 - digit2;
        }else  if(instruction[1].equals(OP_MULTIPLE)) {
            resultInt = digit1 * digit2;
        }else  if(instruction[1].equals(OP_DEVIDE)) {
            return "Dalyba per sunku";
        }
        if(resultInt == null){
            return "Nesuskaičiuoju: " + command;
        }
        return "gavau atsakymą " + resultInt;
    }

    @Override
    public boolean isSupport(String command) {
        if(command.startsWith(CALCULATOR)){
            String digitOperationDigit = command.replace("suskaičiuok ", "").replace("iš","").trim();
            String[] instruction = digitOperationDigit.split("\\s+");
            return  instruction.length == 3;
        }
        return false;

    }

    @Override
    public String retrieveCommandSample() {
        return CALCULATOR;
    }
}
