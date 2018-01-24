package com.bignerdranch.android.criminalintent;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<CrimeListActivity> mCrimeListActivityRule =
            new ActivityTestRule<CrimeListActivity>(CrimeListActivity.class);

    // Gets the user to the new crime screen via the menu button
    private void pressNewCrimeButton() {
        Espresso.onView(withId(R.id.new_crime)).perform(click());
    }

    // Creates a new crime, sets the title and verifies that the title is represented as expected
    @Test
    public void verifyNewCrimeTypeText() {
        String TEST_TITLE_TEXT = "ADAMS CRIME";
        pressNewCrimeButton();
        Espresso.onView(withId(R.id.crime_title)).perform(typeText(TEST_TITLE_TEXT), closeSoftKeyboard());
        Espresso.onView(withId(R.id.crime_title)).check(matches(withText(TEST_TITLE_TEXT)));
    }

    // Creates a new crime, sets the date, and verifies that the date is what we set it to
    @Test
    public void verifyNewCrimeDateButtonPressed() {
        int year = 1999;
        int month = 1;
        int day = 1;
        pressNewCrimeButton();
        Espresso.onView(withId(R.id.crime_date)).perform(click());
        Espresso.onView(withId(R.id.dialog_date_picker)).perform(PickerActions.setDate(year, month, day));
        Espresso.onView(withId(android.R.id.button1)).perform(click());
    }
}
