package org.sphinxdroid.demo.command;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by mgreibus on 14.3.23.
 */
public class ViewNewsCommand implements GeneralCommand {
    private static final String DELFI = "DELFIO";
    private static final String LRT = "ELERTĖ";
    private static final String LRYTAS = "LIETRYČIO";

    private final Context context;

    private static final String VIEW_NEWS = "RODYK NAUJIENAS";

    public ViewNewsCommand(Context context) {
        this.context = context;
    }

    @Override
    public String execute(String command) {
        String chosenPortal = command.replace("RODYK", "").replace("NAUJIENAS", "").trim();
        String newPortal = "m.delfi.lt";
        String portalName ="delfį";
        if(DELFI.equals(chosenPortal)){
            portalName = "delfį";
            newPortal = "m.delfi.lt";
        }else if(LRT.equals(chosenPortal)){
            portalName = LRT;
            newPortal = "lrt.lt";
        }else if(LRYTAS.equals(chosenPortal)){
            portalName = "Lietuvos rytą";
            newPortal = "m.lrytas.lt";
        }
        return openBrowser(newPortal, portalName);
    }

    @Override
    public boolean isSupport(String command) {
        return command.startsWith("RODYK") && command.endsWith("NAUJIENAS");
    }

    @Override
    public String retrieveCommandSample() {
        return VIEW_NEWS;
    }

    private String openBrowser(String siteUrl, String portalName) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + siteUrl));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(browserIntent);
        return "atidarau "+portalName;
    }

    public Context getContext() {
        return context;
    }
}
