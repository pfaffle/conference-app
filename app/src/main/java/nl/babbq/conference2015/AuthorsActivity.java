package nl.babbq.conference2015;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import nl.babbq.conference2015.adapters.AboutAdapter;
import nl.babbq.conference2015.objects.AboutItem;

/**
 * Displays the authors of this app.
 *
 * @author Craig Meinschein
 */
public class AuthorsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<AboutItem> authors = new ArrayList<AboutItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        initializeToolbar();

        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        generateListOfAuthors();
        AboutAdapter mAdapter = new AboutAdapter(this, 0x00, authors);
        mListView.setAdapter(mAdapter);
    }

    private void initializeToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void generateListOfAuthors() {
        authors.clear();
        authors.add(new AboutItem(getString(R.string.author0),
                getString(R.string.author0_text),
                R.drawable.ic_mood_grey600_48dp));
        authors.add(new AboutItem(getString(R.string.author1),
                getString(R.string.author1_text),
                R.drawable.ic_mood_grey600_48dp));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // Launch Navigation to the author's Twitter
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(getString(R.string.author0_url)));
                startActivity(i0);
                break;
            case 1: // Launch Navigation to the author's Twitter
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(getString(R.string.author1_url)));
                startActivity(i1);
                break;
        }
    }
}
