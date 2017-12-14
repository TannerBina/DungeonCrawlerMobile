package hu.ait.android.dungeoncrawler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import hu.ait.android.dungeoncrawler.adapters.GameAdapter;

public class GameListActivity extends AppCompatActivity {

    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewGames);
        adapter = new GameAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
    }
}
