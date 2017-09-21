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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.dcworks.sample.android_web_api.api.common.RequestDownloader;
import jp.dcworks.sample.android_web_api.api.listener.OnRequestResultListener;

/**
 * Sample API 関連呼び出しクラス.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class Sample extends ApiBase {

    /** WAPIのURL：Sample取得API. */
    private static final String WAPI_URL_GET_SAMPLE = "http://localhost/app/api/v1/sample/getSample";

    /**
     * API：sample/getSample をリクエストする.
     *
     * @param listener リスナー
     *
     * @author tomo-sato
     * @since 1.0.0 2017/09/19
     */
    public void getSample(OnRequestResultListener<JSONObject> listener) {
        // リクエストパラメータ初期化
        Map<String, Object> params = new HashMap<String, Object>();
        call(WAPI_URL_GET_SAMPLE, RequestDownloader.HttpMethod.POST, params, listener);
    }
}
