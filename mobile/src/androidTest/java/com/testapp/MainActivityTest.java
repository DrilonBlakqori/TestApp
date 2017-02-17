package com.testapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.TextView;

import com.testapp.activities.MainActivity;
import com.testapp.adapters.recycler.HistoricalRatesAdapter.HistoricalViewHolder;
import com.testapp.adapters.recycler.MainRateAdapter.RateViewHolder;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest  {

	@Rule
	public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void recyclerAndSwipeRefreshVisible() {
		onView(withId(R.id.swipeRefresh)).check(ViewAssertions.matches(isDisplayed()));
		onView(withId(R.id.recyclerView)).check(ViewAssertions.matches(isDisplayed()));
	}

	@Test
	public void twoWeeksAgoRateExists() {
		onView(withId(R.id.recyclerView))
				.perform(RecyclerViewActions.actionOnHolderItem(withUnitedStatesDollarText(), ViewActions.click()));
		onView(withId(R.id.recyclerView))
				.perform(RecyclerViewActions.scrollToHolder(withTwoWeeksAgoDate()));
	}

	public static Matcher<RecyclerView.ViewHolder> withUnitedStatesDollarText() {
		return new BoundedMatcher<ViewHolder, RateViewHolder>(RateViewHolder.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("No ViewHolder found with id = currencyText & text.length > 0");
			}

			@Override
			protected boolean matchesSafely(RateViewHolder item) {
				TextView currencyText = (TextView) item.itemView.findViewById(R.id.currencyText);
				return currencyText != null && currencyText.getText().toString().equals("United States Dollar");
			}
		};
	}

	public static Matcher<RecyclerView.ViewHolder> withTwoWeeksAgoDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, -14);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		final String twoWeeksAgo = "Date: " + simpleDateFormat.format(calendar.getTime());
		return new BoundedMatcher<ViewHolder, HistoricalViewHolder>(HistoricalViewHolder.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("No ViewHolder found with text = " + twoWeeksAgo);
			}

			@Override
			protected boolean matchesSafely(HistoricalViewHolder item) {
				TextView dateText = (TextView) item.itemView.findViewById(R.id.dateText);
				return dateText != null && dateText.getText().toString().equals(twoWeeksAgo);
			}
		};
	}
}
