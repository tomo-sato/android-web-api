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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import jp.dcworks.sample.android_web_api.api.Sample;
import jp.dcworks.sample.android_web_api.api.common.AsyncRequestTask;
import jp.dcworks.sample.android_web_api.api.listener.OnRequestResultListener;
import jp.dcworks.sample.android_web_api.models.User;
import jp.dcworks.sample.android_web_api.models.User_Table;

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

        // DB実装サンプル
        User user = new User();
        user.id = UUID.randomUUID();
        user.name = "Andrew Grosner";
        user.age = 27;

        // インサート
        ModelAdapter<User> adapter = FlowManager.getModelAdapter(User.class);
        adapter.insert(user);

        // 検索
        List<User> usersList = SQLite.select()
                .from(User.class)
                .where(User_Table.age.greaterThan(18))
                .queryList();

        TextView textView = new TextView(this);
        textView.setText(usersList.get(0).name + ":" + usersList.get(0).tall);

        LinearLayout view = (LinearLayout) findViewById(R.id.layout);
        view.addView(textView);

        // 生SQL実行
        StringQuery query = new StringQuery(User.class, "select * from user");
        List<User> usersList2 = query.queryList();

        for (User user2 : usersList2) {
            TextView textView2 = new TextView(this);
            textView2.setText(user2.name + ":" + usersList.get(0).tall);
            view.addView(textView2);
        }
    }
}
