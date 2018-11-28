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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StudentMainButtonTest {

    @Rule
    public ActivityTestRule<StudentMainActivity> mActivityTestRule = new ActivityTestRule<>(StudentMainActivity.class);

    @Test
    public void createNewReportButton() {
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.student_main_button_createreport), withText("CREATE NEW REPORT"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatTextView.perform(click());

        onView(withId(R.id.createreport_button_submit)).check(matches(isDisplayed()));
    }

    @Test
    public void submittedReportButton() {
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.student_main_viewhistory), withText("VIEW SUBMITTED REPORTS"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                3),
                        isDisplayed()));
        appCompatTextView.perform(click());

        onView(withId(R.id.alertcards_rv)).check(matches(isDisplayed()));
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
