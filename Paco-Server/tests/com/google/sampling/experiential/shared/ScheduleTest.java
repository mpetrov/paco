// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.sampling.experiential.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

/**
 * @author corycornelius@google.com (Cory Cornelius)
 *
 */
public class ScheduleTest {
  private class ScheduleImpl extends Schedule {
    public ScheduleImpl(String type) {
      super(type);
    }
  }

  @Test
  public void testEquality() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testInequality() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.WEEKLY);

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testEqualityWhenStartDateSet() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setStartDate(new Date(3));
    schedule2.setStartDate(new Date(3));

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testEqualityWhenStartDateSetNull() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setStartDate(null);
    schedule2.setStartDate(null);

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testInequalityWhenStartDateSet() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setStartDate(new Date(3));
    schedule2.setStartDate(new Date(4));

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testInequalityWhenStartDateSetNull() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setStartDate(null);
    schedule2.setStartDate(new Date(4));

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testEqualityWhenEndDateSet() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEndDate(new Date(3));
    schedule2.setEndDate(new Date(3));

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testEqualityWhenEndDateSetNull() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEndDate(null);
    schedule2.setEndDate(null);

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testInequalityWhenEndDateSet() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEndDate(new Date(3));
    schedule2.setEndDate(new Date(4));

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testInequalityWhenEndDateSetNull() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEndDate(null);
    schedule2.setEndDate(new Date(4));

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testEqualityWhenEditableSet() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEditable(true);
    schedule2.setEditable(true);

    assertTrue(schedule1.equals(schedule2));
  }

  @Test
  public void testInequalityWhenEditableSetFalse() {
    ScheduleImpl schedule1 = new ScheduleImpl(Schedule.DAILY);
    ScheduleImpl schedule2 = new ScheduleImpl(Schedule.DAILY);

    schedule1.setEditable(false);
    schedule1.setEditable(true);

    assertFalse(schedule1.equals(schedule2));
  }

  @Test
  public void testStartDateIsNullable() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setStartDate(null);

    assertNull(schedule.getStartDate());
  }

  @Test
  public void testEndDateIsNullable() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setEndDate(null);

    assertNull(schedule.getEndDate());
  }

  @Test
  public void testHasStartDate() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setStartDate(new Date(3));

    assertTrue(schedule.hasStartDate());
  }

  @Test
  public void testHasEndDate() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setEndDate(new Date(3));

    assertTrue(schedule.hasEndDate());
  }

  @Test
  public void testHasStartDateWhenNull() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setStartDate(null);

    assertFalse(schedule.hasStartDate());
  }

  @Test
  public void testHasEndDateWhenNull() {
    ScheduleImpl schedule = new ScheduleImpl(Schedule.DAILY);

    schedule.setEndDate(null);

    assertFalse(schedule.hasEndDate());
  }
}