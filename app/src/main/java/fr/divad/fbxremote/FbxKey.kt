package fr.divad.fbxremote

/**
 * Touches reconnues par l'API HTTP du Player Freebox
 * (`/pub/remote_control?key=...`). Les codes sont ceux documentés par Free.
 */
enum class FbxKey(val code: String) {
    POWER("power"),
    DIGIT_0("0"), DIGIT_1("1"), DIGIT_2("2"), DIGIT_3("3"), DIGIT_4("4"),
    DIGIT_5("5"), DIGIT_6("6"), DIGIT_7("7"), DIGIT_8("8"), DIGIT_9("9"),
    RED("red"), GREEN("green"), YELLOW("yellow"), BLUE("blue"),
    UP("up"), DOWN("down"), LEFT("left"), RIGHT("right"), OK("ok"),
    VOL_UP("vol_inc"), VOL_DOWN("vol_dec"), MUTE("mute"),
    PRGM_UP("prgm_inc"), PRGM_DOWN("prgm_dec"),
    HOME("home"), BACK("back"), INFO("info"), EPG("epg"), LIST("list"),
    TV("tv"), SWAP("swap"), OPTIONS("options"), HELP("help"),
    MAIL("mail"), MEDIA("media"), PIP("pip"),
    PLAY("play"), REC("rec"), BWD("bwd"), FWD("fwd"), PREV("prev"), NEXT("next");

    companion object {
        fun digit(d: Int): FbxKey = entries.first { it.code == d.toString() }
    }
}
