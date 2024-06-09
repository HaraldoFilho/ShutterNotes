package com.apps.mohb.shutternotes;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FlickrUploadToPhotosUnitTest {

    FlickrUploadToPhotosActivity activity = new FlickrUploadToPhotosActivity();

    @Test
    public void getNumOfPagesTest() {

        assertEquals(4, activity.getNumOfPages(40, 10));
        assertEquals(5, activity.getNumOfPages(45, 10));
        assertEquals(1, activity.getNumOfPages(23, 23));
        assertEquals(1, activity.getNumOfPages(10, 23));
        assertEquals(2, activity.getNumOfPages(24, 23));
        assertEquals(13, activity.getNumOfPages(123, 10));
        assertEquals(6, activity.getNumOfPages(584, 100));
        assertEquals(1, activity.getNumOfPages(19, 20));
        assertEquals(0, activity.getNumOfPages(0, 10));

    }

    @Test
    public void getNewTagsArrayTest() {

        String[] array1 = {"Elem1", "Elem2", "Elem3"};
        String[] array2 = {"Elem4", "Elem5", "Elem6", "Elem7"};
        String[] newArray = {"\"\"Elem1\"\"", "\"\"Elem2\"\"", "\"\"Elem3\"\"", "\"\"Elem4\"\"", "\"\"Elem5\"\"", "\"\"Elem6\"\"", "\"\"Elem7\"\""};

        assertArrayEquals(newArray, activity.getNewPhotoTagsArray(array1, array2));

    }
}
