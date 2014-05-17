package org.sphindroid.sample.command.executor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by mgreibus on 14.3.23.
 */
public class ViewNewsCommand implements GeneralCommand {
    private static final String DELFI = "delfio";
    private static final String LRT = "elertė";
    private static final String LRYTAS = "lietryčio";

//    private Context context;

    private static final String VIEW_NEWS = "rodyk naujienas";


    @Override
    public String execute(String command, Context context) {
        String chosenPortal = command.replace("rodyk", "").replace("naujienas", "").trim();
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
        return openBrowser(newPortal, portalName,context);
    }

    @Override
    public boolean isSupport(String command) {
        return command.startsWith("rodyk") && command.endsWith("naujienas");
    }

    @Override
    public String retrieveCommandSample() {
        return VIEW_NEWS;
    }

    private String openBrowser(String siteUrl, String portalName, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + siteUrl));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
        return "atidarau "+portalName;
    }


}
