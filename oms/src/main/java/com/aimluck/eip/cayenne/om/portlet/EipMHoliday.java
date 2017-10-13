package com.aimluck.eip.cayenne.om.portlet;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMHoliday;

public class EipMHoliday extends _EipMHoliday {

  public static final String HOLIDAY_NAME_PROPERTY = "holidayName";

  public Integer getHolidayId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(HOLIDAY_ID_PK_COLUMN);
      if (obj instanceof Long) {
        Long value = (Long) obj;
        return Integer.valueOf(value.intValue());
      } else {
        return (Integer) obj;
      }
    } else {
      return null;
    }
  }

  public void setHolidayId(String id) {
    setObjectId(new ObjectId("EipMHoliday", HOLIDAY_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

  public String getHolidayDateString() {
    String year = null;
    String month = null;
    String day = null;

    Calendar calendar = new GregorianCalendar();
    calendar.setTime(getHolidayDate());
    year = Integer.toString(calendar.get(Calendar.YEAR));
    month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
    day = Integer.toString(calendar.get(Calendar.DATE));

    return year + "年" + month + "月" + day + "日";
  }

}
