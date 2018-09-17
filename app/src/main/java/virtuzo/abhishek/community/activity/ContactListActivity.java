package virtuzo.abhishek.community.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import virtuzo.abhishek.community.AppController;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.ContactListAdapter;
import virtuzo.abhishek.community.model.Contact;
import virtuzo.abhishek.community.realm.RealmHelper;

public class ContactListActivity extends LangSupportBaseActivity {

    private static final int PICK_CONTACT = 101;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ContactListAdapter contactListAdapter;
    ArrayList<Contact> contactArrayList;
    TextView noContactsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initToolbar();
        loadActivity();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_emergency_contacts));
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

    private void loadActivity() {
        noContactsTextView = findViewById(R.id.noContactsTextView);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        contactArrayList = new ArrayList<>();

        contactListAdapter = new ContactListAdapter(contactArrayList, this, new ContactListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Contact contact) {
//                Toast.makeText(ContactListActivity.this, contact.getName(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(OfficeBearerListActivity.this, OfficeBearerDetailsActivity.class);
//
//                Bundle bundle = new Bundle();
//                Gson gson = new Gson();
//                String jsonString = gson.toJson(officeBearer);
//                bundle.putInt("ID", officeBearer.getID());
//                intent.putExtras(bundle);
//                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                contactArrayList.remove(position);
                contactListAdapter.notifyDataSetChanged();

                refreshContactList();
                refreshNoContactTextView();
            }

        });
        recyclerView.setAdapter(contactListAdapter);

        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        contactArrayList.clear();
        contactArrayList.addAll(RealmHelper.getInstance().getContacts());
        contactListAdapter.notifyDataSetChanged();

        refreshNoContactTextView();
    }

    public void onAddNewContactButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
// filter the contacts with phone number only
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the URI that points to the selected contact
                    Uri contactUri = data.getData();
                    Cursor c =  getContentResolver().query(contactUri, null, null, null, null);
                    c.moveToFirst();

                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    for (Contact contact : contactArrayList) {
                        if (contact.getNumber().equals(number)) {
                            Toast.makeText(this, "Contact already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Contact contact = new Contact(name, number);
                    contactArrayList.add(contact);
                    contactListAdapter.notifyDataSetChanged();

                    refreshContactList();
                    refreshNoContactTextView();
                }
                break;
        }
    }

    public void refreshContactList() {
        Realm realm = AppController.realm;
        realm.beginTransaction();

        RealmResults<Contact> contacts = realm.where(Contact.class).findAll();
        contacts.deleteAllFromRealm();
        for (Contact contact : contactArrayList) {
            realm.copyToRealm(contact);
        }

        realm.commitTransaction();
    }

    public void refreshNoContactTextView() {
        if (contactArrayList.size() == 0) {
            noContactsTextView.setVisibility(View.VISIBLE);
        } else {
            noContactsTextView.setVisibility(View.GONE);
        }
    }

}
