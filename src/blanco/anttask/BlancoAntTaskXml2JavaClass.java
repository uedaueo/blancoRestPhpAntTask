/*
 * blanco Framework
 * Copyright (C) 2004-2005 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.anttask;

import java.io.File;
import java.io.IOException;
import java.util.List;

import blanco.anttask.resourcebundle.BlancoAntTaskResourceBundle;
import blanco.anttask.valueobject.BlancoAntTaskAttributeStructure;
import blanco.anttask.valueobject.BlancoAntTaskStructure;
import blanco.batchprocess.BlancoBatchProcessXml2SourceFile;
import blanco.batchprocess.stringgroup.BlancoBatchProcessBlancoTypeStringGroup;
import blanco.batchprocess.valueobject.BlancoBatchProcessInputItemStructure;
import blanco.batchprocess.valueobject.BlancoBatchProcessOutputStructure;
import blanco.batchprocess.valueobject.BlancoBatchProcessStructure;
import blanco.cg.BlancoCgObjectFactory;
import blanco.cg.transformer.BlancoCgTransformerFactory;
import blanco.cg.valueobject.BlancoCgClass;
import blanco.cg.valueobject.BlancoCgField;
import blanco.cg.valueobject.BlancoCgMethod;
import blanco.cg.valueobject.BlancoCgSourceFile;
import blanco.commons.util.BlancoJavaSourceUtil;
import blanco.commons.util.BlancoNameAdjuster;

/**
 * 中間XMLファイル(メタ情報)からJavaソースコードを自動生成します。
 * 
 * 中間XMLファイルを入力してJavaクラス・ソースコードを自動生成します。 <br>
 * 
 * @author IGA Tosiki
 */
public class BlancoAntTaskXml2JavaClass {
    /**
     * リソースバンドル・オブジェクト。
     */
    protected final BlancoAntTaskResourceBundle fBundle = new BlancoAntTaskResourceBundle();

    /**
     * 内部的に利用するblancoCg用ファクトリ。
     */
    private BlancoCgObjectFactory fCgFactory = null;

    /**
     * 内部的に利用するblancoCg用ソースファイル情報。
     */
    private BlancoCgSourceFile fCgSourceFile = null;

    /**
     * 内部的に利用するblancoCg用クラス情報。
     */
    private BlancoCgClass fCgClass = null;

    /**
     * 自動生成するソースファイルの文字エンコーディング。
     */
    private String fEncoding = null;

    /**
     * 自動生成するソースファイルの文字エンコーディングを指定します。
     * 
     * @param argEncoding
     *            自動生成するソースファイルの文字エンコーディング。
     */
    public void setEncoding(final String argEncoding) {
        fEncoding = argEncoding;
    }

    /**
     * 中間XMLファイルからJavaソースコードを生成します。
     * 
     * @param fileSource
     *            中間XMLメタ入力ファイル
     * @param directoryTarget
     *            ソースコード出力ディレクトリ
     */
    public void process(final File fileSource, final File directoryTarget) {
        final BlancoAntTaskStructure[] structures = new BlancoAntTaskXmlParser()
                .parse(fileSource);
        final int sizeListSheet = structures.length;
        for (int index = 0; index < sizeListSheet; index++) {
            structure2Source(structures[index], directoryTarget);
        }
    }

