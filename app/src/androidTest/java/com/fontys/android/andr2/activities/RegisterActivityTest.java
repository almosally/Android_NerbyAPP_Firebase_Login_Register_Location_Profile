package com.fontys.android.andr2.activities;

import android.view.View;
import android.widget.EditText;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.fontys.android.andr2.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test

        public void clickSignUpButton_opensSignUpScreen() {
            //locate and click on the login button
            onView(withId(R.id.signUpBtn)).perform(click());

            //check if the sign up screen is displayed by asserting that the email edittext is displayed
            onView(withId(R.id.regEmail)).check(matches(Matchers.allOf(isDescendantOfA(withId(R.id.register_layout)), isDisplayed())));
    }

    @Test
    public void clickSignUpButtonAfterFillingForm_showProgressAndSuccessScreen() {
        String reg_name = "Firstname";
        String reg_email = "Firstname@gmail.com";
        String reg_mobile = "0611111111";
        String reg_password = "password";

        //find the name edit text and type in the  name
        onView(withId(R.id.regName)).perform(typeText(reg_name), closeSoftKeyboard());

        //find the email edit text and type in the email
        onView(withId(R.id.regEmail)).perform(typeText(reg_email), closeSoftKeyboard());

        //find the mobile edit text and type in the mobile
        onView(withId(R.id.regMobile)).perform(typeText(reg_mobile), closeSoftKeyboard());

        //find the password edit text and type in the password
        onView(withId(R.id.regPassword)).perform(typeText(reg_password), closeSoftKeyboard());

        //click the signup button
        onView(withId(R.id.signUpBtn)).perform(click());

    }

}