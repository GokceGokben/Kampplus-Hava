package com.kampplus.hava.model

import com.google.gson.annotations.SerializedName

/**
 * Şehir bilgilerini tutan veri modeli.
 */
data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    val plaka: String? = null,
    // Open-Meteo yer tipi; il merkezleri "PPLA" olarak gelir
    @SerializedName("feature_code")
    val featureCode: String? = null,
)

/**
 * Türkiye şehirlerinin varsayılan plaka kodları eşlemesi.
 */
val TURKEY_PLATE_CODES = mapOf(
    "Adana" to "01",
    "Adıyaman" to "02",
    "Afyonkarahisar" to "03",
    "Ağrı" to "04",
    "Amasya" to "05",
    "Ankara" to "06",
    "Antalya" to "07",
    "Artvin" to "08",
    "Aydın" to "09",
    "Balıkesir" to "10",
    "Bilecik" to "11",
    "Bingöl" to "12",
    "Bitlis" to "13",
    "Bolu" to "14",
    "Burdur" to "15",
    "Bursa" to "16",
    "Çanakkale" to "17",
    "Çankırı" to "18",
    "Çorum" to "19",
    "Denizli" to "20",
    "Diyarbakır" to "21",
    "Edirne" to "22",
    "Elazığ" to "23",
    "Erzincan" to "24",
    "Erzurum" to "25",
    "Eskişehir" to "26",
    "Gaziantep" to "27",
    "Giresun" to "28",
    "Gümüşhane" to "29",
    "Hakkari" to "30",
    "Hatay" to "31",
    "Isparta" to "32",
    "Mersin" to "33",
    "İstanbul" to "34",
    "İzmir" to "35",
    "Kars" to "36",
    "Kastamonu" to "37",
    "Kayseri" to "38",
    "Kırklareli" to "39",
    "Kırşehir" to "40",
    "Kocaeli" to "41",
    "Konya" to "42",
    "Kütahya" to "43",
    "Malatya" to "44",
    "Manisa" to "45",
    "Kahramanmaraş" to "46",
    "Mardin" to "47",
    "Muğla" to "48",
    "Muş" to "49",
    "Nevşehir" to "50",
    "Niğde" to "51",
    "Ordu" to "52",
    "Rize" to "53",
    "Sakarya" to "54",
    "Samsun" to "55",
    "Siirt" to "56",
    "Sinop" to "57",
    "Sivas" to "58",
    "Tekirdağ" to "59",
    "Tokat" to "60",
    "Trabzon" to "61",
    "Tunceli" to "62",
    "Şanlıurfa" to "63",
    "Uşak" to "64",
    "Van" to "65",
    "Yozgat" to "66",
    "Zonguldak" to "67",
    "Aksaray" to "68",
    "Bayburt" to "69",
    "Karaman" to "70",
    "Kırıkkale" to "71",
    "Batman" to "72",
    "Şırnak" to "73",
    "Bartın" to "74",
    "Ardahan" to "75",
    "Iğdır" to "76",
    "Yalova" to "77",
    "Karabük" to "78",
    "Kilis" to "79",
    "Osmaniye" to "80",
    "Düzce" to "81",
)

/**
 * Şehrin plaka rozeti için plaka kodunu döner.
 */
val City.plakaCode: String
    get() = plaka ?: TURKEY_PLATE_CODES[name] ?: TURKEY_PLATE_CODES[admin1] ?: "--"

/**
 * Türkçe büyük/küçük ünlü uyumuna göre yönelme hâl eki (örn. Ankara'ya, İstanbul'a, İzmir'e).
 */
val City.dativeName: String
    get() {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return ""
        val vowelsBack = listOf('a', 'ı', 'o', 'u', 'A', 'I', 'O', 'U', 'â', 'û', 'ô')
        val vowelsFront = listOf('e', 'i', 'ö', 'ü', 'E', 'İ', 'Ö', 'Ü', 'î')
        var lastVowel: Char? = null
        for (char in trimmed.reversed()) {
            if (char in vowelsBack || char in vowelsFront) {
                lastVowel = char
                break
            }
        }
        val isBackVowel = lastVowel in vowelsBack
        val lastChar = trimmed.last()
        val isLastCharVowel = lastChar in vowelsBack || lastChar in vowelsFront
        val suffix = if (isLastCharVowel) {
            if (isBackVowel) "ya" else "ye"
        } else {
            if (isBackVowel) "a" else "e"
        }
        return "$trimmed'$suffix"
    }

/**
 * Türkçe ünlü uyumuna göre tam öneri cümlesi (örn. Ankara'ya mı bakmak istemiştiniz?).
 */
