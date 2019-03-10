package h.alexeypipchuk.calc;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnswerApi {
    String ApiKey = "/api/latest?access_key=2676c8d7b3fc6422300c576d9e2773ee";
    @GET(ApiKey)
    Call<Answer> answers();
}


