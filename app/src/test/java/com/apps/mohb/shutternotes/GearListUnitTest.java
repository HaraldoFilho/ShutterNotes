package com.apps.mohb.shutternotes;

import com.apps.mohb.shutternotes.lists.GearList;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GearListUnitTest {

    @Test
    public void gettersAndSetters() {

        GearList gearList = new GearList();

        gearList.add("Tripod");
        gearList.add("Filter ND");
        gearList.add("Filter PL");
        gearList.add("Teleconverter");
        gearList.add(0, "Lens");
        gearList.add(0, "Camera");

        assertEquals(6, gearList.size());
        assertEquals("Tripod", gearList.getGearItem(2));
        assertEquals("Camera", gearList.getGearItem(0));

        gearList.setGearItem(3, "Monopod");
        assertEquals("Monopod", gearList.getGearItem(3));

        gearList.add(3, "Close-up Lens");
        assertEquals("Close-up Lens", gearList.getGearItem(3));

        gearList.remove(3);
        assertEquals("Monopod", gearList.getGearItem(3));

    }

    @Test
    public void moveMethods() {

        GearList gearList = new GearList();

        gearList.add("Tripod");
        gearList.add("Filter ND");
        gearList.add("Filter PL");
        gearList.add("Teleconverter");
        gearList.add(0, "Lens");
        gearList.add(0, "Camera");
        gearList.setGearItem(3, "Monopod");
        gearList.add(3, "Close-up Lens");
        gearList.remove(3);

        gearList.get(3).setSelected(true);
        gearList.moveToBottomOfLastSelected(3);
        assertEquals("Monopod", gearList.getGearItem(0));
        assertTrue(gearList.get(0).isSelected());

        String gear = gearList.getGearItem(4);
        gearList.get(4).setSelected(true);
        gearList.moveToBottomOfLastSelected(4);
        assertEquals(gear, gearList.getGearItem(1));

        gearList.get(0).setSelected(false);
        gearList.moveToBottom(0);
        assertEquals("Monopod", gearList.get(gearList.size() - 1).getGearItem());
        assertFalse(gearList.get(gearList.size() - 1).isSelected());
        assertEquals(gear, gearList.getGearItem(0));
        assertTrue(gearList.get(0).isSelected());

    }

    @Test
    public void textAndTags() {

        GearList gearList = new GearList();

        gearList.add("Tripod");
        gearList.add("Filter ND");
        gearList.add("Filter PL");
        gearList.add("Teleconverter");

        for (int i = 0; i < gearList.size(); i++) {
            gearList.get(i).setSelected(true);
        }
        gearList.get(2).setSelected(false);

        String text = "Tripod\nFilter ND\nTeleconverter\n";

        ArrayList<String> tags = new ArrayList<>();

        tags.add("Tripod");
        tags.add("Filter ND");
        tags.add("Teleconverter");

        assertEquals(text, gearList.getGearListText());
        assertEquals(tags, gearList.getFlickrTags());

    }
}
