package org.sphindroid.sample.call.dto;

import java.util.Set;

import org.sphindroid.core.dto.AsrGrammar;

public class CallAsrGrammar extends AsrGrammar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5866411978270055232L;
	

	private Set<String> dictionary;
	


	public void setDictionary(Set<String> dictionary) {
		this.dictionary = dictionary;
	}

	public Set<String> getDictionary() {
		return dictionary;
	}

}
