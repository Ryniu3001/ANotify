package pl.allenotify.anotify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import pl.allenotify.anotify.model.UserSearchContent;

/**
 * Główne activity - lista wyszukiwań
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener {

    public static final String INTENT_ITEM_ID = "pl.allenotify.item.id";
    public static final String INTENT_ITEM_NAME = "pl.allenotify.item.name";
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
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
        startActivityForResult(intent, 1); //UPDATE
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        fragment.onActivityResult(requestCode,resultCode,data);
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
    public void onListLongClick(UserSearchContent.UserSearchItem item, View v) {
        openContextMenu(v);
    }

    @Override
    public void onBackPressed() {
       moveTaskToBack(true);
    }

}
