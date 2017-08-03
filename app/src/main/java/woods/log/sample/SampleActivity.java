package woods.log.sample;

import android.app.Activity;
import android.os.Bundle;

import woods.log.timber.Timber;

public class SampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        Timber.builder()
                .addTreeFactory(TreeFactory.class)
                .build();
    }
}
