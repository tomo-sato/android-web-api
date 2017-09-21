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

package jp.dcworks.sample.android_web_api.api.listener;

import jp.dcworks.sample.android_web_api.api.common.AsyncRequestTask;

/**
 * リクエスト結果コールバックリスナー.
 * <p>※必要に応じてコールバックを増やす、リスナーを外出しにする等対応を入れる。
 *
 * @param <T> レスポンスボディを返却するときの型。
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/20
 */
public interface OnRequestResultListener<T> {

    /**
     * エラー用コールバック.
     * <p>リクエストの結果としてエラーが返ってきた。もしくはリクエストに失敗した。
     *
     * @param statusCode HTTPステータスコード
     * @param result レスポンスの中のresultの値
     * @param asyncRequestTask リトライ用のタスク
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/20
     */
    void onError(int statusCode, int result, AsyncRequestTask asyncRequestTask);

    /**
     * 通信エラー用コールバック.
     * <p>リクエスト前の通信チェックでエラー判定となった場合。
     *
     * @param asyncRequestTask リトライ用のタスク
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/20
     */
    void onConnectNetworkError(AsyncRequestTask asyncRequestTask);

    /**
     * 通常処理用コールバック.
     * <p>リクエストのレスポンスを受信した。
     *
     * @param statusCode HTTPステータスコード
     * @param response レスポンスボディ
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/20
     */
    void onResponse(int statusCode, T response);
}