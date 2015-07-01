/*
 * blanco Framework
 * Copyright (C) 2004-2005 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.anttask.task;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import blanco.anttask.BlancoAntTaskConstants;
import blanco.anttask.BlancoAntTaskMeta2Xml;
import blanco.anttask.BlancoAntTaskXml2JavaClass;
import blanco.anttask.message.BlancoAntTaskMessage;
import blanco.anttask.task.valueobject.BlancoAntTaskProcessInput;

/**
 * blancoAntTaskの処理。
 * 
 * @author IGA Tosiki
 */
public class BlancoAntTaskProcessImpl implements BlancoAntTaskProcess {
    /**
     * メッセージ定義。
     */
    protected final BlancoAntTaskMessage fMsg = new BlancoAntTaskMessage();

    /**
     * 具体的な処理内容を記述するためのメソッドです。
     * 
     * このメソッドに実際の処理内容を記述します。
     * 
     * @param input
     *            処理の入力パラメータ。
     * @return 処理の終了コード。BlancoAntTaskBatchProcessクラスの END_SUCCESS,
     *         END_ILLEGAL_ARGUMENT_EXCEPTION, END_IO_EXCEPTION, END_ERROR
     *         のいずれかの値を戻します。
     * @throws IOException
     *             入出力例外が発生した場合。
     * @throws IllegalArgumentException
     *             入力値に不正が見つかった場合。
     */
    public int execute(BlancoAntTaskProcessInput input) throws IOException,
            IllegalArgumentException {
        System.out.println("- " + BlancoAntTaskConstants.PRODUCT_NAME + " ("
                + BlancoAntTaskConstants.VERSION + ")");

        try {
            final File blancoTmpAntTaskDirectory = new File(input.getTmpdir()
                    + BlancoAntTaskConstants.TARGET_SUBDIRECTORY);
            blancoTmpAntTaskDirectory.mkdirs();

            final File fileMetadir = new File(input.getMetadir());
            if (fileMetadir.exists() == false) {
                throw new IllegalArgumentException(fMsg.getMbata001(input
                        .getMetadir()));
            }

            new BlancoAntTaskMeta2Xml().processDirectory(fileMetadir,
                    blancoTmpAntTaskDirectory.getAbsolutePath());

            final File[] fileTmp = blancoTmpAntTaskDirectory.listFiles();
            if (fileTmp == null) {
                // 何かしらの理由によりファイル一覧の取得に失敗した場合。
                throw new IllegalArgumentException(fMsg
                        .getMbata002(blancoTmpAntTaskDirectory
                                .getAbsolutePath()));
            }

            for (int index = 0; index < fileTmp.length; index++) {
                if (fileTmp[index].getName().endsWith(".xml")) {
                    final BlancoAntTaskXml2JavaClass xml2source = new BlancoAntTaskXml2JavaClass();
                    xml2source.setEncoding(input.getEncoding());
                    xml2source.process(fileTmp[index], new File(input
                            .getTargetdir()));
                }
            }
        } catch (TransformerException e) {
            throw new IOException("XML変換の過程で例外が発生しました: " + e.toString());
        }

        return BlancoAntTaskBatchProcess.END_SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}
