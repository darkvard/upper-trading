package com.upper.trading.data.datasource

import com.upper.trading.R
import com.upper.trading.domain.model.Country

object CountryDataSource {

    val COUNTRIES = listOf(
        Country("US", "United States", "+1", R.drawable.ic_flag_us),
        Country("NG", "Nigeria", "+234", R.drawable.ic_flag_ng),
        Country("BD", "Bangladesh", "+880", R.drawable.ic_flag_bd),
        Country("PH", "Philippines", "+63", R.drawable.ic_flag_ph),
        Country("GN", "Guinea", "+224", R.drawable.ic_flag_gn),
        Country("CA", "Canada", "+1", R.drawable.ic_flag_ca),
        Country("GB-ENG", "England", "+44", R.drawable.ic_flag_eng),
        Country("AR", "Argentina", "+54", R.drawable.ic_flag_ar),
        Country("TH", "Thailand", "+66", R.drawable.ic_flag_th),
        Country("MG", "Madagascar", "+261", R.drawable.ic_flag_mg),
        Country("GB", "United Kingdom", "+44", R.drawable.ic_flag_gb),
        Country("CH", "Switzerland", "+41", R.drawable.ic_flag_ch),
        Country("VE", "Venezuela", "+58", R.drawable.ic_flag_ve),
        Country("AT", "Austria", "+43", R.drawable.ic_flag_at),
        Country("ID", "Indonesia", "+62", R.drawable.ic_flag_id),
        Country("BO", "Bolivia", "+591", R.drawable.ic_flag_bo),
        Country("NO", "Norway", "+47", R.drawable.ic_flag_no),
        Country("ML", "Mali", "+223", R.drawable.ic_flag_ml),
        Country("EE", "Estonia", "+372", R.drawable.ic_flag_ee),
        Country("BR", "Brazil", "+55", R.drawable.ic_flag_br),
        Country("NL", "Netherlands", "+31", R.drawable.ic_flag_nl),

        Country("DE", "Germany", "+49", R.drawable.ic_flag_de),
        Country("RU", "Russia", "+7", R.drawable.ic_flag_ru),
        Country("VN", "Vietnam", "+84", R.drawable.ic_flag_vn),
        Country("EG", "Egypt", "+20", R.drawable.ic_flag_eg),
        Country("CR", "Costa Rica", "+506", R.drawable.ic_flag_cr),
        Country("FR", "France", "+33", R.drawable.ic_flag_fr),
        Country("IT", "Italy", "+39", R.drawable.ic_flag_it),
        Country("MX", "Mexico", "+52", R.drawable.ic_flag_mx),
        Country("KR", "South Korea", "+82", R.drawable.ic_flag_kr),
        Country("AE", "United Arab Emirates", "+971", R.drawable.ic_flag_ae),
        Country("CN", "China", "+86", R.drawable.ic_flag_cn),
        Country("IL", "Israel", "+972", R.drawable.ic_flag_il),
        Country("CL", "Chile", "+56", R.drawable.ic_flag_cl),
        Country("HU", "Hungary", "+36", R.drawable.ic_flag_hu),
        Country("CG", "Republic of the Congo", "+242", R.drawable.ic_flag_cg),
        Country("PL", "Poland", "+48", R.drawable.ic_flag_pl),
        Country("RO", "Romania", "+40", R.drawable.ic_flag_ro),
        Country("IE", "Ireland", "+353", R.drawable.ic_flag_ie),
        Country("LV", "Latvia", "+371", R.drawable.ic_flag_lv),
        Country("YE", "Yemen", "+967", R.drawable.ic_flag_ye),
        Country("CZ", "Czech Republic", "+420", R.drawable.ic_flag_cz),

        Country("AU", "Australia", "+61", R.drawable.ic_flag_au),
        Country("JP", "Japan", "+81", R.drawable.ic_flag_jp),
        Country("CO", "Colombia", "+57", R.drawable.ic_flag_co),
        Country("TR", "Turkey", "+90", R.drawable.ic_flag_tr),
        Country("LT", "Lithuania", "+370", R.drawable.ic_flag_lt),
        Country("SE", "Sweden", "+46", R.drawable.ic_flag_se),
        Country("ES", "Spain", "+34", R.drawable.ic_flag_es),
        Country("UA", "Ukraine", "+380", R.drawable.ic_flag_ua),
        Country("BG", "Bulgaria", "+359", R.drawable.ic_flag_bg),
        Country("IN", "India", "+91", R.drawable.ic_flag_in),
        Country("LU", "Luxembourg", "+352", R.drawable.ic_flag_lu),
        Country("FI", "Finland", "+358", R.drawable.ic_flag_fi),
        Country("PT", "Portugal", "+351", R.drawable.ic_flag_pt),
        Country("AM", "Armenia", "+374", R.drawable.ic_flag_am),
        Country("PK", "Pakistan", "+92", R.drawable.ic_flag_pk),
        Country("PE", "Peru", "+51", R.drawable.ic_flag_pe),
        Country("GR", "Greece", "+30", R.drawable.ic_flag_gr),
        Country("GA", "Gabon", "+241", R.drawable.ic_flag_ga),
        Country("CU", "Cuba", "+53", R.drawable.ic_flag_cu),
        Country("BE", "Belgium", "+32", R.drawable.ic_flag_be),
    )

    val POPULAR_COUNTRY = listOf(
        Country("VN", "Vietnam", "+84", R.drawable.ic_flag_vn),
        Country("RU", "Russia", "+7", R.drawable.ic_flag_ru),
        Country("CU", "Cuba", "+53", R.drawable.ic_flag_cu),
        Country("CN", "China", "+86", R.drawable.ic_flag_cn),
        Country("US", "United States", "+1", R.drawable.ic_flag_us),
    )

    val DEFAULT_COUNTRY = Country("VN", "Vietnam", "+84", R.drawable.ic_flag_vn)
}
