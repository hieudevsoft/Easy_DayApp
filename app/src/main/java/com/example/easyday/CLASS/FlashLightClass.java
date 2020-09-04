package com.example.easyday.CLASS;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

public class FlashLightClass {
    private Camera mCamera;
    Context context;
    private CameraManager mCameraManager;
    private android.hardware.Camera.Parameters parameters;
    public FlashLightClass(Context context) {
        this.context = context;
    }
    public void turnFlashLightOn()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameraID = null;
            if(mCameraManager!=null)
            {
                try {
                    cameraID = mCameraManager.getCameraIdList()[0];
                    mCameraManager.setTorchMode(cameraID, true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            mCamera = android.hardware.Camera.open();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
    public void turnFlashLightOff()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameraID = null;
            if(mCameraManager!=null)
            {
                try {
                    cameraID = mCameraManager.getCameraIdList()[0];
                    mCameraManager.setTorchMode(cameraID, false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            mCamera = android.hardware.Camera.open();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
}
