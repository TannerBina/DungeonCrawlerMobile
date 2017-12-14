package hu.ait.android.dungeoncrawler;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import hu.ait.android.dungeoncrawler.data.User;

public class GameActivity extends AppCompatActivity {

    public static String IN_GAME = "IN_GAME";

    private TextView tvMessage;
    private EditText etMessageSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvMessage = (TextView) findViewById(R.id.tvMessageField);
        tvMessage.append("Successfully Joined Game\n");

        TextView tvGameName = (TextView) findViewById(R.id.tvCurrentGameName);
        TextView tvGamePassword = (TextView) findViewById(R.id.tvCurrentGamePassword);

        tvGameName.setText("Game Name : " + User.getInstance().getActiveGame().getName());
        tvGamePassword.setText("Game Password : " + User.getInstance().getActiveGame().getPassword());

        initBtn();

        initTimer();

        initMessageBtn();
    }

    private void initMessageBtn() {
        etMessageSend = (EditText) findViewById(R.id.etSendMessage);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                User.getInstance().send(etMessageSend.getText().toString());
                etMessageSend.setText("");
            }
        });
    }

    private void initBtn() {
        Button btnViewCharacter = (Button) findViewById(R.id.btnViewCharacter);
        btnViewCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CharacterViewActivity.class);
                intent.putExtra(IN_GAME, true);
                startActivity(intent);
            }
        });
    }

    private void initTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String mes = User.getInstance().popMessage();
                        if (mes != null){
                            tvMessage.append(mes + "\n");
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, 100);
    }

    @Override
    public void onBackPressed() {
        User.getInstance().close();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        User.getInstance().close();
        super.onDestroy();
    }
}
