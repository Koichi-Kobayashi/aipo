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
package com.aimluck.eip.modules.actions.mylink;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.mylink.MyLinkResultData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Myリンクのアクションクラスです。<BR>
 *
 */
public class MyLinkAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(MyLinkAction.class.getName());

  /** タイトルパラメータの接頭語 */
  private static final String TITLE = "title";

  /** URLパラメータの接頭語 */
  private static final String LINK = "link";

  /**
   * 通常表示の際の処理を記述します。<BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    if (getMode() == null) {
      doMylink_list(rundata, context);
    }

  }

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildMaximizedContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doMylink_list(rundata, context);
        setTemplate(rundata, "mylink-list");
      }
      if (getMode() == null) {
        doMylink_list(rundata, context);
        setTemplate(rundata, "mylink-list");
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void doMylink_list(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);

    // タイトル
    String title = null;
    // URL
    String link = null;
    // ResultData
    MyLinkResultData rd = null;
    // ResultDataのリスト
    List<MyLinkResultData> linkList = new ArrayList<MyLinkResultData>();

    try {
      for (int i = 1; i < 11; i++) {
        StringBuffer sb = new StringBuffer();
        rd = new MyLinkResultData();
        rd.initField();
        sb.append("p").append(Integer.toHexString(i)).append("a-")
            .append(TITLE).append(Integer.toHexString(i));
        title = portlet.getPortletConfig().getInitParameter(sb.toString());
        sb = new StringBuffer();
        sb.append("p").append(Integer.toHexString(i)).append("b-").append(LINK)
            .append(Integer.toHexString(i));
        link = portlet.getPortletConfig().getInitParameter(sb.toString());

        if (title != null && (!title.equals(""))) {
          rd.setTitle(title);
        } else {
          rd.setTitle(link);
        }
        if (link != null && (!link.equals(""))) {
          rd.setLink(link);
          linkList.add(rd);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    setResultData(linkList);
    putData(rundata, context);
  }

}
