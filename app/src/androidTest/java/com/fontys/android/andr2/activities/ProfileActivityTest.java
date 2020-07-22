package com.fontys.android.andr2.activities;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.fontys.android.andr2.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<ProfileActivity> mActivityTestRule= new ActivityScenarioRule<ProfileActivity>(ProfileActivity.class);
    //public ActivityTestRule<ProfileActivity> mActivityTestRule= new ActivityTestRule<ProfileActivity>(ProfileActivity.class);
    // private ProfileActivity PActivity= null;



    @Test
    public void test_isActivityInView()throws Exception{
        onView(withId(R.id.profileLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_visibility_textViews()throws Exception{
        onView(withId(R.id.userName)).check(matches(isDisplayed()));
        onView(withId(R.id.userEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.userPhoneNumber)).check(matches(isDisplayed()));
        onView(withId(R.id.userLocation)).check(matches(isDisplayed()));

    }

//    @Test
//    public void test_visibility_buttons()throws Exception{
//        onView(withId(R.id.signOutBtn)).check(matches(isDisplayed()));
//        onView(withId(R.id.usr_location_btn)).check(matches(isDisplayed()));
//
//    }
    @Test
    public void test_visibility_profileImage()throws Exception{
        onView(withId(R.id.userImage)).check(matches(isDisplayed()));

    }


//    @Test
//    public void test_isUsernameTextDisplayed()throws Exception{
//        onView(withId(R.id.userName)).check(matches(withText(R.string.userName)));
//
//    }

}