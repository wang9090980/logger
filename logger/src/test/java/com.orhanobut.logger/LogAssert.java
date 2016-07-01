package com.orhanobut.logger;

import org.robolectric.shadows.ShadowLog;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

final class LogAssert {
  private static final String DEFAULT_TAG = "PRETTYLOGGER";

  private static final char TOP_LEFT_CORNER = '╔';
  private static final char BOTTOM_LEFT_CORNER = '╚';
  private static final char MIDDLE_CORNER = '╟';
  private static final char HORIZONTAL_DOUBLE_LINE = '║';
  private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
  private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
  private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

  private final List<ShadowLog.LogItem> items;
  private final int priority;

  private String tag;

  private int index = 0;

  LogAssert(List<ShadowLog.LogItem> items, String tag, int priority) {
    this.items = items;
    this.tag = tag == null ? DEFAULT_TAG : tag;
    this.priority = priority;
  }

  public LogAssert hasTopBorder() {
    return hasLog(priority, tag, TOP_BORDER);
  }

  public LogAssert hasBottomBorder() {
    return hasLog(priority, tag, BOTTOM_BORDER);
  }

  public LogAssert hasMiddleBorder() {
    return hasLog(priority, tag, MIDDLE_BORDER);
  }

  public LogAssert hasThread(String threadName) {
    return hasLog(priority, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + threadName);
  }

  public LogAssert hasMethodInfo(String methodInfo) {
    return hasLog(priority, tag, HORIZONTAL_DOUBLE_LINE + " " + methodInfo);
  }

  public LogAssert hasMessage(String message) {
    return hasLog(priority, tag, HORIZONTAL_DOUBLE_LINE + " " + message);
  }

  private LogAssert hasLog(int priority, String tag, String message) {
    ShadowLog.LogItem item = items.get(index++);
    assertThat(item.type).isEqualTo(priority);
    assertThat(item.tag).isEqualTo(tag);
    assertThat(item.msg).isEqualTo(message);
    return this;
  }

  private LogAssert matchLog(int priority, String tag, String message) {
    ShadowLog.LogItem item = items.get(index++);
    assertThat(item.type).isEqualTo(priority);
    assertThat(item.tag).isEqualTo(tag);

    int header_end = message.indexOf("|");
    int msg_start = message.indexOf("|", header_end + 1);
    int header_end2 = item.msg.indexOf("|");
    int msg_start2 = item.msg.indexOf("|", header_end + 1);
    assertThat(item.msg.substring(0, header_end2)).isEqualTo(message.substring(0, header_end));
    assertThat(item.msg.substring(header_end2 + msg_start2)).isEqualTo(message.substring(header_end + msg_start));
    return this;
  }

  public LogAssert skip() {
    index++;
    return this;
  }

  public LogAssert defaultTag() {
    tag = DEFAULT_TAG;
    return this;
  }

  public LogAssert hasTag(String tag) {
    assertThat(tag).isEqualTo(this.tag);
    return this;
  }

  public void hasNoMoreMessages() {
    assertThat(items).hasSize(index);
    ShadowLog.getLogs().clear();
  }

  public LogAssert hasMessageWithDefaultSettings(String... messages) {
    hasTopBorder();
    skip();
    hasMiddleBorder();
    skip();
    skip();
    hasMiddleBorder();

    for (String message : messages) {
      hasMessage(message);
    }

    hasBottomBorder();
    hasNoMoreMessages();

    return this;
  }

  public LogAssert hasShortThread(String threadName) {
    return hasLog(priority, tag, TOP_LEFT_CORNER + " Thread: " + threadName + " " + DOUBLE_DIVIDER);
  }

  public LogAssert hasMessageWithShortSettings(String... messages) {
    skip();
    skip();
    skip();

    for (String message : messages) {
      hasMessage(message);
    }

    hasNoMoreMessages();

    return this;
  }

  public LogAssert hasSingleMessage(long threadId, String message) {
    String header = HORIZONTAL_DOUBLE_LINE + Long.toString(threadId) + "\t|";
    header += "SingleLoggerTest.java" + "\t| ";
    return matchLog(priority, tag, header + message);
  }

  public LogAssert hasMessageWithSingleSettings(long threadId, String... messages) {
    for (String message : messages) {
      hasSingleMessage(threadId, message);
    }

    hasNoMoreMessages();

    return this;
  }
}
