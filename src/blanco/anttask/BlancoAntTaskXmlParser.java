/*
 * blanco Framework
 * Copyright (C) 2004-2006 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.anttask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import blanco.anttask.message.BlancoAntTaskMessage;
import blanco.anttask.valueobject.BlancoAntTaskAttributeStructure;
import blanco.anttask.valueobject.BlancoAntTaskStructure;
import blanco.commons.util.BlancoStringUtil;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

/**
 * 「バッチ処理定義書」Excel様式から情報を抽出します。
 * 
 * このクラスは、中間XMLファイルから情報抽出する機能を担います。
 * 
 * @author IGA Tosiki
 */
public class BlancoAntTaskXmlParser {
    /**
     * メッセージ定義。
     */
    protected final BlancoAntTaskMessage fMsg = new BlancoAntTaskMessage();

    /**
     * 中間XMLファイルのXMLドキュメントをパースして、情報の配列を取得します。
     * 
     * @param argMetaXmlSourceFile
     *            中間XMLファイル。
     * @return パースの結果得られた情報の配列。
     */
    public BlancoAntTaskStructure[] parse(final File argMetaXmlSourceFile) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                .unmarshal(argMetaXmlSourceFile);
        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);
    }

    /**
     * 中間XMLファイル形式のXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     * 
     * @param argXmlDocument
     *            中間XMLファイルのXMLドキュメント。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoAntTaskStructure[] parse(final BlancoXmlDocument argXmlDocument) {
        final List<BlancoAntTaskStructure> listStructure = new ArrayList<BlancoAntTaskStructure>();
        // ルートエレメントを取得します。
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // ルートエレメントが無い場合には処理中断します。
            return null;
        }

        // sheet(Excelシート)のリストを取得します。
        final List<blanco.xml.bind.valueobject.BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");
        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            final BlancoAntTaskStructure structure = parseElementSheet(elementSheet);
            if (structure != null) {
                // 得られた情報を記憶します。
                listStructure.add(structure);
            }
        }

        final BlancoAntTaskStructure[] result = new BlancoAntTaskStructure[listStructure
                .size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメントをパースして、バリューオブジェクト情報を取得します。
     * 
     * @param argElementSheet
     *            中間XMLファイルの「sheet」XMLエレメント。
     * @return パースの結果得られたバリューオブジェクト情報。「name」が見つからなかった場合には nullを戻します。
     */
    public BlancoAntTaskStructure parseElementSheet(
            final BlancoXmlElement argElementSheet) {
        final BlancoAntTaskStructure structure = new BlancoAntTaskStructure();
        // 入力パラメータ情報を取得します。

        final List<blanco.xml.bind.valueobject.BlancoXmlElement> listCommon = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, "blancoanttask-common");
        if (listCommon.size() == 0) {
            // commonが無い場合にはスキップします。
            return null;
        }

        // 最初のアイテムのみ処理しています。
        final BlancoXmlElement elementCommon = listCommon.get(0);

        // シートから詳細な情報を取得します。
        structure.setName(BlancoXmlBindingUtil.getTextContent(elementCommon,
                "taskName"));
        structure.setPackage(BlancoXmlBindingUtil.getTextContent(elementCommon,
                "packageName"));

        if (BlancoStringUtil.null2Blank(structure.getName()).length() == 0) {
            return null;
        }

        if (BlancoStringUtil.null2Blank(structure.getPackage()).trim().length() == 0) {
            throw new IllegalArgumentException("パッケージ名が指定されていません["
                    + structure.getName() + "]");
        }

        if (BlancoXmlBindingUtil.getTextContent(elementCommon, "description") != null) {
            structure.setDescription(BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "description"));
        }

        if (BlancoXmlBindingUtil.getTextContent(elementCommon, "suffix") != null) {
            structure.setSuffix(BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "suffix"));
        }

        final BlancoXmlElement elementAttributes = BlancoXmlBindingUtil
                .getElement(argElementSheet, "blancoanttask-list");
        if (elementAttributes == null) {
            return null;
        }

        // 一覧の内容を取得します。
        final List<blanco.xml.bind.valueobject.BlancoXmlElement> listField = BlancoXmlBindingUtil
                .getElementsByTagName(elementAttributes, "attribute");
        for (int indexField = 0; indexField < listField.size(); indexField++) {
            final BlancoXmlElement elementField = listField.get(indexField);

            final BlancoAntTaskAttributeStructure attributeStructure = new BlancoAntTaskAttributeStructure();

            attributeStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementField, "no"));
            attributeStructure.setName(BlancoXmlBindingUtil.getTextContent(
                    elementField, "attribute"));
            if (BlancoStringUtil.null2Blank(attributeStructure.getName())
                    .length() == 0) {
                // attributeが指定されていない場合には処理しません。
                continue;
            }

            if (BlancoStringUtil.null2Blank(
                    BlancoXmlBindingUtil.getTextContent(elementField, "type"))
                    .length() > 0) {
                attributeStructure.setType(BlancoXmlBindingUtil.getTextContent(
                        elementField, "type"));
            }

            attributeStructure.setRequire("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementField, "require")));
            attributeStructure.setDefault(BlancoXmlBindingUtil.getTextContent(
                    elementField, "default"));
            attributeStructure.setDescription(BlancoXmlBindingUtil
                    .getTextContent(elementField, "description"));

            if (attributeStructure.getRequire()) {
                if (BlancoStringUtil
                        .null2Blank(attributeStructure.getDefault()).length() > 0) {
                    // タスク名[{0}]、アトリビュート[{1}]において、[必須]と[デフォルト]が同時に指定されています。
                    // しかし[必須]と[デフォルト]は排他的に、しかしいずれかは指定する必要があります。
                    throw new IllegalArgumentException(fMsg.getMbati001(
                            structure.getName(), attributeStructure.getName()));
                }
            }

            structure.getAttributeList().add(attributeStructure);
        }

        return structure;
    }
}
