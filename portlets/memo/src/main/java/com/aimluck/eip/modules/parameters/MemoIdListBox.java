/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.modules.parameters;

import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.modules.parameters.ListBox;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳の設定値を処理するクラスです。 <br />
 */
public class MemoIdListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private String DEF_INITIAL_VALUE = "（メモの選択）";

  /**
   * Initialize options
   *
   * @param data
   */
  protected void init(RunData data) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMemo.class);
      Expression exp = ExpressionFactory.matchExp(EipTMemo.OWNER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(data)));
      query.setQualifier(exp);
      List<?> memos = dataContext.performQuery(query);

      int length = 1;
      if (memos != null && memos.size() > 0) {
        length = memos.size() + 1;
      }

      String[] memoKeys = new String[length];
      String[] memoValues = new String[length];

      memoKeys[0] = "";
      memoValues[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
      int count = 1;

      EipTMemo memo = null;
      Iterator<?> iter = memos.iterator();
      while (iter.hasNext()) {
        memo = (EipTMemo) iter.next();
        memoKeys[count] = memo.getMemoId().toString();
        memoValues[count] = memo.getMemoName();
        count++;
      }

      this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
      this.items = memoKeys;
      this.values = memoValues;
      this.size = Integer.toString(length);
      this.multiple = Boolean.valueOf(
          (String) this.getParm(MULTIPLE_CHOICE, "false")).booleanValue();
    } catch (Exception e) {
      ALEipUtils.redirectPageNotFound(data);
    }

  }
}
