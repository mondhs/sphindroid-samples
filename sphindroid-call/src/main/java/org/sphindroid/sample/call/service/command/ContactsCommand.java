package org.sphindroid.sample.call.service.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.SfdCoreFactory;
import org.sphindroid.sample.call.dto.AsrContact;
import org.sphindroid.sample.call.service.AsrContantDaoImpl;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ContactsCommand extends AbstractAsrCommand {
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
		for (AsrContact contact : asrContantService.findContacts()) {
			dictionary.add(createNicknameInDativeCase(contact));
		}
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
		for (AsrContact contact : aContacts) {

        	sb.append(separator).append(createNicknameInDativeCase(contact));
        	separator = " | ";
        }
        return sb;
	}


	private String createNicknameInDativeCase(AsrContact contact) {
		String nick = contact.getKeyword();
		if(nick == null){
			nick = contact.getFamilyName();
		}
		if(nick == null){
			String[] nameArr = contact.getDisplayName().split(" ");
			if(nameArr!=null && nameArr.length >2){
				nick = nameArr[nameArr.length-1];
			}
		}
		nick  = nick.toUpperCase();
		return SfdCoreFactory.getInstance().createLithuanianGrammarHelper().makeNounInDativeCase(nick);
	}
	
	@Override
	public boolean execute(AsrCommandParcelable cmd) {
			AsrContact contact = asrContantService.findContactById(cmd.getId());
			LOG.debug("callContact1({})", contact);
			if(contact == null){
				String callName = cmd.getCommandName();
				callName = callName.replaceAll(KEY_COMMAND, "");
				callName = callName.replace(" ", "");
				for (AsrContact iContact : asrContantService.findContacts()) {
					if(createNicknameInDativeCase(iContact).toUpperCase().contains(callName)){
						contact = iContact;
						break;
					}
				}
			}
			LOG.debug("callContact2({})", contact);
			if(contact != null){
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + contact.getNumber()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			}
		return true;
	}

}
