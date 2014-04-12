package org.sphindroid.sample.command.executor;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgreibus on 14.3.23.
 */
public class CalculatorCommand implements GeneralCommand {

    static final String CALCULATOR = "SUSKAIČIUOK ";

    private static final String OP_PLUS = "PLIUS";
    private static final String OP_MINUS = "MINUS";
    private static final String OP_MULTIPLE = "KART";
    private static final String OP_DEVIDE = "DALINTI";

    private final static Map<String, Integer> DIGITS = new HashMap<String, Integer>();

    static {
        DIGITS.put("NULIS",0);
        DIGITS.put("VIENAS",1);
        DIGITS.put("DU",2);
        DIGITS.put("TRYS",3);
        DIGITS.put("KETURI",4);
        DIGITS.put("PENKI",5);
        DIGITS.put("ŠEŠI",6);
        DIGITS.put("SEPTYNI",7);
        DIGITS.put("AŠTUONI",8);
        DIGITS.put("DEVYNI",9);
    }


    @Override
    public String execute(String command, Context context) {
        String digitOperationDigit = command.replace("SUSKAIČIUOK ", "").replace("IŠ","").trim();
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
            String digitOperationDigit = command.replace("SUSKAIČIUOK ", "").replace("IŠ","").trim();
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
