package de.mcstangl.projectplanner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Date;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class DateServiceTest {

    @ParameterizedTest
    @MethodSource("getArgumentsForCalculateBusinessDaysTest")
    public void calculateBusinessDays(Date startDate, int daysToAdd, int expected){
        // Given
        DateService dateService = new DateService();
        Date date = Date.valueOf("2021-08-30");

        // When
        Date actual = dateService.addBusinessDays(startDate, daysToAdd);

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actual);
        // Then

        assertThat(actualCalendar.get(Calendar.DAY_OF_WEEK), is(expected));

    }

    private static Stream<Arguments> getArgumentsForCalculateBusinessDaysTest(){
        return Stream.of(
                Arguments.of(Date.valueOf("2021-08-30"), 1, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 2, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 3, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 4, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 5, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 6, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 7, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 8, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-08-30"), 9, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-08-30"),10, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 1, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 2, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 3, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 4, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 5, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 6, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 7, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 8, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-08-31"), 9, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-08-31"),10, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 1, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 2, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 3, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 4, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 5, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 6, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 7, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 8, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-01"), 9, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-01"),10, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 1, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 2, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 3, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 4, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 5, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 6, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 7, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 8, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-02"), 9, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-02"),10, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 1, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 2, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 3, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 4, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 5, Calendar.FRIDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 6, Calendar.MONDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 7, Calendar.TUESDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 8, Calendar.WEDNESDAY),
                Arguments.of(Date.valueOf("2021-09-03"), 9, Calendar.THURSDAY),
                Arguments.of(Date.valueOf("2021-09-03"),10, Calendar.FRIDAY)
        );
    }
}