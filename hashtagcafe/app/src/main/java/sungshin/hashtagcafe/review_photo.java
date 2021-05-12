package sungshin.hashtagcafe;

import android.net.Uri;

public class review_photo {

    private Uri pic;
    // ImageButton Del;
    public static int picCount=0;

    public review_photo(Uri pic) {
        this.pic = pic;
    }

    public Uri getPic() {
        return pic;
    }

    public void setPic(Uri pic) {
        this.pic = pic;
    }

}