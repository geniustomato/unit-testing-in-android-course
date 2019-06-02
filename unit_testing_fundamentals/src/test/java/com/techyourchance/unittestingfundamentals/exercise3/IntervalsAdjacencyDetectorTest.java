package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    private IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();

    }

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(1, 5);
        Interval interval2 = new Interval(7, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(11, 15);
        Interval interval2 = new Interval(7, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1WithinInterval2_falseReturned() {
        Interval interval1 = new Interval(6, 9);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1WithinAndStartsTheSameAsInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 9);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1WithinAndEndsTheSameAsInterval2_falseReturned() {
        Interval interval1 = new Interval(6, 10);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1TheSameAsInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1AdjacentToStartOfInterval2_trueReturned() {
        Interval interval1 = new Interval(1, 5);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1AdjacentToEndOfInterval2_trueReturned() {
        Interval interval1 = new Interval(10, 15);
        Interval interval2 = new Interval(5, 10);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_negativeInterval1AdjacentToPositiveInterval2_trueReturned() {
        Interval interval1 = new Interval(-5, -1);
        Interval interval2 = new Interval(-1, 5);

        boolean result = SUT.isAdjacent(interval1, interval2);

        assertThat(result, is(true));
    }
}