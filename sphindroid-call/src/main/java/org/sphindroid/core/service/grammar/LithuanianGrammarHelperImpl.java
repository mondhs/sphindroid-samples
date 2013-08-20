package org.sphindroid.core.service.grammar;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.SparseArray;


public class LithuanianGrammarHelperImpl {
	
	public static final SparseArray<String> resolveNumberDivMap = new SparseArray<String>();
	/**
	 * http://ualgiman.dtiltas.lt/skaitvardis.html
	 */
	public static final SparseArray<String> resolveNumberModFemininumMap = new SparseArray<String>();
	public static final SparseArray<String> resolveNumberModMasculinumMap = new SparseArray<String>();
	public static Map<String, String> genitivusSingularis = new LinkedHashMap<String, String>();

	
	public static Map<String, String> nominativusPluralis = new LinkedHashMap<String, String>();


	static {
		genitivusSingularis.put("TĖ$", "ČIŲ");
		genitivusSingularis.put("A$", "Ų");
		genitivusSingularis.put("IS$", "IŲ");
		nominativusPluralis.put("TĖ$", "TĖS");
		nominativusPluralis.put("A$", "OS");
		nominativusPluralis.put("IS$", "IAI");
		
		resolveNumberDivMap.put(2, "dvidešimt");
		resolveNumberDivMap.put(3, "trisdešimt");
		resolveNumberDivMap.put(4, "keturesdešimt");
		resolveNumberDivMap.put(5, "penkiasdešimt");

		resolveNumberModFemininumMap.put(0, "nulis");
		resolveNumberModFemininumMap.put(1, "viena");
		resolveNumberModFemininumMap.put(2, "dvi");
		resolveNumberModFemininumMap.put(3, "trys");
		resolveNumberModFemininumMap.put(4, "keturios");
		resolveNumberModFemininumMap.put(5, "penkios");
		resolveNumberModFemininumMap.put(6, "šešios");
		resolveNumberModFemininumMap.put(7, "septynios");
		resolveNumberModFemininumMap.put(8, "aštuonios");
		resolveNumberModFemininumMap.put(9, "devynios");
		resolveNumberModFemininumMap.put(10, "dešimt");
		resolveNumberModFemininumMap.put(11, "vienuolika");
		resolveNumberModFemininumMap.put(12, "dvylika");
		resolveNumberModFemininumMap.put(13, "trylika");
		resolveNumberModFemininumMap.put(14, "keturiolika");
		resolveNumberModFemininumMap.put(15, "penkiolika");
		resolveNumberModFemininumMap.put(16, "šešiolika");
		resolveNumberModFemininumMap.put(17, "septyniolika");
		resolveNumberModFemininumMap.put(18, "aštuoniolika");
		resolveNumberModFemininumMap.put(19, "devyniolika");
		
		resolveNumberModMasculinumMap.put(0, "nulis");
		resolveNumberModMasculinumMap.put(1, "vienas");
		resolveNumberModMasculinumMap.put(2, "du");
		resolveNumberModMasculinumMap.put(3, "trys");
		resolveNumberModMasculinumMap.put(4, "keturi");
		resolveNumberModMasculinumMap.put(5, "penki");
		resolveNumberModMasculinumMap.put(6, "šeši");
		resolveNumberModMasculinumMap.put(7, "septyni");
		resolveNumberModMasculinumMap.put(8, "aštuoni");
		resolveNumberModMasculinumMap.put(9, "devyni");
		resolveNumberModMasculinumMap.put(10, "dešimt");
		resolveNumberModMasculinumMap.put(11, "vienuolika");
		resolveNumberModMasculinumMap.put(12, "dvylika");
		resolveNumberModMasculinumMap.put(13, "trylika");
		resolveNumberModMasculinumMap.put(14, "keturiolika");
		resolveNumberModMasculinumMap.put(15, "penkiolika");
		resolveNumberModMasculinumMap.put(16, "šešiolika");
		resolveNumberModMasculinumMap.put(17, "septyniolika");
		resolveNumberModMasculinumMap.put(18, "aštuoniolika");
		resolveNumberModMasculinumMap.put(19, "devyniolika");
	}

	public String resolveNumber(int number, GenusEnum genus) {
		SparseArray<String> resolveNumberMod = resolveNumberModFemininumMap;
		if(GenusEnum.masculine.equals(genus)){
			resolveNumberMod = resolveNumberModMasculinumMap;
		}
		String rtn = "";
		int numberDiv = number / 10;
		int numberMod = number % 10;
		if (number < 20) {
			rtn = resolveNumberMod.get(number);
		} else if (numberDiv == 0) {// 0-9
			rtn = resolveNumberMod.get(numberMod);
		} else if (numberDiv == 1) {// #10-19
			rtn = "" + number;
		} else if (numberMod == 0) {// 20,30,40,50
			rtn = resolveNumberDivMap.get(numberDiv);
		} else {
			rtn = "" + resolveNumberDivMap.get(numberDiv) + " "
					+ resolveNumberMod.get(numberMod);
		}

		return rtn;
	}
	
	/**
	 * lt: Naudininkas. en: Dative Case
	 * @param contact
	 * @return
	 */
	public String makeNounInDativeCase(String aNoun) {
		String noun = aNoun;
		if(noun.endsWith("AS")){
			noun = noun.replaceFirst("AS$", "UI");
		}else if(noun.endsWith("A")){
			noun = noun.replaceFirst("A$", "AI");
		}else if(noun.endsWith("IS")){
			noun = noun.replaceFirst("IS$", "IUI");
		}else if(noun.endsWith("Ė")){
			noun = noun.replaceFirst("Ė$", "EI");
		}
		return noun;
	}
	
	public String matchNounToNumerales(int number, String nounSingularNominative) {
//		String noun = "minutė";

		String nounNominativusPluralis = null; 
		String nounGenitivusSingularis = null;
				
		String rtn = nounSingularNominative.toUpperCase();
		for (Entry<String, String> ending : genitivusSingularis.entrySet()) {
			if(rtn.matches(".*"+ending.getKey())){
				nounGenitivusSingularis = rtn.replaceFirst(ending.getKey(), ending.getValue());
				nounNominativusPluralis = rtn.replaceFirst(ending.getKey(), nominativusPluralis.get(ending.getKey()));
			}
		}
		if(nounNominativusPluralis == null){
			return nounSingularNominative;
		}
		
		int minutesDiv = number / 10;
		int minutesMod = number % 10;
		if (minutesMod == 0) { // 0, 10, 20, 30, 40, 50
			rtn = nounGenitivusSingularis;//minučių
		} else if (minutesDiv == 1) {// 10-19
			rtn = nounGenitivusSingularis;//minučių, laipsnių, valandų
		} else if (minutesMod == 1) {// 1, 21,
			rtn = nounSingularNominative;//"minutė";
		}else{
			rtn = nounGenitivusSingularis;//"minutės";
		}
		return rtn;
	}
	
}
