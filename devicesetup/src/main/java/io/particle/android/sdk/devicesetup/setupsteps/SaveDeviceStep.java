package io.particle.android.sdk.devicesetup.setupsteps;


import android.content.Context;

import com.google.gson.Gson;

import org.kaazing.gateway.client.impl.http.HttpRequestListener;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.devicesetup.R;
import io.particle.android.sdk.devicesetup.model.AddDeviceParams;
import io.particle.android.sdk.devicesetup.model.DeviceInfo;
import io.particle.android.sdk.devicesetup.model.HttpUtils;
import io.particle.android.sdk.devicesetup.model.UserUtils;
import io.particle.android.sdk.utils.ui.Ui;


public class SaveDeviceStep extends SetupStep {

    private final ParticleCloud sparkCloud;
    private final String deviceBeingConfiguredId;
    private boolean result = false;
    private Context context;
    private final String defaultName = "Light";

    SaveDeviceStep(StepConfig stepConfig, ParticleCloud sparkCloud,
                   String deviceBeingConfiguredId, Context context) {
        super(stepConfig);
        this.sparkCloud = sparkCloud;
        this.deviceBeingConfiguredId = deviceBeingConfiguredId;
        this.context = context;
    }

    @Override
    protected void onRunStep() throws SetupStepException {
        AddDeviceParams deviceParams;
        try {
            if (UserUtils.isLogin(context)) {
                deviceParams = new AddDeviceParams(deviceBeingConfiguredId, defaultName, UserUtils.getUserInfo(context).id, DeviceInfo.getSign(context), 1, 2, true);
            } else {
                deviceParams = new AddDeviceParams(deviceBeingConfiguredId, defaultName, DeviceInfo.getSign(context), 1, 2, true);
            }
            Gson gson = new Gson();
            String paramStr = gson.toJson(deviceParams);
            try {
                HttpUtils.getInstance().postRequestInfo(context.getString(R.string.dmi_url) + "/devices/claim_device?access_token=" + UserUtils.getAccessToken(context),
                        paramStr, null, new HttpUtils.OnHttpRequestCallBack() {
                            @Override
                            public void onHttpRequestSuccess(Object r) {
                                result = true;
                            }

                            @Override
                            public void onHttpRequestFail(int code, String errMsg) {
                                result = false;
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                throw new SetupStepException("Device save to cloud error.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // 提示异常
            throw new SetupStepException("Device save to cloud error.");
        }

    }

    @Override
    public boolean isStepFulfilled() {
        return result;
    }
}
