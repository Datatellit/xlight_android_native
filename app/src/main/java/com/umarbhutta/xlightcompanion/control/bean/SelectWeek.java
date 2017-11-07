package com.umarbhutta.xlightcompanion.control.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SelectWeek implements Parcelable {

    public String name;
    public boolean isSelect;
    public String weekdays;
    public int isrepeat;

    public SelectWeek(String name, boolean isSelect, String weekdays, int isrepeat) {
        this.name = name;
        this.isSelect = isSelect;
        this.weekdays = weekdays;
        this.isrepeat = isrepeat;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化过程：必须按成员变量声明的顺序进行封装
        dest.writeString(name);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(weekdays);
        dest.writeInt(isrepeat);
    }
    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<SelectWeek> CREATOR = new Creator<SelectWeek>() {

        @Override
        public SelectWeek createFromParcel(Parcel source) {
            return new SelectWeek(source.readString(), source.readByte() != 0,source.readString(), source.readInt());
        }

        @Override
        public SelectWeek[] newArray(int size) {
            return new SelectWeek[size];
        }
    };
}
