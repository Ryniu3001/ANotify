package pl.allenotify.anotify;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import pl.allenotify.anotify.model.UserSearchContent;

public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener {

    public static final String INTENT_ITEM_ID = "pl.allenotify.item.id";
    public static final String INTENT_ITEM_NAME = "pl.allenotify.item.name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onListFragmentInteraction(UserSearchContent.UserSearchItem item) {
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra(INTENT_ITEM_ID, item.getId());
        intent.putExtra(INTENT_ITEM_NAME, item.getName());
        startActivity(intent);

    }

    @Override
    public void onListFragmentStatusIconInteraction(UserSearchContent.UserSearchItem item) {
       // Toast.makeText(getApplicationContext(), "Status: " + item.getStatusId(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(item.getStatusId());
        dlgAlert.setTitle("Status");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.create().show();
    }

    @Override
    public void onBackPressed() {
       moveTaskToBack(true);
    }
}