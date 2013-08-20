package org.sphindroid.core.service;

import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;

public class CoreFactory {

	private static CoreFactory factory;
	private LithuanianGrammarHelperImpl lithuanianGrammarHelper;
	
	public static CoreFactory getInstance(){
		if(factory == null){
			factory = new CoreFactory();
		}
		return factory;
	}


	
	public LithuanianGrammarHelperImpl createLithuanianGrammarHelper(){
		if(lithuanianGrammarHelper == null){
			lithuanianGrammarHelper = new LithuanianGrammarHelperImpl();
		}
		return lithuanianGrammarHelper;
	}
}
