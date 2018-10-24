package tech.honeysharma.techbmechat.Retrofit;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tech.honeysharma.techbmechat.Models.Example;

public interface APIService {


    @GET("top-headlines")
    Call<Example> postProblem(@Query("country") String country ,
                              @Query("category") String category ,
                              @Query("apiKey") String apiKey);


}
