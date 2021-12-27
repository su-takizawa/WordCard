# 単語帳アプリ

## 紹介

- 外国語の単語帳アプリです。サンプルとして旅行等で使いそうなワードを入れています。
- 再生ボタンや一覧画面でタップすると現地語で音声が再生されます。
- Google翻訳をインストール済みの場合、単語入力画面から遷移してお好きなワードが翻訳できます。
![フォルダ一覧画面](https://user-images.githubusercontent.com/96267567/147460171-1195b302-d2de-4652-a16f-cb9d0cab6c9f.png)
![単語閲覧画面](https://user-images.githubusercontent.com/96267567/147460251-65c99511-6d44-40e0-8bb1-5ecce70e9ca2.png)
![単語一覧画面](https://user-images.githubusercontent.com/96267567/147460291-41f5f102-737a-4b34-9bd7-02808e3c3732.png)
## ビルド方法

### 開発環境構築
- 初めてAndroidアプリを触る方はこちらのチュートリアルをご参照ください。  
 android-basics-kotlin[初めてのアプリ](https://developer.android.com/courses/pathways/android-basics-kotlin-two?hl=ja)

### プロジェクト取得・ビルド

- 右上の「Code」をクリックし、Download ZIPを選択
- ダウンロードされたファイルを解凍し、そのフォルダをAndroid Studioで開く
- 下記の手順で実行  
[アプリをビルドして実行する](https://developer.android.com/studio/run)

## 開発環境と主なライブラリ

### 開発環境

- Kotlin 1.5.31
- Java 1.8
- Android Studio Arctic Fox | 2020.3.1
- CompileSdkVersion:31
- MinSdkVersion:26
- TargetSdkVersion:31

### ライブラリ

- Room(データ永続化)
- cardview(カード型の左右にスワイプできる画面部品)
- viewpager2(フラグメントを画面遷移する部品)
- RecyclerView(リスト型の画面部品)

## 基本仕様

### フォルダ一覧画面
  1. 追加ボタン
      - チェック済みの項目を編集するフォルダ編集画面へ遷移
  2. 編集ボタン
      - チェック済みの項目がある場合
          - チェック済みの項目を編集するフォルダ編集画面へ遷移
  3. チェックボタン
      - 初期状態はチェックなし状態
  4. スワイプ
      - 削除確認画面を表示
          - "削除"の場合はそのまま削除
  4. タップ
      - 単語閲覧画面へ遷移

### フォルダ編集画面
  1. テキストエリア  
      - フォルダ名を表示/入力
  2. 保存ボタン
      - 保存し、前画面へ遷移

### 単語閲覧画面
  1. 一覧ボタン
      - 単語一覧画面へ遷移
  2. テキストエリア
      - 左右にスワイプできる
      - タップすることで表裏の単語を切り替えられる
  3. AUTOトグルボタン
      - TextToSpeech機能を使用して再生処理する
          - 表単語、裏単語の順で再生し、次の単語へ遷移する
  4. 再生停止ボタン
      - 停止中の場合
          - TextToSpeech機能を使用して再生処理をする
      - 再生中の場合
          - 停止処理をする
  5. シークバー
      - 再生速度をTextToSpeech機能に設定する

### 単語一覧画面
  1. 追加ボタン
      - チェック済みの項目を編集する単語編集画面へ遷移
  2. 編集ボタン
      - チェック済みの項目がある場合
          - チェック済みの項目を編集する単語編集画面へ遷移
  3. チェックボタン
      - 初期状態はチェックなし状態
  4. スワイプ
      - 削除確認画面を表示
          - "削除"の場合はそのまま削除
  5. タップ
      - 表単語をTextToSpeech機能を使用して再生処理する

### 単語編集画面
  1. ドロップダウン1
      - 表単語の言語を表示/選択
  2. テキストエリア1
      - 表単語を表示/入力
  3. テキストエリア2
      - 裏単語の言語を表示/選択
  4. ドロップダウン2
      - 裏単語を表示/入力
  5. 翻訳ボタン
      - 裏単語をインテントで渡し、文字列処理アプリの選択画面へ遷移
          - Google翻訳がインストールされていることを想定
  6. 保存ボタン
      - 保存し、前画面へ遷移

### その他仕様
  1. 対応言語は下記の物とする
      - 日本語
      - 英語
      - フランス語
      - 中国語
      - 韓国語
      - ベトナム語
      - タイ語
      - ロシア語
  2. 音声合成ライブラリTextToSpeech(TTS)制御用の状態遷移表  
   
| E:イベント\S:ステート | 停止中        | 再生中          |  
| ------------- | ---------- | ------------ |
| 再生停止ボタン   | 再生処理/S:再生中 | 停止処理/S:遷移しない |  
| AUTO ON       | 再生処理/S:再生中 | X            |  
| 再生終了イベント(onDone,onStop)       | X |次再生処理/S:停止中,S:再生中|

※処理内容/次に遷移するステート


