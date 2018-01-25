package com.bignerdranch.android.criminalintent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.text.format.DateFormat;
import android.util.Log;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CrimeListTester extends InstrumentationTestCase {
    private static final String LOG_TAG = "CrimeListTester";

    @Rule
    public ActivityTestRule<CrimeListActivity> mCrimeListActivityRule =
            new ActivityTestRule<CrimeListActivity>(CrimeListActivity.class);

    @Before
    public void setUp() {
        clearDatabase();
    }

    @After
    public void tearDown() {
        clearDatabase();
    }

    private void clearDatabase() {
        CrimeBaseHelper dbHelper = new CrimeBaseHelper(mCrimeListActivityRule.getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.i(LOG_TAG, "Dropping db table");
        db.execSQL("DROP TABLE IF EXISTS " + CrimeDbSchema.CrimeTable.NAME);

        Log.i(LOG_TAG, "creating new table");
        dbHelper.onCreate(db);

        Cursor c = db.query(CrimeDbSchema.CrimeTable.NAME, new String[]{CrimeDbSchema.CrimeTable.Cols.UUID}, null, null, null, null, null);

        if (c.getCount() != 0) {
            throw new RuntimeException("Error during cleanup of DB, no records should be present for table " + CrimeDbSchema.CrimeTable.NAME);
        }

        Log.i(LOG_TAG, "The database has been cleaned!");
        dbHelper.close();
    }

    // Gets the user to the new crime screen via the menu button
    private void pressNewCrimeButton() {
        Espresso.onView(withId(R.id.new_crime)).perform(click());
    }

    // Creates a new crime, sets the title and verifies that the title is represented as expected
    @Test
    public void verifyNewCrimeButton() {
        String TEST_TITLE_TEXT = "ADAMS CRIME";
        pressNewCrimeButton();
        Espresso.onView(allOf(withId(R.id.crime_title), isDisplayed())).perform(typeText(TEST_TITLE_TEXT), closeSoftKeyboard());
        Espresso.onView(allOf(withId(R.id.crime_title), isDisplayed())).check(matches(withText(TEST_TITLE_TEXT)));
    }

    // Creates a new crime, sets the date, and verifies that the date is what we set it to
    @Test
    public void verifyNewCrimeDateButtonPressed() {
        int year = 1999;
        int month = 1;
        int day = 1;
        pressNewCrimeButton();
        Espresso.onView(allOf(withId(R.id.crime_date), isDisplayed())).perform(click());
        Espresso.onView(withId(R.id.dialog_date_picker)).perform(PickerActions.setDate(year, month, day));
        Espresso.onView(withId(android.R.id.button1)).perform(click());

        Date date = new GregorianCalendar(year, Calendar.JANUARY, day).getTime();
        Espresso.onView(allOf(withId(R.id.crime_date), isDisplayed())).check(matches(withText(DateFormat.format("MM/dd/yyyy", date).toString())));
    }

    // Creates a new crime, sets the date, and verifies that the date is what we set it to
    @Test
    public void verifyNewCrimeTimeButtonPressed() {
        int hour = 6;
        int minute = 6;
        pressNewCrimeButton();
        Espresso.onView(allOf(withId(R.id.crime_time), isDisplayed())).perform(click());
        Espresso.onView(withId(R.id.dialog_time_picker)).perform(PickerActions.setTime(hour, minute));
        Espresso.onView(withId(android.R.id.button1)).perform(click());

        Date date;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        date = cal.getTime();
        Espresso.onView(allOf(withId(R.id.crime_time), isDisplayed())).check(matches(withText(DateFormat.format("hh:mm:ss", date).toString())));
    }

}