val City.suggestionSentence: String
    get() {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return ""
        val vowelsBack = listOf('a', 'ı', 'o', 'u', 'A', 'I', 'O', 'U', 'â', 'û', 'ô')
        val vowelsFront = listOf('e', 'i', 'ö', 'ü', 'E', 'İ', 'Ö', 'Ü', 'î')
        var lastVowel: Char? = null
        for (char in trimmed.reversed()) {
            if (char in vowelsBack || char in vowelsFront) {
                lastVowel = char
                break
            }
        }
        val isBackVowel = lastVowel in vowelsBack
        val mi = if (isBackVowel) "mı" else "mi"
        return "${dativeName} $mi bakmak istemiştiniz?"
    }

/**
 * Türkiye'nin 81 ili (il merkezi koordinatlarıyla). Arama bu liste üzerinden yapılır.
 */
val turkeyProvinces = listOf(
    City(id = 1, name = "Adana", latitude = 37.0000, longitude = 35.3213, country = "Türkiye", admin1 = "Adana", plaka = "01"),
    City(id = 2, name = "Adıyaman", latitude = 37.7648, longitude = 38.2786, country = "Türkiye", admin1 = "Adıyaman", plaka = "02"),
    City(id = 3, name = "Afyonkarahisar", latitude = 38.7507, longitude = 30.5567, country = "Türkiye", admin1 = "Afyonkarahisar", plaka = "03"),
    City(id = 4, name = "Ağrı", latitude = 39.7191, longitude = 43.0503, country = "Türkiye", admin1 = "Ağrı", plaka = "04"),
    City(id = 5, name = "Amasya", latitude = 40.6499, longitude = 35.8353, country = "Türkiye", admin1 = "Amasya", plaka = "05"),
    City(id = 6, name = "Ankara", latitude = 39.91987, longitude = 32.85427, country = "Türkiye", admin1 = "Ankara", plaka = "06"),
    City(id = 7, name = "Antalya", latitude = 36.8841, longitude = 30.7056, country = "Türkiye", admin1 = "Antalya", plaka = "07"),
    City(id = 8, name = "Artvin", latitude = 41.1828, longitude = 41.8183, country = "Türkiye", admin1 = "Artvin", plaka = "08"),
    City(id = 9, name = "Aydın", latitude = 37.8560, longitude = 27.8416, country = "Türkiye", admin1 = "Aydın", plaka = "09"),
    City(id = 10, name = "Balıkesir", latitude = 39.6484, longitude = 27.8826, country = "Türkiye", admin1 = "Balıkesir", plaka = "10"),
    City(id = 11, name = "Bilecik", latitude = 40.1451, longitude = 29.9799, country = "Türkiye", admin1 = "Bilecik", plaka = "11"),
    City(id = 12, name = "Bingöl", latitude = 38.8847, longitude = 40.4939, country = "Türkiye", admin1 = "Bingöl", plaka = "12"),
    City(id = 13, name = "Bitlis", latitude = 38.4006, longitude = 42.1095, country = "Türkiye", admin1 = "Bitlis", plaka = "13"),
    City(id = 14, name = "Bolu", latitude = 40.7350, longitude = 31.6061, country = "Türkiye", admin1 = "Bolu", plaka = "14"),
    City(id = 15, name = "Burdur", latitude = 37.7203, longitude = 30.2908, country = "Türkiye", admin1 = "Burdur", plaka = "15"),
    City(id = 16, name = "Bursa", latitude = 40.1885, longitude = 29.0610, country = "Türkiye", admin1 = "Bursa", plaka = "16"),
    City(id = 17, name = "Çanakkale", latitude = 40.1553, longitude = 26.4142, country = "Türkiye", admin1 = "Çanakkale", plaka = "17"),
    City(id = 18, name = "Çankırı", latitude = 40.6013, longitude = 33.6134, country = "Türkiye", admin1 = "Çankırı", plaka = "18"),
    City(id = 19, name = "Çorum", latitude = 40.5489, longitude = 34.9533, country = "Türkiye", admin1 = "Çorum", plaka = "19"),
    City(id = 20, name = "Denizli", latitude = 37.7765, longitude = 29.0864, country = "Türkiye", admin1 = "Denizli", plaka = "20"),
    City(id = 21, name = "Diyarbakır", latitude = 37.9144, longitude = 40.2306, country = "Türkiye", admin1 = "Diyarbakır", plaka = "21"),
    City(id = 22, name = "Edirne", latitude = 41.6818, longitude = 26.5623, country = "Türkiye", admin1 = "Edirne", plaka = "22"),
    City(id = 23, name = "Elazığ", latitude = 38.6810, longitude = 39.2264, country = "Türkiye", admin1 = "Elazığ", plaka = "23"),
    City(id = 24, name = "Erzincan", latitude = 39.7500, longitude = 39.5000, country = "Türkiye", admin1 = "Erzincan", plaka = "24"),
    City(id = 25, name = "Erzurum", latitude = 39.9043, longitude = 41.2679, country = "Türkiye", admin1 = "Erzurum", plaka = "25"),
    City(id = 26, name = "Eskişehir", latitude = 39.7767, longitude = 30.5206, country = "Türkiye", admin1 = "Eskişehir", plaka = "26"),
    City(id = 27, name = "Gaziantep", latitude = 37.0662, longitude = 37.3833, country = "Türkiye", admin1 = "Gaziantep", plaka = "27"),
    City(id = 28, name = "Giresun", latitude = 40.9128, longitude = 38.3895, country = "Türkiye", admin1 = "Giresun", plaka = "28"),
    City(id = 29, name = "Gümüşhane", latitude = 40.4386, longitude = 39.5086, country = "Türkiye", admin1 = "Gümüşhane", plaka = "29"),
    City(id = 30, name = "Hakkari", latitude = 37.5744, longitude = 43.7408, country = "Türkiye", admin1 = "Hakkari", plaka = "30"),
    City(id = 31, name = "Hatay", latitude = 36.2021, longitude = 36.1600, country = "Türkiye", admin1 = "Hatay", plaka = "31"),
    City(id = 32, name = "Isparta", latitude = 37.7648, longitude = 30.5566, country = "Türkiye", admin1 = "Isparta", plaka = "32"),
    City(id = 33, name = "Mersin", latitude = 36.8000, longitude = 34.6333, country = "Türkiye", admin1 = "Mersin", plaka = "33"),
    City(id = 34, name = "İstanbul", latitude = 41.0082, longitude = 28.9784, country = "Türkiye", admin1 = "İstanbul", plaka = "34"),
    City(id = 35, name = "İzmir", latitude = 38.4192, longitude = 27.1287, country = "Türkiye", admin1 = "İzmir", plaka = "35"),
    City(id = 36, name = "Kars", latitude = 40.6013, longitude = 43.0975, country = "Türkiye", admin1 = "Kars", plaka = "36"),
    City(id = 37, name = "Kastamonu", latitude = 41.3887, longitude = 33.7827, country = "Türkiye", admin1 = "Kastamonu", plaka = "37"),
    City(id = 38, name = "Kayseri", latitude = 38.7312, longitude = 35.4787, country = "Türkiye", admin1 = "Kayseri", plaka = "38"),
    City(id = 39, name = "Kırklareli", latitude = 41.7333, longitude = 27.2167, country = "Türkiye", admin1 = "Kırklareli", plaka = "39"),
    City(id = 40, name = "Kırşehir", latitude = 39.1425, longitude = 34.1709, country = "Türkiye", admin1 = "Kırşehir", plaka = "40"),
    City(id = 41, name = "Kocaeli", latitude = 40.7654, longitude = 29.9408, country = "Türkiye", admin1 = "Kocaeli", plaka = "41"),
    City(id = 42, name = "Konya", latitude = 37.8713, longitude = 32.4846, country = "Türkiye", admin1 = "Konya", plaka = "42"),
    City(id = 43, name = "Kütahya", latitude = 39.4167, longitude = 29.9833, country = "Türkiye", admin1 = "Kütahya", plaka = "43"),
    City(id = 44, name = "Malatya", latitude = 38.3552, longitude = 38.3095, country = "Türkiye", admin1 = "Malatya", plaka = "44"),
    City(id = 45, name = "Manisa", latitude = 38.6191, longitude = 27.4289, country = "Türkiye", admin1 = "Manisa", plaka = "45"),
    City(id = 46, name = "Kahramanmaraş", latitude = 37.5858, longitude = 36.9371, country = "Türkiye", admin1 = "Kahramanmaraş", plaka = "46"),
    City(id = 47, name = "Mardin", latitude = 37.3212, longitude = 40.7245, country = "Türkiye", admin1 = "Mardin", plaka = "47"),
    City(id = 48, name = "Muğla", latitude = 37.2153, longitude = 28.3636, country = "Türkiye", admin1 = "Muğla", plaka = "48"),
    City(id = 49, name = "Muş", latitude = 38.9462, longitude = 41.7539, country = "Türkiye", admin1 = "Muş", plaka = "49"),
    City(id = 50, name = "Nevşehir", latitude = 38.6939, longitude = 34.6857, country = "Türkiye", admin1 = "Nevşehir", plaka = "50"),
    City(id = 51, name = "Niğde", latitude = 37.9667, longitude = 34.6833, country = "Türkiye", admin1 = "Niğde", plaka = "51"),
    City(id = 52, name = "Ordu", latitude = 40.9839, longitude = 37.8764, country = "Türkiye", admin1 = "Ordu", plaka = "52"),
    City(id = 53, name = "Rize", latitude = 41.0201, longitude = 40.5234, country = "Türkiye", admin1 = "Rize", plaka = "53"),
    City(id = 54, name = "Sakarya", latitude = 40.7569, longitude = 30.3781, country = "Türkiye", admin1 = "Sakarya", plaka = "54"),
    City(id = 55, name = "Samsun", latitude = 41.2928, longitude = 36.3313, country = "Türkiye", admin1 = "Samsun", plaka = "55"),
    City(id = 56, name = "Siirt", latitude = 37.9333, longitude = 41.9500, country = "Türkiye", admin1 = "Siirt", plaka = "56"),
    City(id = 57, name = "Sinop", latitude = 42.0231, longitude = 35.1531, country = "Türkiye", admin1 = "Sinop", plaka = "57"),
    City(id = 58, name = "Sivas", latitude = 39.7477, longitude = 37.0179, country = "Türkiye", admin1 = "Sivas", plaka = "58"),
    City(id = 59, name = "Tekirdağ", latitude = 40.9833, longitude = 27.5167, country = "Türkiye", admin1 = "Tekirdağ", plaka = "59"),
    City(id = 60, name = "Tokat", latitude = 40.3167, longitude = 36.5500, country = "Türkiye", admin1 = "Tokat", plaka = "60"),
    City(id = 61, name = "Trabzon", latitude = 41.0015, longitude = 39.7178, country = "Türkiye", admin1 = "Trabzon", plaka = "61"),
    City(id = 62, name = "Tunceli", latitude = 39.1079, longitude = 39.5401, country = "Türkiye", admin1 = "Tunceli", plaka = "62"),
    City(id = 63, name = "Şanlıurfa", latitude = 37.1674, longitude = 38.7955, country = "Türkiye", admin1 = "Şanlıurfa", plaka = "63"),
    City(id = 64, name = "Uşak", latitude = 38.6823, longitude = 29.4082, country = "Türkiye", admin1 = "Uşak", plaka = "64"),
    City(id = 65, name = "Van", latitude = 38.4891, longitude = 43.4089, country = "Türkiye", admin1 = "Van", plaka = "65"),
    City(id = 66, name = "Yozgat", latitude = 39.8181, longitude = 34.8147, country = "Türkiye", admin1 = "Yozgat", plaka = "66"),
    City(id = 67, name = "Zonguldak", latitude = 41.4564, longitude = 31.7987, country = "Türkiye", admin1 = "Zonguldak", plaka = "67"),
    City(id = 68, name = "Aksaray", latitude = 38.3687, longitude = 34.0370, country = "Türkiye", admin1 = "Aksaray", plaka = "68"),
    City(id = 69, name = "Bayburt", latitude = 40.2552, longitude = 40.2249, country = "Türkiye", admin1 = "Bayburt", plaka = "69"),
    City(id = 70, name = "Karaman", latitude = 37.1759, longitude = 33.2287, country = "Türkiye", admin1 = "Karaman", plaka = "70"),
    City(id = 71, name = "Kırıkkale", latitude = 39.8468, longitude = 33.5153, country = "Türkiye", admin1 = "Kırıkkale", plaka = "71"),
    City(id = 72, name = "Batman", latitude = 37.8812, longitude = 41.1351, country = "Türkiye", admin1 = "Batman", plaka = "72"),
    City(id = 73, name = "Şırnak", latitude = 37.5188, longitude = 42.4537, country = "Türkiye", admin1 = "Şırnak", plaka = "73"),
    City(id = 74, name = "Bartın", latitude = 41.6344, longitude = 32.3375, country = "Türkiye", admin1 = "Bartın", plaka = "74"),
    City(id = 75, name = "Ardahan", latitude = 41.1105, longitude = 42.7022, country = "Türkiye", admin1 = "Ardahan", plaka = "75"),
    City(id = 76, name = "Iğdır", latitude = 39.9237, longitude = 44.0450, country = "Türkiye", admin1 = "Iğdır", plaka = "76"),
    City(id = 77, name = "Yalova", latitude = 40.6500, longitude = 29.2667, country = "Türkiye", admin1 = "Yalova", plaka = "77"),
    City(id = 78, name = "Karabük", latitude = 41.2061, longitude = 32.6204, country = "Türkiye", admin1 = "Karabük", plaka = "78"),
    City(id = 79, name = "Kilis", latitude = 36.7184, longitude = 37.1212, country = "Türkiye", admin1 = "Kilis", plaka = "79"),
    City(id = 80, name = "Osmaniye", latitude = 37.0742, longitude = 36.2478, country = "Türkiye", admin1 = "Osmaniye", plaka = "80"),
    City(id = 81, name = "Düzce", latitude = 40.8438, longitude = 31.1565, country = "Türkiye", admin1 = "Düzce", plaka = "81"),
)

