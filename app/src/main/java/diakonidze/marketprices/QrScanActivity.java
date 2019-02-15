package diakonidze.marketprices;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class QrScanActivity extends AppCompatActivity {

    private Context mContext = QrScanActivity.this;
    private String TAG = "mainActivity";

    CameraSource cameraSource;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    TextView textViewQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_page);

        surfaceView = findViewById(R.id.surface);
        textViewQR = findViewById(R.id.textv);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(800, 600)
                .setAutoFocusEnabled(true)
                .setRequestedFps(15.0f)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QrScanActivity.this, new String[]{Manifest.permission.CAMERA}, 909);
                    Log.d(TAG, "return");
                    return;
                }
                startCam();
                Log.d(TAG, "CamSorcState: " + cameraSource.toString());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestr");
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d(TAG, "set_Proc_relese");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() > 0) {
//                    Log.d(TAG, "set_proc_Detect " + qrCodes.size());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("scan_result", qrCodes.valueAt(0).displayValue);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 909) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCam();
            }
        }
    }

    private void startCam() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start(surfaceView.getHolder());
            Log.d(TAG, "try");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }
}
