package com.apps.mohb.shutternotes;

import com.apps.mohb.shutternotes.notes.FlickrNote;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FlickrNoteUnitTest {

    @Test
    public void gettersAndSetters() {

        ArrayList<String> tags = new ArrayList<>();

        String startTime = "2019:02:17 10:00:00";
        String finishTime = "2019:02:17 11:00:00";

        FlickrNote note = new FlickrNote(
                "Test", "", tags, 0.0, 0.0, "", "");

        assertEquals("Test", note.getTitle());
        assertEquals("", note.getDescription());
        assertEquals(tags, note.getTags());
        assertEquals(0.0, note.getLatitude(), 0);
        assertEquals(0.0, note.getLongitude(), 0);
        assertEquals("", note.getStartTime());
        assertEquals("", note.getFinishTime());
        assertFalse(note.isSelected());

        tags.add("tag1");
        tags.add("tag2");
        tags.add("tag3");

        note.setTitle("Testing title setter");
        note.setDescription("Testing description setter");
        note.setTags(tags);
        note.setLatitude(12345.6789);
        note.setLongitude(98765.4321);
        note.setStartTime(startTime);
        note.setFinishTime(finishTime);
        note.setSelected(true);

        assertEquals("Testing title setter", note.getTitle());
        assertEquals("Testing description setter", note.getDescription());
        assertEquals(tags, note.getTags());
        assertEquals(12345.6789, note.getLatitude(), 0);
        assertEquals(98765.4321, note.getLongitude(), 0);
        assertEquals(startTime, note.getStartTime());
        assertEquals(finishTime, note.getFinishTime());
        assertTrue(note.isSelected());

    }

    @Test
    public void timeIsInInterval() {

        ArrayList<String> tags = new ArrayList<>();

        String startTime = "2019:02:17 10:00:00";
        String finishTime = "2019:02:17 11:00:00";

        FlickrNote note = new FlickrNote(
                "Test", "", tags, 0.0, 0.0, startTime, finishTime);

        String time1 = "2018:04:15 15:10:00";
        String time2 = "2019:02:17 10:30:00";
        String time3 = "2019:02:17 12:00:00";
        String time4 = "2019:02:17 10:00:00";
        String time5 = "2019:02:17 11:00:00";
        String time6 = "2019:02:17 10:01:00";
        String time7 = "2019:02:17 10:59:00";

        assertFalse(note.isInTimeInterval(time1));
        assertTrue(note.isInTimeInterval(time2));
        assertFalse(note.isInTimeInterval(time3));
        assertFalse(note.isInTimeInterval(time4));
        assertFalse(note.isInTimeInterval(time5));
        assertTrue(note.isInTimeInterval(time6));
        assertTrue(note.isInTimeInterval(time7));

    }

}
