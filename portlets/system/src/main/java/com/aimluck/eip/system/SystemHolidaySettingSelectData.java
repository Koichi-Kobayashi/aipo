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

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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

  /** 現在のページの年 */
  private ALDateTimeField viewYear;

  /** 前の年 */
  private ALDateTimeField prevYear;

  /** 次の年 */
  private ALDateTimeField nextYear;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException {

    Calendar cal = Calendar.getInstance();
    // 前の年　次の年
    prevYear = new ALDateTimeField("yyyy");
    nextYear = new ALDateTimeField("yyyy");
    holiday_date = new ALDateField();
    holiday_date.setValue(cal.getTime());
    // 現在のページの年
    viewYear = new ALDateTimeField("yyyy");
    viewYear.setNotNull(true);

    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_year")) {
        ALEipUtils.setTemp(rundata, context, "view_year", rundata
          .getParameters()
          .getString("view_year"));
      }
    }
    String tmpViewYear = ALEipUtils.getTemp(rundata, context, "view_year");
    if (tmpViewYear == null || tmpViewYear.equals("")) {
      viewYear.setValue(cal.getTime());
    } else {
      viewYear.setValue(tmpViewYear);
      if (!viewYear.validate(new ArrayList<String>())) {
        ALEipUtils.removeTemp(rundata, context, "view_year");
        throw new ALPageNotFoundException();
      }
    }

    // 前の年　次の年
    prevYear = new ALDateTimeField("yyyy");
    nextYear = new ALDateTimeField("yyyy");

    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(viewYear.getValue());
    cal1.add(Calendar.YEAR, 1);
    nextYear.setValue(cal1.getTime());
    cal1.add(Calendar.YEAR, -2);
    prevYear.setValue(cal1.getTime());

  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMHoliday> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMHoliday> query = Database.query(EipMHoliday.class);
    Expression exp1 =
      ExpressionFactory.greaterOrEqualExp(
        EipMHoliday.HOLIDAY_DATE_PROPERTY,
        viewYear.getYear() + "-01-01");
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.lessOrEqualExp(
        EipMHoliday.HOLIDAY_DATE_PROPERTY,
        viewYear.getYear() + "-12-31");
    query.andQualifier(exp2);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipMHoliday> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      SelectQuery<EipMHoliday> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      query.orderAscending(EipMHoliday.HOLIDAY_DATE_PROPERTY);
      return query.getResultList();
    } catch (Exception ex) {
      logger.error("system", ex);
      return null;
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

  /**
   * 前の年を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewYear() {
    return viewYear;
  }

  /**
   * 前の年を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevYear() {
    return prevYear;
  }

  /**
   * 次の年を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextYear() {
    return nextYear;
  }

}
