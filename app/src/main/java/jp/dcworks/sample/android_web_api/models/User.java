package jp.dcworks.sample.android_web_api.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.UUID;

import jp.dcworks.sample.android_web_api.AppDatabase;

/**
 * Created by tomomichi on 2017/09/21.
 */

@Table(database = AppDatabase.class)
public class User {

    @PrimaryKey // at least one primary key required
    public UUID id;

    @Column
    public String name;

    @Column
    public int age;

    @Column
    public int tall;
}