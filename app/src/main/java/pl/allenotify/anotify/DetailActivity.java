package pl.allenotify.anotify;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Activity szczegółów wyszukiwania (edycja/dodawanie)
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        String searchId = extras.getString(MainActivity.INTENT_ITEM_ID);
        String searchName = extras.getString(MainActivity.INTENT_ITEM_NAME);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.

            DetailFragment fragment = DetailFragment.newInstance(searchId, searchName);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
