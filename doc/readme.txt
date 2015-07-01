blancoAntTask は「Antタスク定義書」というExcel様式を記入するだけで 簡単に Apache AntのTaskが作成できるようにするためのツールです。
Apache Antタスクおよび その処理内容であるバッチ処理の抽象親クラスが自動生成されるので、そのクラスを継承して内部処理を実装するだけで、すぐにあなたのAntタスクが新規開発できます。
Antタスクの入力パラメータチェックなどが自動生成されるので、簡単に手早く安全な Antタスクを開発することができるようになります。

[開発者]
 1.伊賀敏樹 (Tosiki Iga / いがぴょん): 開発および維持メンテ担当
 2.山本耕司 (ymoto) : 仕様決定およびリリース判定担当

[ライセンス]
 1.blancoAntTask は ライセンス として GNU Lesser General Public License を採用しています。

[依存するライブラリ]
blancoAntTaskは下記のライブラリを利用しています。
※各オープンソース・プロダクトの提供者に感謝します。
 1.JExcelApi - Java Excel API - A Java API to read, write and modify Excel spreadsheets
     http://jexcelapi.sourceforge.net/
     http://sourceforge.net/projects/jexcelapi/
     http://www.andykhan.com/jexcelapi/ 
   概要: JavaからExcelブック形式を読み書きするためのライブラリです。
   ライセンス: GNU Lesser General Public License
 2.blancoCg
   概要: ソースコード生成ライブラリ
   ライセンス: GNU Lesser General Public License
 3.その他の blanco Framework
   概要: このプロダクトは それ自身が blanco Frameworkにより自動生成されています。
         このプロダクトは 実行時に blanco Framework各種プロダクトに依存して動作します。
   ライセンス: GNU Lesser General Public License
