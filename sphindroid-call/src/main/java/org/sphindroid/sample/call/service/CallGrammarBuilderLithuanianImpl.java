package org.sphindroid.sample.call.service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.sphindroid.core.service.grammarbuilder.AbstractAsrGrammarBuilder;
import org.sphindroid.core.service.impl.GraphemeToPhonemeMapperLithuanianImpl;
import org.sphindroid.sample.call.dto.CallAsrGrammar;
import org.sphindroid.sample.call.service.command.AsrCommandProcessor;

public class CallGrammarBuilderLithuanianImpl extends AbstractAsrGrammarBuilder<CallAsrGrammar> {
	private AsrContantDaoImpl asrContantService;
	private GraphemeToPhonemeMapperLithuanianImpl graphemeToPhonemeMapper;
	
	private Collection<AsrCommandProcessor> commands;
	
	public CallGrammarBuilderLithuanianImpl(Collection<AsrCommandProcessor> commands) {
		this.commands=commands;
	}

	@Override
	public CallAsrGrammar createAsrGrammar() {
		CallAsrGrammar callAsrGrammar = super.createAsrGrammar();
		Set<String> dictionary = new HashSet<String>();
		for (AsrCommandProcessor cmd : commands) {
			dictionary.addAll(cmd.getDictionary());
		}
		callAsrGrammar.setDictionary(dictionary);
		return callAsrGrammar;
	}
	
	@Override
	public StringBuilder buildGrammarBody(CallAsrGrammar asrGrammar) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append("public <COMMAND> = ");
		String separator = "";
		StringBuilder sbContent = new StringBuilder();
		for (AsrCommandProcessor cmd : commands) {
			for (Entry<String, String> cmdEntry : cmd.getCommandMap().entrySet()) {
				sbHeader.append(MessageFormat.format("{0} <{1}>", separator, cmdEntry.getKey()));
				sbContent.append(MessageFormat.format("<{0}> = {1};\n", cmdEntry.getKey(), cmdEntry.getValue()));
				separator = " | ";
			}
			for (Entry<String, String> cmdEntry : cmd.getAditionalGrammarMap().entrySet()) {
				sbContent.append(MessageFormat.format("<{0}> = {1};\n", cmdEntry.getKey(), cmdEntry.getValue()));
			}
		}
		sbHeader.append(";\n");
		sb.append(sbHeader).append(sbContent);
		return sb;
	}
	
	@Override
	public StringBuilder buildDictionary(CallAsrGrammar asrGrammar) {
		StringBuilder sb = new StringBuilder();
		for (String graphemeWord : asrGrammar.getDictionary()) {
			List<String> phonemeWord = getGraphemeToPhonemeMapper().transform(graphemeWord);
			StringBuilder phonemeString = joinPhonemes(phonemeWord);
			sb.append(graphemeWord).append("\t").append(phonemeString).append("\n");
		}
		return sb;
	}
	
	private StringBuilder joinPhonemes(List<String> phonemeWord) {
		StringBuilder sb = new StringBuilder();
		String separator = "";
		for (String phoneme : phonemeWord) {
			sb.append(separator).append(phoneme);
			separator = " ";
		}
		return sb;
	}
	
	public void setAsrContantService(AsrContantDaoImpl asrContantService) {
		this.asrContantService = asrContantService;
	}
	public GraphemeToPhonemeMapperLithuanianImpl getGraphemeToPhonemeMapper() {
		return graphemeToPhonemeMapper;
	}

	public void setGraphemeToPhonemeMapper(
			GraphemeToPhonemeMapperLithuanianImpl graphemeToPhonemeMapper) {
		this.graphemeToPhonemeMapper = graphemeToPhonemeMapper;
	}

	public AsrContantDaoImpl getAsrContantService() {
		return asrContantService;
	}
}
