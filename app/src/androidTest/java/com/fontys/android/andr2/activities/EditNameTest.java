package com.fontys.android.andr2.activities;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.fontys.android.andr2.R;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@LargeTest
public class EditNameTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void editNameTest() {
        ViewInteraction appCompatTextView = onView(withId(R.id.regSingin));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(withId(R.id.regEmail));
        appCompatEditText.perform(replaceText("Obaida@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.regPassword));
        appCompatEditText2.perform(replaceText("Obaida"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(withId(R.id.regPassword));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(withId(R.id.signUpBtn));
        appCompatButton.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(withId(R.id.edit_profile_btn));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText4 = onView(withId(R.id.editUserName));
        appCompatEditText4.perform(replaceText("Obaida Bolbol"));

        ViewInteraction appCompatEditText5 = onView(withId(R.id.editUserName));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(withId(R.id.save_edit_btn));

        ViewInteraction textView = onView(withId(R.id.userName));
        textView.check(matches(withText("Obaida Bolbol")));
    }


}
