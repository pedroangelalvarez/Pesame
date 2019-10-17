package com.aim.pesame;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

        private static final String TAG = "MyActivity" ;
        private CameraBridgeViewBase mOpenCvCameraView;


        //checks if opencv manager and the libray is installed on your devices and if not it will ask permission to install it.
        private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

            @Override
            //check that you are able to use opencv
            public void onManagerConnected(int status) {
                switch (status) {
                    //if you can initialize openCV successfully then record so in log
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");
                        mOpenCvCameraView.enableView();
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        @Override
        public void onResume()
        {
            super.onResume();

            // Loads and initializes OpenCV library using OpenCV Manager service
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            //when using the app keep the screen active
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.activity_main);
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_view);
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);

        }

        @Override
        public void onPause()
        {
            super.onPause();
            if (mOpenCvCameraView != null)
                //disable camera connection and stop the delivery of frames
                mOpenCvCameraView.disableView();
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mOpenCvCameraView != null)
                //disable camera connection and stop the delivery of frames
                mOpenCvCameraView.disableView();
        }
        public void onCameraViewStarted(int width, int height) {
            //Display the image constantly regardless of phone orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onCameraViewStopped(){}

        @Override
        //take image frame from camera modify it and display it on the screen.
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            // take captures image frame from camera and turn it into grayscale
            Mat frameRGB = inputFrame.rgba();
            Mat frameGray = new Mat(frameRGB.rows(), frameRGB.cols(), CvType.CV_8UC1);
            Imgproc.cvtColor(frameRGB, frameGray, Imgproc.COLOR_BGRA2GRAY);
            HOGDescriptor hog = new HOGDescriptor();
            hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

            MatOfRect cajas = new MatOfRect();
            MatOfDouble pesos = new MatOfDouble();
            hog.detectMultiScale(frameGray, cajas, pesos);
            Rect [] rectas = cajas.toArray();
            if (rectas.length >0) {
                for (Rect rect : cajas.toArray()) {
                    Imgproc.rectangle(frameRGB, new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(20, 255, 120),5);
                }
            }
            //hog.detectMultiScale(frameRGB, found, foundDouble, 0, new Size(8,8), new Size(32,32), 1.05, 2 );
            return frameRGB;
        }

    }