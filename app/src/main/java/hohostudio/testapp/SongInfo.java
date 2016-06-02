package hohostudio.testapp;

/**
 * Created by Roger Ho on 5/4/2016.
 */
public class SongInfo {
    protected String mArtistName;
    protected String mSongName;
    protected String mURL;

    public SongInfo(String artistName, String songName, String url) {
        mArtistName = artistName;
        mSongName = songName;
        mURL = url;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public String getSongName() {
        return mSongName;
    }

    public String getURL() {
        return mURL;
    }

}
