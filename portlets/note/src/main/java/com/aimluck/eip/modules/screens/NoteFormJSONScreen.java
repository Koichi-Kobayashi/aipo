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
package com.aimluck.eip.modules.screens;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.note.NoteFormData;
import com.aimluck.eip.note.NoteMultiDelete;
import com.aimluck.eip.note.NoteMultiStateUpdate;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 伝言メモをJSONデータとして出力するクラスです。 <br />
 *
 */
public class NoteFormJSONScreen extends ALJSONScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(NoteFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        NoteFormData formData = new NoteFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
          int msgType = formData.getMsgType();
          context.put("msg_type", "" + msgType);
          ALEipUtils.setTemp(rundata, context, "tab", "sent_notes");
        } else {
          JSONArray json = JSONArray.fromObject(context
              .get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {

        NoteMultiStateUpdate data = new NoteMultiStateUpdate();
        if (data.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json = JSONArray.fromObject(context
              .get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {

        NoteFormData formData = new NoteFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json = JSONArray.fromObject(context
              .get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("multi_delete".equals(mode)) {

        NoteMultiDelete delete = new NoteMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json = JSONArray.fromObject(context
              .get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if ("multi_complete".equals(mode)) {

        NoteMultiStateUpdate update = new NoteMultiStateUpdate();
        if (update.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json = JSONArray.fromObject(context
              .get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else {

      }
    } catch (Exception e) {
      logger.error("[NoteFormJSONScreen]", e);
    }

    return result;
  }
}
