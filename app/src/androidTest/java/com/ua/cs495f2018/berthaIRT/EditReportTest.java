package com.ua.cs495f2018.berthaIRT;


import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditReportTest {

    @Rule
    public ActivityTestRule<Client> mActivityTestRule = new ActivityTestRule<>(Client.class);

    @Before
    public void setup() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView = onView(withId(R.id.newuser_button_adminlogin)).check(matches(isDisplayed()));
        cardView.perform(click());

        onView(withId(R.id.adminlogin_input_email)).perform(clearText(), typeText("ssinischo@gmail.com"));
        onView(withId(R.id.adminlogin_input_password)).perform(clearText(), typeText("111111"));

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.adminlogin_button_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView2),
                                        0),
                                6),
                        isDisplayed()));
        cardView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.adminmain_button_reports),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.adminmain_bottomnav),
                                        0),
                                2),
                        isDisplayed()));
        constraintLayout.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.admin_reports_rv),
                        childAtPosition(
                                withId(R.id.container),
                                2)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
    }

    @Test
    //Test that when open report is clicked that the value in map is updated and the display is updated
    public void openReport() {
        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.cardviewOpen),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(R.id.cardView17),
                                                0)),
                                0),
                        isDisplayed()));
        cardView3.perform(click());

        String expected = "Open";
        //makes sure that the value was stored
        assertEquals(expected, Client.activeReport.getStatus());
        onView(withId(R.id.admin_reportdetails_alt_status)).check(matches(withText(expected)));
    }

    @Test
    //Test that when close report is clicked that the value in map is updated and the display is updated
    public void closeReport() {
        ViewInteraction cardView4 = onView(
                allOf(withId(R.id.cardviewClosed),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(R.id.cardView17),
                                                0)),
                                1),
                        isDisplayed()));
        cardView4.perform(click());

        String expected = "Closed";
        //makes sure that the value was stored
        assertEquals(expected, Client.activeReport.getStatus());
        onView(withId(R.id.admin_reportdetails_alt_status)).check(matches(withText(expected)));
    }

    @Test
    //Test that when resolve report is clicked that the value in map is updated and the display is updated
    public void resolveReport() {
        ViewInteraction cardView5 = onView(
                allOf(withId(R.id.cardviewResolved),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(R.id.cardView17),
                                                0)),
                                2),
                        isDisplayed()));
        cardView5.perform(click());

        String expected = "Resolved";
        //makes sure that the value was stored
        assertEquals(expected, Client.activeReport.getStatus());
        onView(withId(R.id.admin_reportdetails_alt_status)).check(matches(withText(expected)));
    }

    @Test
    //Test if the logs display properly
    public void testLogClickExpand() {
        ViewInteraction cardView6 = onView(
                allOf(withId(R.id.admin_reportdetails_button_viewlog),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView5),
                                        0),
                                8)));
        cardView6.perform(scrollTo(), click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.log_recycler_view),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));


        onView(withId(R.id.log_recycler_view))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.log_alt_timestamp), isDisplayed())))));

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.log_recycler_view),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.log_recycler_view))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.log_alt_timestamp), not(isDisplayed()))))));
    }

    @Test
    //Edits the category and make sure the active/inactive views work and then on confirm that the proper info is updated
    //Only test the first item to see if it matches "Alcohol"
    public void testAddCategoryToReport() {
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.admin_reportdetails_button_editcategory),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                0)));
        appCompatImageView.perform(scrollTo(), click());

        //if the box is checked you've got to uncheck it
        ViewInteraction v = onView(withId(R.id.checkboxes_rv)).check(matches(
                withViewAtPosition(0, hasDescendant(allOf(withId(R.id.checkbox_button_active), isDisplayed())))));
        if(viewIsDisplayed(v)) {
            onView(allOf(withId(R.id.checkboxes_rv),
                    childAtPosition(
                            withClassName(is("android.widget.LinearLayout")),
                            1))).perform(actionOnItemAtPosition(0, click()));
        }

        //make sure the view is now inactive
        onView(withId(R.id.checkboxes_rv))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.checkbox_button_inactive), isDisplayed())))));

        //attempt to set the view active
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.checkboxes_rv),
                    childAtPosition(withClassName(is("android.widget.LinearLayout")),
                        1)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        //make sure it was set active
        onView(withId(R.id.checkboxes_rv))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.checkbox_button_active), isDisplayed())))));

        //confirm the view
        ViewInteraction cardView8 = onView(
                allOf(withId(R.id.checkboxes_button_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.constraint.ConstraintLayout")),
                                        0),
                                2),
                        isDisplayed()));
        cardView8.perform(click());

        //make sure it was added to the activeReport
        assertTrue(Client.activeReport.getCategories().contains("Abuse"));
        onView(withId(R.id.admin_reportdetails_container_categories))
                .check(matches(hasDescendant(withText("Abuse"))));
    }

    @Test
    //Test adding and removing a tag but not to report
    public void testAddThenRemoveTag() {
        String addString = "Jim";

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.admin_reportdetails_button_edittags),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                6)));
        appCompatImageView2.perform(scrollTo(), click());

        int rvSize = getCountFromRecyclerView(R.id.addremove_rv);

        onView(withId(R.id.addremove_input)).perform(clearText(), typeText(addString));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.addremove_button_add),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView10),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        int rvNewSize = getCountFromRecyclerView(R.id.addremove_rv);

        //new size should be 1 greater than old size
        assertEquals(rvSize,rvNewSize-1);

        //the last item's text should be what I added
        onView(withId(R.id.addremove_rv))
                .perform(RecyclerViewActions.scrollToPosition(rvNewSize - 1))
                .check(matches(atPositionOnView(rvNewSize - 1, withText(addString), R.id.addremove_alt_text)));

        //remove that view
        onView(withId(R.id.addremove_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(rvNewSize-1, MyViewAction.clickChildViewWithId(R.id.addremove_button_delete)));

        //now size should equal original size
        assertEquals(rvSize,getCountFromRecyclerView(R.id.addremove_rv));
    }

    @Test
    //Test adding a tag to a report
    public void addTagToReport() {
        String addString = "Jim";
        int timesStringExistInReport = Collections.frequency(Client.activeReport.getTags(), addString);

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.admin_reportdetails_button_edittags),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                6)));
        appCompatImageView2.perform(scrollTo(), click());

        int rvSize = getCountFromRecyclerView(R.id.addremove_rv);

        onView(withId(R.id.addremove_input)).perform(clearText(), typeText(addString));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.addremove_button_add),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView10),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        int rvNewSize = getCountFromRecyclerView(R.id.addremove_rv);

        //new size should be 1 greater than old size
        assertEquals(rvSize,rvNewSize-1);

        //the last item's text should be what I added
        onView(withId(R.id.addremove_rv))
                .perform(RecyclerViewActions.scrollToPosition(rvNewSize - 1))
                .check(matches(atPositionOnView(rvNewSize - 1, withText(addString), R.id.addremove_alt_text)));

        ViewInteraction cardView9 = onView(
                allOf(withId(R.id.addremove_button_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        cardView9.perform(click());

        //make sure it was added to the activeReport
        assertTrue(Client.activeReport.getTags().contains(addString));
        onView(withId(R.id.admin_reportdetails_container_tags))
                .check(matches(hasDescendant(withText(addString))));
        //there should be 1 more thing in view, handles case where the string might have already existed
        assertEquals(timesStringExistInReport + 1, Collections.frequency(Client.activeReport.getTags(), addString));
    }

    @Test
    //Test removing tag from report, it automatically fails if there wasn't one to remove
    public void RemoveTagFromReport() {
        String addString = "Jim";
        int timesStringExistInReport = Collections.frequency(Client.activeReport.getTags(), addString);

        //Open the edit tags
        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.admin_reportdetails_button_edittags),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                6)));
        appCompatImageView4.perform(scrollTo(), click());

        int rvSize = getCountFromRecyclerView(R.id.addremove_rv);

        //only run the test if there is a tag to remove
        if(rvSize > 0) {
            //remove that last view
            onView(withId(R.id.addremove_rv)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(rvSize - 1, MyViewAction.clickChildViewWithId(R.id.addremove_button_delete)));

            //now size should be one less than the original
            assertEquals(rvSize - 1, getCountFromRecyclerView(R.id.addremove_rv));

            ViewInteraction cardView10 = onView(
                    allOf(withId(R.id.addremove_button_confirm),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.v7.widget.CardView")),
                                            0),
                                    2),
                            isDisplayed()));
            cardView10.perform(click());

            //make sure the number of things in the report are now 1 less than before
            assertEquals(timesStringExistInReport - 1, Collections.frequency(Client.activeReport.getTags(), addString));
        }
        else
            Log.e("Testing", "There was no tag to remove");
    }


    @Test
    //Test adding then removing tag from report
    public void addThenRemoveTagFromReport() {
        String addString = "Jim";
        int timesStringExistInReport = Collections.frequency(Client.activeReport.getTags(), addString);

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.admin_reportdetails_button_edittags),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                6)));
        appCompatImageView2.perform(scrollTo(), click());

        int rvSize = getCountFromRecyclerView(R.id.addremove_rv);

        onView(withId(R.id.addremove_input)).perform(clearText(), typeText(addString));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.addremove_button_add),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView10),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        int rvNewSize = getCountFromRecyclerView(R.id.addremove_rv);

        //new size should be 1 greater than old size
        assertEquals(rvSize,rvNewSize-1);

        //the last item's text should be what I added
        onView(withId(R.id.addremove_rv))
                .perform(RecyclerViewActions.scrollToPosition(rvNewSize - 1))
                .check(matches(atPositionOnView(rvNewSize - 1, withText(addString), R.id.addremove_alt_text)));

        ViewInteraction cardView9 = onView(
                allOf(withId(R.id.addremove_button_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        cardView9.perform(click());

        //make sure it was added to the activeReport
        assertTrue(Client.activeReport.getTags().contains(addString));
        onView(withId(R.id.admin_reportdetails_container_tags))
                .check(matches(hasDescendant(withText(addString))));
        //there should be 1 more thing in view, handles case where the string might have already existed
        assertEquals(timesStringExistInReport + 1, Collections.frequency(Client.activeReport.getTags(), addString));


        //open the edit tags again
        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.admin_reportdetails_button_edittags),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                6)));
        appCompatImageView4.perform(scrollTo(), click());

        //remove that view
        onView(withId(R.id.addremove_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(rvNewSize-1, MyViewAction.clickChildViewWithId(R.id.addremove_button_delete)));

        //now size should equal original size
        assertEquals(rvSize,getCountFromRecyclerView(R.id.addremove_rv));

        ViewInteraction cardView10 = onView(
                allOf(withId(R.id.addremove_button_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        cardView10.perform(click());

        //make sure the number of things in the report are equal since you added and removed
        assertEquals(timesStringExistInReport, Collections.frequency(Client.activeReport.getTags(), addString));
    }

    @Test
    //Test adding notes to a report
    public void addNotesToReport() {
        String addString = "Test Notes";

        onView(allOf(withId(R.id.admin_reportdetails_button_addnotes),
                childAtPosition(
                        childAtPosition(
                                withClassName(is("android.support.v7.widget.CardView")),
                                0),
                        2))).perform(scrollTo());

        int rvSize = 0;

        if(!viewIsDisplayed(onView(withId(R.id.admin_reportdetails_notes_no))))
            rvSize = getCountFromRecyclerView(R.id.admin_reportdetails_notes_rv);

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.admin_reportdetails_button_addnotes),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2)));
        cardView3.perform(scrollTo(), click());

        onView(withId(R.id.notesdialog_alt_text)).perform(clearText(), typeText(addString));

        ViewInteraction cardView4 = onView(
                allOf(withId(R.id.notesdialog_button_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                3),
                        isDisplayed()));
        cardView4.perform(click());


        //last notes message body in the report should match
        assertTrue(Client.activeReport.getNotes().get(rvSize).getMessageBody().contains(addString));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.admin_reportdetails_button_addnotes)).perform(scrollTo());

        //the last item's text should be what I added
        onView(withId(R.id.admin_reportdetails_notes_rv))
                .perform(RecyclerViewActions.scrollToPosition(rvSize))
                .check(matches(atPositionOnView(rvSize, withText(addString), R.id.note_alt_text)));

        assertEquals(rvSize + 1, getCountFromRecyclerView(R.id.admin_reportdetails_notes_rv));
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

    public static Matcher<View> withViewAtPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static Matcher<View> atPositionOnView(final int position, final Matcher<View> itemMatcher, @NonNull final int targetViewId) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has view id " + itemMatcher + " at position " + position);
            }

            @Override
            public boolean matchesSafely(final RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                View targetView = viewHolder.itemView.findViewById(targetViewId);
                return itemMatcher.matches(targetView);
            }
        };
    }

    //You can pass this function a view and see if it exist
    public static boolean viewIsDisplayed(ViewInteraction v) {
        final boolean[] isDisplayed = {true};
        v.withFailureHandler((error, viewMatcher) ->
                isDisplayed[0] = false).check(matches(isDisplayed()));
        return isDisplayed[0];
    }

    public static int getCountFromRecyclerView(@IdRes int RecyclerViewId) {
        final int[] COUNT = {0};
        Matcher matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                COUNT[0] = ((RecyclerView) item).getAdapter().getItemCount();
                return true;
            }
            @Override
            public void describeTo(Description description) {}
        };
        onView(allOf(withId(RecyclerViewId),isDisplayed())).check(matches(matcher));
        return COUNT[0];
    }

    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }
}
