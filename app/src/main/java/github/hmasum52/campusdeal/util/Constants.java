package github.hmasum52.campusdeal.util;

import java.util.ArrayList;
import java.util.List;

import github.hmasum52.campusdeal.R;

public class Constants {
    // fire store constants

    // user collection
    public static final String USER_COLLECTION = "users";

    // user wishlist collection
    public static final String WISHLIST_COLLECTION = "wishlist";

    // user deals collection
    // users/{userId}/deals/{adId}
    public static final String USER_DEALS_COLLECTION = "deals";

    // user buy history collection
    // users/{userId}/buy_history/{adId}
    public static final String USER_BUY_HISTORY_COLLECTION = "buy_history";

    // ads collection
    public static final String ADS_COLLECTION = "ads";

    // ad key
    public static final String AD_KEY = "ad";

    public static final String DEAL_KEY = "deal_key";

    // buy request collection
    public static final String DEAL_REQUEST_COLLECTION = "deal_request";

    public static final ArrayList<String> CATEGORY_LIST = new ArrayList<String>() {{
        add("Books");
        add("Stationery");
        add("Electronics and Gadgets");
        add("Clothes");
        add("Sports");
        add("Tutoring");
        add("Musical Instruments");
        add("Others");
    }};

    // category icons
    public  static  final List<Integer> CATEGORY_ICON_LIST = new ArrayList<Integer>() {{
        add(R.drawable.books_svgrepo_com);
        add(R.drawable.stationery_compass_svgrepo_com);
        add(R.drawable.game_console_psp_svgrepo_com);
        add(R.drawable.black_turtleneck_svgrepo_com);
        add(R.drawable.man_bouncing_ball_svgrepo_com);
        add(R.drawable.teacher_light_skin_tone_svgrepo_com);
        add(R.drawable.guitar_svgrepo_com);
        add(R.drawable.baseline_category_24);
    }};

}
