/*
 * Copyright 2017 tomo-sato This software is licensed under the Apache 2 license, quoted below.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.dcworks.sample.android_web_api.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jp.dcworks.sample.android_web_api.api.common.AsyncRequestTask;
import jp.dcworks.sample.android_web_api.api.common.RequestDownloader;
import jp.dcworks.sample.android_web_api.api.common.ResponseParser;
import jp.dcworks.sample.android_web_api.api.listener.OnRequestResultListener;

/**
 * Web API 呼び出し基底クラス.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class ApiBase {

    /** タグ. */
    private static final String _TAG = ApiBase.class.getName();

    /** レスポンスの成否判定用のキー. */
    public static final String RESULT_JUDGMENT_KEY = "status";


    /** 処理結果：1.成功 */
    public static final int STATUS_CODE_SUCCESS = 1;

    /** 処理結果：300.パラメータエラー */
    public static final int STATUS_CODE_PARAMETER_ERROR = 300;

    /** 処理結果：999.システムエラー */
    public static final int STATUS_CODE_SYSTEM_ERROR = 999;


    /** レスポンスの中のresultがない場合はこれを使用する */
    public static final int RESULT_NO_VALUE = -1;

    /** ダウンローダ. */
    private AsyncRequestTask downloader;

    /**
     * API を呼び出す.
     *
     * @param url APIのURL
     * @param method リクエストメソッドの種類
     * @param params リクエストパラメータ
     * @param listener リクエスト結果コールバックリスナー
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/20
     */
    protected void call(
            final String url, final RequestDownloader.HttpMethod method, final Map<String, Object> params,
            final OnRequestResultListener<JSONObject> listener) {

        Log.i(_TAG, "API CLASS: " + this.getClass());
        Log.i(_TAG, "URL=" + url);
        Log.i(_TAG, "Params=" + params);

        this.downloader = new AsyncRequestTask(method, params, new OnRequestResultListener<String>() {

            @Override
            public void onError(int statusCode, int result, AsyncRequestTask asyncRequestTask) {
                // このクラスでの処理結果をセットし、呼び出し元のリスナーに通知
                listener.onError(statusCode, RESULT_NO_VALUE, ApiBase.this.downloader);
            }

            @Override
            public void onConnectNetworkError(AsyncRequestTask asyncRequestTask) {
                // このクラスでの処理結果をセットし、呼び出し元のリスナーに通知
                listener.onConnectNetworkError(ApiBase.this.downloader);
            }

            @Override
            public void onResponse(int statusCode, String response) {

                // レスポンスのjsonから処理結果判定用のキーを検索し処理判定を行い、呼び出し元のリスナーに通知する。
                Log.d(_TAG, "API呼び出し：レスポンス処理。");

                try {
                    JSONObject jsonObject = ResponseParser.parseBodyJson(response);
                    if (!jsonObject.has(RESULT_JUDGMENT_KEY)) {
                        // RESULT_JUDGMENT_KEY がレスポンスに含まれていない場合、処理結果の判定ができないためエラー
                        Log.d(_TAG, "レスポンスに " + RESULT_JUDGMENT_KEY + " が含まれていない。");

                        listener.onError(statusCode, RESULT_NO_VALUE, ApiBase.this.downloader);
                        return;
                    }

                    // 成否判定用の値をチェック
                    int result = jsonObject.getInt(RESULT_JUDGMENT_KEY);

                    switch (result) {
                        // 1.成功
                        case STATUS_CODE_SUCCESS:
                            listener.onResponse(statusCode, jsonObject);
                            break;

                        // 300.パラメータエラー（※エラメッセージ等のレスポンス取得を想定。）
                        case STATUS_CODE_PARAMETER_ERROR:
                            listener.onResponse(statusCode, jsonObject);
                            break;

                        // 999.システムエラー
                        case STATUS_CODE_SYSTEM_ERROR:
                            listener.onError(statusCode, RESULT_NO_VALUE, ApiBase.this.downloader);
                            break;

                        // 上記以外のレスポンス（※定義不明な為エラー）
                        default:
                            listener.onError(statusCode, RESULT_NO_VALUE, ApiBase.this.downloader);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    // レスポンス解析中に例外が発生したためエラー
                    Log.d(_TAG, "レスポンス解析中に例外が発生。");
                    listener.onError(statusCode, RESULT_NO_VALUE, ApiBase.this.downloader);
                }
            }
        });
        this.downloader.executeOnExecutor(AsyncRequestTask.THREAD_POOL_EXECUTOR, url);
    }
}
