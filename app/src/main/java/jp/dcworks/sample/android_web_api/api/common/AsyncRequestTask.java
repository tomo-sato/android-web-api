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

package jp.dcworks.sample.android_web_api.api.common;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.Map;

import jp.dcworks.sample.android_web_api.api.listener.OnRequestResultListener;
import jp.dcworks.sample.android_web_api.util.Helper;

/**
 * HTTP(S) リクエスト/ダウンローダー.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class AsyncRequestTask extends AsyncTask<String, Void, String> {

    /** タグ. */
    private static final String _TAG = AsyncRequestTask.class.getName();

    /** 特に指定するステータスコードがない場合はこれを使用する. */
    public static final int STATUS_CODE_NOVALUE = -1;

    /** 通信エラー時のステータスコード. */
    public static final int STATUS_CODE_NETWORK_UNAVAILABLE = 950;

    /** リスナー. */
    private OnRequestResultListener<String> listener;

    /** HTTPステータスコード. */
    private int statusCode;

    /** HTTPメソッド. */
    private RequestDownloader.HttpMethod method;

    /** リクエストパラメータマップ. */
    private Map<String, Object> params;

    /** リクエストURL（リトライ時のために保存）. */
    private String url;

    /**
     * コンストラクタ.
     *
     * @param method   HTTPメソッド
     * @param params   リクエストパラメータマップ
     * @param listener リスナー
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    public AsyncRequestTask(RequestDownloader.HttpMethod method, Map<String, Object> params, OnRequestResultListener<String> listener) {
        this.listener = listener;
        this.method = method;
        this.params = params;
    }

    @Override
    protected String doInBackground(String... url) {
        if (this.url == null || (url != null && url.length > 0)) {
            this.url = url[0];
        }

        // 通信状態のチェック
        if (!Helper.isConnectNetwork()) {
            Log.d(_TAG, "通信エラーが発生しました。：url=" + this.url);
            // 通信不可
            this.statusCode = STATUS_CODE_NETWORK_UNAVAILABLE;
            return null;
        }

        RequestDownloader downloader = new RequestDownloader();
        String result = downloader.requestString(this.url, method, params);
        this.statusCode = downloader.getStatusCode();
        return result;
    }

    /**
     * HTTPステータスコードによるコールバックの処理わけ.
     * <p>※必要に応じてステータスに対する処理わけを増やす。
     *
     * @param result レスポンス
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/20
     */
    @Override
    protected void onPostExecute(String result) {
        Log.d(_TAG, "statusCode=" + this.statusCode);

        switch (this.statusCode) {

            // HTTPステータス：200
            case HttpURLConnection.HTTP_OK:
                // 通常処理用コールバック
                this.listener.onResponse(this.statusCode, result);
                break;

            // 内部定義のステータス：950
            case STATUS_CODE_NETWORK_UNAVAILABLE:
                // 通常処理用コールバック
                this.listener.onConnectNetworkError(this);
                break;

            // エラー
            default:
                // エラー用コールバック
                this.listener.onError(this.statusCode, STATUS_CODE_NOVALUE, this);
        }
    }
}
