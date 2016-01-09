package nhl.containing.managmentinterface.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import nhl.containing.managmentinterface.R;

/**
 * Activity that holds a PreferencesFragment
 */
public class SettingsActivity extends AppCompatActivity {

    private final int PERMISSIONREQUEST = 1;
    /**
     * Creates the activity
     * @param savedInstanceState saved instace of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new SettingsActivityFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
    * Called when clicked on an option item
    * @param item clicked item
    * @return returns true when clicked home, else super.onOptionsItemSelected(item)
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_QR:
                checkPermissionAndStartScanning();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks the permissions and starts the scanning
     */
    private void checkPermissionAndStartScanning(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                AlertDialog.Builder dialog = new  AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
                dialog.setTitle(R.string.qr_permission_request_title);
                dialog.setMessage(R.string.qr_permission_request_message);
                dialog.setPositiveButton(R.string.qr_permission_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        askPermission();
                    }
                });
                dialog.show();

            }else{
                askPermission();
            }
        }else {
            startScanner();
        }
    }

    /**
     * Asks for permission
     */
    private void askPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSIONREQUEST);
    }

    /**
     * Starts the scanner Activity
     */
    private void startScanner(){
        startActivity(new Intent(this,QRScanner.class));
    }

    /**
     * Handles the permission request results
     * @param requestCode request code
     * @param permissions permissions
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSIONREQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startScanner();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
