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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import jp.dcworks.sample.android_web_api.api.Sample;
import jp.dcworks.sample.android_web_api.api.common.AsyncRequestTask;
import jp.dcworks.sample.android_web_api.api.listener.OnRequestResultListener;

/**
 * Web APIデモ Activity.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/19
 */
public class MainActivity extends AppCompatActivity {

    /** タグ. */
    private static final String _TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Sample().getSample(new OnRequestResultListener<JSONObject>() {
            @Override
            public void onError(int statusCode, int result, AsyncRequestTask asyncRequestTask) {
                Log.d(_TAG, "エラー。：statusCode=" + statusCode + ", result=" + result);
            }

            @Override
            public void onConnectNetworkError(AsyncRequestTask asyncRequestTask) {
                Log.d(_TAG, "通信エラー。");
            }

            @Override
            public void onResponse(int statusCode, JSONObject responseJson) {
                Log.d(_TAG, "取得完了。：statusCode=" + statusCode + ", responseJson=" + responseJson.toString());
            }
        });
    }
}
