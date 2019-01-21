package io.particle.android.sdk.devicesetup.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 75932 on 2017/11/9.
 */

public class AnonymousParams implements Serializable {
    public int id;
    public String uniqueId;
    public int type;
    public String imei;
    public String systemInfo;
    public String hardwareInfo;
    public String wlanMac;
    public String bluetoothMac;
    public String access_token;
    public Date expires;

    @Override
    public String toString() {
        return "AnonymousParams{" +
                "id=" + id +
                ",uniqueId='" + uniqueId + "'" +
                ",type=" + type +
                ",imei='" + imei + "'" +
                ",systemInfo='" + systemInfo + "'" +
                ",hardwareInfo='" + hardwareInfo + "'" +
                ",wlanMac='" + wlanMac + "'" +
                ",bluetoothMac='" + bluetoothMac + "'" +
                ",access_token='" + access_token + "'" +
                ",expires='" + expires + "'" +
                '}';
    }
}
