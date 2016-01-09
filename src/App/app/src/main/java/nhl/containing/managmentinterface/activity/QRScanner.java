package nhl.containing.managmentinterface.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nhl.containing.managmentinterface.R;

/**
 * Creates an QR scan activity
 */
public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler,AlertDialog.OnClickListener {

    private ZXingScannerView mScannerView;
    private String[] split;

    /**
     * On create
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if(toolbar == null){
            finish();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mScannerView = new ZXingScannerView(this);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formats);
        ViewGroup group = (ViewGroup)findViewById(R.id.content_frame);
        group.addView(mScannerView);
    }

    /**
     * On resume
     */
    @Override
    public void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    /**
     * On pause
     */
    @Override
    public void onPause(){
        super.onPause();
        mScannerView.stopCamera();
    }

    /**
     * On option selected on the actionbar
     * @param item ite
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the data that is received
     * @param result result
     */
    @Override
    public void handleResult(Result result) {
        String input = result.getText();
        split = input.split(":");
        if(split.length < 2){
            Toast.makeText(this,R.string.qr_failed,Toast.LENGTH_LONG).show();
            this.finish();
        }
        AlertDialog.Builder dialog = new  AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        dialog.setTitle(R.string.qr_data_dialog_title);
        dialog.setMessage(getResources().getString(R.string.qr_data_dialog_message,split[0], split[1]));
        dialog.setPositiveButton(R.string.qr_data_dialog_pos_button, this);
        dialog.setNegativeButton(R.string.qr_data_dialog_neg_button, this);
        dialog.show();

    }

    /**
     * Listens to the on click of the alert dialog
     * @param dialog dialog
     * @param which the button clicked
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        switch (which){
            case AlertDialog.BUTTON_POSITIVE:
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
                prefs.putString("Connection_Host", split[0]);
                prefs.putString("Connection_Port", split[1]);
                prefs.apply();
                finish();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                mScannerView.resumeCameraPreview(this);
                break;
        }
    }
}
