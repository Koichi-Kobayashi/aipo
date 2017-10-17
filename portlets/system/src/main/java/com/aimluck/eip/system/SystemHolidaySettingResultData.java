/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2017 TOWN, Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.system;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class SystemHolidaySettingResultData implements ALData {

  /** HOLIDAY_ID */
  private ALNumberField holiday_id;

  /** 個別の休日の日付 */
  private ALDateTimeField holiday_date;

  /** 個別の休日の名前 */
  private ALStringField holiday_name;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /**
   *
   */
  @Override
  public void initField() {
    holiday_id = new ALNumberField();
    holiday_date = new ALDateTimeField();
    holiday_name = new ALStringField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();
  }

  /**
   * + * @return +
   */
  public ALNumberField getHolidayId() {
    return holiday_id;
  }

  /**
   * @param i
   */
  public void setHolidayId(long i) {
    holiday_id.setValue(i);
  }

  /**
   * @return
   */
  public String getHolidayName() {
    return ALCommonUtils.replaceToAutoCR(holiday_name.toString());

  }

  /**
   * @param string
   */
  public void setHolidayName(String string) {
    holiday_name.setValue(string);
  }

  /**
   * @return
   */
  public ALDateTimeField getHolidayDate() {
    return holiday_date;
  }

  /**
   * @return
   */
  public String getHolidayDateString() {
    return holiday_date.getYear()
      + "年"
      + holiday_date.getMonth()
      + "月"
      + holiday_date.getDay()
      + "日";
  }

  /**
   * @param date
   */
  public void setHolidayDate(Date date) {
    holiday_date.setValue(date);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

}
