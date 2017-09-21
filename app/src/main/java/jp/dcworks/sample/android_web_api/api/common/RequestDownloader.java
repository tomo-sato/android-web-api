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

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import jp.dcworks.sample.android_web_api.AndroidWebApiApp;

/**
 * HTTP(S) リクエスト/ダウンローダー.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class RequestDownloader {

    /** タグ. */
    private static final String _TAG = RequestDownloader.class.getName();

    /** システムエラー時のステータスコード */
    public static final int STATUS_CODE_SYSTEM_ERROR = 999;

    /** 読み取りタイムアウト（ミリ秒） */
    private static final int READ_TIMEOUT = 15000;
    /** 参照するリソースへの通信リンクのオープン時（ミリ秒） */
    private static final int CONNECTION_TIMEOUT = 30000;

    /** マルチパート生成用文字列："--" */
    private final static String TWO_HYPHEN = "--";
    /** マルチパート生成用文字列：改行 "\r\n" */
    private final static String EOL = "\r\n";
    /** マルチパート生成用文字列：境界 */
    private final static String BOUNDARY = String.format("%x", new Random().hashCode());

    /** リクエストの方式. */
    public enum HttpMethod {
        GET,
        POST,
        POST_JSON
    }

    /** HTTPステータスコード. */
    private int statusCode;

    /**
     * ステータスコードを取得する.
     *
     * @return HTTPステータスコード
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * レスポンスが文字列のリクエストを投げる.
     *
     * @param url URL
     * @param method HTTPメソッド
     * @param params リクエストパラメータ
     * @return レスポンスボディ（文字列）
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    public String requestString(String url, HttpMethod method, Map<String, Object> params) {
        try {
            return request(url, method, params);
        } catch (IOException e) {
            e.printStackTrace();
            this.statusCode = STATUS_CODE_SYSTEM_ERROR;
        }
        return null;
    }

    /**
     * リクエスト処理.
     *
     * @param urlString URL文字列
     * @param method HTTPメソッド
     * @param params リクエストパラメータ
     * @return レスポンスボディ
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    public String request(String urlString, HttpMethod method, Map<String, Object> params) throws IOException {
        Log.i(_TAG, "urlString=" + urlString + ", method=" + method);

        if (urlString == null || urlString.length() <= 0) {
            Log.i(_TAG, "invalid URL string.");
            return null;
        }

        InputStream is = null;
        HttpURLConnection urlConnection;
        try {
            Uri uri = Uri.parse(urlString);
            if (method == HttpMethod.GET) {
                // （有れば）クエリパラメータを追加する
                if (params != null) {
                    Uri.Builder uriBuilder = uri.buildUpon();
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    uri = uriBuilder.build();
                }
            }

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

            // キャッシュはしない。
            urlConnection.setUseCaches(false);

            // データ受信可能とする
            urlConnection.setDoInput(true);

            if (method == HttpMethod.POST || method == HttpMethod.POST_JSON) {
                Log.d(_TAG, "Use POST method.. method=" + method);
                urlConnection.setRequestMethod("POST");

                //データ送信可能とする
                urlConnection.setDoOutput(true);

                // ヘッダーの設定(複数設定可能)
                urlConnection.setRequestProperty("Content-Language", "jp");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

                Log.d(_TAG, "requestProperties=" + urlConnection.getRequestProperties());
                postMultipart(urlConnection, params);

            } else {
                Log.d(_TAG, "Use GET method..");
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Language", "jp");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");

                urlConnection.connect();
            }

            this.statusCode = urlConnection.getResponseCode();
            Log.d(_TAG, "statusCode=" + this.statusCode);
            switch (this.statusCode) {
                case HttpURLConnection.HTTP_OK:
                    is = urlConnection.getInputStream();
                    return readStream(is);
                default:
                    return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            this.statusCode = STATUS_CODE_SYSTEM_ERROR;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    /**
     * InputStream の読み込み処理.
     *
     * @param stream InputStream
     * @return 読み込み結果の文字列
     * @throws IOException
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    private String readStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        Log.d(_TAG, sb.toString());
        return sb.toString();
    }

    /**
     * マルチパート処理.
     *
     * @param urlConnection HttpURLConnection
     * @param params パラメータ
     * @throws IOException
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/13
     */
    private void postMultipart(HttpURLConnection urlConnection, Map<String, Object> params) throws IOException {

        if (params != null) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new DataOutputStream(urlConnection.getOutputStream()), "UTF-8");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = null;
                if (entry.getValue() != null) {
                    value = entry.getValue().toString();
                }
                outputStreamWriter.write(TWO_HYPHEN + BOUNDARY + EOL);
                outputStreamWriter.write("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + EOL);
                outputStreamWriter.write("Content-Type: text/plain; charset=utf-8" + EOL);
                outputStreamWriter.write(EOL);
                outputStreamWriter.write((value == null) ? "" : value);
                outputStreamWriter.write(EOL);
            }

            outputStreamWriter.write(TWO_HYPHEN + BOUNDARY + TWO_HYPHEN + EOL);
            outputStreamWriter.close();
        }
    }

    static {
        // Basic認証用の設定
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(AndroidWebApiApp.BASIC_AUTH_USER, AndroidWebApiApp.BASIC_AUTH_PASSWORD.toCharArray());
            }
        });
    }
}
