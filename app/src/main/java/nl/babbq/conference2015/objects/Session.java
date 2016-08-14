package nl.babbq.conference2015.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.opencsv.CSVReader;

import org.joda.time.Instant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nl.babbq.conference2015.R;
import nl.babbq.conference2015.utils.PreferenceManager;
import nl.babbq.conference2015.utils.Utils;

/**
 * Session object, created by the CSV file
 * @author Arnaud Camus
 */
public class Session implements Serializable, Parcelable {

    public static final String SESSIONS = "sessions";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT
            = new SimpleDateFormat("d/M/yyy HH:mm:ss", Locale.ENGLISH);
    public static final int END_DATE_COLUMN = 1;
    public static final int START_DATE_COLUMN = 0;


    private String[] CSVLine;
    private Date startDate;
    private Date endDate;
    private String headeline;
    private String speaker;
    private String speakerImageUrl;
    private String text;
    private String location;
    private Calendar calendar;

    public Session(@NonNull String[] fromCSV) {
        CSVLine = fromCSV;
        calendar = Calendar.getInstance();
        try {
            startDate = Instant.parse(fromCSV[START_DATE_COLUMN]).toDate();
            endDate = Instant.parse(fromCSV[END_DATE_COLUMN]).toDate();

            calendar.setTime(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        headeline = fromCSV[2];
        if (fromCSV.length > 12) {
            location = fromCSV[9];
            speaker = fromCSV[10];
            speakerImageUrl = fromCSV[11];
            text = fromCSV[12];
        } else {
            location = "";
            speaker = "";
            speakerImageUrl = "";
            text = "";
        }
    }

    /**
     * Save the new state of the conference.
     * @param ctx a valid context
     * @return true if the conference is favorite
     */
    public boolean toggleFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        boolean actual = prefManager.favorite(getHeadeline())
                .getOr(false);
        prefManager.favorite(getHeadeline())
                .put(!actual)
                .apply();
        return !actual;
    }

    /**
     * Determine if the talk has started or not
     * @return true if it is past
     */
    public boolean isPast() {
        Calendar cal1 = Calendar.getInstance();
        return calendar.before(cal1);
    }

    /**
     * Parse a inputStream reader and generate the list
     * of {@link Session}.
     */
    public static List<Session> parseInputStream(Context context, InputStreamReader stream) {
        List<Session> sessions = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(stream, '\t');
            reader.readNext(); // file headline
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 1 && !Utils.arrayContains(nextLine, context.getString(R.string.program_d2))) {
                    sessions.add(new Session(nextLine));
                }
            }
            saveInPreferences(context, sessions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Loads the sessions from sharedPreferences
     * @param context a valid context
     * @return the list of talks
     */
    public static List<Session> loadFromPreferences(Context context) {
        List<Session> list = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Set<String> sessions = prefs.getStringSet(SESSIONS, new HashSet<String>());
        for (String sessionLine : sessions) {
            String[] splitSessionLine = sessionLine.split("\t");
            if (splitSessionLine.length > 0) {
                list.add(new Session(splitSessionLine));
            }
        }

        Collections.sort(list, new Comparator<Session>() {
            @Override
            public int compare(Session session, Session session2) {
                return session.startDate.before(session2.startDate) ? -1 : 1;
            }
        });
        return list;
    }

    /**
     * Find the next available talk
     * @param data a list of Session objects
     * @return the position of the next item, or 0
     */
    public static int findNextEventPosition(@NonNull List<Session> data) {
        int position = 0;
        for (Session c: data) {
            if (c.isPast()) {
                position++;
            } else {
                return position;
            }
        }
        return 0;
    }


    private static void saveInPreferences(Context context, List<Session> sessions) {
        SharedPreferences.Editor prefsEditor
                = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
        Set<String> stringSet = new HashSet<>();
        for (Session session : sessions) {
            stringSet.add(TextUtils.join("\t", session.getCSVLine()));
        }
        prefsEditor.putStringSet(SESSIONS, stringSet);
        prefsEditor.apply();
    }

    //////////////////////////////////////
    //          GETTERS / SETTERS       //
    //////////////////////////////////////


    public boolean isFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        return prefManager.favorite(getHeadeline())
                .getOr(false);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getHeadeline() {
        return headeline;
    }

    public void setHeadeline(String headeline) {
        this.headeline = headeline;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSpeakerImageUrl() {
        return speakerImageUrl;
    }

    public void setSpeakerImageUrl(String speakerImageUrl) {
        this.speakerImageUrl = speakerImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getCSVLine() { return this.CSVLine; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CSVLine.length);
        dest.writeStringArray(this.CSVLine);
        dest.writeString(new Instant(this.startDate).toString());
        dest.writeString(new Instant(this.endDate).toString());
        dest.writeString(this.headeline);
        dest.writeString(this.speaker);
        dest.writeString(this.speakerImageUrl);
        dest.writeString(this.text);
        dest.writeString(this.location);
        dest.writeSerializable(this.calendar);
    }

    protected Session(Parcel in) {
        CSVLine = new String[in.readInt()];
        in.readStringArray(this.CSVLine);
        this.startDate = Instant.parse(in.readString()).toDate();
        this.endDate = Instant.parse(in.readString()).toDate();
        this.headeline = in.readString();
        this.speaker = in.readString();
        this.speakerImageUrl = in.readString();
        this.text = in.readString();
        this.location = in.readString();
        this.calendar = (Calendar) in.readSerializable();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        public Session createFromParcel(Parcel source) {
            return new Session(source);
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
