import org.junit.Test;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by mustarohman on 27/01/2017.
 */
public class PeriodTest {
    final int sessionSize = 45;
    Topic topic =  new Topic("Logic", 240, sessionSize);
    Period period = new Period(Period.PERIOD_TYPE.SUBJECT, topic.getName() ,3, sessionSize);
    Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null ,0, 15);

    @Test
    public void testSetDateTime() {
        Calendar dateTime = new GregorianCalendar(2017, 1, 26, 9, 0);
        period.setDateTime(dateTime);
        assertEquals(period.getDateTime(), dateTime);
    }

    @Test
    public void testSetDateTimeFalse() {
        Calendar dateTime = new GregorianCalendar(2017, 1, 26, 9, 0);
        period.setDateTime(dateTime);
        assertNotEquals(period.getDateTime(), new GregorianCalendar(2017, 1, 27, 9, 0));
    }

    @Test
    public void testGetDuration() {
        assertEquals(sessionSize, period.getPeriodDuration());
    }

    @Test
    public void testToStringSubject() {
        assertEquals("Logic Part: 3", period.toString());
    }

    @Test
    public void testToStringBreak() {
        assertEquals("BREAK", breakPeriod.toString());
    }
}
