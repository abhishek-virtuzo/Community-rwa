package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.MessagesListAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.Message;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

public class MessageListActivity extends LangSupportBaseActivity implements Network.Listener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    MessagesListAdapter messagesListAdapter;
    ArrayList<Message> messages;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        initToolbar();
        loadActivity();

    }

    private void loadActivity() {
        baseUtils = new BaseUtils(this);
        appUtils = new AppUtils(this);
        network = new Network(this, this);
        lang = new Lang(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        messages = new ArrayList<>();

        messagesListAdapter = new MessagesListAdapter(messages, this, new MessagesListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Message message) {
//                Toast.makeText(MessageListActivity.this, message.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MessageListActivity.this, MessageDetailsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("ID", message.getID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(messagesListAdapter);

//        loadData();
        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        messages.clear();
        messages.addAll(RealmHelper.getInstance().getMessages());
        messagesListAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        HashMap<String, String> map = new HashMap<>();
        network.setShowProgress(true);
        network.makeRequest(map, UrlConfig.MessageList);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_messages));
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

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                messages.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    Message message = gson.fromJson(json.toString(), Message.class);
                    messages.add(message);
                }
                messagesListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }

    }

    @Override
    public void onNetworkError(String error, String url) {

    }
}
