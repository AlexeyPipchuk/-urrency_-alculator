package h.alexeypipchuk.calc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.math.RoundingMode;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MyLogs";
    private double RubKef;
    TextView Cost;
    TextView result;
    EditText input;
    Button ER;
    Button RE;

    String error;

    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        df.setRoundingMode(RoundingMode.CEILING);
        RubKef = 0;
        Cost = (TextView)findViewById(R.id.Cost);
        result = (TextView)findViewById(R.id.result);
        input = (EditText)findViewById(R.id.input);
        RE = (Button)findViewById(R.id.btnRE);

        if (savedInstanceState != null) {
            Cost.setText(savedInstanceState.getString("Cost"));
            result.setText(savedInstanceState.getString("result"));
        }

        RE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (error == null)
                    if (!input.getText().toString().equals(""))
                        result.setText(df.format(Double.valueOf(input.getText().toString()) / RubKef));
                    else Toast.makeText(getApplicationContext(), "Введите число", Toast.LENGTH_LONG).show();
                else result.setText(error);
            }
        });
        ER = (Button)findViewById(R.id.btnER);
        ER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (error == null)
                    if (!input.getText().toString().equals(""))
                        result.setText(df.format(Double.valueOf(input.getText().toString()) * RubKef));
                    else Toast.makeText(getApplicationContext(), "Введите число", Toast.LENGTH_LONG).show();
                else result.setText(error);
            }
        });
        QueryToServer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Cost", Cost.getText().toString());
        savedInstanceState.putString("result", result.getText().toString());
    }

    private void QueryToServer()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://data.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AnswerApi messagesApi = retrofit.create(AnswerApi.class);

        Call<Answer> messages = messagesApi.answers();

        messages.enqueue(new Callback<Answer>() {
            @Override
            public void onResponse(Call<Answer> call, Response<Answer> response) {
                if (response.isSuccessful()) {
                    Log.d(LOG_TAG,"response " + response.body().getRates().getrUB());
                    RubKef = response.body().getRates().getrUB().doubleValue();
                    Cost.setText(df.format(RubKef));
                } else {
                    Log.d(LOG_TAG,"response code " + response.code() + " " + response.errorBody());
                    error = "Некорректный ответ от сервера";
                }
            }

            @Override
            public void onFailure(Call<Answer> call, Throwable t) {
                Log.d(LOG_TAG,"failure " + t);
                error = "Нет соединения с сетью";
            }
        });
    }
}
