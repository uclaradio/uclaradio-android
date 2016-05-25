package hohostudio.uclaradio;

/**
 * Created by Roger Ho on 5/4/2016.
 */
public class ScheduleInfo {
    protected String mShowTime;
    protected String mShowName;
    protected String mHostName;
    protected String mGenreName;

    public ScheduleInfo(String showTime, String showName, String hostName, String genreName) {
        mShowTime = showTime;
        mShowName = showName;
        mHostName = hostName;
        mGenreName = genreName;
    }

    public String getShowTime() {
        return mShowTime;
    }

    public String getShowName() {
        return mShowName;
    }

    public String getHostName() {
        return mHostName;
    }

    public String getGenreName() {
        return mGenreName;
    }
}
