package com.fontys.android.andr2.activities;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.fontys.android.andr2.R;
import org.junit.Rule;
import org.junit.Test;
import static org.hamcrest.Matchers.allOf;


@LargeTest
public class LogoutTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void logoutTest() {
        ViewInteraction appCompatTextView = onView(withId(R.id.regSingin));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(withId(R.id.regEmail));
        appCompatEditText.perform(replaceText("Obaida@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.regPassword));
        appCompatEditText2.perform(replaceText("Obaida"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.signUpBtn));
        appCompatButton.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction overflowMenuButton = onView(withContentDescription("More options"));
        overflowMenuButton.perform(click());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Sign out"),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction button = onView(withId(R.id.signUpBtn));
        button.check(matches(isDisplayed()));
    }
}
