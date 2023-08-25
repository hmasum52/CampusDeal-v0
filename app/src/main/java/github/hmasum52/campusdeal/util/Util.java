package github.hmasum52.campusdeal.util;

import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class Util {

    public static String calculateTimeAlgo(Date uploadDate) {
        long diff = new Date().getTime() - uploadDate.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays  = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
        long diffMonths = diff / (30L * 24 * 60 * 60 * 1000);
        long diffYears = diff / (365L * 24 * 60 * 60 * 1000);

        if(diffSeconds < 60){
            return diffSeconds+" sec ago";
        }else if(diffMinutes < 60){
            return diffMinutes+" min ago";
        }else if(diffHours < 24){
            return diffHours+" hours ago";
        }else if(diffDays < 7){
            return diffDays+" days ago";
        }else if(diffWeeks < 4){
            return diffWeeks+" weeks ago";
        }else if(diffMonths < 12){
            return diffMonths+" months ago";
        }else {
            return diffYears+" years ago";
        }
    }

    /**
     * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     *
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance)/1000.0; // return in km
    }

    public static int getViewPagerFragmentIndex(Fragment fragment, int numberOfFragment){
        //get fragment tag set by the view pager
        // https://stackoverflow.com/questions/55728719/get-current-fragment-with-viewpager2
        int startIndex = fragment.toString().indexOf("tag=") + 4; // Start index of the tag value
        if(startIndex<0) return -1;
        int endIndex = fragment.toString().indexOf(")", startIndex); // End index of the tag value
        String fragmentTag = fragment.toString().substring(startIndex, endIndex);
        Log.d("Util.getViewPagerFragmentIndex", "onViewCreated: fragment tag = "+fragmentTag);
        int index = Integer.parseInt(fragmentTag.substring(1));
        if(index<0 || index>=numberOfFragment) return -1;
        return index;
    }
}
