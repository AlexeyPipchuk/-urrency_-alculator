package h.alexeypipchuk.calc;

import android.os.AsyncTask;
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
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MyLogs";
    private double RubKef; // 1 Eur in Rub
    public TextView Cost; // 1 Eur in Rub
    TextView result;
    EditText input;
    Button ER;  // Eur to Rub
    Button RE;

    String error;

    MyBackgroundTask taskER; // здесь сделаем сетевой запрос

    String regex = "\\d+"; // регулярка на проверку, что только цифры в строке
    DecimalFormat df = new DecimalFormat("#.##");  // на шаблоне было два знака после запятой, делаю так же

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Pattern pattern = Pattern.compile(regex);
        df.setRoundingMode(RoundingMode.CEILING);
        RubKef = 0;
        Cost = (TextView)findViewById(R.id.Cost);
        result = (TextView)findViewById(R.id.result);
        input = (EditText)findViewById(R.id.input);
        RE = (Button)findViewById(R.id.btnRE);

        if (savedInstanceState != null) { // восстанавливаем состояние при поаороте итп
            Cost.setText(savedInstanceState.getString("Cost"));
            result.setText(savedInstanceState.getString("result"));
        }

        taskER = new MyBackgroundTask();
        taskER.execute(); // запускаем сетевой запрос после всех инициализаций

        RE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (error == null)
                    // следующая проверка не пропускает пустую строку и символы кроме цифр
                    if (!input.getText().toString().equals("") && pattern.matcher(input.getText().toString()).matches())
                        result.setText(df.format(Double.valueOf(input.getText().toString()) / RubKef));
                    else Toast.makeText(getApplicationContext(), "Некорректный ввод", Toast.LENGTH_LONG).show();
                else result.setText(error);
            }
        });
        ER = (Button)findViewById(R.id.btnER);
        ER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (error == null)
                    // следующая проверка не пропускает пустую строку и символы кроме цифр
                    if (!input.getText().toString().equals("") && pattern.matcher(input.getText().toString()).matches())
                        result.setText(df.format(Double.valueOf(input.getText().toString()) * RubKef));
                    else Toast.makeText(getApplicationContext(), "Некорректный ввод", Toast.LENGTH_LONG).show();
                else result.setText(error);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Cost", Cost.getText().toString());
        savedInstanceState.putString("result", result.getText().toString());
    }

    /////---------------------------------------------------------------------

    public class MyBackgroundTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
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
            return null;
        }
    }
}
