package woods.log.sample;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import woods.log.timber.Timber;

public class SampleActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.checkBox_v)
    CheckBox checkBoxV;
    @BindView(R.id.checkBox_d)
    CheckBox checkBoxD;
    @BindView(R.id.checkBox_i)
    CheckBox checkBoxI;
    @BindView(R.id.checkBox_w)
    CheckBox checkBoxW;
    @BindView(R.id.checkBox_e)
    CheckBox checkBoxE;
    @BindView(R.id.checkBox_wtf)
    CheckBox checkBoxWtf;
    @BindView(R.id.checkBox_ex)
    CheckBox checkBoxEx;
    @BindView(R.id.checkBox_thr)
    CheckBox checkBoxThr;

    private int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_WRITE_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Timber.builder()
                    .addTreeFactory(Seed.class)
                    .build();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.uprootall();
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        Exception e = null;
        String textMessage = editText.getText().toString();

        if (checkBoxEx.isChecked()) {
            e = new IOException("This is pkgname test exception for Timber.");
        }

        if (e == null) {
            if (checkBoxV.isChecked()) {
                Timber.v(textMessage);
            }
            if (checkBoxD.isChecked()) {
                Timber.d(textMessage);
            }
            if (checkBoxI.isChecked()) {
                Timber.i(textMessage);
            }
            if (checkBoxW.isChecked()) {
                Timber.w(textMessage);
            }
            if (checkBoxE.isChecked()) {
                Timber.e(textMessage);
            }
            if (checkBoxWtf.isChecked()) {
                Timber.wtf(textMessage);
            }
        } else {
            if (checkBoxV.isChecked()) {
                Timber.v(e, textMessage);
            }
            if (checkBoxD.isChecked()) {
                Timber.d(e, textMessage);
            }
            if (checkBoxI.isChecked()) {
                Timber.i(e, textMessage);
            }
            if (checkBoxW.isChecked()) {
                Timber.w(e, textMessage);
            }
            if (checkBoxE.isChecked()) {
                Timber.e(e, textMessage);
            }
            if (checkBoxWtf.isChecked()) {
                Timber.wtf(e, textMessage);
            }
        }

        if (checkBoxThr.isChecked()) {
            throw new IllegalArgumentException("Trigger runtime exception test.");
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {

        Timber.builder()
                .addTreeFactory(Seed.class)
                .build();

        Timber.supervise();
    }

}
