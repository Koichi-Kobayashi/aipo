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
package com.aimluck.eip.memo;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * メモ帳のLiteResultDataです。 <BR>
 * 
 */
public class MemoLiteResultData implements ALData {

  /** Memo ID */
  private ALNumberField memo_id;

  /** Memo名 */
  private ALStringField memo_name;

  /**
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    memo_id = new ALNumberField();
    memo_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getMemoId() {
    return memo_id;
  }

  /**
   * @return
   */
  public ALStringField getMemoName() {
    return memo_name;
  }

  /**
   * @param i
   */
  public void setMemoId(long i) {
    memo_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setMemoName(String string) {
    memo_name.setValue(string);
  }
}
