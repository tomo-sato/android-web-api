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

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import jp.dcworks.sample.android_web_api.models.User;
import jp.dcworks.sample.android_web_api.models.User_Table;

/**
 * データベース定義クラス.
 *
 * @author tomo-sato
 * @since 1.0.0 2017/09/25
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "AppDatabase";
    public static final int VERSION = 2;

    // データUpdateサンプル
    @Migration(version = 2, database = AppDatabase.class)
    public static class Migration2 extends BaseMigration {
        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            SQLite.update(User.class)
                    .set(User_Table.name.eq("test hoge"))
                    .where(User_Table.name.eq("Andrew Grosner"))
                    .execute(database); // required inside a migration to pass the wrapper
        }
    }

    @Migration(version = 3, priority = 0, database = AppDatabase.class)
    public static class Migration3_ddl extends AlterTableMigration<User> {

        public Migration3_ddl(Class<User> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "tall");
        }
    }

    // データUpdateサンプル
    @Migration(version = 3, priority = 1, database = AppDatabase.class)
    public static class Migration3_dml extends BaseMigration {
        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            SQLite.update(User.class)
                    .set(User_Table.tall.eq(3))
                    .execute(database);
        }
    }
}