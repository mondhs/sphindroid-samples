package org.sphindroid.sample.call;

import java.util.List;

import org.sphindroid.core.service.AsrAssert;
import org.sphindroid.sample.call.service.SphindroidClientImpl;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;
import org.sphindroid.sample.call.service.aidl.AsrContactParcelable;
import org.sphindroid.sample.call.service.command.ContactsCommand;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {

	private Activity activity;
	private List<AsrContactParcelable> contacts;
	private SphindroidClientImpl sphindroidClient;
	private static LayoutInflater inflater = null;

	public ContactAdapter(Activity a, List<AsrContactParcelable> contacts, SphindroidClientImpl sphindroidClient) {
		this.sphindroidClient = sphindroidClient;
		this.activity = a;
		this.contacts = contacts;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null){
			vi = inflater.inflate(R.layout.contact_details, null);
		}
		View contactDetailHolder = vi
				.findViewById(R.id.contactDetailHolder);
		
		TextView contactDisplayName = (TextView) vi
				.findViewById(R.id.contactDisplayName);
		AsrAssert.isNotNull(contactDisplayName, "contactDisplayName cannot be found");
		ImageView contanctAvatar = (ImageView) vi
				.findViewById(R.id.contanctAvatar);

		AsrContactParcelable conact = contacts.get(position);

		// Setting all values in listview
		if(conact != null){
			contactDisplayName.setText(conact.getDisplayName());
			if(conact.getAvatar()!=null){
				Uri uri = Uri.parse(conact.getAvatar());
				contanctAvatar.setImageURI(uri);
				if(contanctAvatar.getDrawable() == null){
					contanctAvatar.setImageResource(R.drawable.ic_launcher);
				}
			}
			contactDetailHolder.setOnClickListener(new ItemClickListener(conact));
		}else{
			contactDisplayName.setText("");
		}
		return vi;
	}
	
	class ItemClickListener implements OnClickListener{
		
		private AsrContactParcelable conact;
		
		public ItemClickListener(AsrContactParcelable conact) {
			this.conact = conact;
		}
		@Override
		public void onClick(View v) {
			AsrCommandParcelable commandDto = new AsrCommandParcelable();
			commandDto.setCommandName(ContactsCommand.KEY_COMMAND);
			commandDto.setId(conact.getId());
			sphindroidClient.executeCommand(commandDto);
		}
		
	}
	
}
