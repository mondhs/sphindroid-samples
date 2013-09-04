package org.sphindroid.sample.call.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.sphindroid.sample.call.dto.AsrContact;
import org.sphindroid.sample.call.dto.AsrContactMatch;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;

public class AsrContantDaoImpl {

	private ContentResolver contentResolver;
	
	List<AsrContact> contactsCached = null;

	public List<AsrContact> findContacts() {
		if(contactsCached != null){
			return contactsCached;
		}
		List<AsrContact> contacts = new LinkedList<AsrContact>();

		Uri select = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME };
		String where = "(1) " +
				"AND (" + Contacts.IN_VISIBLE_GROUP + " = '1') " +
				"AND (" + Contacts.HAS_PHONE_NUMBER+" = '1' ) " +
				"AND (" + Contacts.STARRED + " = '1' )";
		String[] whereParams = null;
		String sortOrder = Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor people = contentResolver.query(select, projection, where,
				whereParams, sortOrder);
		while (people.moveToNext()) {
			contacts.add(parseToContact(people));
		}
		people.close();
		contactsCached = contacts;
		return contactsCached;
	}
	/**
	 * 
	 * @param contactId
	 * @return
	 */
	public AsrContact findContactById(Long contactId) {
		List<AsrContact> contancts = contactsCached;
		if(contancts != null){
			contancts = findContacts();
		}
		for (AsrContact asrContact : contancts) {
			if(asrContact.getId().equals(contactId)){
				return asrContact;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param contactId
	 * @return
	 */
	public Collection<AsrContactMatch> findContactGivenOrFamilyNameStartsWith(String name) {
		String aName = name.toUpperCase();
		List<AsrContact> contancts = contactsCached;
		List<AsrContactMatch> rtn = new LinkedList<AsrContactMatch>();
		if(contancts != null){
			contancts = findContacts();
		}
		for (AsrContact asrContact : contancts) {
			String given = asrContact.getGivenName();
			String family= asrContact.getFamilyName();
			if(safeUpperStartWith(given, aName)){
				rtn.add(new AsrContactMatch(asrContact, family));
			}else if(safeUpperStartWith(family,aName)){
				rtn.add(new AsrContactMatch(asrContact, given));
			}
		}
		return rtn;
	}
	
	private boolean safeUpperStartWith(String  expectedNalbleName, String startWithUpperName) {
		if(expectedNalbleName == null && startWithUpperName == null){
			return true;
		}else if(expectedNalbleName == null || startWithUpperName == null){
			return false;
		}else{
			return expectedNalbleName.toUpperCase().startsWith(startWithUpperName);
		}
	}
	/**
	 * 
	 * @param people
	 * @return
	 */
	private AsrContact parseToContact(Cursor people) {
		AsrContact asrContact = new AsrContact();
		
		int indexId = people
				.getColumnIndexOrThrow(Contacts._ID);
		int indexDisplayName = people
				.getColumnIndexOrThrow(Contacts.DISPLAY_NAME);
		
		asrContact.setDisplayName(people.getString(indexDisplayName));
		asrContact.setId(people.getLong(indexId));
		updateContactData(asrContact);
		updateAvatar(asrContact);
		updatePhoneNumberDetails(asrContact);
		return asrContact;
	}


	private void updatePhoneNumberDetails(AsrContact asrContact) {
		String contactId = asrContact.getId().toString();
		String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
				+ " = ?";
		String[] as = { ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.TYPE };
		String[] like = { contactId };
		Cursor phoneNumbers = contentResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, as, where,
				like, null);
	
		if (phoneNumbers != null) {
			int numberColumnIndex = phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	//		List<Contact.Number> phoneNumberList = new ArrayList<Contact.Number>();
			while (phoneNumbers.moveToNext()) {
				String phoneType = phoneNumbers
						.getString(phoneNumbers
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				if (phoneType == null) {
					phoneType = new String("0");
				}
				asrContact.setNumber(phoneNumbers.getString(numberColumnIndex));
				break;
			}
		}
		phoneNumbers.close();
	}
	
	/**
	 * 
	 * @param asrContact
	 */
	private void updateContactData(AsrContact asrContact) {
		String contactId = asrContact.getId().toString();
		Uri select = ContactsContract.Data.CONTENT_URI;
		String[] projection = new String[] { 
				StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME,
				};
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
		String[] whereParams = new String[]{contactId, StructuredName.CONTENT_ITEM_TYPE};
		String sortOrder = null;
		
		Cursor people = contentResolver.query(select, projection, where,
				whereParams, sortOrder);

		int indexGivenName = people
				.getColumnIndexOrThrow(StructuredName.GIVEN_NAME);
		int indexFamilyName = people
				.getColumnIndexOrThrow(StructuredName.FAMILY_NAME);
		
		if (people.moveToNext()) {
			asrContact.setFamilyName(people.getString(indexFamilyName));
			asrContact.setGivenName(people.getString(indexGivenName));
		}

		people.close();
	}
	
	private void updateAvatar(AsrContact asrContact) {
		String contactId = asrContact.getId().toString();
		Uri select = ContactsContract.Data.CONTENT_URI;
		String[] as = { ContactsContract.Data.CONTACT_ID };
		String where = ContactsContract.Data.CONTACT_ID + "= ? " + " AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] like = { contactId,
				ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE };
		Cursor cur = getContentResolver().query(select, as, where, like, null);

		if (cur != null) {
			if (cur.moveToFirst()) {
				Uri person = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						Long.parseLong(contactId));
				asrContact.setAvatar(Uri.withAppendedPath(person,
						ContactsContract.Contacts.Photo.CONTENT_DIRECTORY));
			}
		}
		
	}
	





	public void setContentResolver(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	public ContentResolver getContentResolver() {
		return contentResolver;
	}
}
