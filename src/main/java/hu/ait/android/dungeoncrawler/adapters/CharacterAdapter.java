package hu.ait.android.dungeoncrawler.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.dungeoncrawler.CharacterViewActivity;
import hu.ait.android.dungeoncrawler.R;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.backend.Character;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder>{

    private Context context;
    private List<Character> characterList;

    public CharacterAdapter(Context context){
        this.context = context;
        characterList = new ArrayList<>(User.getInstance().getAllCharacters());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View characterRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.character_row, parent, false);
        return new ViewHolder(characterRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Character characterData = characterList.get(position);
        holder.tvName.setText(characterData.getStat(Character.StatTag.NAME));
        holder.tvRace.setText(characterData.getStat(Character.StatTag.RACE));
        holder.tvClass.setText(String.format("Level %s %s",
                characterData.getStat(Character.StatTag.LEVEL),
                characterData.getStat(Character.StatTag.CLASS)));
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public Character findCharacter(String name){
        for (Character c : characterList){
            if (c.getStat(Character.StatTag.NAME).equals(name)){
                return c;
            }
        }
        return null;
    }

    public String deleteCharacter(String s) {
        Character found = null;
        int pos = -1;
        for (int i = 0; i < characterList.size(); i++){
            if (characterList.get(i).getStat(Character.StatTag.NAME).equals(s)){
                found = characterList.get(i);
                pos = i;
                break;
            }
        }

        if (found == null) return null;

        User.getInstance().getAllCharacters().remove(found);
        characterList.remove(found);
        notifyItemRemoved(pos);

        return found.getStat(Character.StatTag.ID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvClass;
        private TextView tvRace;

        public ViewHolder(View characterView){
            super(characterView);

            tvName = characterView.findViewById(R.id.tvName);
            tvClass = characterView.findViewById(R.id.tvClass);
            tvRace = characterView.findViewById(R.id.tvRace);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.getInstance().setActiveCharacter(findCharacter(tvName.getText().toString()));
                    if (User.getInstance().getActiveCharacter() == null){
                        Toast.makeText(context,
                                context.getString(R.string.error_unexpected),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(context, CharacterViewActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
