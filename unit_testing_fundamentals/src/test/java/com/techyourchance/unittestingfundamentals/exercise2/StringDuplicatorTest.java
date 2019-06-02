package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    private StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String result = SUT.duplicate("");
        assertThat(result, is(""));
    }

    @Test
    public void duplicate_emptyCharacter_twoEmptyCharactersReturned() {
        String result = SUT.duplicate(" ");
        assertThat(result, is("  "));
    }

    @Test
    public void duplicate_shortString_doubleTheStringReturned() {
        String result = SUT.duplicate("abc");
        assertThat(result, is("abcabc"));
    }

    @Test
    public void duplicate_longString_doubleTheStringReturned() {
        String result = SUT.duplicate("Carlo Trajano");
        assertThat(result, is("Carlo TrajanoCarlo Trajano"));
    }
}