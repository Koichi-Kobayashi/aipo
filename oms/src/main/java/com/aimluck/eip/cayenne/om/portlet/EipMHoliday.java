package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMHoliday;

public class EipMHoliday extends _EipMHoliday {

  public static final String HOLIDAY_NAME_PROPERTY = "holidayName";

  public Integer getFacilityId() {
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

  public void setFacilityId(String id) {
    setObjectId(
      new ObjectId("EipMHoliday", HOLIDAY_ID_PK_COLUMN, Integer.valueOf(id)));
  }

}
