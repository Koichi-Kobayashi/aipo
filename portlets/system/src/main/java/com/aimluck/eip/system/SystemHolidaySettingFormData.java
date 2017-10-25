/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.config.ALConfigHandler;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class SystemHolidaySettingFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemHolidaySettingFormData.class.getName());

  private ALStringField week1;

  private ALStringField week2;

  private ALStringField week3;

  private ALStringField week4;

  private ALStringField week5;

  private ALStringField week6;

  private ALStringField week7;

  private ALStringField statutoryHoliday;

  private ALStringField holiday;

  /** 個別の休日の日付 */
  private ALDateField p_holiday;

  /** 個別の休日の名前 */
  private ALStringField p_holiday_name;

  /** <code>viewMonth</code> 現在の月 */
  private ALDateTimeField viewYear;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevYear;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextYear;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   *
   */
  @Override
  public void initField() {
    week1 = new ALStringField();
    week1.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK1"));
    week2 = new ALStringField();
    week2.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK2"));
    week3 = new ALStringField();
    week3.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK3"));
    week4 = new ALStringField();
    week4.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK4"));
    week5 = new ALStringField();
    week5.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK5"));
    week6 = new ALStringField();
    week6.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK6"));
    week7 = new ALStringField();
    week7.setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_WEEK7"));
    statutoryHoliday = new ALStringField();
    statutoryHoliday.setFieldName(ALLocalizationUtils
      .getl10n("HOLIDAY_SETTING_STATUTORY_HOLIDAY"));
    holiday = new ALStringField();
    holiday
      .setFieldName(ALLocalizationUtils.getl10n("HOLIDAY_SETTING_HOLIDAY"));
    // 個別の休日の日付
    p_holiday = new ALDateField();
    p_holiday.setFieldName(ALLocalizationUtils
      .getl10n("HOLIDAY_SETTING_PERSONAL_HOLIDAY"));
    p_holiday.setValue(new Date());
    // 個別の休日の名前
    p_holiday_name = new ALStringField();
    p_holiday_name.setFieldName(ALLocalizationUtils
      .getl10n("HOLIDAY_SETTING_PERSONAL_HOLIDAY"));
    p_holiday_name.setTrim(true);

    // 現在の月
    viewYear = new ALDateTimeField("yyyy");
    viewYear.setNotNull(true);
    // 前の月
    prevYear = new ALDateTimeField("yyyy");
    // 次の月
    nextYear = new ALDateTimeField("yyyy");

  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);

    return res;
  }

  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    p_holiday.setNotNull(true);
    p_holiday_name.setNotNull(true);
    p_holiday_name.limitMaxLength(50);
  }

  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    // 個別の休日の日付
    p_holiday.validate(msgList);
    // 個別の日付の名前
    p_holiday_name.validate(msgList);

    return (msgList.size() == 0);
  }

  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      String holidayOfWeek =
        ALConfigService.get(ALConfigHandler.Property.HOLIDAY_OF_WEEK);
      week1.setValue(holidayOfWeek.charAt(0) != '0' ? "1" : null);
      week2.setValue(holidayOfWeek.charAt(1) != '0' ? "1" : null);
      week3.setValue(holidayOfWeek.charAt(2) != '0' ? "1" : null);
      week4.setValue(holidayOfWeek.charAt(3) != '0' ? "1" : null);
      week5.setValue(holidayOfWeek.charAt(4) != '0' ? "1" : null);
      week6.setValue(holidayOfWeek.charAt(5) != '0' ? "1" : null);
      week7.setValue(holidayOfWeek.charAt(6) != '0' ? "1" : null);
      statutoryHoliday.setValue(String.valueOf(holidayOfWeek.charAt(7)));
      holiday.setValue(holidayOfWeek.charAt(8) != '0' ? "1" : null);

    } catch (Exception ex) {
      logger.error("SystemHolidaySettingFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 個別の休日をデータベースに格納します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipMHoliday p_holiday_data = Database.create(EipMHoliday.class);
      // 個別の休日の日付
      p_holiday_data.setHolidayDate(p_holiday.getValue().getDate());
      // 個別の休日の名前
      p_holiday_data.setHolidayName(p_holiday_name.getValue());
      // 作成日
      p_holiday_data.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      p_holiday_data.setUpdateDate(Calendar.getInstance().getTime());

      // 個別の休日を登録
      Database.commit();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("system", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されている個別の休日を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // EipMHoliday p_holiday_data = SystemUtils.getEipMHoliday(rundata,
      // context);
      String value = statutoryHoliday.getValue();
      if ("1".equals(value)) {
        week1.setValue("1");
      }
      if ("2".equals(value)) {
        week2.setValue("1");
      }
      if ("3".equals(value)) {
        week3.setValue("1");
      }
      if ("4".equals(value)) {
        week4.setValue("1");
      }
      if ("5".equals(value)) {
        week5.setValue("1");
      }
      if ("6".equals(value)) {
        week6.setValue("1");
      }
      if ("7".equals(value)) {
        week7.setValue("1");
      }
      StringBuilder b = new StringBuilder();
      b.append("1".equals(week1.getValue()) ? "1" : "0");
      b.append("1".equals(week2.getValue()) ? "1" : "0");
      b.append("1".equals(week3.getValue()) ? "1" : "0");
      b.append("1".equals(week4.getValue()) ? "1" : "0");
      b.append("1".equals(week5.getValue()) ? "1" : "0");
      b.append("1".equals(week6.getValue()) ? "1" : "0");
      b.append("1".equals(week7.getValue()) ? "1" : "0");
      b.append(statutoryHoliday.getValue());
      b.append("1".equals(holiday.getValue()) ? "1" : "0");
      ALConfigService.put(ALConfigHandler.Property.HOLIDAY_OF_WEEK, b
        .toString());

      // // 個別の休日の日付
      // p_holiday_data.setHolidayDate(p_holiday.getValue().getDate());
      // // 個別の休日の名前
      // p_holiday_data.setHolidayName(p_holiday_name.getValue());
      // // 作成日
      // p_holiday_data.setCreateDate(Calendar.getInstance().getTime());
      // // 更新日
      // p_holiday_data.setUpdateDate(Calendar.getInstance().getTime());
      //
      // // 個別の休日を登録
      // Database.commit();

    } catch (Exception ex) {
      logger.error("SystemHolidaySettingFormData", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    // // オブジェクトモデルを取得
    // EipMHoliday p_holiday_data = SystemUtils.getEipTTodo(rundata, context,
    // false);
    // if (p_holiday_data == null) {
    // return false;
    // }
    //
    // // entityIdの取得
    // int entityId = p_holiday_data.getHolidayId();
    // // タイトルの取得
    // String todoName = p_holiday_data.getHolidayName();
    //
    // // Todoを削除
    // Database.delete(p_holiday_data);
    // Database.commit();

    return false;
  }

  /**
   * 詳細データを取得する抽象メソッドです。
   *
   * @param rundata
   * @param context
   * @return
   */
  protected Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  public ALStringField getWeek1() {
    return week1;
  }

  public ALStringField getWeek2() {
    return week2;
  }

  public ALStringField getWeek3() {
    return week3;
  }

  public ALStringField getWeek4() {
    return week4;
  }

  public ALStringField getWeek5() {
    return week5;
  }

  public ALStringField getWeek6() {
    return week6;
  }

  public ALStringField getWeek7() {
    return week7;
  }

  public ALStringField getStatutoryHoliday() {
    return statutoryHoliday;
  }

  public ALStringField getHoliday() {
    return holiday;
  }

  public ALDateField getPersonalHoliday() {
    return p_holiday;
  }

  public ALStringField getPersonalHolidayName() {
    return p_holiday_name;
  }

}