import org.junit.Assert;
import org.junit.Test;
import timetable.Period;
import timetable.Subject;
import timetable.Topic;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by mustarohman on 27/01/2017.
 */
public class SubjectTest {
    Subject subject = new Subject("CSL" , new Topic("Logic", "CSL",  300, 45), new Topic("Trees", "CSL", 300, 45));
    Subject subject2 = new Subject("CSL" , new Topic("Logic", "CSL", 300, 45), new Topic("Trees", "CSL", 360, 45));

    @Test
    public void testGetPeriods() {
        ArrayList<Period> periods = subject.getPeriods();
        assertEquals(14, periods.size());
        int[] subjectCounter = new int[2];
        for (Period period: periods) {
            if (period.getTopicName().equals("Logic")) {
                subjectCounter[0]++;
            } else {
                subjectCounter[1]++;
            }
        }
        assertEquals(subjectCounter[0], subjectCounter[1]);

        periods = subject2.getPeriods();
        subjectCounter = new int[2];
        for (Period period: periods) {
            if (period.getTopicName().equals("Logic")) {
                subjectCounter[0]++;
            } else {
                subjectCounter[1]++;
            }
        }
        assertFalse(subjectCounter[0] == subjectCounter[1]);
    }
}
