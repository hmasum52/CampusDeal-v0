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

    // product collection
    public static final String ADS_COLLECTION = "ads";

    public static final ArrayList<String> CATEGORY_LIST = new ArrayList<String>() {{
        add("Books");
        add("Stationery");
        add("Electronics and Gadgets");
        add("Clothes");
        add("Sports");
        add("Tutoring");
        add("Musical Instruments");
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
    }};

}
