package fancontroller.jai.com.fancontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

/**
 * Created by Jai Pandit on 1/18/18.
 */

public class MainActivity extends AppCompatActivity {


    private FanController fanController;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        fanController = new FanController(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(fanController, params);
    }
}
