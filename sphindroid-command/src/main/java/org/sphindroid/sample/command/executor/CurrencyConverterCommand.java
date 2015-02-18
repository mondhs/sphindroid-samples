package org.sphindroid.sample.command.executor;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mgreibus on 14.3.23.
 */
public class CurrencyConverterCommand implements GeneralCommand {

    static final String EURO_CONVERTER = "Paversk litą eurais";

    static final Pattern EURO_CONVERTER_PATTERN = Pattern.compile("paversk (\\w+) (\\w+) (\\w+)");



    private final static Map<String, Integer> DIGITS = new HashMap<String, Integer>();

    static {
        DIGITS.put("vienas", 1);
        DIGITS.put("du", 2);
        DIGITS.put("trys", 3);
        DIGITS.put("keturi", 4);
        DIGITS.put("penki", 5);
        DIGITS.put("šeši", 6);
        DIGITS.put("septyni", 7);
        DIGITS.put("aštuoni", 8);
        DIGITS.put("devyni", 9);
    }


    @Override
    public String execute(String command, Context context) {
        Matcher matcher = EURO_CONVERTER_PATTERN .matcher(command);
        String currencyAmountStr = "nulis";
        String currencyFrom = "euras";
        String currencyTo = "litais";

        if (matcher.find()) {
            currencyAmountStr = matcher.group(1);
            currencyFrom = matcher.group(2);
            currencyTo = matcher.group(3);
        }else{
            return "Nesuprantau: " + command;
        }
        Integer currencyAmount = DIGITS.get(currencyAmountStr);
        Float resultEuroFloat = 0F;
        if(currencyTo.startsWith("eur")){
            resultEuroFloat = currencyAmount.floatValue() / 3.4528f;
            currencyTo = "eurų";
        }else{
            resultEuroFloat = currencyAmount.floatValue() * 3.4528f;
            currencyTo = "litų";
        }


        Double newEuroRound = Math.round(resultEuroFloat * 100.0) / 100.0;
        DecimalFormat df = new DecimalFormat("###.##");
        String newEuroStr = df.format(newEuroRound);

        if (newEuroStr == null) {
            return "Nesuskaičiuoju: " + command;
        }
        return "Tai bus " + newEuroStr + " " + currencyTo;
    }

    @Override
    public boolean isSupport(String command) {
        if (EURO_CONVERTER_PATTERN .matcher(command).find()) {
//        if("kiek pinigų vienas euras litais".equals(command)){
            //String digitOperationDigit = command.replace("Kiek pinigų ", "").replace("litais", "").trim();
            //String[] instruction = digitOperationDigit.split("\\s+");
            return true;//instruction.length == 3;
        }
        return false;

    }

    @Override
    public String retrieveCommandSample() {
        return EURO_CONVERTER;
    }
}
