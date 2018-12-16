package jeong.hyejin.searchmovieinfo;

public class MovieInfoItem {

    private String mImageUrl; // 이미지 주소
    private String mTitle;    // 영화 제목
    private double mUserRating; // 평점
    private String mPubDate;    // 개봉일
    private String mDirector;   // 감독
    private String mActors;     // 배우
    private String mLink;       // 링크

    public MovieInfoItem(String imageUrl, String title, double userRating, String pubDate, String director, String actors, String link){
       mImageUrl = imageUrl;
       mTitle = title;
       mUserRating = userRating;
       mPubDate = pubDate;
       mDirector = director;
       mActors = actors;
       mLink = link;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getDirector() {
        return mDirector;
    }

    public String getActors() {
        return mActors;
    }

    public String getLink(){ return mLink;}
}
