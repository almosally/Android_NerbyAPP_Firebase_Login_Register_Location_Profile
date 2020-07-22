package com.fontys.android.andr2.activities;

import android.graphics.Rect;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.fontys.android.andr2.R;
import com.google.android.gms.maps.GoogleMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.security.acl.Group;
import java.util.regex.Pattern;

import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LocationComponentTest {

        @Rule
        public ActivityTestRule<LocationComponent> mActivityTestRule= new ActivityTestRule<LocationComponent>(LocationComponent.class);
        private LocationComponent lActivity= null;


        @Before
        public void setUp() throws Exception {
            lActivity= mActivityTestRule.getActivity();
        }



        @Test
        public void testLaunch()throws Exception{
            View view =lActivity.findViewById(R.id.frameLayout);
            assertNotNull(view);
        }


        @Test
        public void testMapMarker() throws Exception{
            UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("I am Here"));
            try {
                marker.click();

            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }

        }


        @Test
        public void testNotification() throws Exception{
            String NOTIFICATION_TITLE= "nErBy";
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.openNotification();
            device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)),TIMEOUT);
            UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));

            assertEquals(NOTIFICATION_TITLE, title.getText());

        }

        @After
        public void tearDown() throws Exception {
        }
}