/**
 * Uygulama için sabit ve yedek (offline) şehir listesi.
 */
val sampleCities = listOf(
    City(id = 1, name = "Adana", latitude = 37.0000, longitude = 35.3213, country = "Türkiye", admin1 = "Adana", plaka = "01"),
    City(id = 2, name = "Ankara", latitude = 39.91987, longitude = 32.85427, country = "Türkiye", admin1 = "Ankara", plaka = "06"),
    City(id = 3, name = "Antalya", latitude = 36.8841, longitude = 30.7056, country = "Türkiye", admin1 = "Antalya", plaka = "07"),
    City(id = 4, name = "Bursa", latitude = 40.1885, longitude = 29.0610, country = "Türkiye", admin1 = "Bursa", plaka = "16"),
    City(id = 5, name = "Eskişehir", latitude = 39.7767, longitude = 30.5206, country = "Türkiye", admin1 = "Eskişehir", plaka = "26"),
    City(id = 6, name = "Gaziantep", latitude = 37.0662, longitude = 37.3833, country = "Türkiye", admin1 = "Gaziantep", plaka = "27"),
    City(id = 7, name = "İstanbul", latitude = 41.0082, longitude = 28.9784, country = "Türkiye", admin1 = "İstanbul", plaka = "34"),
    City(id = 8, name = "İzmir", latitude = 38.4192, longitude = 27.1287, country = "Türkiye", admin1 = "İzmir", plaka = "35"),
    City(id = 9, name = "Konya", latitude = 37.8713, longitude = 32.4846, country = "Türkiye", admin1 = "Konya", plaka = "42"),
    City(id = 10, name = "Trabzon", latitude = 41.0015, longitude = 39.7178, country = "Türkiye", admin1 = "Trabzon", plaka = "61"),
    City(id = 11, name = "Muğla", latitude = 37.2153, longitude = 28.3636, country = "Türkiye", admin1 = "Muğla", plaka = "48"),
    City(id = 12, name = "Şanlıurfa", latitude = 37.1674, longitude = 38.7955, country = "Türkiye", admin1 = "Şanlıurfa", plaka = "63"),
    City(id = 13, name = "Çanakkale", latitude = 40.1553, longitude = 26.4142, country = "Türkiye", admin1 = "Çanakkale", plaka = "17"),
    City(id = 14, name = "Şırnak", latitude = 37.5188, longitude = 42.4537, country = "Türkiye", admin1 = "Şırnak", plaka = "73"),
    City(id = 15, name = "Çorum", latitude = 40.5489, longitude = 34.9533, country = "Türkiye", admin1 = "Çorum", plaka = "19"),
)

/**
 * Verilen koordinata en yakın ili döner (büyük daire mesafesi, haversine).
 * En yakın il merkezi [MAX_PROVINCE_DISTANCE_KM] değerinden uzaksa konum Türkiye dışındadır ve null döner;
 * aksi halde yurt dışındaki bir konuma en yakın sınır ili (örn. Edirne) yanlışlıkla önerilirdi.
 */
fun nearestProvince(latitude: Double, longitude: Double): City? {
    val nearest = turkeyProvinces.minBy { distanceKm(latitude, longitude, it.latitude, it.longitude) }
    return nearest.takeIf { distanceKm(latitude, longitude, it.latitude, it.longitude) <= MAX_PROVINCE_DISTANCE_KM }
}

// En geniş illerde (Konya, Sivas) il merkezine uzaklık yüzlerce km'ye çıkabildiği için cömert bir sınır
private const val MAX_PROVINCE_DISTANCE_KM = 250.0

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val sinLat = Math.sin(dLat / 2)
    val sinLon = Math.sin(dLon / 2)
    val a = sinLat * sinLat + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * sinLon * sinLon
    return 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}
