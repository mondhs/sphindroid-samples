package org.sphinxdroid.demo.command;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by mgreibus on 14.3.23.
 */
public class FlashlightCommand implements GeneralCommand{

    private static final String TAG = FlashlightCommand.class.getSimpleName();
    private static final String TURN_LIGHT_ON= "ĮJUNK ŠVIESĄ";
    private static final String TURN_LIGHT_OFF= "IŠJUNK ŠVIESĄ";
    private final Context context;
    private Camera camera = null;
    private boolean isFlashOn =false;

    public FlashlightCommand(Context context){
        this.context = context;
        boolean hasFlash = getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                Log.e(TAG, "Camera Error. Failed to Open. Error: ", e);
            }
        }

    }



    @Override
    public String execute(String command) {
        if(camera == null){
            return null;
        }
        return changeFlashState(getCamera());
    }

    @Override
    public boolean isSupport(String command) {
        return TURN_LIGHT_ON.equals(command) || TURN_LIGHT_OFF.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return TURN_LIGHT_ON + ". " + TURN_LIGHT_OFF;
    }


    private Camera getCamera() {
        return this.camera;
    }

    /*
    * Turning On flash
    */
    private String changeFlashState(Camera aCamera) {
        if (aCamera == null) {
            return null;
        }
        Camera.Parameters params = aCamera.getParameters();
        if (!isFlashOn) {
            params = aCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            aCamera.setParameters(params);
            aCamera.startPreview();
            isFlashOn = true;
            return "šviesa įjungta";
        }else{
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            return "šviesa išjungta";
        }

    }

    public Context getContext() {
        return context;
    }
}
