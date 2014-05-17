package org.sphindroid.sample.command.executor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by mgreibus on 14.3.23.
 */
public class FlashlightCommand implements GeneralCommand{

    private static final String TAG = FlashlightCommand.class.getSimpleName();
    private static final String TURN_LIGHT_ON= "įjunk šviesą";
    private static final String TURN_LIGHT_OFF= "išjunk šviesą";
//    private final Context context;
//    private Camera camera = null;
    private boolean isFlashOn =false;

    public Camera createCamera(Context context){
        boolean hasFlash = context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        Camera camera = null;
        if (hasFlash) {
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                Log.e(TAG, "Camera Error. Failed to Open. Error: ", e);
            }
        }
        return camera;
    }



    @Override
    public String execute(String command, Context context) {
        Camera camera = createCamera(context);
        if(camera == null){
            return "Negaliu perjungti šviesą";
        }
        String rtn = changeFlashState(camera);
        camera.release();
        return rtn;
    }

    @Override
    public boolean isSupport(String command) {
        return TURN_LIGHT_ON.equals(command) || TURN_LIGHT_OFF.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return TURN_LIGHT_ON + ". " + TURN_LIGHT_OFF;
    }


//    private Camera getCamera() {
//        return this.camera;
//    }

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
            params = aCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            aCamera.setParameters(params);
            aCamera.stopPreview();
            isFlashOn = false;
            return "šviesa išjungta";
        }
    }

}
