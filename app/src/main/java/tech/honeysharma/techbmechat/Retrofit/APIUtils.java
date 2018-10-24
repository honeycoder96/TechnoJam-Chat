package tech.honeysharma.techbmechat.Retrofit;

public class APIUtils {

    //https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=f5c378275ae2436e96fc7eb61924cc1d

    private APIUtils(){}

    public static final String BASE_URL = "https://newsapi.org/v2/";
    //

    public static APIService getAPIService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
