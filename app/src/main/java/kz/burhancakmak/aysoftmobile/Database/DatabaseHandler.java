package kz.burhancakmak.aysoftmobile.Database;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaDepolar;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaOdemeSekli;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientKasa;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSiparis;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaret;
import kz.burhancakmak.aysoftmobile.Models.Clients.ShipInfo;
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportKasaTask;
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportSiparisTask;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirma;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Firms.Doviz;
import kz.burhancakmak.aysoftmobile.Models.Products.Items;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepoStokYerleri;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepolar;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepolarAdresler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsItmunita;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsPrclist;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsUnitBarcode;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler instance = null;
    private static final String DATABASE_NAME = "aysoft.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CIHAZLAR_FIRMA = "CihazlarFirma";
    private static final String TABLE_CIHAZLAR_MENU = "CihazlarMenu";
    private static final String TABLE_CIHAZLAR_FIRMA_PARAMETRELER = "CihazlarFirmaParametreler";
    private static final String TABLE_DOVIZ = "Doviz";
    private static final String TABLE_CIHAZLAR_FIRMA_ODEMESEKLI = "CihazlarFirmaOdemeSekli";
    private static final String TABLE_CIHAZLAR_FIRMA_DEPOLAR = "CihazlarFirmaDepolar";
    private static final String TABLE_ZIYARET = "Ziyaret";

    private DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table_cihazlarFirma_create = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazKayitNo INTEGER, "
                + "MenuGrupKayitNo INTEGER, "
                + "FirmaNo TEXT, "
                + "DonemNo TEXT, "
                + "SatisElemaniKodu TEXT, "
                + "TicariIslemGrubu TEXT, "
                + "Isyeri INTEGER, "
                + "Depo INTEGER, "
                + "OndegerFiyatGrubu1 TEXT, "
                + "OndegerFiyatGrubu2 TEXT, "
                + "OndegerKasaKodu TEXT, "
                + "OndegerAciklamaAlani TEXT, "
                + "DepoListesi1 TEXT, "
                + "DepoListesi2 TEXT, "
                + "CariFiltre TEXT, "
                + "CariSevkiyatAdresiFiltre TEXT, "
                + "StokFiltre TEXT, "
                + "FiyatFiltre TEXT, "
                + "ResimAdresi TEXT, "
                + "Depo1Aciklama1 TEXT, "
                + "Depo1Aciklama2 TEXT, "
                + "Depo2Aciklama1 TEXT, "
                + "Depo2Aciklama2 TEXT, "
                + "StokEkraniGorunumSekli TEXT, "
                + "Kullanim TEXT, "
                + "GecmisFirmaNo TEXT, "
                + "GecmisDonemNo TEXT, "
                + "FirmaAdi1 TEXT, "
                + "FirmaAdi2 TEXT, "
                + "FirmaAdi3 TEXT, "
                + "YerelDovizKayitNo INTEGER, "
                + "RaporlamaDoviziKayitNo INTEGER );";

        String table_cihazlarMenu_create = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_MENU + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "MenuGrupKayitNo INTEGER, "
                + "Tip INTEGER, "
                + "Aciklama1 TEXT, "
                + "Aciklama2 TEXT, "
                + "Filtre TEXT, "
                + "Siralama TEXT, "
                + "Ondeger INTEGER, "
                + "Kullanim TEXT, "
                + "SiraNo TEXT, "
                + "UstMenuKayitNo INTEGER, "
                + "MenuTipi INTEGER );";

        String table_cihazlarFirmaParametreler_create = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "ParametreTipi TEXT, "
                + "ParametreAdi TEXT, "
                + "ParametreDegeri TEXT, "
                + "Aciklama TEXT, "
                + "MobilCihazdaDegistirebilir INTEGER, "
                + "Grup TEXT );";

        String table_CihazlarFirmaOdemeSekli_create = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_ODEMESEKLI + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "IslemYonu TEXT, "
                + "Aciklama1 TEXT, "
                + "Aciklama2 TEXT, "
                + "Aciklama3 TEXT );";

        String table_CihazlarFirmaDepolar_create = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_DEPOLAR + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "DepoNo INTEGER, "
                + "DepoIsmi TEXT, "
                + "DepoAciklama1 TEXT, "
                + "DepoAciklama2 TEXT, "
                + "DepoAciklama3 TEXT );";

        String table_doviz_create = "CREATE TABLE IF NOT EXISTS " + TABLE_DOVIZ + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "DovizKayitNo INTEGER, "
                + "DovizIsareti TEXT, "
                + "Aciklama TEXT );";

        String table_ziyaret_create = "CREATE TABLE IF NOT EXISTS " + TABLE_ZIYARET + " ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CariKayitNo INTEGER, "
                + "BeginKordinatLongitude REAL, "
                + "BeginKordinatLatitute REAL, "
                + "EndKordinatLongitude REAL, "
                + "EndKordinatLatitute REAL, "
                + "BaslangicTarihi TEXT, "
                + "BaslangicSaati TEXT, "
                + "BitisTarihi TEXT, "
                + "BitisSaati TEXT, "
                + "ErpGonderildi  INTEGER DEFAULT 0, "
                + "Kapatildi INTEGER DEFAULT 0, "
                + "Notlar TEXT );";

        db.execSQL(table_cihazlarFirma_create);
        db.execSQL(table_cihazlarMenu_create);
        db.execSQL(table_cihazlarFirmaParametreler_create);
        db.execSQL(table_CihazlarFirmaOdemeSekli_create);
        db.execSQL(table_CihazlarFirmaDepolar_create);
        db.execSQL(table_doviz_create);
        db.execSQL(table_ziyaret_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIHAZLAR_FIRMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIHAZLAR_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOVIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_BARCODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_TOPLAMLAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_PRCLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_ITMUNITA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIP_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KASA_ISLEMLERI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIPARIS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEPET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZIYARET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPOLAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPOLAR_ADRESLER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPO_STOK_YERLERI);
        onCreate(db);*/
    }

    public void createCihazlarFirmaTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazKayitNo INTEGER, "
                + "MenuGrupKayitNo INTEGER, "
                + "FirmaNo TEXT, "
                + "DonemNo TEXT, "
                + "SatisElemaniKodu TEXT, "
                + "TicariIslemGrubu TEXT, "
                + "Isyeri INTEGER, "
                + "Depo INTEGER, "
                + "OndegerFiyatGrubu1 TEXT, "
                + "OndegerFiyatGrubu2 TEXT, "
                + "OndegerKasaKodu TEXT, "
                + "OndegerAciklamaAlani TEXT, "
                + "DepoListesi1 TEXT, "
                + "DepoListesi2 TEXT, "
                + "CariFiltre TEXT, "
                + "CariSevkiyatAdresiFiltre TEXT, "
                + "StokFiltre TEXT, "
                + "FiyatFiltre TEXT, "
                + "ResimAdresi TEXT, "
                + "Depo1Aciklama1 TEXT, "
                + "Depo1Aciklama2 TEXT, "
                + "Depo2Aciklama1 TEXT, "
                + "Depo2Aciklama2 TEXT, "
                + "StokEkraniGorunumSekli TEXT, "
                + "Kullanim TEXT, "
                + "GecmisFirmaNo TEXT, "
                + "GecmisDonemNo TEXT, "
                + "FirmaAdi1 TEXT, "
                + "FirmaAdi2 TEXT, "
                + "FirmaAdi3 TEXT, "
                + "YerelDovizKayitNo INTEGER, "
                + "RaporlamaDoviziKayitNo INTEGER );";

        db.execSQL(sql);
        db.close();
    }

    public void createCihazlarMenuTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_MENU + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "MenuGrupKayitNo INTEGER, "
                + "Tip INTEGER, "
                + "Aciklama1 TEXT, "
                + "Aciklama2 TEXT, "
                + "Filtre TEXT, "
                + "Siralama TEXT, "
                + "Ondeger INTEGER, "
                + "Kullanim TEXT, "
                + "SiraNo TEXT, "
                + "UstMenuKayitNo INTEGER, "
                + "MenuTipi INTEGER );";
        db.execSQL(sql);
        db.close();
    }

    public void createCihazlarFirmaParametrelerTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "ParametreTipi TEXT, "
                + "ParametreAdi TEXT, "
                + "ParametreDegeri TEXT, "
                + "Aciklama TEXT, "
                + "MobilCihazdaDegistirebilir TEXT, "
                + "Grup TEXT );";
        db.execSQL(sql);
        db.close();
    }

    public void createCihazlarFirmaOdemeSekliTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_ODEMESEKLI + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "IslemYonu TEXT, "
                + "Aciklama1 TEXT, "
                + "Aciklama2 TEXT, "
                + "Aciklama3 TEXT );";
        db.execSQL(sql);
        db.close();
    }

    public void createCihazlarFirmaDepolarTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CIHAZLAR_FIRMA_DEPOLAR + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CihazlarFirmaKayitNo INTEGER, "
                + "DepoNo INTEGER, "
                + "DepoIsmi TEXT, "
                + "DepoAciklama1 TEXT, "
                + "DepoAciklama2 TEXT, "
                + "DepoAciklama3 TEXT );";
        db.execSQL(sql);
        db.close();
    }

    public void createDovizTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_DOVIZ + " ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "DovizKayitNo INTEGER, "
                + "DovizIsareti TEXT, "
                + "Aciklama TEXT );";
        db.execSQL(sql);
        db.close();
    }

    public void createZiyaretTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ZIYARET + " ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CariKayitNo INTEGER, "
                + "BeginKordinatLongitude REAL, "
                + "BeginKordinatLatitute REAL, "
                + "EndKordinatLongitude REAL, "
                + "EndKordinatLatitute REAL, "
                + "BaslangicTarihi TEXT, "
                + "BaslangicSaati TEXT, "
                + "BitisTarihi TEXT, "
                + "BitisSaati TEXT, "
                + "ErpGonderildi INTEGER DEFAULT 0, "
                + "Kapatildi INTEGER DEFAULT 0, "
                + "Notlar TEXT );";
        db.execSQL(sql);
        db.close();
    }

    public void createClientsTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_clients_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ClCard ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "Kod TEXT, "
                + "Unvani1 TEXT, "
                + "Unvani2 TEXT, "
                + "FiyatGrubu TEXT, "
                + "OzelKod1 TEXT, "
                + "OzelKod2 TEXT, "
                + "OzelKod3 TEXT, "
                + "OzelKod4 TEXT, "
                + "OzelKod5 TEXT, "
                + "Adres1 TEXT, "
                + "Adres2 TEXT, "
                + "Sehir TEXT, "
                + "TicariIslemGrubu TEXT, "
                + "Telefon1 TEXT, "
                + "Telefon2 TEXT, "
                + "VergiNo TEXT, "
                + "IndirimOrani TEXT, "
                + "KordinatLongitude TEXT, "
                + "KordinatLatitute TEXT, "
                + "IlgiliKisi1 TEXT, "
                + "IlgiliKisi2 TEXT, "
                + "EmailAdresi1 TEXT, "
                + "EmailAdresi2 TEXT, "
                + "SiparisGrubuKullanimi TEXT, "
                + "SonSatisTarihi TEXT, "
                + "SonTahsilatTarihi TEXT, "
                + "SonSatisGunSayisi TEXT, "
                + "SonTahsilatGunSayisi TEXT, "
                + "Pazartesi TEXT, "
                + "PazartesiSiraNo TEXT, "
                + "Sali TEXT, "
                + "SaliSiraNo TEXT, "
                + "Carsamba TEXT, "
                + "CarsambaSiraNo TEXT, "
                + "Persembe TEXT, "
                + "PersembeSiraNo TEXT, "
                + "Cuma TEXT, "
                + "CumaSiraNo TEXT, "
                + "Cumartesi TEXT, "
                + "CumartesiSiraNo TEXT, "
                + "Pazar TEXT, "
                + "PazarSiraNo TEXT, "
                + "SiparisGunleriToplami REAL, "
                + "RiskLimiti REAL, "
                + "NetSatis REAL, "
                + "NetTahsilat REAL, "
                + "Bakiye REAL, "
                + "WhatsappId TEXT, "
                + "TelegramId TEXT, "
                + "KartTipi TEXT, "
                + "Foto1 TEXT, "
                + "Foto2 TEXT, "
                + "Foto3 TEXT, "
                + "OdemeSekli INTEGER, "
                + "DovizKayitNo INTEGER, "
                + "DovizIsareti TEXT );";

        db.execSQL(table_clients_create);
        db.close();
    }

    public void createItemsTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_items_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_Items ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "StokKodu TEXT, "
                + "UreticiKodu TEXT, "
                + "StokAdi1 TEXT, "
                + "StokAdi2 TEXT, "
                + "StokAdi3 TEXT, "
                + "VKod TEXT, "
                + "VAciklama1 TEXT, "
                + "VAciklama2 TEXT, "
                + "VAciklama3 TEXT, "
                + "ResimDosyasiKucuk TEXT, "
                + "ResimDosyasiBuyuk1 TEXT, "
                + "ResimDosyasiBuyuk2 TEXT, "
                + "ResimDosyasiBuyuk3 TEXT, "
                + "GrupKodu TEXT, "
                + "OzelKod1 TEXT, "
                + "OzelKod2 TEXT, "
                + "OzelKod3 TEXT, "
                + "OzelKod4 TEXT, "
                + "OzelKod5 TEXT, "
                + "MarkaKodu TEXT, "
                + "YetkiKodu TEXT, "
                + "SiparisGrubu TEXT, "
                + "IndirimYapilamaz TEXT, "
                + "Kalan1 REAL, "
                + "Kalan2 REAL, "
                + "NewIcon TEXT, "
                + "IndirimIcon TEXT, "
                + "KampanyaIcon TEXT, "
                + "SiparisCarpani INTEGER DEFAULT 0);";
        db.execSQL(table_items_create);
        db.close();
    }

    public void createItemsItmunitaTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_itmunita_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ItemsItmunita ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "StokKayitNo INTEGER, "
                + "SiraNo INTEGER, "
                + "BirimSiraKayitNo INTEGER, "
                + "Birim TEXT, "
                + "Hacim REAL, "
                + "Agirlik REAL, "
                + "Carpan1 INTEGER, "
                + "Carpan2 INTEGER );";
        db.execSQL(table_itmunita_create);
        db.close();
    }

    public void createItemsPrclistTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_prclist_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ItemsPrclist ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "StokKayitNo INTEGER, "
                + "BirimKayitNo INTEGER, "
                + "FiyatGrubu TEXT, "
                + "CariHesapKodu TEXT, "
                + "Fiyat REAL, "
                + "BaslangicTarih TEXT, "
                + "BitisTarih TEXT, "
                + "DovizTipiKayitNo INTEGER, "
                + "DovizIsareti TEXT );";
        db.execSQL(table_prclist_create);
        db.close();
    }

    public void createItemsToplamTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_toplam_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ItemsToplamlar ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "StokKayitNo INTEGER, "
                + "DepoNo INTEGER, "
                + "DepoAdi TEXT, "
                + "StokKodu TEXT, "
                + "Toplam REAL, "
                + "StokYeriKodu TEXT );";
        db.execSQL(table_toplam_create);
        db.close();
    }

    public void createItemsBarkodTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_barkod_create = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ItemsUnitBarcode ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "Itmunitaref INTEGER, "
                + "StokKayitNo INTEGER, "
                + "SatirNo INTEGER, "
                + "Barkod TEXT );";
        db.execSQL(table_barkod_create);
        db.close();
    }

    public void createShipInfoTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table_ship_info = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_ShipInfo ( "
                + "KayitNo INTEGER PRIMARY KEY, "
                + "CariKayitNo INTEGER, "
                + "Kod TEXT, "
                + "Unvani1 TEXT, "
                + "Unvani2 TEXT, "
                + "FiyatGrubu TEXT, "
                + "OzelKod1 TEXT, "
                + "OzelKod2 TEXT, "
                + "OzelKod3 TEXT, "
                + "OzelKod4 TEXT, "
                + "OzelKod5 TEXT, "
                + "Adres1 TEXT, "
                + "Adres2 TEXT, "
                + "Sehir TEXT, "
                + "TicariIslemGrubu TEXT, "
                + "Telefon1 TEXT, "
                + "Telefon2 TEXT, "
                + "VergiNo TEXT, "
                + "KordinatLongitude TEXT, "
                + "KordinatLatitute TEXT, "
                + "IligiliKisi1 TEXT, "
                + "IligiliKisi2 TEXT, "
                + "EmailAdresi1 TEXT, "
                + "EmailAdresi2 TEXT, "
                + "Pazartesi TEXT, "
                + "PazartesiSiraNo TEXT, "
                + "Sali TEXT, "
                + "SaliSiraNo TEXT, "
                + "Carsamba TEXT, "
                + "CarsambaSiraNo TEXT, "
                + "Persembe TEXT, "
                + "PersembeSiraNo TEXT, "
                + "Cuma TEXT, "
                + "CumaSiraNo TEXT, "
                + "Cumartesi TEXT, "
                + "CumartesiSiraNo TEXT, "
                + "Pazar TEXT, "
                + "PazarSiraNo TEXT, "
                + "Resim1 TEXT, "
                + "Resim2 TEXT, "
                + "Resim3 TEXT, "
                + "WhatsappId TEXT, "
                + "TelegramId TEXT );";
        db.execSQL(table_ship_info);
        db.close();
    }

    public void createKasaIslemleriTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_KasaIslemleri ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CariKayitNo INTEGER, "
                + "ZiyaretKayitNo INTEGER, "
                + "Tarih TEXT, "
                + "EklenmeSaati TEXT, "
                + "DegisiklikSaati TEXT, "
                + "Tutar REAL, "
                + "Aciklama TEXT, "
                + "MakbuzNo TEXT, "
                + "IslemTipi  INTEGER, "
                + "KasaKodu TEXT, "
                + "ErpGonderildi  INTEGER DEFAULT 0, "
                + "ErpKayitNo INTEGER, "
                + "ErpFisNo TEXT);";
        db.execSQL(table);
        db.close();
    }

    public void createSiparisTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_Siparis ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CariKayitNo INTEGER, "
                + "ZiyaretKayitNo INTEGER, "
                + "Tarih TEXT, "
                + "EklenmeSaati TEXT, "
                + "DegisiklikSaati TEXT, "
                + "Aciklama TEXT, "
                + "SiparisTeslimTarihi TEXT, "
                + "IslemTipi  INTEGER, "
                + "OdemeSekli INTEGER, "
                + "ErpGonderildi INTEGER DEFAULT 0, "
                + "ErpKayitNo INTEGER DEFAULT 0, "
                + "ErpSiparisFisNo TEXT, "
                + "KordinatLatitute REAL, "
                + "KordinatLongitude REAL, "
                + "SiparisOncesiFoto1 TEXT, "
                + "SiparisOncesiFoto2 TEXT, "
                + "SiparisOncesiFoto3 TEXT, "
                + "Tutar REAL, "
                + "GenelIndirimOrani REAL, "
                + "GenelIndirimTutari REAL, "
                + "SatirIndirimTutari REAL, "
                + "NetTutar REAL, "
                + "SevkiyatAdresiKayitno INTEGER, "
                + "GenelIndirimYontemi INTEGER);";
        db.execSQL(table);
        db.close();
    }

    public void createSepetTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_Sepet ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "SiparisKayitNo INTEGER, "
                + "StokKayitNo INTEGER, "
                + "VaryantKayitNo INTEGER, "
                + "StokKodu TEXT, "
                + "StokAdi TEXT, "
                + "StokMiktar INTEGER, "
                + "StokFiyat REAL, "
                + "StokTutar REAL, "
                + "StokBirim TEXT, "
                + "SatirIndirimOrani REAL, "
                + "SatirIndirimTutari REAL, "
                + "GenelIndirimTutari REAL, "
                + "NetTutar REAL );";
        db.execSQL(table);
        db.close();
    }

    public void createDepolarTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_Depolar ( "
                + "SiraNo INTEGER, "
                + "DepoKayitNo INTEGER, "
                + "DepoNo INTEGER, "
                + "DepoAdi TEXT);";
        db.execSQL(table);
        db.close();
    }

    public void createDepolarAdreslerTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_DepolarAdresler ( "
                + "LokasyonKayitNo INTEGER, "
                + "DepoNo INTEGER, "
                + "LokasyonKodu TEXT, "
                + "LokasyonAdi TEXT);";
        db.execSQL(table);
        db.close();
    }

    public void createDepoStokYerleriTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_DepoStokYerleri ( "
                + "DepoNo INTEGER, "
                + "DepoAdi TEXT, "
                + "StokKodu TEXT, "
                + "Toplam INTEGER, "
                + "StokYeriKodu TEXT);";
        db.execSQL(table);
        db.close();
    }

    public void createSayimTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_Sayim ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Tarih TEXT, "
                + "EklenmeSaati TEXT, "
                + "DegisiklikSaati TEXT, "
                + "Aciklama TEXT, "
                + "IslemTipi  INTEGER, "
                + "ErpGonderildi INTEGER DEFAULT 0, "
                + "ErpKayitNo INTEGER DEFAULT 0, "
                + "ErpSiparisFisNo TEXT, "
                + "KordinatLatitute REAL, "
                + "KordinatLongitude REAL, "
                + "Tutar REAL );";
        db.execSQL(table);
        db.close();
    }

    public void createSayimSatirlariTable(String firmNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "CREATE TABLE IF NOT EXISTS AY_" + firmNo + "_SayimSatirlar ( "
                + "KayitNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "SiparisKayitNo INTEGER, "
                + "StokKayitNo INTEGER, "
                + "VaryantKayitNo INTEGER, "
                + "StokKodu TEXT, "
                + "StokAdi TEXT, "
                + "StokMiktar INTEGER, "
                + "StokFiyat REAL, "
                + "StokTutar REAL, "
                + "StokBirim TEXT );";
        db.execSQL(table);
        db.close();
    }

    public void insertCihazlarFirma(CihazlarFirma firma) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", firma.getKayitNo());
        cv.put("CihazKayitNo", firma.getCihazKayitNo());
        cv.put("MenuGrupKayitNo", firma.getMenuGrupKayitNo());
        cv.put("FirmaNo", firma.getFirmaNo());
        cv.put("DonemNo", firma.getDonemNo());
        cv.put("SatisElemaniKodu", firma.getSatisElemaniKodu());
        cv.put("TicariIslemGrubu", firma.getTicariIslemGrubu());
        cv.put("Isyeri", firma.getIsyeri());
        cv.put("Depo", firma.getDepo());
        cv.put("OndegerFiyatGrubu1", firma.getOndegerFiyatGrubu1());
        cv.put("OndegerFiyatGrubu2", firma.getOndegerFiyatGrubu2());
        cv.put("OndegerKasaKodu", firma.getOndegerKasaKodu());
        cv.put("OndegerAciklamaAlani", firma.getOndegerAciklamaAlani());
        cv.put("DepoListesi1", firma.getDepoListesi1());
        cv.put("DepoListesi2", firma.getDepoListesi2());
        cv.put("CariFiltre", firma.getCariFiltre());
        cv.put("CariSevkiyatAdresiFiltre", firma.getCariSevkiyatAdresiFiltre());
        cv.put("StokFiltre", firma.getStokFiltre());
        cv.put("FiyatFiltre", firma.getFiyatFiltre());
        cv.put("ResimAdresi", firma.getResimAdresi());
        cv.put("Depo1Aciklama1", firma.getDepo1Aciklama1());
        cv.put("Depo1Aciklama2", firma.getDepo1Aciklama2());
        cv.put("Depo2Aciklama1", firma.getDepo2Aciklama1());
        cv.put("Depo2Aciklama2", firma.getDepo2Aciklama2());
        cv.put("StokEkraniGorunumSekli", firma.getStokEkraniGorunumSekli());
        cv.put("Kullanim", firma.getKullanim());
        cv.put("GecmisFirmaNo", firma.getGecmisFirmaNo());
        cv.put("GecmisDonemNo", firma.getGecmisDonemNo());
        cv.put("FirmaAdi1", firma.getFirmaAdi1());
        cv.put("FirmaAdi2", firma.getFirmaAdi2());
        cv.put("FirmaAdi3", firma.getFirmaAdi3());
        cv.put("YerelDovizKayitNo", firma.getYerelDovizKayitNo());
        cv.put("RaporlamaDoviziKayitNo", firma.getRaporlamaDoviziKayitNo());
        db.insert(TABLE_CIHAZLAR_FIRMA, null, cv);
        db.close();
    }

    public void insertCihazlarMenu(CihazlarMenu menu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", menu.getKayitNo());
        cv.put("MenuGrupKayitNo", menu.getMenuGrupKayitNo());
        cv.put("Tip", menu.getTip());
        cv.put("Aciklama1", menu.getAciklama1());
        cv.put("Aciklama2", menu.getAciklama2());
        cv.put("Filtre", menu.getFiltre());
        cv.put("Siralama", menu.getSiralama());
        cv.put("Ondeger", menu.getOndeger());
        cv.put("Kullanim", menu.getKullanim());
        cv.put("SiraNo", menu.getSiraNo());
        cv.put("UstMenuKayitNo", menu.getUstMenuKayitNo());
        cv.put("MenuTipi", menu.getMenuTipi());
        db.insert(TABLE_CIHAZLAR_MENU, null, cv);
        db.close();
    }

    public void insertCihazlarFirmaParametreler(CihazlarFirmaParametreler parametreler) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", parametreler.getKayitNo());
        cv.put("CihazlarFirmaKayitNo", parametreler.getCihazlarFirmaKayitNo());
        cv.put("ParametreTipi", parametreler.getParametreTipi());
        cv.put("ParametreAdi", parametreler.getParametreAdi());
        cv.put("ParametreDegeri", parametreler.getParametreDegeri());
        cv.put("Aciklama", parametreler.getAciklama());
        cv.put("MobilCihazdaDegistirebilir", parametreler.getMobilCihazdaDegistirebilir());
        cv.put("Grup", parametreler.getGrup());
        db.insert(TABLE_CIHAZLAR_FIRMA_PARAMETRELER, null, cv);
        db.close();
    }

    public void insertCihazlarFirmaOdemeSekli(CihazlarFirmaOdemeSekli parametreler) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", parametreler.getKayitNo());
        cv.put("CihazlarFirmaKayitNo", parametreler.getCihazlarFirmaKayitNo());
        cv.put("IslemYonu", parametreler.getIslemYonu());
        cv.put("Aciklama1", parametreler.getAciklama1());
        cv.put("Aciklama2", parametreler.getAciklama2());
        cv.put("Aciklama3", parametreler.getAciklama3());
        db.insert(TABLE_CIHAZLAR_FIRMA_ODEMESEKLI, null, cv);
        db.close();
    }

    public void insertCihazlarFirmaDepolar(CihazlarFirmaDepolar parametreler) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", parametreler.getKayitNo());
        cv.put("CihazlarFirmaKayitNo", parametreler.getFirmaKayitNo());
        cv.put("DepoNo", parametreler.getDepoNo());
        cv.put("DepoIsmi", parametreler.getDepoIsmi());
        cv.put("DepoAciklama1", parametreler.getDepoAciklama1());
        cv.put("DepoAciklama2", parametreler.getDepoAciklama2());
        cv.put("DepoAciklama3", parametreler.getDepoAciklama3());
        db.insert(TABLE_CIHAZLAR_FIRMA_DEPOLAR, null, cv);
        db.close();
    }

    public void insertDoviz(Doviz doviz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", doviz.getKayitNo());
        cv.put("DovizKayitNo", doviz.getDovizKayitNo());
        cv.put("DovizIsareti", doviz.getDovizIsareti());
        cv.put("Aciklama", doviz.getAciklama());
        db.insert(TABLE_DOVIZ, null, cv);
        db.close();
    }

    public long insertZiyaret(ClientZiyaret visit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CariKayitNo", visit.getCariKayitNo());
        cv.put("BeginKordinatLongitude", visit.getBeginKordinatLongitude());
        cv.put("BeginKordinatLatitute", visit.getBeginKordinatLatitute());
        cv.put("EndKordinatLongitude", visit.getEndKordinatLongitude());
        cv.put("EndKordinatLatitute", visit.getEndKordinatLatitute());
        cv.put("BaslangicTarihi", visit.getBaslangicTarihi());
        cv.put("BaslangicSaati", visit.getBaslangicSaati());
        cv.put("ErpGonderildi", visit.getErpGonderildi());
        cv.put("Notlar", visit.getNotlar());
        cv.put("Kapatildi", visit.getKapatildi());
        long id = db.insert(TABLE_ZIYARET, null, cv);
        db.close();
        return id;
    }

    public void insertClients(ClCard client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", client.getKayitNo());
        cv.put("Kod", client.getKod());
        cv.put("Unvani1", client.getUnvani1());
        cv.put("Unvani2", client.getUnvani2());
        cv.put("FiyatGrubu", client.getFiyatGrubu());
        cv.put("OzelKod1", client.getOzelKod1());
        cv.put("OzelKod2", client.getOzelKod2());
        cv.put("OzelKod3", client.getOzelKod3());
        cv.put("OzelKod4", client.getOzelKod4());
        cv.put("OzelKod5", client.getOzelKod5());
        cv.put("Adres1", client.getAdres1());
        cv.put("Adres2", client.getAdres2());
        cv.put("Sehir", client.getSehir());
        cv.put("TicariIslemGrubu", client.getTicariIslemGrubu());
        cv.put("Telefon1", client.getTelefon1());
        cv.put("Telefon2", client.getTelefon2());
        cv.put("VergiNo", client.getVergiNo());
        cv.put("IndirimOrani", client.getIndirimOrani());
        cv.put("KordinatLongitude", client.getKordinatLongitude());
        cv.put("KordinatLatitute", client.getKordinatLatitute());
        cv.put("IlgiliKisi1", client.getIlgiliKisi1());
        cv.put("IlgiliKisi2", client.getIlgiliKisi2());
        cv.put("EmailAdresi1", client.getEmailAdresi1());
        cv.put("EmailAdresi2", client.getEmailAdresi2());
        cv.put("SiparisGrubuKullanimi", client.getSiparisGrubuKullanimi());
        cv.put("SonSatisTarihi", client.getSonSatisTarihi());
        cv.put("SonTahsilatTarihi", client.getSonTahsilatTarihi());
        cv.put("SonSatisGunSayisi", client.getSonSatisGunSayisi());
        cv.put("SonTahsilatGunSayisi", client.getSonTahsilatGunSayisi());
        cv.put("Pazartesi", client.getPazartesi());
        cv.put("PazartesiSiraNo", client.getPazartesiSiraNo());
        cv.put("Sali", client.getSali());
        cv.put("SaliSiraNo", client.getSaliSiraNo());
        cv.put("Carsamba", client.getCarsamba());
        cv.put("CarsambaSiraNo", client.getCarsambaSiraNo());
        cv.put("Persembe", client.getPersembe());
        cv.put("PersembeSiraNo", client.getPersembeSiraNo());
        cv.put("Cuma", client.getCuma());
        cv.put("CumaSiraNo", client.getCumaSiraNo());
        cv.put("Cumartesi", client.getCumartesi());
        cv.put("CumartesiSiraNo", client.getCumartesiSiraNo());
        cv.put("Pazar", client.getPazar());
        cv.put("PazarSiraNo", client.getPazarSiraNo());
        cv.put("SiparisGunleriToplami", client.getSiparisGunleriToplami());
        cv.put("RiskLimiti", client.getRiskLimiti());
        cv.put("NetSatis", client.getNetSatis());
        cv.put("NetTahsilat", client.getNetTahsilat());
        cv.put("Bakiye", client.getBakiye());
        cv.put("WhatsappId", client.getWhatsappId());
        cv.put("TelegramId", client.getTelegramId());
        cv.put("KartTipi", client.getKartTipi());
        cv.put("Foto1", client.getFoto1());
        cv.put("Foto2", client.getFoto2());
        cv.put("Foto3", client.getFoto3());
        cv.put("OdemeSekli", client.getOdemeSekli());
        cv.put("DovizKayitNo", client.getDovizKayitNo());
        cv.put("DovizIsareti", client.getDovizIsareti());
        db.insert("AY_" + FIRMA_NO + "_ClCard", null, cv);
        db.close();
    }

    public void insertItems(Items items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", items.getKayitNo());
        cv.put("StokKodu", items.getStokKodu());
        cv.put("UreticiKodu", items.getUreticiKodu());
        cv.put("StokAdi1", items.getStokAdi1());
        cv.put("StokAdi2", items.getStokAdi2());
        cv.put("StokAdi3", items.getStokAdi3());
        cv.put("VKod", items.getVKod());
        cv.put("VAciklama1", items.getVAciklama1());
        cv.put("VAciklama2", items.getVAciklama2());
        cv.put("VAciklama3", items.getVAciklama3());
        cv.put("ResimDosyasiKucuk", items.getResimDosyasiKucuk());
        cv.put("ResimDosyasiBuyuk1", items.getResimDosyasiBuyuk1());
        cv.put("ResimDosyasiBuyuk2", items.getResimDosyasiBuyuk2());
        cv.put("ResimDosyasiBuyuk3", items.getResimDosyasiBuyuk3());
        cv.put("GrupKodu", items.getGrupKodu());
        cv.put("OzelKod1", items.getOzelKod1());
        cv.put("OzelKod2", items.getOzelKod2());
        cv.put("OzelKod3", items.getOzelKod3());
        cv.put("OzelKod4", items.getOzelKod4());
        cv.put("OzelKod5", items.getOzelKod5());
        cv.put("MarkaKodu", items.getMarkaKodu());
        cv.put("YetkiKodu", items.getYetkiKodu());
        cv.put("SiparisGrubu", items.getSiparisGrubu());
        cv.put("IndirimYapilamaz", items.getIndirimYapilamaz());
        cv.put("Kalan1", items.getKalan1());
        cv.put("Kalan2", items.getKalan2());
        cv.put("NewIcon", items.getNewIcon());
        cv.put("IndirimIcon", items.getIndirimIcon());
        cv.put("KampanyaIcon", items.getKampanyaIcon());
        cv.put("SiparisCarpani", items.getSiparisCarpani());
        db.insert("AY_" + FIRMA_NO + "_Items", null, cv);
        db.close();
    }

    public void insertItmunita(ItemsItmunita itmunita) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", itmunita.getKayitNo());
        cv.put("StokKayitNo", itmunita.getStokKayitNo());
        cv.put("SiraNo", itmunita.getSiraNo());
        cv.put("BirimSiraKayitNo", itmunita.getBirimSiraKayitNo());
        cv.put("Birim", itmunita.getBirim());
        cv.put("Hacim", itmunita.getHacim());
        cv.put("Agirlik", itmunita.getAgirlik());
        cv.put("Carpan1", itmunita.getCarpan1());
        cv.put("Carpan2", itmunita.getCarpan2());
        db.insert("AY_" + FIRMA_NO + "_ItemsItmunita", null, cv);
        db.close();
    }

    public void insertPrclist(ItemsPrclist prclist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", prclist.getKayitNo());
        cv.put("StokKayitNo", prclist.getStokKayitNo());
        cv.put("BirimKayitNo", prclist.getBirimKayitNo());
        cv.put("FiyatGrubu", prclist.getFiyatGrubu());
        cv.put("CariHesapKodu", prclist.getCariHesapKodu());
        cv.put("Fiyat", prclist.getFiyat());
        cv.put("BaslangicTarih", prclist.getBaslangicTarih());
        cv.put("BitisTarih", prclist.getBitisTarih());
        cv.put("DovizTipiKayitNo", prclist.getDovizTipiKayitNo());
        cv.put("DovizIsareti", prclist.getDovizIsareti());
        db.insert("AY_" + FIRMA_NO + "_ItemsPrclist", null, cv);
        db.close();
    }

    public void insertToplam(ItemsToplamlar toplamlar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", toplamlar.getKayitNo());
        cv.put("StokKayitNo", toplamlar.getStokKayitNo());
        cv.put("DepoNo", toplamlar.getDepoNo());
        cv.put("DepoAdi", toplamlar.getDepoAdi());
        cv.put("Toplam", toplamlar.getToplam());
        cv.put("StokKodu", toplamlar.getStokKodu());
        cv.put("StokYeriKodu", toplamlar.getStokYeriKodu());
        db.insert("AY_" + FIRMA_NO + "_ItemsToplamlar", null, cv);
        db.close();
    }

    public void insertBarkod(ItemsUnitBarcode barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", barcode.getKayitNo());
        cv.put("Itmunitaref", barcode.getItmunitaref());
        cv.put("StokKayitNo", barcode.getStokKayitNo());
        cv.put("SatirNo", barcode.getSatirNo());
        cv.put("Barkod", barcode.getBarkod());
        db.insert("AY_" + FIRMA_NO + "_ItemsUnitBarcode", null, cv);
        db.close();
    }

    public void insertShipInfo(ShipInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", info.getKayitNo());
        cv.put("CariKayitNo", info.getCariKayitNo());
        cv.put("Kod", info.getKod());
        cv.put("Unvani1", info.getUnvani1());
        cv.put("Unvani2", info.getUnvani2());
        cv.put("FiyatGrubu", info.getFiyatGrubu());
        cv.put("OzelKod1", info.getOzelKod1());
        cv.put("OzelKod2", info.getOzelKod2());
        cv.put("OzelKod3", info.getOzelKod3());
        cv.put("OzelKod4", info.getOzelKod4());
        cv.put("OzelKod5", info.getOzelKod5());
        cv.put("Adres1", info.getAdres1());
        cv.put("Adres2", info.getAdres2());
        cv.put("Sehir", info.getSehir());
        cv.put("TicariIslemGrubu", info.getTicariIslemGrubu());
        cv.put("Telefon1", info.getTelefon1());
        cv.put("Telefon2", info.getTelefon2());
        cv.put("VergiNo", info.getVergiNo());
        cv.put("KordinatLongitude", info.getKordinatLongitude());
        cv.put("KordinatLatitute", info.getKordinatLatitute());
        cv.put("IligiliKisi1", info.getIligiliKisi1());
        cv.put("IligiliKisi2", info.getIligiliKisi2());
        cv.put("EmailAdresi1", info.getEmailAdresi1());
        cv.put("EmailAdresi2", info.getEmailAdresi2());
        cv.put("Pazartesi", info.getPazartesi());
        cv.put("PazartesiSiraNo", info.getPazartesiSiraNo());
        cv.put("Sali", info.getSali());
        cv.put("SaliSiraNo", info.getSaliSiraNo());
        cv.put("Carsamba", info.getCarsamba());
        cv.put("CarsambaSiraNo", info.getCarsambaSiraNo());
        cv.put("Persembe", info.getPersembe());
        cv.put("PersembeSiraNo", info.getPersembeSiraNo());
        cv.put("Cuma", info.getCuma());
        cv.put("CumaSiraNo", info.getCumaSiraNo());
        cv.put("Cumartesi", info.getCumartesi());
        cv.put("CumartesiSiraNo", info.getCumartesiSiraNo());
        cv.put("Pazar", info.getPazar());
        cv.put("PazarSiraNo", info.getPazarSiraNo());
        cv.put("Resim1", info.getResim1());
        cv.put("Resim2", info.getResim2());
        cv.put("Resim3", info.getResim3());
        cv.put("WhatsappId", info.getWhatsappId());
        cv.put("TelegramId", info.getTelegramId());
        db.insert("AY_" + FIRMA_NO + "_ShipInfo", null, cv);
        db.close();
    }

    public long insertKasaIslemleri(ClientKasa kasa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", kasa.getKayitNo());
        cv.put("CariKayitNo", kasa.getCariKayitNo());
        cv.put("ZiyaretKayitNo", kasa.getZiyaretKayitNo());
        cv.put("Tarih", kasa.getTarih());
        cv.put("Tutar", kasa.getTutar());
        cv.put("EklenmeSaati", kasa.getEklenmeSaati());
        cv.put("DegisiklikSaati", kasa.getDegisiklikSaati());
        cv.put("Aciklama", kasa.getAciklama());
        cv.put("MakbuzNo", kasa.getMakbuzNo());
        cv.put("IslemTipi ", kasa.getIslemTipi());
        cv.put("KasaKodu", kasa.getKasaKodu());
        cv.put("ErpGonderildi", kasa.getErpGonderildi());
        cv.put("ErpKayitNo", kasa.getErpKayitNo());
        cv.put("ErpFisNo", kasa.getErpFisNo());
        long kayitno = db.insert("AY_" + FIRMA_NO + "_KasaIslemleri", null, cv);
        db.close();
        return kayitno;
    }

    public long insertSiparis(ClientSiparis order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CariKayitNo", order.getCariKayitNo());
        cv.put("ZiyaretKayitNo", order.getZiyaretKayitNo());
        cv.put("Tarih", order.getTarih());
        cv.put("EklenmeSaati", order.getEklenmeSaati());
        cv.put("DegisiklikSaati", order.getDegisiklikSaati());
        cv.put("Aciklama", order.getAciklama());
        cv.put("SiparisTeslimTarihi", order.getSiparisTeslimTarihi());
        cv.put("IslemTipi", order.getIslemTipi());
        cv.put("OdemeSekli", order.getOdemeSekli());
        cv.put("ErpGonderildi", order.getErpGonderildi());
        cv.put("ErpKayitNo", order.getErpKayitNo());
        cv.put("ErpSiparisFisNo", order.getErpSiparisFisNo());
        cv.put("KordinatLatitute", order.getKordinatLatitute());
        cv.put("KordinatLongitude", order.getKordinatLongitude());
        cv.put("SiparisOncesiFoto1", order.getSiparisOncesiFoto1());
        cv.put("SiparisOncesiFoto2", order.getSiparisOncesiFoto2());
        cv.put("SiparisOncesiFoto3", order.getSiparisOncesiFoto3());
        cv.put("Tutar", order.getTutar());
        cv.put("GenelIndirimOrani", order.getGenelIndirimOrani());
        cv.put("GenelIndirimTutari", order.getGenelIndirimTutari());
        cv.put("SatirIndirimTutari", order.getSatirIndirimTutari());
        cv.put("NetTutar", order.getNetTutar());
        cv.put("SevkiyatAdresiKayitno", order.getSevkiyatAdresiKayitno());
        cv.put("GenelIndirimYontemi", order.getGenelIndirimYontemi());
        long id = db.insert("AY_" + FIRMA_NO + "_Siparis", null, cv);
        db.close();
        return id;
    }

    public void insertSepet(ClientSepet cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SiparisKayitNo", cart.getSiparisKayitNo());
        cv.put("StokKayitNo", cart.getStokKayitNo());
        cv.put("VaryantKayitNo", cart.getVaryantKayitNo());
        cv.put("StokKodu", cart.getStokKodu());
        cv.put("StokAdi", cart.getStokAdi());
        cv.put("StokMiktar", cart.getStokMiktar());
        cv.put("StokFiyat", cart.getStokFiyat());
        cv.put("StokTutar", cart.getStokTutar());
        cv.put("StokBirim", cart.getStokBirim());
        cv.put("SatirIndirimOrani", cart.getSatirIndirimOrani());
        cv.put("SatirIndirimTutari", cart.getSatirIndirimTutari());
        cv.put("GenelIndirimTutari", cart.getGenelIndirimTutari());
        cv.put("NetTutar", cart.getNetTutar());
        db.insert("AY_" + FIRMA_NO + "_Sepet", null, cv);
        db.close();
    }

    public void insertDepolar(ItemsDepolar depolar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SiraNo", depolar.getSiraNo());
        cv.put("DepoKayitNo", depolar.getDepoKayitNo());
        cv.put("DepoNo", depolar.getDepoNo());
        cv.put("DepoAdi", depolar.getDepoAdi());
        db.insert("AY_" + FIRMA_NO + "_Depolar", null, cv);
        db.close();
    }

    public void insertDepolarAdresler(ItemsDepolarAdresler depolar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("LokasyonKayitNo", depolar.getLokasyonKayitNo());
        cv.put("DepoNo", depolar.getDepoNo());
        cv.put("LokasyonKodu", depolar.getLokasyonKodu());
        cv.put("LokasyonAdi", depolar.getLokasyonAdi());
        db.insert("AY_" + FIRMA_NO + "_DepolarAdresler", null, cv);
        db.close();
    }

    public void insertDepoStokYerleri(ItemsDepoStokYerleri depolar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DepoNo", depolar.getDepoNo());
        cv.put("DepoAdi", depolar.getDepoAdi());
        cv.put("StokKodu", depolar.getStokKodu());
        cv.put("Toplam", depolar.getToplam());
        cv.put("StokYeriKodu", depolar.getStokYeriKodu());
        db.insert("AY_" + FIRMA_NO + "_DepoStokYerleri", null, cv);
        db.close();
    }

    public void updateClientBalance(ClCard card, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Kod", card.getKod());
        cv.put("NetSatis", card.getNetSatis());
        cv.put("NetTahsilat", card.getNetTahsilat());
        cv.put("Bakiye", card.getBakiye());
        db.update("AY_" + FIRMA_NO + "_ClCard", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateClientOrderDays(ClCard card, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Pazartesi", card.getPazartesi());
        cv.put("PazartesiSiraNo", card.getPazartesiSiraNo());
        cv.put("Sali", card.getSali());
        cv.put("SaliSiraNo", card.getSaliSiraNo());
        cv.put("Carsamba", card.getCarsamba());
        cv.put("CarsambaSiraNo", card.getCarsambaSiraNo());
        cv.put("Persembe", card.getPersembe());
        cv.put("PersembeSiraNo", card.getPersembeSiraNo());
        cv.put("Cuma", card.getCuma());
        cv.put("CumaSiraNo", card.getCumaSiraNo());
        cv.put("Cumartesi", card.getCumartesi());
        cv.put("CumartesiSiraNo", card.getCumartesiSiraNo());
        cv.put("Pazar", card.getPazar());
        cv.put("PazarSiraNo", card.getPazarSiraNo());
        db.update("AY_" + FIRMA_NO + "_ClCard", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateZiyaretKapat(ClientZiyaret visit, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("BitisTarihi", visit.getBitisTarihi());
        cv.put("BitisSaati", visit.getBitisSaati());
        cv.put("EndKordinatLongitude", visit.getEndKordinatLongitude());
        cv.put("EndKordinatLatitute", visit.getEndKordinatLatitute());
        cv.put("Kapatildi", visit.getKapatildi());
        db.update(TABLE_ZIYARET, cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateZiyaretErpGonderildi(ClientZiyaret visit, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ErpGonderildi", visit.getErpGonderildi());
        db.update(TABLE_ZIYARET, cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateZiyaretDefinition(String definition, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Notlar", definition);
        db.update(TABLE_ZIYARET, cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateToplam(ItemsToplamlar items, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KayitNo", items.getKayitNo());
        cv.put("StokKayitNo", items.getStokKayitNo());
        cv.put("DepoNo", items.getDepoNo());
        cv.put("Toplam", items.getToplam());
        cv.put("StokKodu", items.getStokKodu());
        db.update("AY_" + FIRMA_NO + "_ItemsToplamlar", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateSiparis(ClientSiparis order, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CariKayitNo", order.getCariKayitNo());
        cv.put("DegisiklikSaati", order.getDegisiklikSaati());
        cv.put("Tutar", order.getTutar());
        cv.put("NetTutar", order.getNetTutar());
        cv.put("IslemTipi", order.getIslemTipi());
        cv.put("GenelIndirimOrani", order.getGenelIndirimOrani());
        cv.put("GenelIndirimTutari", order.getGenelIndirimTutari());
        cv.put("Aciklama", order.getAciklama());
        cv.put("ErpGonderildi", order.getErpGonderildi());
        db.update("AY_" + FIRMA_NO + "_Siparis", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateSepet(ClientSepet cart, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SiparisKayitNo", cart.getSiparisKayitNo());
        cv.put("StokKayitNo", cart.getStokKayitNo());
        cv.put("VaryantKayitNo", cart.getVaryantKayitNo());
        cv.put("StokKodu", cart.getStokKodu());
        cv.put("StokAdi", cart.getStokAdi());
        cv.put("StokMiktar", cart.getStokMiktar());
        cv.put("StokFiyat", cart.getStokFiyat());
        cv.put("StokTutar", cart.getStokTutar());
        cv.put("StokBirim", cart.getStokBirim());
        cv.put("SatirIndirimOrani", cart.getSatirIndirimOrani());
        cv.put("SatirIndirimTutari", cart.getSatirIndirimTutari());
        cv.put("GenelIndirimTutari", cart.getGenelIndirimTutari());
        cv.put("NetTutar", cart.getNetTutar());
        db.update("AY_" + FIRMA_NO + "_Sepet", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateKasaIslemleri(ClientKasa kasa, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Tarih", kasa.getTarih());
        cv.put("Tutar", kasa.getTutar());
        cv.put("EklenmeSaati", kasa.getEklenmeSaati());
        cv.put("DegisiklikSaati", kasa.getDegisiklikSaati());
        cv.put("Aciklama", kasa.getAciklama());
        cv.put("MakbuzNo", kasa.getMakbuzNo());
        db.update("AY_" + FIRMA_NO + "_KasaIslemleri", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateKasaIslemleriErpGonder(ClientKasa kasa, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ErpGonderildi", kasa.getErpGonderildi());
        cv.put("ErpKayitNo", kasa.getErpKayitNo());
        cv.put("ErpFisNo", kasa.getErpFisNo());
        db.update("AY_" + FIRMA_NO + "_KasaIslemleri", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateSiparisIslemleriErpGonder(ClientSiparis siparis, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ErpKayitNo", siparis.getErpKayitNo());
        cv.put("ErpSiparisFisNo", siparis.getErpSiparisFisNo());
        cv.put("ErpGonderildi", siparis.getErpGonderildi());
        db.update("AY_" + FIRMA_NO + "_Siparis", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateClientLocation(String latitude, String longitude, String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("KordinatLongitude", longitude);
        cv.put("KordinatLatitute", latitude);
        db.update("AY_" + FIRMA_NO + "_ClCard", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void updateShelveName(ItemsToplamlar toplamlar, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("StokYeriKodu", toplamlar.getStokYeriKodu());
        db.update("AY_" + FIRMA_NO + "_ItemsToplamlar", cv, "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void deleteSepet(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AY_" + FIRMA_NO + "_Sepet", "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void deleteSiparis(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AY_" + FIRMA_NO + "_Siparis", "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void deleteSepetBySiparisNo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AY_" + FIRMA_NO + "_Sepet", "SiparisKayitNo = ?", new String[]{id});
        db.close();
    }

    public void deleteKasaIslemleri(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AY_" + FIRMA_NO + "_KasaIslemleri", "KayitNo = ?", new String[]{id});
        db.close();
    }

    public void deleteClients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ClCard");
        db.close();
    }

    public void deleteItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_Items");
        db.close();
    }

    public void deleteItmunita() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ItemsItmunita");
        db.close();
    }

    public void deletePrclist() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ItemsPrclist");
        db.close();
    }

    public void deleteToplam() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ItemsToplamlar");
        db.close();
    }

    public void deleteBarkod() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ItemsUnitBarcode");
        db.close();
    }

    public void deleteShipInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_ShipInfo");
        db.close();
    }

    public void deleteCihazlarFirma() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CIHAZLAR_FIRMA);
        db.close();
    }

    public void deleteCihazlarMenu() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CIHAZLAR_MENU);
        db.close();
    }

    public void deleteCihazlarFirmaParametreler() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER);
        db.close();
    }

    public void deleteCihazlarFirmaOdemeSekli() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CIHAZLAR_FIRMA_ODEMESEKLI);
        db.close();
    }

    public void deleteCihazlarFirmaDepolar() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CIHAZLAR_FIRMA_DEPOLAR);
        db.close();
    }

    public void deleteDoviz() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DOVIZ);
        db.close();
    }

    public void deleteDepolar() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_Depolar");
        db.close();
    }

    public void deleteDepolarAdresler() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_DepolarAdresler");
        db.close();
    }

    public void deleteDepoStokYerleri() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "AY_" + FIRMA_NO + "_DepoStokYerleri");
        db.close();
    }

    public List<ItemsWithPrices> selectAllItems(String fiyat1, String fiyat2, String filtre) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsWithPrices> list = new ArrayList<>();
        String sql = "SELECT items.KayitNo, items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.Kalan2, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "unt.Birim, " +
                "prc1.Fiyat as Fiyat1, " +
                "prc2.Fiyat as Fiyat2, " +
                "prc1.DovizIsareti AS dov1, " +
                "prc2.DovizIsareti AS dov2 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc2 ON prc2.StokKayitNo = items.KayitNo AND prc2.FiyatGrubu = '" + fiyat2 + "' AND prc2.BirimKayitNo=unt.KayitNo " +
                "WHERE items.StokKodu <> '' ";
        if (filtre != null) sql += filtre;
        sql += " ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsWithPrices items = new ItemsWithPrices();
                items.setKayitNo(cursor.getInt(0));
                items.setStokKodu(cursor.getString(1));
                items.setStokAdi1(cursor.getString(2));
                items.setKalan1(cursor.getDouble(3));
                items.setKalan2(cursor.getDouble(4));
                items.setStokResim(cursor.getString(5));
                items.setStokResim1(cursor.getString(6));
                items.setStokResim2(cursor.getString(7));
                items.setStokResim3(cursor.getString(8));
                items.setBirim(cursor.getString(9));
                items.setFiyat1(cursor.getDouble(10));
                items.setFiyat2(cursor.getDouble(11));
                items.setDoviz1(cursor.getString(12));
                items.setDoviz2(cursor.getString(13));
                list.add(items);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ItemsWithPrices> selectAllItemsZero(String fiyat1, String fiyat2, String filtre) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsWithPrices> list = new ArrayList<>();
        String sql = "SELECT items.KayitNo, items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.Kalan2, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "unt.Birim, " +
                "prc1.Fiyat as Fiyat1, " +
                "prc2.Fiyat as Fiyat2, " +
                "prc1.DovizIsareti AS dov1, " +
                "prc2.DovizIsareti AS dov2 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc2 ON prc2.StokKayitNo = items.KayitNo AND prc2.FiyatGrubu = '" + fiyat2 + "' AND prc2.BirimKayitNo=unt.KayitNo " +
                "WHERE items.Kalan1 = 0 AND items.StokKodu <> '' ";
        if (filtre != null) sql += filtre;
        sql += " ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsWithPrices items = new ItemsWithPrices();
                items.setKayitNo(cursor.getInt(0));
                items.setStokKodu(cursor.getString(1));
                items.setStokAdi1(cursor.getString(2));
                items.setKalan1(cursor.getDouble(3));
                items.setKalan2(cursor.getDouble(4));
                items.setStokResim(cursor.getString(5));
                items.setStokResim1(cursor.getString(6));
                items.setStokResim2(cursor.getString(7));
                items.setStokResim3(cursor.getString(8));
                items.setBirim(cursor.getString(9));
                items.setFiyat1(cursor.getDouble(10));
                items.setFiyat2(cursor.getDouble(11));
                items.setDoviz1(cursor.getString(12));
                items.setDoviz2(cursor.getString(13));
                list.add(items);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ItemsWithPrices> selectItemsByBarcode(String fiyat1, String fiyat2, String filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsWithPrices> list = new ArrayList<>();
        String sql = "SELECT items.KayitNo, items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.Kalan2, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "unt.Birim, " +
                "prc1.Fiyat as Fiyat1, " +
                "prc2.Fiyat as Fiyat2, " +
                "prc1.DovizIsareti AS dov1, " +
                "prc2.DovizIsareti AS dov2 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc2 ON prc2.StokKayitNo = items.KayitNo AND prc2.FiyatGrubu = '" + fiyat2 + "' AND prc2.BirimKayitNo=unt.KayitNo " +
                "WHERE items.KayitNo = (SELECT StokKayitNo FROM AY_" + FIRMA_NO + "_ItemsUnitBarcode WHERE Barkod = '"+ filter + "') " +
                "OR items.StokKodu = '"+ filter + "' " +
                "ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsWithPrices items = new ItemsWithPrices();
                items.setKayitNo(cursor.getInt(0));
                items.setStokKodu(cursor.getString(1));
                items.setStokAdi1(cursor.getString(2));
                items.setKalan1(cursor.getDouble(3));
                items.setKalan2(cursor.getDouble(4));
                items.setStokResim(cursor.getString(5));
                items.setStokResim1(cursor.getString(6));
                items.setStokResim2(cursor.getString(7));
                items.setStokResim3(cursor.getString(8));
                items.setBirim(cursor.getString(9));
                items.setFiyat1(cursor.getDouble(10));
                items.setFiyat2(cursor.getDouble(11));
                items.setDoviz1(cursor.getString(12));
                items.setDoviz2(cursor.getString(13));
                list.add(items);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public ItemsWithPrices selectItemByBarcode(String fiyat1, String fiyat2, String filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        ItemsWithPrices item = new ItemsWithPrices();
        String sql = "SELECT items.KayitNo, items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.Kalan2, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "unt.Birim, " +
                "prc1.Fiyat as Fiyat1, " +
                "prc2.Fiyat as Fiyat2, " +
                "prc1.DovizIsareti AS dov1, " +
                "prc2.DovizIsareti AS dov2 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc2 ON prc2.StokKayitNo = items.KayitNo AND prc2.FiyatGrubu = '" + fiyat2 + "' AND prc2.BirimKayitNo=unt.KayitNo " +
                "WHERE items.KayitNo = (SELECT StokKayitNo FROM AY_" + FIRMA_NO + "_ItemsUnitBarcode WHERE Barkod = '"+ filter + "') " +
                "OR lower(items.StokKodu) = '"+ filter.toLowerCase() + "' " +
                "ORDER BY items.StokKodu LIMIT 1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                item.setKayitNo(cursor.getInt(0));
                item.setStokKodu(cursor.getString(1));
                item.setStokAdi1(cursor.getString(2));
                item.setKalan1(cursor.getDouble(3));
                item.setKalan2(cursor.getDouble(4));
                item.setStokResim(cursor.getString(5));
                item.setStokResim1(cursor.getString(6));
                item.setStokResim2(cursor.getString(7));
                item.setStokResim3(cursor.getString(8));
                item.setBirim(cursor.getString(9));
                item.setFiyat1(cursor.getDouble(10));
                item.setFiyat2(cursor.getDouble(11));
                item.setDoviz1(cursor.getString(12));
                item.setDoviz2(cursor.getString(13));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return item;
    }

    public List<ItemsWithPrices> selectAllItemsByStock(String fiyat1, String fiyat2, String filtre, long min, long max) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsWithPrices> list = new ArrayList<>();
        String sql = "SELECT items.KayitNo, items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.Kalan2, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "unt.Birim, " +
                "prc1.Fiyat as Fiyat1, " +
                "prc2.Fiyat as Fiyat2, " +
                "prc1.DovizIsareti AS dov1, " +
                "prc2.DovizIsareti AS dov2 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc2 ON prc2.StokKayitNo = items.KayitNo AND prc2.FiyatGrubu = '" + fiyat2 + "' AND prc2.BirimKayitNo=unt.KayitNo " +
                "WHERE items.Kalan1 >= " + min + " AND items.Kalan1 <= " + max + "  AND items.StokKodu <> '' ";
        if (filtre != null) sql += filtre;
        sql += " ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsWithPrices items = new ItemsWithPrices();
                items.setKayitNo(cursor.getInt(0));
                items.setStokKodu(cursor.getString(1));
                items.setStokAdi1(cursor.getString(2));
                items.setKalan1(cursor.getDouble(3));
                items.setKalan2(cursor.getDouble(4));
                items.setStokResim(cursor.getString(5));
                items.setStokResim1(cursor.getString(6));
                items.setStokResim2(cursor.getString(7));
                items.setStokResim3(cursor.getString(8));
                items.setBirim(cursor.getString(9));
                items.setFiyat1(cursor.getDouble(10));
                items.setFiyat2(cursor.getDouble(11));
                items.setDoviz1(cursor.getString(12));
                items.setDoviz2(cursor.getString(13));
                list.add(items);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ItemsToplamlar> selectAllItemsByAmbar(int kayitno) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsToplamlar> toplamList = new ArrayList<>();
        String sql = "SELECT itemsToplamlar.KayitNo, itemsToplamlar.StokKayitNo, itemsToplamlar.DepoNo, itemsToplamlar.DepoAdi, "
                + "itemsToplamlar.StokKodu, itemsToplamlar.Toplam, itemsToplamlar.StokYeriKodu, items.StokAdi1 "
                + "FROM " + "AY_" + FIRMA_NO + "_ItemsToplamlar AS itemsToplamlar "
                + "INNER JOIN AY_" + FIRMA_NO + "_Items AS items ON itemsToplamlar.StokKayitNo = items.KayitNo "
                + "WHERE StokKayitNo = " + kayitno;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsToplamlar toplamlar = new ItemsToplamlar();
                toplamlar.setKayitNo(cursor.getInt(0));
                toplamlar.setStokKayitNo(cursor.getInt(1));
                toplamlar.setDepoNo(cursor.getInt(2));
                toplamlar.setDepoAdi(cursor.getString(3));
                toplamlar.setStokKodu(cursor.getString(4));
                toplamlar.setToplam(cursor.getDouble(5));
                toplamlar.setStokYeriKodu(cursor.getString(6));
                toplamlar.setStokAciklamasi(cursor.getString(7));
                toplamList.add(toplamlar);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toplamList;
    }

    public ItemsWithPrices selectProductByCode(String code, String fiyat1) {
        SQLiteDatabase db = this.getReadableDatabase();
        ItemsWithPrices items = new ItemsWithPrices();
        String sql = "SELECT items.StokKodu, " +
                "items.StokAdi1 as StokAdi1, " +
                "items.Kalan1, " +
                "items.ResimDosyasiKucuk AS StokResim, " +
                "items.ResimDosyasiBuyuk1 AS StokResim1, " +
                "items.ResimDosyasiBuyuk2 AS StokResim2, " +
                "items.ResimDosyasiBuyuk3 AS StokResim3, " +
                "prc1.Fiyat as Fiyat1 " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt ON unt.StokKayitNo = items.KayitNo AND unt.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " AS prc1 ON prc1.StokKayitNo = items.KayitNo " +
                "AND prc1.FiyatGrubu = '" + fiyat1 + "' AND prc1.BirimKayitNo=unt.KayitNo " +
                "WHERE items.StokKodu = '" + code + "' " +
                "ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            items.setStokKodu(cursor.getString(0));
            items.setStokAdi1(cursor.getString(1));
            items.setKalan1(cursor.getDouble(2));
            items.setStokResim(cursor.getString(3));
            items.setStokResim1(cursor.getString(4));
            items.setStokResim2(cursor.getString(5));
            items.setStokResim3(cursor.getString(6));
            items.setFiyat1(cursor.getDouble(7));
        }
        cursor.close();
        db.close();
        return items;
    }

    public ItemsWithPrices selectProductInfo(int kayitNo, String fiyat1) {
        SQLiteDatabase db = this.getReadableDatabase();
        ItemsWithPrices items = new ItemsWithPrices();
        String sql = "SELECT items.KayitNo, items.StokKodu, items.UreticiKodu, items.StokAdi1, " +
                "items.ResimDosyasiKucuk, items.ResimDosyasiBuyuk1, items.ResimDosyasiBuyuk2, " +
                "items.ResimDosyasiBuyuk3, items.OzelKod1, items.OzelKod2, " +
                "items.OzelKod3, items.OzelKod4, items.OzelKod5, items.Kalan1, items.Kalan2, " +
                "unt1.Birim, unt2.Birim AS birim2, unt2.Carpan2, brk1.Barkod AS barkod1, brk2.Barkod AS barkod2, " +
                "(SELECT sum(sep.StokMiktar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " sip LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_Sepet" + " AS sep ON sip.KayitNo = sep.SiparisKayitNo AND sip.IslemTipi = 1 " +
                "AND sep.StokKodu = items.StokKodu) as SiparisSatinalma, " +
                "(SELECT sum(sep.StokMiktar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " sip LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_Sepet" + " AS sep ON sip.KayitNo = sep.SiparisKayitNo AND sip.IslemTipi = 2 " +
                "AND sep.StokKodu = items.StokKodu) as SiparisSatis " +
                "FROM " + "AY_" + FIRMA_NO + "_Items" + " items " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt1 ON unt1.StokKayitNo = items.KayitNo AND unt1.SiraNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsItmunita" + " AS unt2 ON unt2.StokKayitNo = items.KayitNo AND unt2.SiraNo = 2 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsUnitBarcode" + " brk1 ON brk1.StokKayitNo = items.KayitNo AND brk1.Itmunitaref = unt1.KayitNo AND brk1.SatirNo = 1 " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_ItemsUnitBarcode" + " brk2 ON brk2.StokKayitNo = items.KayitNo AND brk2.Itmunitaref = unt2.KayitNo AND brk2.SatirNo = 1 " +
                "WHERE items.KayitNo = " + kayitNo + " " +
                "ORDER BY items.StokKodu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            items.setKayitNo(cursor.getInt(0));
            items.setStokKodu(cursor.getString(1));
            items.setUreticiKodu(cursor.getString(2));
            items.setStokAdi1(cursor.getString(3));
            items.setStokResim(cursor.getString(4));
            items.setStokResim1(cursor.getString(5));
            items.setStokResim2(cursor.getString(6));
            items.setStokResim3(cursor.getString(7));
            items.setOzelKod1(cursor.getString(8));
            items.setOzelKod2(cursor.getString(9));
            items.setOzelKod3(cursor.getString(10));
            items.setOzelKod4(cursor.getString(11));
            items.setOzelKod5(cursor.getString(12));
            items.setKalan1(cursor.getDouble(13));
            items.setKalan2(cursor.getDouble(14));
            items.setBirim(cursor.getString(15));
            items.setBirim2(cursor.getString(16));
            items.setCarpan2(cursor.getInt(17));
            items.setBarkod(cursor.getString(18));
            items.setBarkod2(cursor.getString(19));
            items.setSiparisSatinalma(cursor.getInt(20));
            items.setSiparisSatis(cursor.getInt(21));
        }
        cursor.close();
        db.close();
        return items;
    }

    public List<ItemsPrclist> selectProductPrices(int kayitNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsPrclist> prclists = new ArrayList<>();
        String sql = "SELECT prc.Fiyat, prc.FiyatGrubu, prc.DovizIsareti, unt.Birim " +
                "FROM AY_" + FIRMA_NO + "_ItemsPrclist prc INNER JOIN AY_" + FIRMA_NO + "_ItemsItmunita AS unt ON prc.BirimKayitNo = unt.KayitNo " +
                "WHERE prc.StokKayitNo = " + kayitNo + " " +
                "ORDER BY prc.StokKayitNo";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsPrclist price = new ItemsPrclist();
                price.setFiyat(cursor.getDouble(0));
                price.setFiyatGrubu(cursor.getString(1));
                price.setDovizIsareti(cursor.getString(2));
                price.setBaslangicTarih(cursor.getString(3));
                prclists.add(price);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return prclists;
    }

    public List<ClCard> selectAllClients(String filter, String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClCard> cardList = new ArrayList<>();
        String sql = "SELECT KayitNo, Kod, Unvani1, Adres1, Telefon1, Bakiye, KordinatLongitude, KordinatLatitute, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 0 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS beklemedeTutar, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 1 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS gonderilenTutar, " +
                "Pazartesi, PazartesiSiraNo, Sali, SaliSiraNo, Carsamba, CarsambaSiraNo, " +
                "Persembe, PersembeSiraNo, Cuma, CumaSiraNo, Cumartesi, CumartesiSiraNo, Pazar, PazarSiraNo "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client "
                + "WHERE Unvani1 <> '' " + filter
                + " ORDER BY " + order;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClCard card = new ClCard();
                card.setKayitNo(cursor.getInt(0));
                card.setKod(cursor.getString(1));
                card.setUnvani1(cursor.getString(2));
                card.setAdres1(cursor.getString(3));
                card.setTelefon1(cursor.getString(4));
                card.setBakiye(cursor.getDouble(5));
                card.setKordinatLongitude(cursor.getDouble(6));
                card.setKordinatLatitute(cursor.getDouble(7));
                card.setSiparisBeklemede(cursor.getInt(8));
                card.setSiparisGonderildi(cursor.getInt(9));
                card.setPazartesi(cursor.getString(10));
                card.setPazartesiSiraNo(cursor.getString(11));
                card.setSali(cursor.getString(12));
                card.setSaliSiraNo(cursor.getString(13));
                card.setCarsamba(cursor.getString(14));
                card.setCarsambaSiraNo(cursor.getString(15));
                card.setPersembe(cursor.getString(16));
                card.setPersembeSiraNo(cursor.getString(17));
                card.setCuma(cursor.getString(18));
                card.setCumaSiraNo(cursor.getString(19));
                card.setCumartesi(cursor.getString(20));
                card.setCumartesiSiraNo(cursor.getString(21));
                card.setPazar(cursor.getString(22));
                card.setPazarSiraNo(cursor.getString(23));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    public List<ClCard> selectAllClientsByBalanceZero(String filter, String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClCard> cardList = new ArrayList<>();
        String sql = "SELECT KayitNo, Kod, Unvani1, Adres1, Telefon1, Bakiye, KordinatLongitude, KordinatLatitute, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 0 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS beklemedeTutar, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 1 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS gonderilenTutar, " +
                "Pazartesi, PazartesiSiraNo, Sali, SaliSiraNo, Carsamba, CarsambaSiraNo, " +
                "Persembe, PersembeSiraNo, Cuma, CumaSiraNo, Cumartesi, CumartesiSiraNo, Pazar, PazarSiraNo "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client "
                + "WHERE Bakiye = 0 AND Unvani1 <> '' " + filter
                + " ORDER BY " + order;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClCard card = new ClCard();
                card.setKayitNo(cursor.getInt(0));
                card.setKod(cursor.getString(1));
                card.setUnvani1(cursor.getString(2));
                card.setAdres1(cursor.getString(3));
                card.setTelefon1(cursor.getString(4));
                card.setBakiye(cursor.getDouble(5));
                card.setKordinatLongitude(cursor.getDouble(6));
                card.setKordinatLatitute(cursor.getDouble(7));
                card.setSiparisBeklemede(cursor.getInt(8));
                card.setSiparisGonderildi(cursor.getInt(9));
                card.setPazartesi(cursor.getString(10));
                card.setPazartesiSiraNo(cursor.getString(11));
                card.setSali(cursor.getString(12));
                card.setSaliSiraNo(cursor.getString(13));
                card.setCarsamba(cursor.getString(14));
                card.setCarsambaSiraNo(cursor.getString(15));
                card.setPersembe(cursor.getString(16));
                card.setPersembeSiraNo(cursor.getString(17));
                card.setCuma(cursor.getString(18));
                card.setCumaSiraNo(cursor.getString(19));
                card.setCumartesi(cursor.getString(20));
                card.setCumartesiSiraNo(cursor.getString(21));
                card.setPazar(cursor.getString(22));
                card.setPazarSiraNo(cursor.getString(23));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    public List<ClCard> selectAllClientsByBalance(String filter, String order, long min, long max) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClCard> cardList = new ArrayList<>();
        String sql = "SELECT KayitNo, Kod, Unvani1, Adres1, Telefon1, Bakiye, KordinatLongitude, KordinatLatitute, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 0 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS beklemedeTutar, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 1 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS gonderilenTutar, " +
                "Pazartesi, PazartesiSiraNo, Sali, SaliSiraNo, Carsamba, CarsambaSiraNo, " +
                "Persembe, PersembeSiraNo, Cuma, CumaSiraNo, Cumartesi, CumartesiSiraNo, Pazar, PazarSiraNo "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client "
                + "WHERE Bakiye >= " + min + " AND Bakiye <= " + max + " AND Unvani1 <> '' " + filter
                + " ORDER BY " + order;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClCard card = new ClCard();
                card.setKayitNo(cursor.getInt(0));
                card.setKod(cursor.getString(1));
                card.setUnvani1(cursor.getString(2));
                card.setAdres1(cursor.getString(3));
                card.setTelefon1(cursor.getString(4));
                card.setBakiye(cursor.getDouble(5));
                card.setKordinatLongitude(cursor.getDouble(6));
                card.setKordinatLatitute(cursor.getDouble(7));
                card.setSiparisBeklemede(cursor.getInt(8));
                card.setSiparisGonderildi(cursor.getInt(9));
                card.setPazartesi(cursor.getString(10));
                card.setPazartesiSiraNo(cursor.getString(11));
                card.setSali(cursor.getString(12));
                card.setSaliSiraNo(cursor.getString(13));
                card.setCarsamba(cursor.getString(14));
                card.setCarsambaSiraNo(cursor.getString(15));
                card.setPersembe(cursor.getString(16));
                card.setPersembeSiraNo(cursor.getString(17));
                card.setCuma(cursor.getString(18));
                card.setCumaSiraNo(cursor.getString(19));
                card.setCumartesi(cursor.getString(20));
                card.setCumartesiSiraNo(cursor.getString(21));
                card.setPazar(cursor.getString(22));
                card.setPazarSiraNo(cursor.getString(23));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    public List<ClCard> selectAllClientsMain() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClCard> cardList = new ArrayList<>();
        String sql = "SELECT KayitNo, Kod, Unvani1, "
                + "(SELECT count(Tutar) FROM AY_" + FIRMA_NO + "_Siparis WHERE client.KayitNo = CariKayitNo AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS siparis_miktar, "
                + "(SELECT sum(Tutar) FROM AY_" + FIRMA_NO + "_Siparis WHERE client.KayitNo = CariKayitNo AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS siparis_tutar, "
                + "(SELECT count(Tutar) FROM AY_" + FIRMA_NO + "_KasaIslemleri WHERE client.KayitNo = CariKayitNo AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS kasa_miktar, "
                + "(SELECT sum(Tutar) FROM AY_" + FIRMA_NO + "_KasaIslemleri WHERE client.KayitNo = CariKayitNo AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS kasa_tutar, "
                + "Pazartesi, Sali, Carsamba, Persembe, Cuma, Cumartesi, Pazar "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client "
                + " ORDER BY client.KayitNo";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClCard card = new ClCard();
                card.setKayitNo(cursor.getInt(0));
                card.setKod(cursor.getString(1));
                card.setUnvani1(cursor.getString(2));
                card.setSiparisMiktar(cursor.getInt(3));
                card.setSiparisTutar(cursor.getDouble(4));
                card.setKasaMiktar(cursor.getInt(5));
                card.setKasaTutar(cursor.getDouble(6));
                card.setPazartesi(cursor.getString(7));
                card.setSali(cursor.getString(8));
                card.setCarsamba(cursor.getString(9));
                card.setPersembe(cursor.getString(10));
                card.setCuma(cursor.getString(11));
                card.setCumartesi(cursor.getString(12));
                card.setPazar(cursor.getString(13));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    public ClCard selectClientById(int kayitno) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClCard card = new ClCard();
        String sql = "SELECT KayitNo, Unvani1, FiyatGrubu, KordinatLongitude, KordinatLatitute, Kod, Adres1, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 0 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS beklemedeTutar, "
                + "(SELECT count(Tutar) FROM " + "AY_" + FIRMA_NO + "_Siparis" + " WHERE client.KayitNo = CariKayitNo AND ErpGonderildi = 1 AND (Tarih = strftime('%Y-%m-%d', datetime('now')))) AS gonderilenTutar "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client WHERE KayitNo = " + kayitno;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            card.setKayitNo(cursor.getInt(0));
            card.setUnvani1(cursor.getString(1));
            card.setFiyatGrubu(cursor.getString(2));
            card.setKordinatLongitude(cursor.getDouble(3));
            card.setKordinatLatitute(cursor.getDouble(4));
            card.setKod(cursor.getString(5));
            card.setAdres1(cursor.getString(6));
            card.setSiparisBeklemede(cursor.getInt(7));
            card.setSiparisGonderildi(cursor.getInt(8));
        }
        cursor.close();
        db.close();
        return card;
    }

    public ClCard selectClientInfo(int kayitno) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClCard card = new ClCard();
        String sql = "SELECT KayitNo, Kod, Unvani1, Unvani2, OzelKod1, OzelKod2, OzelKod3, OzelKod4, " +
                "OzelKod5, Adres1, Adres2, Telefon1, Telefon2,  KordinatLatitute, KordinatLongitude, " +
                "IlgiliKisi1, IlgiliKisi2, NetSatis, NetTahsilat, Bakiye, WhatsappId, TelegramId "
                + "FROM " + "AY_" + FIRMA_NO + "_ClCard" + " AS client WHERE KayitNo = " + kayitno;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            card.setKayitNo(cursor.getInt(0));
            card.setKod(cursor.getString(1));
            card.setUnvani1(cursor.getString(2));
            card.setUnvani2(cursor.getString(3));
            card.setOzelKod1(cursor.getString(4));
            card.setOzelKod2(cursor.getString(5));
            card.setOzelKod3(cursor.getString(6));
            card.setOzelKod4(cursor.getString(7));
            card.setOzelKod5(cursor.getString(8));
            card.setAdres1(cursor.getString(9));
            card.setAdres2(cursor.getString(10));
            card.setTelefon1(cursor.getString(11));
            card.setTelefon2(cursor.getString(12));
            card.setKordinatLatitute(cursor.getDouble(13));
            card.setKordinatLongitude(cursor.getDouble(14));
            card.setIlgiliKisi1(cursor.getString(15));
            card.setIlgiliKisi2(cursor.getString(16));
            card.setNetSatis(cursor.getDouble(17));
            card.setNetTahsilat(cursor.getDouble(18));
            card.setBakiye(cursor.getDouble(19));
            card.setWhatsappId(cursor.getString(20));
            card.setTelegramId(cursor.getString(21));
        }
        cursor.close();
        db.close();
        return card;
    }

    public ClCard selectClientByZiyaret(int kayitNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClCard card = new ClCard();
        String sql = "SELECT Kod, Unvani1 FROM AY_" + FIRMA_NO + "_ClCard AS client " +
                "INNER JOIN Ziyaret AS ziyaret ON client.KayitNo = ziyaret.CariKayitNo " +
                "WHERE ziyaret.KayitNo = " + kayitNo;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            card.setKod(cursor.getString(0));
            card.setUnvani1(cursor.getString(1));
        }
        cursor.close();
        db.close();
        return card;
    }

    public ClientZiyaret selectClientByOpenZiyaret(int kayitNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClientZiyaret ziyaret = new ClientZiyaret();
        String sql = "SELECT KayitNo, CariKayitNo, BeginKordinatLongitude, " +
                "BeginKordinatLatitute, BaslangicTarihi, BaslangicSaati, BitisTarihi, " +
                "BitisSaati, Notlar, ErpGonderildi, EndKordinatLongitude, EndKordinatLatitute, Kapatildi " +
                "FROM " + TABLE_ZIYARET + " WHERE KayitNo = " + kayitNo;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            ziyaret.setKayitNo(cursor.getInt(0));
            ziyaret.setCariKayitNo(cursor.getInt(1));
            ziyaret.setBeginKordinatLongitude(cursor.getDouble(2));
            ziyaret.setBeginKordinatLatitute(cursor.getDouble(3));
            ziyaret.setBaslangicTarihi(cursor.getString(4));
            ziyaret.setBaslangicSaati(cursor.getString(5));
            ziyaret.setBitisTarihi(cursor.getString(6));
            ziyaret.setBitisSaati(cursor.getString(7));
            ziyaret.setNotlar(cursor.getString(8));
            ziyaret.setErpGonderildi(cursor.getInt(9));
            ziyaret.setEndKordinatLongitude(cursor.getDouble(10));
            ziyaret.setEndKordinatLatitute(cursor.getDouble(11));
            ziyaret.setKapatildi(cursor.getInt(12));
        }
        cursor.close();
        db.close();
        return ziyaret;
    }

    public List<ClientKasa> selectClientKasa(int cariKayitNo, String date1, String date2) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClientKasa> kasaList = new ArrayList<>();
        String sql = "SELECT kasa.KayitNo, kasa.CariKayitNo, kasa.Tarih, kasa.Tutar, kasa.Aciklama, kasa.MakbuzNo, kasa.KasaKodu, " +
                "kasa.ErpGonderildi, kasa.ErpKayitNo, kasa.ErpFisNo, kasa.IslemTipi, kasa.ZiyaretKayitNo, ziyaret.Kapatildi " +
                "FROM " + "AY_" + FIRMA_NO + "_KasaIslemleri AS kasa LEFT OUTER JOIN Ziyaret AS ziyaret ON kasa.ZiyaretKayitNo = ziyaret.KayitNo " +
                "WHERE kasa.CariKayitNo = " + cariKayitNo + " AND kasa.Tarih >= '" + date1 + "' AND kasa.Tarih <= '" + date2 + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClientKasa kasa = new ClientKasa();
                kasa.setKayitNo(cursor.getInt(0));
                kasa.setCariKayitNo(cursor.getInt(1));
                kasa.setTarih(cursor.getString(2));
                kasa.setTutar(cursor.getDouble(3));
                kasa.setAciklama(cursor.getString(4));
                kasa.setMakbuzNo(cursor.getString(5));
                kasa.setKasaKodu(cursor.getString(6));
                kasa.setErpGonderildi(cursor.getInt(7));
                kasa.setErpKayitNo(cursor.getInt(8));
                kasa.setErpFisNo(cursor.getString(9));
                kasa.setIslemTipi(cursor.getInt(10));
                kasa.setZiyaretKayitNo(cursor.getInt(11));
                kasa.setKapatildi(cursor.getInt(12));
                kasaList.add(kasa);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return kasaList;
    }

    public List<ClientSiparis> selectAllSiparis(int cariKayitno, String date1, String date2) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClientSiparis> orderList = new ArrayList<>();
        String sql = "SELECT siparis.KayitNo, siparis.CariKayitNo, card.Kod, siparis.Tarih, siparis.Aciklama, siparis.SiparisTeslimTarihi, " +
                "siparis.IslemTipi, siparis.OdemeSekli, siparis.ErpGonderildi, siparis.ErpKayitNo, siparis.ErpSiparisFisNo, " +
                "siparis.KordinatLatitute, siparis.KordinatLongitude, siparis.SiparisOncesiFoto1, siparis.SiparisOncesiFoto2, siparis.SiparisOncesiFoto3, " +
                "siparis.Tutar, siparis.GenelIndirimOrani, siparis.GenelIndirimTutari, siparis.SatirIndirimTutari, siparis.GenelIndirimYontemi, " +
                "siparis.NetTutar, siparis.SevkiyatAdresiKayitno, siparis.EklenmeSaati, siparis.DegisiklikSaati " +
                "FROM AY_" + FIRMA_NO + "_Siparis AS siparis INNER JOIN AY_" + FIRMA_NO + "_ClCard AS card ON siparis.CariKayitNo = card.KayitNo " +
                "WHERE siparis.CariKayitNo = " + cariKayitno + " AND siparis.Tarih >= '" + date1 + "' AND siparis.Tarih <= '" + date2 + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClientSiparis order = new ClientSiparis();
                order.setKayitNo(cursor.getLong(0));
                order.setCariKayitNo(cursor.getInt(1));
                order.setCariKod(cursor.getString(2));
                order.setTarih(cursor.getString(3));
                order.setAciklama(cursor.getString(4));
                order.setSiparisTeslimTarihi(cursor.getString(5));
                order.setIslemTipi(cursor.getInt(6));
                order.setOdemeSekli(cursor.getInt(7));
                order.setErpGonderildi(cursor.getInt(8));
                order.setErpKayitNo(cursor.getInt(9));
                order.setErpSiparisFisNo(cursor.getString(10));
                order.setKordinatLatitute(cursor.getDouble(11));
                order.setKordinatLongitude(cursor.getDouble(12));
                order.setSiparisOncesiFoto1(cursor.getString(13));
                order.setSiparisOncesiFoto2(cursor.getString(14));
                order.setSiparisOncesiFoto3(cursor.getString(15));
                order.setTutar(cursor.getDouble(16));
                order.setGenelIndirimOrani(cursor.getDouble(17));
                order.setGenelIndirimTutari(cursor.getDouble(18));
                order.setSatirIndirimTutari(cursor.getDouble(19));
                order.setGenelIndirimYontemi(cursor.getInt(20));
                order.setNetTutar(cursor.getDouble(21));
                order.setSevkiyatAdresiKayitno(cursor.getInt(22));
                order.setEklenmeSaati(cursor.getString(23));
                order.setDegisiklikSaati(cursor.getString(24));
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    public List<ClientSepet> selectAllSepet(long siparisNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClientSepet> sepetList = new ArrayList<>();
        String sql = "SELECT sepet.KayitNo, sepet.SiparisKayitNo, sepet.StokKayitNo, sepet.VaryantKayitNo, sepet.StokKodu, sepet.StokAdi, sepet.StokMiktar, " +
                "sepet.StokFiyat, sepet.StokTutar, sepet.StokBirim, sepet.SatirIndirimOrani, sepet.SatirIndirimTutari, sepet.GenelIndirimTutari, " +
                "sepet.NetTutar, items.ResimDosyasiKucuk, items.ResimDosyasiBuyuk1, items.ResimDosyasiBuyuk2, items.ResimDosyasiBuyuk3 " +
                "FROM AY_" + FIRMA_NO + "_Sepet AS sepet INNER JOIN AY_" + FIRMA_NO + "_Items items ON sepet.StokKayitNo = items.KayitNo " +
                "WHERE SiparisKayitNo = " + siparisNo;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClientSepet sepet = new ClientSepet();
                sepet.setKayitNo(cursor.getInt(0));
                sepet.setSiparisKayitNo(cursor.getLong(1));
                sepet.setStokKayitNo(cursor.getInt(2));
                sepet.setVaryantKayitNo(cursor.getInt(3));
                sepet.setStokKodu(cursor.getString(4));
                sepet.setStokAdi(cursor.getString(5));
                sepet.setStokMiktar(cursor.getInt(6));
                sepet.setStokFiyat(cursor.getDouble(7));
                sepet.setStokTutar(cursor.getDouble(8));
                sepet.setStokBirim(cursor.getString(9));
                sepet.setSatirIndirimOrani(cursor.getDouble(10));
                sepet.setSatirIndirimTutari(cursor.getDouble(11));
                sepet.setGenelIndirimTutari(cursor.getDouble(12));
                sepet.setNetTutar(cursor.getDouble(13));
                sepet.setStokResim1(cursor.getString(14));
                sepet.setStokResim2(cursor.getString(15));
                sepet.setStokResim3(cursor.getString(16));
                sepet.setStokResim4(cursor.getString(17));
                sepetList.add(sepet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sepetList;
    }

    public List<DataExportSiparisTask> selectDataExportSiparis(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<DataExportSiparisTask> list = new ArrayList<>();
        String sql = "SELECT client.KayitNo AS CariKayitno, client.Kod, client.Unvani1, siparis.KayitNo, siparis.Tarih, siparis.IslemTipi, " +
                "siparis.ErpGonderildi, siparis.ErpSiparisFisNo, siparis.ErpKayitNo, siparis.NetTutar, siparis.Aciklama, '1' AS tur,  " +
                "siparis.Tutar, siparis.GenelIndirimTutari " +
                "FROM AY_" + FIRMA_NO + "_ClCard AS client " +
                "INNER JOIN AY_" + FIRMA_NO + "_Siparis AS siparis ON client.KayitNo = siparis.CariKayitNo " +
                "WHERE siparis.Tarih = '" + date + "' " +
                "ORDER BY siparis.Tarih";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                DataExportSiparisTask task = new DataExportSiparisTask();
                task.setCariKayitNo(cursor.getInt(0));
                task.setKod(cursor.getString(1));
                task.setUnvani(cursor.getString(2));
                task.setKayitNo(cursor.getLong(3));
                task.setTarih(cursor.getString(4));
                task.setIslemTipi(cursor.getInt(5));
                task.setErpGonderildi(cursor.getInt(6));
                task.setErpFisNo(cursor.getString(7));
                task.setErpKayitNo(cursor.getInt(8));
                task.setNetTutar(cursor.getDouble(9));
                task.setAciklama(cursor.getString(10));
                task.setTur(cursor.getInt(11));
                task.setTutar(cursor.getDouble(12));
                task.setGenelIndirimTutari(cursor.getDouble(13));
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<DataExportKasaTask> selectDataExportKasa(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<DataExportKasaTask> list = new ArrayList<>();
        String sql = "SELECT client.KayitNo AS CariKayitno, client.Kod, client.Unvani1, kasa.KayitNo, kasa.Tarih, kasa.IslemTipi, " +
                "kasa.ErpGonderildi, kasa.ErpFisNo, kasa.ErpKayitNo, kasa.Tutar, kasa.Aciklama, '2' AS tur, kasa.KasaKodu, kasa.EklenmeSaati, kasa.DegisiklikSaati " +
                "FROM  AY_" + FIRMA_NO + "_ClCard AS client " +
                "INNER JOIN AY_" + FIRMA_NO + "_KasaIslemleri AS kasa ON client.KayitNo = kasa.CariKayitNo " +
                "WHERE kasa.Tarih = '" + date + "' " +
                "ORDER BY kasa.Tarih";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                DataExportKasaTask task = new DataExportKasaTask();
                task.setCariKayitNo(cursor.getInt(0));
                task.setKod(cursor.getString(1));
                task.setUnvani(cursor.getString(2));
                task.setKayitNo(cursor.getLong(3));
                task.setTarih(cursor.getString(4));
                task.setIslemTipi(cursor.getInt(5));
                task.setErpGonderildi(cursor.getInt(6));
                task.setErpFisNo(cursor.getString(7));
                task.setErpKayitNo(cursor.getInt(8));
                task.setTutar(cursor.getDouble(9));
                task.setAciklama(cursor.getString(10));
                task.setTur(cursor.getInt(11));
                task.setKasaKodu(cursor.getString(12));
                task.setEklenmeSaati(cursor.getString(13));
                task.setDegisiklikSaati(cursor.getString(14));
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public CihazlarFirma selectCihazlarFirma(String firmano) {
        SQLiteDatabase db = this.getReadableDatabase();
        CihazlarFirma firma = null;
        String sql = "SELECT * FROM " + TABLE_CIHAZLAR_FIRMA + " WHERE FirmaNo = " + firmano;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            firma = new CihazlarFirma();
            firma.setKayitNo(cursor.getInt(0));
            firma.setCihazKayitNo(cursor.getInt(1));
            firma.setMenuGrupKayitNo(cursor.getInt(2));
            firma.setFirmaNo(cursor.getString(3));
            firma.setDonemNo(cursor.getString(4));
            firma.setSatisElemaniKodu(cursor.getString(5));
            firma.setTicariIslemGrubu(cursor.getString(6));
            firma.setIsyeri(cursor.getInt(7));
            firma.setDepo(cursor.getInt(8));
            firma.setOndegerFiyatGrubu1(cursor.getString(9));
            firma.setOndegerFiyatGrubu2(cursor.getString(10));
            firma.setOndegerKasaKodu(cursor.getString(11));
            firma.setOndegerAciklamaAlani(cursor.getString(12));
            firma.setDepoListesi1(cursor.getString(13));
            firma.setDepoListesi2(cursor.getString(14));
            firma.setCariFiltre(cursor.getString(15));
            firma.setCariSevkiyatAdresiFiltre(cursor.getString(16));
            firma.setStokFiltre(cursor.getString(17));
            firma.setFiyatFiltre(cursor.getString(18));
            firma.setResimAdresi(cursor.getString(19));
            firma.setKullanim(cursor.getString(20));
            firma.setDepo1Aciklama1(cursor.getString(21));
            firma.setDepo1Aciklama2(cursor.getString(22));
            firma.setDepo2Aciklama1(cursor.getString(23));
            firma.setDepo2Aciklama2(cursor.getString(24));
        }
        cursor.close();
        db.close();
        return firma;
    }

    public List<CihazlarMenu> selectCihazlarMenu(int tip, int menuGrupKayitNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CihazlarMenu> menuList = new ArrayList<>();
        String sql = "SELECT KayitNo, MenuGrupKayitNo, Tip, " +
                "Aciklama1, Aciklama2, Filtre, Siralama, " +
                "Ondeger, Kullanim, SiraNo, UstMenuKayitNo, MenuTipi " +
                "FROM " + TABLE_CIHAZLAR_MENU + " WHERE tip = "
                + tip + " AND MenuGrupKayitNo = " + menuGrupKayitNo + " ORDER BY SiraNo";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                CihazlarMenu menu = new CihazlarMenu();
                menu.setKayitNo(cursor.getInt(0));
                menu.setMenuGrupKayitNo(cursor.getInt(1));
                menu.setTip(cursor.getInt(2));
                menu.setAciklama1(cursor.getString(3));
                menu.setAciklama2(cursor.getString(4));
                menu.setFiltre(cursor.getString(5));
                menu.setSiralama(cursor.getString(6));
                menu.setOndeger(cursor.getInt(7));
                menu.setKullanim(cursor.getString(8));
                menu.setSiraNo(cursor.getString(9));
                menu.setUstMenuKayitNo(cursor.getInt(10));
                menu.setMenuTipi(cursor.getInt(11));
                menuList.add(menu);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return menuList;
    }

    public List<ClientZiyaret> selectAllZiyaret(int CariKayitNo, String date1, String date2) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ClientZiyaret> ziyaretList = new ArrayList<>();
        String sql = "SELECT KayitNo, CariKayitNo, BeginKordinatLongitude, " +
                "BeginKordinatLatitute, BaslangicTarihi, BaslangicSaati, BitisTarihi, " +
                "BitisSaati, Notlar, ErpGonderildi, EndKordinatLongitude, EndKordinatLatitute, Kapatildi " +
                "FROM " + TABLE_ZIYARET + " WHERE CariKayitNo = " + CariKayitNo +
                " AND BaslangicTarihi >= '" + date1 + "' AND BaslangicTarihi <= '" + date2 + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ClientZiyaret ziyaret = new ClientZiyaret();
                ziyaret.setKayitNo(cursor.getInt(0));
                ziyaret.setCariKayitNo(cursor.getInt(1));
                ziyaret.setBeginKordinatLongitude(cursor.getDouble(2));
                ziyaret.setBeginKordinatLatitute(cursor.getDouble(3));
                ziyaret.setBaslangicTarihi(cursor.getString(4));
                ziyaret.setBaslangicSaati(cursor.getString(5));
                ziyaret.setBitisTarihi(cursor.getString(6));
                ziyaret.setBitisSaati(cursor.getString(7));
                ziyaret.setNotlar(cursor.getString(8));
                ziyaret.setErpGonderildi(cursor.getInt(9));
                ziyaret.setEndKordinatLongitude(cursor.getDouble(10));
                ziyaret.setEndKordinatLatitute(cursor.getDouble(11));
                ziyaret.setKapatildi(cursor.getInt(12));
                ziyaretList.add(ziyaret);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ziyaretList;
    }

    public ClientZiyaret selectZiyaretByCode(int kayitNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClientZiyaret visit = new ClientZiyaret();
        String sql = "SELECT KayitNo, CariKayitNo, BeginKordinatLongitude, " +
                "BeginKordinatLatitute, BaslangicTarihi, BaslangicSaati, BitisTarihi, " +
                "BitisSaati, Notlar, Kapatildi " +
                "FROM " + TABLE_ZIYARET + " WHERE KayitNo = " + kayitNo;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            visit.setKayitNo(cursor.getInt(0));
            visit.setCariKayitNo(cursor.getInt(1));
            visit.setBeginKordinatLongitude(cursor.getDouble(2));
            visit.setBeginKordinatLatitute(cursor.getDouble(3));
            visit.setBaslangicTarihi(cursor.getString(4));
            visit.setBaslangicSaati(cursor.getString(5));
            visit.setBitisTarihi(cursor.getString(6));
            visit.setBitisSaati(cursor.getString(7));
            visit.setNotlar(cursor.getString(8));
            visit.setKapatildi(cursor.getInt(9));
        }
        cursor.close();
        db.close();
        return visit;
    }

    public int selectCihazlarUrunSayisi(String filtre) {
        SQLiteDatabase db = this.getReadableDatabase();
        int sayi = 0;
        String sql = "SELECT count(KayitNo) " +
                "FROM AY_" + FIRMA_NO + "_Items " +
                "WHERE StokKodu <> '' " + filtre;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            sayi = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return sayi;
    }

    public int selectCihazlarCariSayisi(String filtre) {
        SQLiteDatabase db = this.getReadableDatabase();
        int sayi = 0;
        String sql = "SELECT count(Kod) " +
                "FROM AY_" + FIRMA_NO + "_ClCard " +
                "WHERE Kod <> '' " + filtre;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            sayi = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return sayi;
    }

    public List<CihazlarFirmaParametreler> selectParametreGetir(String firmaNo, String paramAdi) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CihazlarFirmaParametreler> params = new ArrayList<>();
        String sql = "SELECT params.KayitNo, params.CihazlarFirmaKayitNo, params.ParametreTipi, params.ParametreAdi, " +
                "params.ParametreDegeri, params.Aciklama, params.MobilCihazdaDegistirebilir, params.Grup " +
                "FROM " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER + " AS params " +
                "INNER JOIN " + TABLE_CIHAZLAR_FIRMA + " AS firma " +
                "ON params.CihazlarFirmaKayitNo = firma.KayitNo " +
                "WHERE firma.FirmaNo = " + firmaNo + " AND params.ParametreAdi = '" + paramAdi + "' ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                CihazlarFirmaParametreler parametreler = new CihazlarFirmaParametreler();
                parametreler.setKayitNo(cursor.getInt(0));
                parametreler.setCihazlarFirmaKayitNo(cursor.getInt(1));
                parametreler.setParametreTipi(cursor.getString(2));
                parametreler.setParametreAdi(cursor.getString(3));
                parametreler.setParametreDegeri(cursor.getString(4));
                parametreler.setAciklama(cursor.getString(5));
                parametreler.setMobilCihazdaDegistirebilir(cursor.getInt(6));
                parametreler.setGrup(cursor.getString(7));
                params.add(parametreler);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return params;
    }

    public List<CihazlarFirmaParametreler> selectParametreList(String firmaNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CihazlarFirmaParametreler> params = new ArrayList<>();
        String sql = "SELECT params.KayitNo, params.CihazlarFirmaKayitNo, params.ParametreTipi, params.ParametreAdi, " +
                "params.ParametreDegeri, params.Aciklama, params.MobilCihazdaDegistirebilir, params.Grup " +
                "FROM " + TABLE_CIHAZLAR_FIRMA_PARAMETRELER + " AS params " +
                "INNER JOIN " + TABLE_CIHAZLAR_FIRMA + " AS firma " +
                "ON params.CihazlarFirmaKayitNo = firma.KayitNo " +
                "WHERE firma.FirmaNo = " + firmaNo;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                CihazlarFirmaParametreler parametreler = new CihazlarFirmaParametreler();
                parametreler.setKayitNo(cursor.getInt(0));
                parametreler.setCihazlarFirmaKayitNo(cursor.getInt(1));
                parametreler.setParametreTipi(cursor.getString(2));
                parametreler.setParametreAdi(cursor.getString(3));
                parametreler.setParametreDegeri(cursor.getString(4));
                parametreler.setAciklama(cursor.getString(5));
                parametreler.setMobilCihazdaDegistirebilir(cursor.getInt(6));
                parametreler.setGrup(cursor.getString(7));
                params.add(parametreler);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return params;
    }

    public List<ItemsPrclist> selectPrclist() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsPrclist> prclists = new ArrayList<>();
        String sql = "SELECT KayitNo, StokKayitNo, BirimKayitNo, FiyatGrubu, CariHesapKodu, Fiyat, BaslangicTarih, BitisTarih, DovizTipiKayitNo " +
                "FROM " + "AY_" + FIRMA_NO + "_ItemsPrclist" + " GROUP BY FiyatGrubu";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsPrclist prclist = new ItemsPrclist();
                prclist.setKayitNo(cursor.getInt(0));
                prclist.setStokKayitNo(cursor.getInt(1));
                prclist.setBirimKayitNo(cursor.getInt(2));
                prclist.setFiyatGrubu(cursor.getString(3));
                prclist.setCariHesapKodu(cursor.getString(4));
                prclist.setFiyat(cursor.getDouble(5));
                prclist.setBaslangicTarih(cursor.getString(6));
                prclist.setBitisTarih(cursor.getString(7));
                prclist.setDovizTipiKayitNo(cursor.getInt(8));
                prclists.add(prclist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return prclists;
    }

    public List<CihazlarFirma> selectCihazlarFirma() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CihazlarFirma> firmaList = new ArrayList<>();
        String sql = "SELECT KayitNo, CihazKayitNo, MenuGrupKayitNo, FirmaNo, DonemNo, " +
                "SatisElemaniKodu, TicariIslemGrubu, Isyeri, Depo, OndegerFiyatGrubu1, OndegerFiyatGrubu2, " +
                "OndegerKasaKodu, OndegerAciklamaAlani, DepoListesi1, DepoListesi2, CariFiltre, " +
                "CariSevkiyatAdresiFiltre, StokFiltre, FiyatFiltre, ResimAdresi, Depo1Aciklama1, " +
                "Depo1Aciklama2, Depo2Aciklama1, Depo2Aciklama2, StokEkraniGorunumSekli, " +
                "Kullanim, GecmisFirmaNo, GecmisDonemNo, " +
                "FirmaAdi1, FirmaAdi2, FirmaAdi3 " +
                "FROM " + TABLE_CIHAZLAR_FIRMA;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                CihazlarFirma firma = new CihazlarFirma();
                firma.setKayitNo(cursor.getInt(0));
                firma.setCihazKayitNo(cursor.getInt(1));
                firma.setMenuGrupKayitNo(cursor.getInt(2));
                firma.setFirmaNo(cursor.getString(3));
                firma.setDonemNo(cursor.getString(4));
                firma.setSatisElemaniKodu(cursor.getString(5));
                firma.setTicariIslemGrubu(cursor.getString(6));
                firma.setIsyeri(cursor.getInt(7));
                firma.setDepo(cursor.getInt(8));
                firma.setOndegerFiyatGrubu1(cursor.getString(9));
                firma.setOndegerFiyatGrubu2(cursor.getString(10));
                firma.setOndegerKasaKodu(cursor.getString(11));
                firma.setOndegerAciklamaAlani(cursor.getString(12));
                firma.setDepoListesi1(cursor.getString(13));
                firma.setDepoListesi2(cursor.getString(14));
                firma.setCariFiltre(cursor.getString(15));
                firma.setCariSevkiyatAdresiFiltre(cursor.getString(16));
                firma.setStokFiltre(cursor.getString(17));
                firma.setFiyatFiltre(cursor.getString(18));
                firma.setResimAdresi(cursor.getString(19));
                firma.setDepo1Aciklama1(cursor.getString(20));
                firma.setDepo1Aciklama2(cursor.getString(21));
                firma.setDepo2Aciklama1(cursor.getString(22));
                firma.setDepo2Aciklama2(cursor.getString(23));
                firma.setStokEkraniGorunumSekli(cursor.getString(24));
                firma.setKullanim(cursor.getString(25));
                firma.setGecmisFirmaNo(cursor.getString(26));
                firma.setGecmisDonemNo(cursor.getString(27));
                firma.setFirmaAdi1(cursor.getString(28));
                firma.setFirmaAdi2(cursor.getString(29));
                firma.setFirmaAdi3(cursor.getString(30));
                firmaList.add(firma);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return firmaList;
    }

    public List<ItemsDepolarAdresler> selectDepolarAdresler(int depono) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ItemsDepolarAdresler> adreslerList = new ArrayList<>();
        String sql = "SELECT LokasyonKayitNo, DepoNo, LokasyonKodu, LokasyonAdi " +
                "FROM AY_" + FIRMA_NO + "_DepolarAdresler WHERE DepoNo = " + depono;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                ItemsDepolarAdresler adresler = new ItemsDepolarAdresler();
                adresler.setLokasyonKayitNo(cursor.getInt(0));
                adresler.setDepoNo(cursor.getInt(1));
                adresler.setLokasyonKodu(cursor.getString(2));
                adresler.setLokasyonAdi(cursor.getString(3));
                adreslerList.add(adresler);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return adreslerList;
    }

    public String selectItemCode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        String sql = "SELECT itm.StokAdi1 " +
                "FROM " + "AY_" + FIRMA_NO + "_ItemsUnitBarcode" + " as Brkd " +
                "LEFT OUTER JOIN " + "AY_" + FIRMA_NO + "_Items" + " AS itm ON itm.KayitNo = brkd.StokKayitNo " +
                "WHERE brkd.Barkod = '" + barcode + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean tableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (tableName == null) {
            return false;
        }
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
                new String[]{"table", tableName}
        );
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count > 0;
    }

    public void dropAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();

        for (String tableName : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        db.close();
    }

    public void deleteParametreTables() {
        deleteCihazlarFirma();
        deleteCihazlarMenu();
        deleteCihazlarFirmaParametreler();
        deleteCihazlarFirmaOdemeSekli();
        deleteCihazlarFirmaDepolar();
        deleteDoviz();
    }

    public void createTables(String firmaNo) {
        if (!tableExists("AY_" + firmaNo + "_ClCard")) {
            createClientsTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_ShipInfo")) {
            createShipInfoTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_Items")) {
            createItemsTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_ItemsItmunita")) {
            createItemsItmunitaTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_ItemsPrclist")) {
            createItemsPrclistTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_ItemsToplamlar")) {
            createItemsToplamTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_ItemsUnitBarcode")) {
            createItemsBarkodTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_KasaIslemleri")) {
            createKasaIslemleriTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_Siparis")) {
            createSiparisTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_Sepet")) {
            createSepetTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_Depolar")) {
            createDepolarTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_DepolarAdresler")) {
            createDepolarAdreslerTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_DepoStokYerleri")) {
            createDepoStokYerleriTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_Sayim")) {
            createSayimTable(firmaNo);
        }
        if (!tableExists("AY_" + firmaNo + "_SayimSatirlar")) {
            createSayimSatirlariTable(firmaNo);
        }
    }

    public void createParametreTables() {
        createCihazlarFirmaTable();
        createCihazlarMenuTable();
        createCihazlarFirmaParametrelerTable();
        createCihazlarFirmaOdemeSekliTable();
        createCihazlarFirmaDepolarTable();
        createDovizTable();
        createZiyaretTable();
    }

    public boolean isFieldExists(String tableName, String fieldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null)) {
            return cursor != null && cursor.getColumnIndex(fieldName) != -1;
        } catch (Exception e) {
            return false;
        }
    }

}


