package nl.babbq.conference2015.objects;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nono on 10/17/15.
 */
public class ConferenceDay implements Serializable {
    private Date day;
    private String shortName;

    public ConferenceDay(Date day, String shortName) {
        this.day = day;
        this.shortName = shortName;
    }

    public boolean isToday() {
        return DateTime.now().withTimeAtStartOfDay().equals(
                new DateTime(day).withTimeAtStartOfDay());
    }

    public Date getDay() {
        return day;
    }

    public String getShortName() {
        return shortName;
    }
}
