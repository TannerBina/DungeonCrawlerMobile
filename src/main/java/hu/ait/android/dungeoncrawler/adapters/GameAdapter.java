package hu.ait.android.dungeoncrawler.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.dungeoncrawler.GameActivity;
import hu.ait.android.dungeoncrawler.R;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.backend.Game;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private Context context;
    private List<Game> gameList;

    public GameAdapter(Context context){
        this.context = context;
        gameList = new ArrayList<>(User.getInstance().getAllGames());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gameRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.game_row, parent, false);
        return new ViewHolder(gameRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Game gameData = gameList.get(position);
        holder.tvName.setText(gameData.getName());
        holder.tvHost.setText(gameData.getHost());
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvHost;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvGameName);
            tvHost = itemView.findViewById(R.id.tvHostName);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setTitle(R.string.passwrd);
                    final EditText password = new EditText(context);
                    password.setHint(R.string.pass);

                    final Game selected = findGame(tvName.getText().toString());

                    builder.setView(password);

                    builder.setPositiveButton(R.string.jn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (password.getText().toString().equals(selected.getPassword())){
                                new AsyncTask<Game, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Game... games) {
                                        return User.getInstance().joinGame(selected.getName(),
                                                selected.getPassword());
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean aBoolean) {
                                        if (aBoolean){
                                            User.getInstance().setActiveGame(selected);
                                            Intent intent = new Intent();
                                            intent.setClass(context, GameActivity.class);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context,
                                                    R.string.error_join,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }.execute(selected);
                            } else {
                                Toast.makeText(context,
                                        R.string.incorrect_game_password,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cncl, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    private Game findGame(String text) {
        for (Game g : gameList){
            if (g.getName().equals(text)) return g;
        }
        return null;
    }
}
