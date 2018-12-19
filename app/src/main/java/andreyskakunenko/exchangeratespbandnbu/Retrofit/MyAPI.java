package andreyskakunenko.exchangeratespbandnbu.Retrofit;

import java.util.List;

import andreyskakunenko.exchangeratespbandnbu.Model.NbuToday;
import andreyskakunenko.exchangeratespbandnbu.Model.PbAndNduArchive;
import andreyskakunenko.exchangeratespbandnbu.Model.PbToday;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface MyAPI {
    @GET("pubinfo?json&exchange&coursid=5")
    Observable<List<PbToday>> getResults();

    @GET
    Observable<List<NbuToday>> getNbuResults(@Url String url);

    @GET
    Observable<PbAndNduArchive> getArchiveResults(@Url String url);
}