    /**
     * 指定のシートの記述内容を展開します。
     * 
     * @param argStructure
     *            タスクの構造。
     * @param directoryTarget
     *            出力先ディレクトリ
     */
    public void structure2Source(final BlancoAntTaskStructure argStructure,
            final File directoryTarget) {

        // 試験的にblancoBatchProcessを組み込み。
        final BlancoBatchProcessStructure batchProcessStructure = new BlancoBatchProcessStructure();
        batchProcessStructure.setName(BlancoNameAdjuster
                .toClassName(argStructure.getName()));
        batchProcessStructure.setSuffix("BatchProcess");
        batchProcessStructure.setPackage(argStructure.getPackage());
        batchProcessStructure.setShowMessageBeginEnd(false);

        {
            final BlancoBatchProcessInputItemStructure inputItem = new BlancoBatchProcessInputItemStructure();
            inputItem.setName("verbose");
            inputItem.setType("blanco:boolean");
            inputItem.setRequire(false);
            inputItem.setDefault("false");
            inputItem.setDescription("verboseモードで動作させるかどうか。");
            batchProcessStructure.getInputItemList().add(inputItem);
        }
        for (int indexArg = 0; indexArg < argStructure.getAttributeList()
                .size(); indexArg++) {
            final BlancoBatchProcessInputItemStructure inputItem = new BlancoBatchProcessInputItemStructure();
            final BlancoAntTaskAttributeStructure attribute = argStructure
                    .getAttributeList().get(indexArg);
            inputItem.setName(attribute.getName());
            inputItem.setType(attribute.getType());
            inputItem.setRequire(attribute.getRequire());
            inputItem.setDefault(attribute.getDefault());
            inputItem.setDescription(attribute.getDescription());
            batchProcessStructure.getInputItemList().add(inputItem);
        }

        final BlancoBatchProcessOutputStructure outputStructure = new BlancoBatchProcessOutputStructure();
        batchProcessStructure.setOutput(outputStructure);

        try {
            final BlancoBatchProcessXml2SourceFile xml2source = new BlancoBatchProcessXml2SourceFile();
            xml2source.setEncoding(fEncoding);
            xml2source.structure2Source(batchProcessStructure, null, "java",
                    directoryTarget);
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        // 従来と互換性を持たせるため、/mainサブフォルダに出力します。
        final File fileBlancoMain = new File(directoryTarget.getAbsolutePath()
                + "/main");

        fCgFactory = BlancoCgObjectFactory.getInstance();
        fCgSourceFile = fCgFactory.createSourceFile(argStructure.getPackage(),
                null);
        fCgSourceFile.setEncoding(fEncoding);

        fCgClass = fCgFactory.createClass(BlancoNameAdjuster
                .toClassName(argStructure.getName())
                + (argStructure.getSuffix() == null ? "" : argStructure
                        .getSuffix()), fBundle.getClasscomment01(argStructure
                .getName()));
        fCgSourceFile.getClassList().add(fCgClass);

        final List<java.lang.String> classDescription = fCgClass.getLangDoc()
                .getDescriptionList();

        if (argStructure.getDescription() != null) {
            classDescription.add(BlancoJavaSourceUtil
                    .escapeStringAsJavaDoc(argStructure.getDescription())
                    + "<br>");
        }
        classDescription.add(fBundle.getClasscomment02());
        classDescription.add(fBundle.getClasscomment03(argStructure
                .getPackage(), BlancoNameAdjuster.toClassName(argStructure
                .getName())
                + "BatchProcess"));
        classDescription.add("<br>");
        classDescription.add(fBundle.getClasscomment04());
        classDescription.add("<pre>");
        classDescription.add(BlancoJavaSourceUtil
                .escapeStringAsJavaDoc("<taskdef name=\""
                        + argStructure.getName().toLowerCase()
                        + "\" classname=\""
                        + argStructure.getPackage()
                        + "."
                        + BlancoNameAdjuster
                                .toClassName(argStructure.getName())
                        + (argStructure.getSuffix() == null ? "" : argStructure
                                .getSuffix()))
                + "\">");
        classDescription.add(BlancoJavaSourceUtil
                .escapeStringAsJavaDoc("    <classpath>"));
        classDescription
                .add(BlancoJavaSourceUtil
                        .escapeStringAsJavaDoc("        <fileset dir=\"lib\" includes=\"*.jar\" />"));
        classDescription
                .add(BlancoJavaSourceUtil
                        .escapeStringAsJavaDoc("        <fileset dir=\"lib.ant\" includes=\"*.jar\" />"));
        classDescription.add(BlancoJavaSourceUtil
                .escapeStringAsJavaDoc("    </classpath>"));
        classDescription.add(BlancoJavaSourceUtil
                .escapeStringAsJavaDoc("</taskdef>"));
        classDescription.add("</pre>");

        fCgClass.getExtendClassList().add(
                fCgFactory.createType("org.apache.tools.ant.Task"));

        {
            final BlancoCgField fieldInput = fCgFactory.createField("fInput",
                    argStructure.getPackage()
                            + ".valueobject."
                            + BlancoNameAdjuster.toClassName(argStructure
                                    .getName()) + "ProcessInput", argStructure
                            .getDescription());
            fieldInput.setAccess("protected");
            fieldInput.setDefault("new "
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "ProcessInput()");
            fCgClass.getFieldList().add(fieldInput);
        }

        for (int index = 0; index < argStructure.getAttributeList().size(); index++) {
            final BlancoAntTaskAttributeStructure attributeStructure = argStructure
                    .getAttributeList().get(index);
            final BlancoCgField fieldIsProcessed = fCgFactory.createField(
                    "fIsField"
                            + BlancoNameAdjuster.toClassName(attributeStructure
                                    .getName()) + "Processed", "boolean",
                    fBundle.getFieldComment02(attributeStructure.getName()));
            fieldIsProcessed.setAccess("protected");
            fieldIsProcessed.setDefault("false");
            fCgClass.getFieldList().add(fieldIsProcessed);
        }

        {
            final BlancoCgMethod methodSetVerbose = fCgFactory.createMethod(
                    "setVerbose", fBundle.getFieldCommentVerbose());
            fCgClass.getMethodList().add(methodSetVerbose);
            methodSetVerbose.getParameterList().add(
                    fCgFactory.createParameter("arg", "boolean", fBundle
                            .getFieldCommentVerbose()));
            methodSetVerbose.getLineList().add("fInput.setVerbose(arg);");

            final BlancoCgMethod methodGetVerbose = fCgFactory.createMethod(
                    "getVerbose", fBundle.getFieldCommentVerbose());
            fCgClass.getMethodList().add(methodGetVerbose);
            methodGetVerbose.setReturn(fCgFactory.createReturn("boolean",
                    fBundle.getFieldCommentVerbose()));
            methodGetVerbose.getLineList().add("return fInput.getVerbose();");
        }

        {
            for (int index = 0; index < argStructure.getAttributeList().size(); index++) {
                final BlancoAntTaskAttributeStructure attributeStructure = argStructure
                        .getAttributeList().get(index);

                final BlancoCgMethod methodSet = fCgFactory.createMethod("set"
                        + BlancoNameAdjuster.toClassName(attributeStructure
                                .getName()), fBundle
                        .getMethodSetComment01(attributeStructure.getName()));
                fCgClass.getMethodList().add(methodSet);

                if (attributeStructure.getNo() != null) {
                    methodSet.getLangDoc().getDescriptionList().add(
                            fBundle.getItemNo() + ": "
                                    + attributeStructure.getNo() + "<br>");
                }
                if (attributeStructure.getDescription() != null) {
                    methodSet.getLangDoc().getDescriptionList().add(
                            BlancoJavaSourceUtil
                                    .escapeStringAsJavaDoc(attributeStructure
                                            .getDescription())
                                    + "<br>");
                }

                switch (new BlancoBatchProcessBlancoTypeStringGroup()
                        .convertToInt(attributeStructure.getType())) {
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_STRING:
                default:
                    methodSet.getParameterList().add(
                            fCgFactory.createParameter("arg",
                                    "java.lang.String", fBundle
                                            .getMethodSetComment02()));
                    methodSet.getLineList().add(
                            "fInput.set"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName()) + "(arg);");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_BOOLEAN:
                    methodSet.getParameterList().add(
                            fCgFactory.createParameter("arg", "boolean",
                                    fBundle.getMethodSetComment02()));
                    methodSet.getLineList().add(
                            "fInput.set"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName()) + "(arg);");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_INT:
                    methodSet.getParameterList().add(
                            fCgFactory.createParameter("arg",
                                    "java.lang.String", fBundle
                                            .getMethodSetComment02()));
                    methodSet.getLineList().add("try {");
                    methodSet.getLineList().add(
                            "fInput.set"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName())
                                    + "(Integer.parseInt(arg));");
                    methodSet.getLineList().add(
                            "} catch (NumberFormatException e) {");
                    methodSet
                            .getLineList()
                            .add(
                                    "throw new IllegalArgumentException(\""
                                            + fBundle
                                                    .getMethodSetLine01(attributeStructure
                                                            .getName())
                                            + "\" + e.toString());");
                    methodSet.getLineList().add("}");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_LONG:
                    methodSet.getParameterList().add(
                            fCgFactory.createParameter("arg",
                                    "java.lang.String", fBundle
                                            .getMethodSetComment02()));
                    methodSet.getLineList().add("try {");
                    methodSet.getLineList().add(
                            "fInput.set"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName())
                                    + "(Long.parseLong(arg));");
                    methodSet.getLineList().add(
                            "} catch (NumberFormatException e) {");
                    methodSet
                            .getLineList()
                            .add(
                                    "throw new IllegalArgumentException(\""
                                            + fBundle
                                                    .getMethodSetLine01(attributeStructure
                                                            .getName())
                                            + "\" + e.toString());");
                    methodSet.getLineList().add("}");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_DECIMAL:
                    methodSet.getParameterList().add(
                            fCgFactory.createParameter("arg",
                                    "java.lang.String", fBundle
                                            .getMethodSetComment02()));
                    methodSet.getLineList().add("try {");
                    fCgSourceFile.getImportList().add("java.math.BigDecimal");
                    methodSet.getLineList().add(
                            "fInput.set"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName())
                                    + "(new BigDecimal(arg));");
                    methodSet.getLineList().add(
                            "} catch (NumberFormatException e) {");
                    methodSet
                            .getLineList()
                            .add(
                                    "throw new IllegalArgumentException(\""
                                            + fBundle
                                                    .getMethodSetLine01(attributeStructure
                                                            .getName())
                                            + "\" + e.toString());");
                    methodSet.getLineList().add("}");
                    break;
                }
                {
                    // 必須項目への値の設定を確認しました。
                    methodSet.getLineList().add(
                            "fIsField"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName())
                                    + "Processed = true;");
                }

                final BlancoCgMethod methodGet = fCgFactory.createMethod("get"
                        + BlancoNameAdjuster.toClassName(attributeStructure
                                .getName()), fBundle
                        .getMethodGetComment01(attributeStructure.getName()));
                fCgClass.getMethodList().add(methodGet);

                if (attributeStructure.getNo() != null) {
                    methodGet.getLangDoc().getDescriptionList().add(
                            fBundle.getItemNo() + ": "
                                    + attributeStructure.getNo() + "<br>");
                }
                if (attributeStructure.getDescription() != null) {
                    methodGet.getLangDoc().getDescriptionList().add(
                            BlancoJavaSourceUtil
                                    .escapeStringAsJavaDoc(attributeStructure
                                            .getDescription())
                                    + "<br>");
                }
                if (attributeStructure.getRequire()) {
                    methodGet.getLangDoc().getDescriptionList().add(
                            fBundle.getMethodGetComment11());
                }
                if (attributeStructure.getDefault() != null) {
                    methodGet.getLangDoc().getDescriptionList().add(
                            fBundle.getMethodGetComment12(BlancoJavaSourceUtil
                                    .escapeStringAsJavaDoc(attributeStructure
                                            .getDefault())));
                }

                switch (new BlancoBatchProcessBlancoTypeStringGroup()
                        .convertToInt(attributeStructure.getType())) {
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_STRING:
                default:
                    methodGet.setReturn(fCgFactory
                            .createReturn("java.lang.String", fBundle
                                    .getMethodGetComment13()));
                    methodGet.getLineList().add(
                            "return fInput.get"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName()) + "();");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_BOOLEAN:
                    methodGet.setReturn(fCgFactory.createReturn("boolean",
                            fBundle.getMethodGetComment13()));
                    methodGet.getLineList().add(
                            "return fInput.get"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName()) + "();");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_INT:
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_LONG:
                    methodGet.setReturn(fCgFactory
                            .createReturn("java.lang.String", fBundle
                                    .getMethodGetComment13()));
                    methodGet.getLineList().add(
                            "return String.valueOf(fInput.get"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName()) + "());");
                    break;
                case BlancoBatchProcessBlancoTypeStringGroup.BLANCO_DECIMAL:
                    methodGet.setReturn(fCgFactory
                            .createReturn("java.lang.String", fBundle
                                    .getMethodGetComment13()));
                    methodGet.getLineList().add(
                            "return fInput.get"
                                    + BlancoNameAdjuster
                                            .toClassName(attributeStructure
                                                    .getName())
                                    + "().toString();");
                    break;
                }
            }
        }

        {
            final BlancoCgMethod methodExecute = fCgFactory.createMethod(
                    "execute", fBundle.getMethodExecuteComment01());
            fCgClass.getMethodList().add(methodExecute);
            methodExecute.setFinal(true);
            methodExecute.getThrowList().add(
                    fCgFactory.createException(
                            "org.apache.tools.ant.BuildException", fBundle
                                    .getMethodExecuteComment02()));
            methodExecute.getAnnotationList().add("Override");

            final List<java.lang.String> listLine = methodExecute.getLineList();

            listLine.add("System.out.println(\"" + argStructure.getName()
                    + "Task begin.\");");
            listLine.add("");

            for (int index = 0; index < argStructure.getAttributeList().size(); index++) {
                final BlancoAntTaskAttributeStructure attributeStructure = argStructure
                        .getAttributeList().get(index);
                if (attributeStructure.getRequire() == false) {
                    continue;
                }

                listLine.add(fBundle.getMethodExecuteLine01((attributeStructure
                        .getNo() == null ? "" : fBundle.getItemNo() + "["
                        + attributeStructure.getNo() + "]、"),
                        attributeStructure.getName()));

                listLine.add("if (fIsField"
                        + BlancoNameAdjuster.toClassName(attributeStructure
                                .getName()) + "Processed == false) {");
                listLine.add("throw new BuildException(\""
                        + fBundle.getMethodExecuteLine02(attributeStructure
                                .getName()) + "\");");
                listLine.add("}");
            }

            listLine.add("");
            listLine.add("if (getVerbose()) {");
            listLine.add("System.out.println(\"- verbose:[true]\");");
            for (int index = 0; index < argStructure.getAttributeList().size(); index++) {
                final BlancoAntTaskAttributeStructure attributeStructure = argStructure
                        .getAttributeList().get(index);

                listLine.add("System.out.println(\"- "
                        + attributeStructure.getName()
                        + ":[\" + get"
                        + BlancoNameAdjuster.toClassName(attributeStructure
                                .getName()) + "() + \"]\");");
            }
            listLine.add("}");
            listLine.add("");

            listLine.add("try {");
            listLine.add(fBundle.getMethodExecuteLine04());
            listLine.add("// この箇所でコンパイルエラーが発生する場合、"
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "Processインタフェースを実装して " + argStructure.getPackage()
                    + "パッケージに "
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "ProcessImplクラスを作成することにより解決できる場合があります。");
            listLine.add("final "
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "Process proc = new "
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "ProcessImpl();");
            listLine.add("if (proc.execute(fInput) != "
                    + BlancoNameAdjuster.toClassName(argStructure.getName())
                    + "BatchProcess.END_SUCCESS) {");
            listLine.add("throw new BuildException(\"タスクは異常終了しました。\");");
            listLine.add("}");
            listLine.add("} catch (IllegalArgumentException e) {");
            listLine.add("if (getVerbose()) {");
            listLine.add("e.printStackTrace();");
            listLine.add("}");
            listLine.add("throw new BuildException(e.getMessage());");
            listLine.add("} catch (Exception e) {");
            listLine.add("e.printStackTrace();");
            listLine.add("throw new BuildException(\""
                    + fBundle.getMethodExecuteLine05() + "\" + e.toString());");
            listLine.add("} catch (Error e) {");
            listLine.add("e.printStackTrace();");
            listLine.add("throw new BuildException(\""
                    + fBundle.getMethodExecuteLine06() + "\" + e.toString());");
            listLine.add("}");
        }

        directoryTarget.mkdirs();

        BlancoCgTransformerFactory.getJavaSourceTransformer().transform(
                fCgSourceFile, fileBlancoMain);
    }
}