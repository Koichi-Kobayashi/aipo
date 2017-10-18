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

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class SystemHolidaySettingSelectData extends
    ALAbstractSelectData<EipMHoliday, EipMHoliday> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemHolidaySettingSelectData.class.getName());

  /** 個別の休日の日付 */
  private ALDateField holiday_date;

  /** <code>viewMonth</code> 現在の月 */
  private ALDateTimeField viewYear;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevYear;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextYear;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException {
    Calendar cal = Calendar.getInstance();
    holiday_date = new ALDateField();
    holiday_date.setValue(cal.getTime());

    // 現在の月
    viewYear = new ALDateTimeField("yyyy");
    viewYear.setNotNull(true);
    viewYear.setValue(cal.getTime());
    if (!viewYear.validate(new ArrayList<String>())) {
      ALEipUtils.removeTemp(rundata, context, "view_month");
      throw new ALPageNotFoundException();
    }
    // 前の月
    prevYear = new ALDateTimeField("yyyy");
    // 次の月
    nextYear = new ALDateTimeField("yyyy");
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipMHoliday> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    SelectQuery<EipMHoliday> query = Database.query(EipMHoliday.class);
    buildSelectQueryForFilter(query, rundata, context);
    buildSelectQueryForListView(query);
    buildSelectQueryForListViewSort(query, rundata, context);

    ResultList<EipMHoliday> list = query.getResultList();
    if (list.isEmpty()) {
      return null;
    } else {
      return list;
    }

  }

  /**
   * @param record
   * @return
   */
  @Override
  protected Object getResultData(EipMHoliday record) {
    try {
      SystemHolidaySettingResultData rd = new SystemHolidaySettingResultData();
      rd.initField();
      rd.setHolidayId(record.getHolidayId().intValue());
      rd.setHolidayName(record.getHolidayName());
      rd.setHolidayDate(record.getHolidayDate());
      return rd;
    } catch (Exception ex) {
      logger.error("system", ex);
      return null;
    }
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipMHoliday selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipMHoliday obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected java.util.jar.Attributes getColumnMap() {
    return null;
  }

  public ALDateField getHolidayDate() {
    return holiday_date;
  }

  public String getHolidayViewYear() {
    return viewYear.getYear();
  }

}
