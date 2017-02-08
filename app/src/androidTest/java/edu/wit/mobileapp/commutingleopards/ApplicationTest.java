package edu.wit.mobileapp.commutingleopards;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ApplicationTest {
    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);

    @Test
    public void Test_goToMap() {

        //This test the Go to Map and automatic location button
        //check if the driving button is located
        onView(withId(R.id.fetch_address_button)).perform(click());
        onView(withId(R.id.btn_go)).perform(click());
        onView(withId(R.id.driving_button)).check(matches(isDisplayed()));

        Espresso.pressBack();

        //Replaces the text and manual input and test
        onView(withId(R.id.autocomplete)).perform(replaceText("Lynn Massachusetts"), closeSoftKeyboard());
        onView(withId(R.id.btn_go)).perform(click());
        onView(withId(R.id.driving_button)).check(matches(isDisplayed()));
    }
}