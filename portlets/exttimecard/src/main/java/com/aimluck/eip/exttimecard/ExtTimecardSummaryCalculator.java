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
package com.aimluck.eip.exttimecard;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;

public class ExtTimecardSummaryCalculator {

  private EipTExtTimecardSystem timecardSystem;

  // FIXME: GroupByで利用する情報なのでとりあえず作ったが、そのへんの計算式を検証した上で必要に応じて修正する。
  private int viewYear;

  // FIXME: GroupByで利用する情報なのでとりあえず作ったが、そのへんの計算式を検証した上で必要に応じて修正する。
  private int viewMonth;

  private boolean _isNewRule = false;

  public ExtTimecardSummaryResultData summarize(
      List<ExtTimecardResultData> timecards) {

    /** user毎のstartDayの更新 */
    int startDay = getTimecardSystem().getStartDay();

    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(getDateByViewMonth(startDay));
    if (cal1.get(Calendar.DAY_OF_WEEK) > 1) {
      cal1.add(Calendar.DATE, -(cal1.get(Calendar.DAY_OF_WEEK) - 1));
    }
    Date queryStartDate = cal1.getTime();

    ExtTimecardListResultDataContainer container =
      ExtTimecardUtils.groupByWeek(
        queryStartDate,
        timecards,
        getTimecardSystem());
    container.calculateWeekOvertime();

    ExtTimecardSummaryResultData result = new ExtTimecardSummaryResultData();

    int total_work_day = 0, work_day = 0, overtime_day = 0, off_day = 0,
        official_off_day = 0, statutory_off_day = 0;
    /** 就業、残業、休出日数 */
    float total_work_hour = 0, work_hour = 0, overtime_hour = 0, off_hour = 0;
    /** みなし外残業時間 */
    float considered_overtime_outside_hour = 0,
        considered_overtime_within_statutory_outside_hour = 0;
    /** 就業、残業、休出時間 */
    int late_coming_day = 0, early_leaving_day = 0, absent_day = 0;
    /** 遅刻、早退、欠勤 */
    int paid_holiday = 0, compensatory_holiday = 0;
    /** 有休、代休 */
    int other_day = 0, no_input = 0;

    /** 遅刻、早退、休憩時間 */
    float late_coming_hour = 0, early_leaving_hour = 0, rest_hour = 0;
    /** 法定内残業時間 */
    float overtime_within_statutory_working_hour = 0;
    /** 所定内深夜業務時間、深夜残業時間 */
    float midnight_work_hour = 0, midnight_overtime_hour = 0,
        total_midnight_work_hour = 0;

    /** 所定休日所定内出勤時間、所定休日所定内深夜出勤時間、所定休日法定内残業時間、所定休日残業時間、所定休日深夜残業時間 */
    float off_day_regular_work_hour = 0f, off_day_regular_midnight_work_hour =
      0f, off_day_within_statutory_overtime_hour = 0f, off_day_overtime_hour =
        0f, off_day_midnight_work_hour = 0f, total_official_off_hour = 0f;
    /** 法定休日所定内出勤時間、法定休日所定内深夜出勤時間、法定休日法定内残業時間、法定休日残業時間、法定休日深夜残業時間 */
    float statutory_off_day_regular_work_hour = 0f,
        statutory_off_day_regular_midnight_work_hour = 0f,
        statutory_off_day_within_statutory_overtime_hour = 0f,
        statutory_off_day_overtime_hour = 0f,
        statutory_off_day_midnight_work_hour = 0f, total_statutory_off_hour =
          0f;

    /** その他、未入力 */
    result.initField();

    for (ExtTimecardResultData timecard : timecards) {
      if (timecard.isCurrentMonth()) {
        ExtTimecardListResultData lrd = new ExtTimecardListResultData();
        lrd.initField();
        lrd.setDate(timecard.getPunchDate().getValue());
        lrd.setRd(timecard);
        lrd.setTimecardSystem(timecardSystem);
        lrd.setNewRule(isNewRule());
        lrd.setWeekOvertime(container.getWeekOvertime(lrd));
        lrd.setStatutoryHoliday(container.isStatutoryOffDay(lrd));
        lrd.calculateWeekOvertime();

        String type = timecard.getType().getValue();
        if (type.equals(EipTExtTimecard.TYPE_WORK)) {
          if (lrd.getTotalWorkHour() != ExtTimecardListResultData.NO_DATA) {
            total_work_day++;
            total_work_hour += lrd.getTotalWorkHour();
          }
          /** 出勤 */
          if (lrd.getWorkHour() > 0) {
            work_day++;
            work_hour += lrd.getWorkHour();
          }
          /** 残業（平日） */
          if (lrd.getOvertimeHourWithoutRestHour() > 0) {
            overtime_day++;
            overtime_hour += lrd.getOvertimeHourWithoutRestHour();
          }
          /** 法定内残業 */
          if (lrd
            .getWithinStatutoryOvertimeWorkHourWithoutOffday() != ExtTimecardListResultData.NO_DATA) {
            overtime_within_statutory_working_hour +=
              lrd.getWithinStatutoryOvertimeWorkHourWithoutOffday();
          }
          /** 休出 */
          if (lrd.getOffHour() != ExtTimecardListResultData.NO_DATA) {
            off_day++;
            off_hour += lrd.getOffHour();
            // 所定内休日、法定内休日に振り分け
            if (container.isStatutoryOffDay(lrd)) {
              statutory_off_day++;
              total_statutory_off_hour += lrd.getTotalStatutoryOffHour();
              statutory_off_day_regular_work_hour += lrd.getInworkHour();
              statutory_off_day_overtime_hour += lrd.getOvertimeHour();
              statutory_off_day_midnight_work_hour +=
                lrd.getMidnightOvertimeWorkHour();
              statutory_off_day_regular_midnight_work_hour +=
                lrd.getMidnightRegularWorkHour();
              if (lrd
                .getWithinStatutoryOvertimeWorkHour() != ExtTimecardListResultData.NO_DATA) {
                statutory_off_day_within_statutory_overtime_hour +=
                  lrd.getWithinStatutoryOvertimeWorkHour();
              }
            } else {
              official_off_day++;
              total_official_off_hour += lrd.getTotalOfficialOffHour();
              off_day_regular_work_hour += lrd.getInworkHour();
              off_day_overtime_hour += lrd.getOvertimeHour();
              off_day_midnight_work_hour += lrd.getMidnightOvertimeWorkHour();
              off_day_regular_midnight_work_hour +=
                lrd.getMidnightRegularWorkHour();
              if (lrd
                .getWithinStatutoryOvertimeWorkHour() != ExtTimecardListResultData.NO_DATA) {
                off_day_within_statutory_overtime_hour +=
                  lrd.getWithinStatutoryOvertimeWorkHour();
              }
            }
          } else {
            /** 深夜勤務（平日） */
            if (lrd
              .getMidnightRegularWorkHour() != ExtTimecardListResultData.NO_DATA) {
              midnight_work_hour += lrd.getMidnightRegularWorkHour();
            }
            if (lrd
              .getMidnightOvertimeWorkHour() != ExtTimecardListResultData.NO_DATA) {
              midnight_overtime_hour += lrd.getMidnightOvertimeWorkHour();
            }
          }
          /** 深夜時間 */
          if (lrd.getMidnightWorkHour() != ExtTimecardListResultData.NO_DATA) {
            total_midnight_work_hour += lrd.getMidnightWorkHour();
          }
          /** 遅刻 */
          if (lrd.isLateComing()) {
            late_coming_day++;
            late_coming_hour += lrd.getLateComingHour();
          }
          /** 早退 */
          if (lrd.isEarlyLeaving()) {
            early_leaving_day++;
            early_leaving_hour += lrd.getEarlyLeavingHour();
          }
          /** 休憩時間 */
          if (lrd.getRestHour() != ExtTimecardListResultData.NO_DATA) {
            rest_hour += lrd.getRestHour();
          }
        } else if (type.equals(EipTExtTimecard.TYPE_ABSENT)) {
          /** 欠勤 */
          absent_day++;
        } else if (type.equals(EipTExtTimecard.TYPE_HOLIDAY)) {
          /** 有休 */
          paid_holiday++;
        } else if (type.equals(EipTExtTimecard.TYPE_COMPENSATORY)) {
          /** 代休 */
          compensatory_holiday++;
        } else if (type.equals(EipTExtTimecard.TYPE_ETC)) {
          /** その他 */
          other_day++;
        }
      }
    }

    /** 未入力数を月日数からデータ数を引いて計算する */
    Calendar cal = Calendar.getInstance();
    cal.setTime(getDateByViewMonth(1));
    no_input =
      (timecards.size() > 0)
        ? cal.getActualMaximum(Calendar.DAY_OF_MONTH) - timecards.size()
        : 0;

    /** みなし外残業時間の計算 */
    // みなし残業の適用対象
    // 法定内残業時間 および 残業時間（法定内残業時間から優先的に適用）
    // 深夜残業時間、所定休日および法定休日の勤務時間及び残業については適用しない
    if (isNewRule()
      && "T".equals(getTimecardSystem().getConsideredOvertimeFlag())) {
      float considered_overtime = getTimecardSystem().getConsideredOvertime();
      float considered_overtime_within_statutory_outside_diff_hour = 0;
      // 法定内残業から優先的に適用
      considered_overtime_within_statutory_outside_hour =
        overtime_within_statutory_working_hour - considered_overtime;
      if (considered_overtime_within_statutory_outside_hour < 0) {
        // みなし残業 > 法定内残業時間のケース
        considered_overtime_within_statutory_outside_diff_hour =
          -considered_overtime_within_statutory_outside_hour;
        considered_overtime_within_statutory_outside_hour = 0;
      }
      // 残業時間（深夜残業時間除く）
      considered_overtime_outside_hour =
        (overtime_hour - midnight_overtime_hour)
          - considered_overtime_within_statutory_outside_diff_hour;
      if (considered_overtime_outside_hour < 0) {
        // みなし残業 > 残業時間（深夜残業時間除く） + 法定内残業時間のケース
        considered_overtime_outside_hour = 0;
      }
      // 深夜残業時間適用
      considered_overtime_outside_hour =
        considered_overtime_outside_hour + midnight_overtime_hour;
    } else {
      considered_overtime_outside_hour = -1;
      considered_overtime_within_statutory_outside_hour = -1;
    }

    /** ユーザーごとの合計をSummaryResultDataに代入する */
    total_work_hour = ExtTimecardUtils.roundHour(total_work_hour);
    work_hour = ExtTimecardUtils.roundHour(work_hour);
    overtime_hour = ExtTimecardUtils.roundHour(overtime_hour);
    considered_overtime_outside_hour =
      ExtTimecardUtils.roundHour(considered_overtime_outside_hour);
    considered_overtime_within_statutory_outside_hour =
      ExtTimecardUtils.roundHour(
        considered_overtime_within_statutory_outside_hour);
    off_hour = ExtTimecardUtils.roundHour(off_hour);
    midnight_work_hour = ExtTimecardUtils.roundHour(midnight_work_hour);
    overtime_within_statutory_working_hour =
      ExtTimecardUtils.roundHour(overtime_within_statutory_working_hour);
    midnight_overtime_hour = ExtTimecardUtils.roundHour(midnight_overtime_hour);
    rest_hour = ExtTimecardUtils.roundHour(rest_hour);
    total_official_off_hour =
      ExtTimecardUtils.roundHour(total_official_off_hour);
    off_day_regular_work_hour =
      ExtTimecardUtils.roundHour(off_day_regular_work_hour);
    off_day_regular_midnight_work_hour =
      ExtTimecardUtils.roundHour(off_day_regular_midnight_work_hour);
    off_day_within_statutory_overtime_hour =
      ExtTimecardUtils.roundHour(off_day_within_statutory_overtime_hour);
    off_day_midnight_work_hour =
      ExtTimecardUtils.roundHour(off_day_midnight_work_hour);
    off_day_overtime_hour = ExtTimecardUtils.roundHour(off_day_overtime_hour);
    total_statutory_off_hour =
      ExtTimecardUtils.roundHour(total_statutory_off_hour);
    statutory_off_day_regular_work_hour =
      ExtTimecardUtils.roundHour(statutory_off_day_regular_work_hour);
    statutory_off_day_regular_midnight_work_hour =
      ExtTimecardUtils.roundHour(statutory_off_day_regular_midnight_work_hour);
    statutory_off_day_within_statutory_overtime_hour =
      ExtTimecardUtils.roundHour(
        statutory_off_day_within_statutory_overtime_hour);
    statutory_off_day_midnight_work_hour =
      ExtTimecardUtils.roundHour(statutory_off_day_midnight_work_hour);
    statutory_off_day_overtime_hour =
      ExtTimecardUtils.roundHour(statutory_off_day_overtime_hour);
    total_midnight_work_hour =
      ExtTimecardUtils.roundHour(total_midnight_work_hour);
    late_coming_hour = ExtTimecardUtils.roundHour(late_coming_hour);
    early_leaving_hour = ExtTimecardUtils.roundHour(early_leaving_hour);

    result.setSystemName(getTimecardSystem().getSystemName());
    result.setTotalWorkDay(total_work_day);
    result.setTotalWorkHour(total_work_hour);
    result.setWorkDayHour(work_day, work_hour);
    result.setOvertimeDayHour(overtime_day, overtime_hour);
    result.setConsideredOvertimeOutsideHour(considered_overtime_outside_hour);
    result.setConsideredOvertimeWithinStatutoryWorkingOutsideHour(
      considered_overtime_within_statutory_outside_hour);
    result.setOffDayHour(off_day, off_hour);
    result.setOfficialOffDay(official_off_day);
    result.setStatutoryOffDay(statutory_off_day);
    result.setLateComingDay(late_coming_day);
    result.setEarlyLeavingDay(early_leaving_day);
    result.setAbsentDay(absent_day);
    result.setPaidHoliday(paid_holiday);
    result.setCompensatoryHoliday(compensatory_holiday);
    result.setOtherDay(other_day);
    result.setNoInput(no_input);
    result.setLateComingDay(late_coming_day, late_coming_hour);
    result.setEarlyLeavingDay(early_leaving_day, early_leaving_hour);
    result.setMidnightWorkHour(midnight_work_hour);
    result.setOvertimeWithinStatutoryWorkingHour(
      overtime_within_statutory_working_hour);
    result.setMidnightOvertimeHour(midnight_overtime_hour);
    result.setRestHour(rest_hour);

    result.setTotalOfficialOffHour(total_official_off_hour);
    result.setOffDayRegularWorkHour(off_day_regular_work_hour);
    result.setOffDayRegularMidnightWorkHour(off_day_regular_midnight_work_hour);
    result.setOffDayWithinStatutoryOvertimeWorkingHour(
      off_day_within_statutory_overtime_hour);
    result.setOffDayMidnightOvertimeWorkHour(off_day_midnight_work_hour);
    result.setOffDayOvertimeHour(off_day_overtime_hour);

    result.setTotalStatutoryOffHour(total_statutory_off_hour);
    result.setStatutoryOffDayRegularWorkHour(
      statutory_off_day_regular_work_hour);
    result.setStatutoryOffDayRegularMidnightWorkHour(
      statutory_off_day_regular_midnight_work_hour);
    result.setStatutoryOffDayWithinStatutoryOvertimeWorkingHour(
      statutory_off_day_within_statutory_overtime_hour);
    result.setStatutoryOffDayMidnightOvertimeWorkHour(
      statutory_off_day_midnight_work_hour);
    result.setStatutoryOffDayOvertimeHour(statutory_off_day_overtime_hour);
    result.setTotalMidnightWorkHour(total_midnight_work_hour);

    return result;
  }

  public EipTExtTimecardSystem getTimecardSystem() {
    return this.timecardSystem;
  }

  public void setTimecardSystem(EipTExtTimecardSystem timecardSystem) {
    this.timecardSystem = timecardSystem;
  }

  public void setIsNewRule(boolean isNewRule) {
    this._isNewRule = isNewRule;
  }

  public boolean isNewRule() {
    return this._isNewRule;
  }

  /**
   * @return viewYear
   */
  public int getViewYear() {
    return viewYear;
  }

  /**
   * @param viewYear
   *          セットする viewYear
   */
  public void setViewYear(int viewYear) {
    this.viewYear = viewYear;
  }

  /**
   * @return viewMonth
   */
  public int getViewMonth() {
    return viewMonth;
  }

  /**
   * @param viewMonth
   *          セットする viewMonth
   */
  public void setViewMonth(int viewMonth) {
    this.viewMonth = viewMonth;
  }

  public Date getDateByViewMonth(int startDay) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, viewYear);
    cal.set(Calendar.MONTH, viewMonth);
    cal.set(Calendar.DAY_OF_MONTH, startDay);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }
}