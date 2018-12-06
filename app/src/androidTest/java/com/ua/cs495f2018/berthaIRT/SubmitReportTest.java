package com.ua.cs495f2018.berthaIRT;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SubmitReportTest {

    @Rule
    public ActivityTestRule<StudentCreateReportActivity> mActivityTestRule = new ActivityTestRule<>(StudentCreateReportActivity.class);


    @Test
    public void submitEmptyReport() {
        ViewInteraction appCompatTextView2 = onView(withId(R.id.createreport_button_submit)).check(matches(isDisplayed()));
        appCompatTextView2.perform(scrollTo(), click());

        onView(withId(R.id.createreport_input_description)).check(matches(hasErrorText("You must provide a description.")));
    }

    @Test
    public void submitNonEmptyReport() {
        onView(withId(R.id.createreport_input_description)).perform(clearText(), typeText("Test"));

        onView(withId(R.id.createreport_button_submit)).perform(scrollTo(), click());

        onView(withId(R.id.checkboxes_button_confirm)).check(matches(isDisplayed()));
    }
}
