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

package jp.dcworks.sample.android_web_api;

import android.app.Application;
import android.content.Context;

/**
 * アプリケーションクラス.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class AndroidWebApiApp extends Application {

    /** Basic認証：ユーザー. */
    public static final String BASIC_AUTH_USER = "";

    /** Basic認証：パスワード. */
    public static final String BASIC_AUTH_PASSWORD = "";

    /** アプリケーションコンテキスト. */
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        this.appContext = getApplicationContext();
    }

    /**
     * アプリケーションコンテキストを取得する.
     *
     * @return アプリケーションコンテキスト
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/19
     */
    public static Context getAppContext() {
        return appContext;
    }
}
