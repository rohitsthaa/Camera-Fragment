package surfaceview.rohit.com.surfaceview;


import android.hardware.Camera;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = "CamTestActivity";
    Preview preview;
    android.hardware.Camera camera;
    MainActivity activity;
    View view;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_main, container, false);

        preview = new Preview(this.getContext(), (SurfaceView) view.findViewById(R.id.surfaceView));

        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout fl = (FrameLayout) view.findViewById(R.id.layout);
        fl.addView(preview);
        preview.setKeepScreenOn(true);
        Button buttonClick = (Button) view.findViewById(R.id.screenshot);

        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
							/*preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);*/
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();

//        customCanvas.mBearingProvider.removeChangeEventListener();

        // Camera...
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }

    }
    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    @Override
    public void onResume() {
        super.onResume();

//        customCanvas.mBearingProvider.setChangeEventListener(customCanvas);


        // Camera...
        int numCams = android.hardware.Camera.getNumberOfCameras();
        if(numCams > 0) {
            try{
                camera = android.hardware.Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
                android.hardware.Camera.Parameters p = camera.getParameters();

            } catch (RuntimeException ex){

            }
        }
    }
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

}
