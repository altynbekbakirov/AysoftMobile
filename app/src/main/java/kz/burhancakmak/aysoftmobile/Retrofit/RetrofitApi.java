package kz.burhancakmak.aysoftmobile.Retrofit;

import kz.burhancakmak.aysoftmobile.Models.Clients.ClientExtractQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaretResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsCancelDataResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsDashboardQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsKasaResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsMap;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsReportsQuery;
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
            @Field("StokKayitNo") int stokKayitNo);

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

    @POST("CariRutOlusturma.php")
    @FormUrlEncoded
    Call<ClientsQuery> getClientOrderDays(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Field("CariKod") String kod,
            @Field("CariKayitNo") Integer kayitNo,
            @Field("Gun1") Integer gun1,
            @Field("Gun2") Integer gun2,
            @Field("Gun3") Integer gun3,
            @Field("Gun4") Integer gun4,
            @Field("Gun5") Integer gun5,
            @Field("Gun6") Integer gun6,
            @Field("Gun7") Integer gun7,
            @Field("Gun1SiraNo") Integer gun1SiraNo,
            @Field("Gun2SiraNo") Integer gun2SiraNo,
            @Field("Gun3SiraNo") Integer gun3SiraNo,
            @Field("Gun4SiraNo") Integer gun4SiraNo,
            @Field("Gun5SiraNo") Integer gun5SiraNo,
            @Field("Gun6SiraNo") Integer gun6SiraNo,
            @Field("Gun7SiraNo") Integer gun7SiraNo);

    @POST("CariEkle.php")
    @FormUrlEncoded
    Call<ClientsQuery> addNewClient(
            @Field("CihazId") String CihazId,
            @Field("Login") String Login,
            @Field("Parola") String Parola,
            @Field("DonemNo") String donem,
            @Field("FirmaNo") String firma,
            @Field("Unvani1") String unvani1,
            @Field("Adres1") String adres1,
            @Field("Yetkili") String yetkili,
            @Field("Telefon1") String telefon1,
            @Field("Telefon2") String telefon2,
            @Field("EmailAdresi") String email);

    @POST("CariDashboard.php")
    @FormUrlEncoded
    Call<ClientsDashboardQuery> getClientDashboard(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Tur") int tur,
            @Field("CariKayitNo") int cariKayitNo,
            @Field("CariKod") String cariKod);

    @POST("CariHesapOzeti.php")
    @FormUrlEncoded
    Call<ClientsReportsQuery> getClientsReports(
            @Field("CihazId") String CihazId,
            @Field("Login") String login,
            @Field("Parola") String Parola,
            @Field("FirmaNo") String firma,
            @Field("DonemNo") String donem,
            @Field("Tur") int tur,
            @Field("CariKayitNo") int cariKayitNo,
            @Field("CariKod") String cariKod);

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
            @Field("CariKod") String cariKod);

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
            @Field("CariKayitNo") int cariKayitNo);



}
