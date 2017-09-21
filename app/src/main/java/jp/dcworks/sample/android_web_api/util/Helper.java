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

package jp.dcworks.sample.android_web_api.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import jp.dcworks.sample.android_web_api.AndroidWebApiApp;

/**
 * ユーティリティメソッド.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class Helper {

    /**
     * コンストラクタの提供はなし.
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/19
     */
    private Helper() {}

    /**
     * ネットワークの状態をチェックする.
     *
     * @return true.オンライン、false.オフライン
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/19
     */
    public static boolean isConnectNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) AndroidWebApiApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni == null ){
            return false;
        }
        return true;
    }
}
