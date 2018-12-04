package com.ua.cs495f2018.berthaIRT;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InstitutionCodeTest {

    @Rule
    public ActivityTestRule<Client> mActivityTestRule = new ActivityTestRule<>(Client.class);

    @Test
    //This test puts in a wrong school code and an error message is shown
    public void wrongInstitutionCode() {
        onView(withId(R.id.newuser_input_accesscode)).perform(clearText());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.newuser_button_join),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView7),
                                        0),
                                2),
                        isDisplayed()));
        cardView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.newuser_input_accesscode)).check(matches(hasErrorText("Invalid access code.")));
    }

    @Test
    //This test goes through the steps to enter a correct school code then makes sure no exist
    public void correctInstitutionCode() {
        onView(withId(R.id.newuser_input_accesscode)).perform(clearText(), typeText("999999"));

        ViewInteraction cardView = onView(
                allOf(withId(R.id.newuser_button_join),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView7),
                                        0),
                                2),
                        isDisplayed()));
        cardView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.generaldialog_button_no)).check(matches(isDisplayed()));
    }

    @Test
    //This test goes through the steps to enter a correct school code then joins it and make sure the next activity was launched
    public void userJoinInstitution() {
        onView(withId(R.id.newuser_input_accesscode)).perform(clearText(), typeText("999999"));

        ViewInteraction cardView = onView(
                allOf(withId(R.id.newuser_button_join),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView7),
                                        0),
                                2),
                        isDisplayed()));
        cardView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView2 = onView(withId(R.id.generaldialog_button_yes)).check(matches(isDisplayed()));
        cardView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.student_main_button_createreport)).check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

