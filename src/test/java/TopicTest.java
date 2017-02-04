import org.junit.Test;
import timetable.Period;
import timetable.Topic;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by mustarohman on 26/01/2017.
 */
public class TopicTest {

    // 5 hours = 300 min
    Topic topic = new Topic("Trees2", 300, 45);

    @Test
    public void testGeneratePeriods() {
        ArrayList<Period> periods = topic.getTopicPeriods();
        assertEquals(7, periods.size());
        for (Period period: periods) {
            assertEquals(period.getTopicName(), topic.getName());
        }
    }
}
