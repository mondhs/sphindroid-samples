package org.sphindroid.sample.call.service.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.SfdCoreFactory;
import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;
import org.sphindroid.sample.call.dto.AsrContact;
import org.sphindroid.sample.call.dto.AsrContactMatch;
import org.sphindroid.sample.call.service.AsrContantDaoImpl;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ContactsCommand extends AbstractTtsAsrCommand {
	private static final Logger LOG= LoggerFactory.getLogger(ContactsCommand.class);
	
	public static final String KEY_COMMAND = "SKAMBINK";
	private AsrContantDaoImpl asrContantService;

	public ContactsCommand(Context context,
			AsrContantDaoImpl asrContantService) {
		super(context);
		this.asrContantService= asrContantService;
	}

	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		String cmd = commandDto.getCommandName().toUpperCase();
		return cmd.startsWith(KEY_COMMAND);
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dictionary = new HashSet<String>();
		dictionary.add(KEY_COMMAND);
		dictionary.addAll(createNamesDative(asrContantService.findContacts()));
		return dictionary;
	}


	@Override
	public Map<String,String> getCommandMap() {
		Map<String,String> cmdMap= new HashMap<String,String>();
//		cmdMap.put("CONTACT_VIEW", "<name>");
		cmdMap.put("CONTACT_CALL", KEY_COMMAND + " <name>");
		return cmdMap;
	}
	
	@Override
	public Map<String, String> getAditionalGrammarMap() {
		Map<String,String> cmdMap= new HashMap<String,String>();
		cmdMap.put("name", createContactsRepresentation(asrContantService.findContacts()).toString());
		return cmdMap;
	}
	
	public StringBuilder createContactsRepresentation(Collection<AsrContact> aContacts) {
        String separator = "";
        StringBuilder sb = new StringBuilder();
        Set<String> nameDative = createNamesDative(aContacts);
		for (String name : nameDative) {
			sb.append(separator).append(name);
			separator = " | ";
		}
        return sb;
	}


	private Set<String> createNamesDative(Collection<AsrContact> aContacts) {
		Set<String> names = new HashSet<String>();
        LithuanianGrammarHelperImpl grammarHelper = SfdCoreFactory.getInstance().createLithuanianGrammarHelper();
		for (AsrContact contact : aContacts) {
			names.add(contact.getFamilyName());
			names.add(contact.getGivenName());
        }
		Set<String> nameDativeSet = new HashSet<String>();
		for (String name : names) {
			if(name == null){
				continue;
			}
			String nameDative = grammarHelper.makeNounToDat(name.toUpperCase());
			if(nameDative !=null){
				nameDativeSet.add(nameDative);
			}
		}
		return nameDativeSet;
	}

	@Override
	public AsrCommandResult execute(AsrCommandParcelable cmd) {
			AsrContact contact = asrContantService.findContactById(cmd.getId());
			LOG.debug("callContact by id ({})", contact);
			if(contact == null){
				contact = findByName(cmd.getCommandName());
			}
			LOG.debug("callContact by name ({})", contact);
			if(contact != null){
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + contact.getNumber()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
				return new AsrCommandResult(true);
			}
		return new AsrCommandResult(false);
	}

	private AsrContact findByName(String commandName) {
		AsrContact contact = null;
		LithuanianGrammarHelperImpl grammarHelper = SfdCoreFactory.getInstance().createLithuanianGrammarHelper();
		String callName = commandName;
		callName = callName.replaceAll(KEY_COMMAND, "");
		callName = callName.replace(" ", "");
		String nameRoot = grammarHelper.stripDatEnding(callName);
		Collection<AsrContactMatch> contancts = asrContantService.findContactGivenOrFamilyNameStartsWith(nameRoot);
		if(contancts.size()==1){
			contact = contancts.iterator().next().getAsrContact();
		}else if(contancts.size()==2){
			LOG.error("[execute] multiple contanct availbe fo for {}", commandName);
			StringBuilder speakMsg = new StringBuilder();
			speakMsg.append("Kuris ");
			String separator = "";
			for (AsrContactMatch asrContactMatch : contancts) {
				speakMsg.append(separator);
				speakMsg.append(asrContactMatch.getDetailedName());
				separator  = " ar ";
			}
			speak(speakMsg.toString());
		}else if(contancts.size()>2){
			speak("Radau "+contancts.size()+ " " +callName);
		}else{//contancts.size()==0
			LOG.debug("[execute] contanct for {}. not found", commandName);
			speak("Nerandu " + commandName);
		}
		return contact;
		
	}

}
