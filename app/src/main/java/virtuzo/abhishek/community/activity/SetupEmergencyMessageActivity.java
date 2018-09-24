package virtuzo.abhishek.community.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.MyFunctions;

public class SetupEmergencyMessageActivity extends LangSupportBaseActivity {

    Toolbar toolbar;
    EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_emergency_message);

        initToolbar();
        initGUI();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void initGUI() {
        messageEditText = findViewById(R.id.messageEditText);

        messageEditText.setText(AppUtils.getInstance(this).getEmergencyMessage());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_setup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClick(View view) {
        String msg = messageEditText.getText().toString();
        if (StringUtils.isNotEmpty(msg)) {
            AppUtils.getInstance(this).setEmergencyMessage(msg);
            finish();
        } else {
            Toast.makeText(this, R.string.text_add_valid_message, Toast.LENGTH_SHORT).show();
        }
    }
}
