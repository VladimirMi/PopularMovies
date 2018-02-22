package io.github.vladimirmi.popularmovies.data.net;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.github.vladimirmi.popularmovies.BuildConfig;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Provider for {@link RestService}, witch is a singleton.
 */

public class RestServiceProvider {

    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static int CONNECT_TIMEOUT = 5000;
    private final static int READ_TIMEOUT = 5000;
    private final static int WRITE_TIMEOUT = 5000;

    private static class ServiceHolder {

        private static final RestService instance = createRetrofit(createClient())
                .create(RestService.class);
    }

    private RestServiceProvider() {
    }

    public static RestService getService() {
        return ServiceHolder.instance;
    }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(getApiKeyInterceptor())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    private static Retrofit createRetrofit(OkHttpClient okHttp) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(createConvertFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttp)
                .build();
    }

    private static Converter.Factory createConvertFactory() {
        return MoshiConverterFactory.create();
    }

    private static Interceptor getApiKeyInterceptor() {
        return chain -> {
            Request originalRequest = chain.request();

            HttpUrl url = originalRequest.url().newBuilder()
                    .addQueryParameter("api_key", BuildConfig.API_KEY)
                    .build();

            Request request = originalRequest.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        };
    }
}
