package kz.burhancakmak.aysoftmobile.Retrofit;

import kz.burhancakmak.aysoftmobile.Models.Clients.ClientExtractQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaretResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsCancelDataResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsDashboardQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsKasaResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsMap;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsSiparisResponse;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarQuery;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsExtractQuery;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsQuery;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitApi {

    @POST("login.php")
    @FormUrlEncoded
    Call<CihazlarQuery> getUser(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola);

    @POST("StokListesi.php")
    @FormUrlEncoded
    Call<ItemsQuery> getStockList(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Field("Tip") Integer type);

    @POST("StokListesi.php")
    @FormUrlEncoded
    Call<ItemsQuery> getStockListQuick(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Field("Tip") Integer type);

    @POST("StokEkstre.php")
    @FormUrlEncoded
    Call<ItemsExtractQuery> getStockExtractList(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Dil") String dil,
            @Field("Tarih1") String tarih1,
            @Field("Tarih2") String tarih2,
            @Field("Tur") int tur,
            @Field("StokKod") String stokKod,
            @Field("StokKayitNo") int stokKayitNo

    );

    @POST("CariListesi.php")
    @FormUrlEncoded
    Call<ClientsQuery> getClientList(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma);

    @POST("CariListesi.php")
    @FormUrlEncoded
    Call<ClientsQuery> getClientListBalance(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Field("Tip") Integer type);

    @POST("KasaHareketEkle.php")
    @FormUrlEncoded
    Call<ClientsKasaResponse> kasaHareketleriGuncelle(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("IslemTipi") Integer islemTipi,
            @Field("MobilKayitNo") Integer MobilKayitNo,
            @Field("Tarih") String tarih,
            @Field("CariKayitNo") Integer CariKayitNo,
            @Field("CariKod") String CariKod,
            @Field("Tutar") Double Tutar,
            @Field("Aciklama") String Aciklama,
            @Field("MakbuzNo") String MakbuzNo,
            @Field("KasaKodu") String KasaKodu,
            @Field("ZiyaretSatiri") String ziyaretSatir);

    @POST("SiparisEkle.php")
    @FormUrlEncoded
    Call<ClientsSiparisResponse> siparisHareketleriGuncelle(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Tur") Integer Tur,
            @Field("SiparisBaslik") String baslik,
            @Field("SiparisSatiri") String satir,
            @Field("ZiyaretSatiri") String ziyaretSatir);

    @POST("ZiyaretEkle.php")
    @FormUrlEncoded
    Call<ClientZiyaretResponse> ziyaretEkle(
            @Field("CihazId") String CihazId,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("ZiyaretSatiri") String ziyaretSatir);

    @POST("IslemGeriAl.php")
    @FormUrlEncoded
    Call<ClientsCancelDataResponse> geriAlIslemleri(
            @Field("CihazId") String CihazId,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Tur") Integer tur,
            @Field("FisNo") String fisNo,
            @Field("FisTarihi") String fisTarihi,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("ErpKayitNo") Integer erpKayitNo,
            @Field("CariKayitNo") Integer cariKayitNo,
            @Field("Tutar") Double tutar);


    @POST()
    @FormUrlEncoded
    @Streaming
    Call<ResponseBody> downloadFileWithDynamicUrlSync(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Url String fileUrl);

    @POST("CariEkstre.php")
    @FormUrlEncoded
    Call<ClientExtractQuery> getClientExtractList(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("CariKayitNo") int cariKayitNo,
            @Field("Dil") String dil,
            @Field("Tarih1") String tarih1,
            @Field("Tarih2") String tarih2,
            @Field("Tur") int tur,
            @Field("CariKod") String cariKod

    );

    @POST("CariGuncelleme.php")
    @FormUrlEncoded
    Call<ClientsMap> updateMapLocation(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("CariKod") String cariKod,
            @Field("DegisecekAlanAdi") String alanAdi,
            @Field("YeniDeger") String yeniDeger,
            @Field("CariKayitNo") int cariKayitNo
    );

    @POST("CariDashboard.php")
    @FormUrlEncoded
    Call<ClientsDashboardQuery> clientDashboard(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Tur") int tur,
            @Field("CariKayitNo") int cariKayitNo,
            @Field("CariKod") String cariKod
    );

}
