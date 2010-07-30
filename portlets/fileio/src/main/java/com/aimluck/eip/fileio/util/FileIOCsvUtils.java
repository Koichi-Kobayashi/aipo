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
package com.aimluck.eip.fileio.util;

import java.io.File;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * ファイル入出力のユーティリティです
 *
 *
 */

public class FileIOCsvUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(FileIOCsvUtils.class.getName());

  /** 一時ファイルの新規生成 */
  public static String getNewAttachmentFolderName(File folder) {
    String FILE_SEPARATOR = System.getProperty("file.separator");
    int maxNum = 1;
    String[] filenames = folder.list();
    File file = null;
    int tmpInt = 1;
    int length = filenames.length;
    for (int i = 0; i < length; i++) {
      file = new File(folder.getAbsolutePath() + FILE_SEPARATOR + filenames[i]);
      if (file.isDirectory()) {
        try {
          tmpInt = Integer.parseInt(file.getName());
          if (maxNum <= tmpInt) {
            maxNum = tmpInt + 1;
          }
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }
    }
    return Integer.toString(maxNum);
  }
}
