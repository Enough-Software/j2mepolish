package com.nutiteq.components;

//TODO jaanus : check for alternative
/**
 * Generic duration object containing days, hours, minutes and seconds.
 */
public class DurationTime {
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;

  public DurationTime(final int days, final int hours, final int minutes, final int seconds) {
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
  }

  public DurationTime() {
    this(0, 0, 0, 0);
  }

  public int getDays() {
    return days;
  }

  public int getHours() {
    return hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getSeconds() {
    return seconds;
  }
}
