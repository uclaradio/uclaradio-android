package hohostudio.testapp;

/**
 * Created by Roger Ho on 5/4/2016.
 */
public class ScheduleInfo {
    protected String mShowTime;
    protected String mShowDay;
    protected String mShowName;
    protected String mHostName;
    protected String mGenreName;

    public ScheduleInfo(String showName, String hostName, String genreName, String showDay, String showTime) {
        mShowDay = showDay;
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

    public String getShowDay() { return mShowDay; }
}
