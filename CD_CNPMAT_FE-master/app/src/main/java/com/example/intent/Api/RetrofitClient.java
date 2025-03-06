package com.example.intent.Api;

    import okhttp3.OkHttpClient;
    import okhttp3.logging.HttpLoggingInterceptor;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class RetrofitClient {

        private static final String url = "http://172.16.65.58:8080";
        private static RetrofitClient instance;
        private Retrofit retrofit;

        private RetrofitClient() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        public static synchronized RetrofitClient getInstance() {
            if (instance == null ) {
                instance = new RetrofitClient();
            }
            return instance;
        }

        public <T> T createService(Class<T> serviceClass) {
            return retrofit.create(serviceClass);
        }

    }
