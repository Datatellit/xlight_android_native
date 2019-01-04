package com.umarbhutta.xlightcompanion.okHttp;

/**
 * Created by guangbinw on 2017/3/12.
 */

public class NetConfig {

    public static final String SERVER_IP = "www.baidu.com";
    public static final String SERVER_ADDRESS_DOMAIN = "http://211.159.174.249:8082";
    public static final String DCI_ADDRESS_DOMAIN = "http://211.159.174.249:8081";


//    public static final String SERVER_IP = "192.168.1.138";
//    public static final String SERVER_ADDRESS_DOMAIN = "http://192.168.1.138:8082";
//    public static final String DCI_ADDRESS_DOMAIN = "http://192.168.1.138:8081";
//
//    public static final String SERVER_ADDRESS_DS = "http://192.168.1.138:8063";

//    public static final String SERVER_IP = "www.baidu.com";
//    public static final String SERVER_ADDRESS_DOMAIN = "http://dmi.datatellit.com";
//    public static final String DCI_ADDRESS_DOMAIN = "http://iot.datatellit.com";

    /**
     * 正式版开关
     */
    public static final boolean isDebug = true;

    /**
     * 数据解析错误
     */
    public static final int ERROR_PARSE = -100;

    /**
     * 网络异常
     */
    public static final int ERROR_NET_ERROT = -101;


    public static final String ERROR_PARSE_MSG = "数据解析异常";

    /**
     * 登录
     */
    public static final String URL_LOGIN = SERVER_ADDRESS_DOMAIN + "/users/login";
    /**
     * 匿名登录
     */
    public static final String URL_ANONYMOUS_LOGIN = SERVER_ADDRESS_DOMAIN + "/anonymous/login";
    /**
     * 注册
     */
    public static final String URL_REGISTER = SERVER_ADDRESS_DOMAIN + "/users";


    /**
     * 首页基本信息
     */
    public static final String URL_FIRST_PAGE_INFO = SERVER_ADDRESS_DOMAIN + "/devices/?access_token=";
    /**
     * 修改密码
     */
    public static final String URL_MODIFY_PWD = SERVER_ADDRESS_DOMAIN + "/users/";
    /**
     * 解绑设备
     */
    public static final String URL_UNBIND_DEVICE = SERVER_ADDRESS_DOMAIN + "/devicenodes/";
    /**
     * 解绑controller
     */
    public static final String URL_UNBIND_CONTROLLER = SERVER_ADDRESS_DOMAIN + "/devices/";
    /**
     * 设置主设备
     */
    public static final String URL_SET_MAIN_DEVICE = SERVER_ADDRESS_DOMAIN + "/devices/%s/setmaindevice?access_token=%s";
    /**
     * 设备详细信息
     */
    public static final String URL_DEVICE_DETAIL_INFO = SERVER_ADDRESS_DOMAIN + "/devices/";
    /**
     * 设备规则列表
     */
    public static final String URL_DEVICE_RULES_LIST = SERVER_ADDRESS_DOMAIN + "/rules/?access_token=";

    /**
     * 删除规则
     */
    public static final String URL_DELETE_RULE = SERVER_ADDRESS_DOMAIN + "/rules/";

    /**
     * 启用、禁用规则
     */
    public static final String URL_RULE_SWITCH = SERVER_ADDRESS_DOMAIN + "/rules/";
    /**
     * 场景列表
     */
    public static final String URL_SCENE_LIST = SERVER_ADDRESS_DOMAIN + "/scenes?access_token=";
    /**
     * 场景详细
     */
    public static final String URL_SCENARIOS = SERVER_ADDRESS_DOMAIN + "/scenarios/?access_token=";
    /**
     * 添加场景
     */
    public static final String URL_ADD_SCENE = SERVER_ADDRESS_DOMAIN + "/scenes?access_token=";
    /**
     * 删除场景
     */
    public static final String URL_DELETE_SCENE = SERVER_ADDRESS_DOMAIN + "/scenes/";
    /**
     * 添加设备
     */
    public static final String URL_ADD_DEVICE = SERVER_ADDRESS_DOMAIN + "/devices/claim_device?access_token=";
    /**
     * 检查设备状态
     */
    public static final String URL_CHECK_DEVICE = DCI_ADDRESS_DOMAIN + "/v1/devices/%s?access_token=%s";
    /**
     * 切换场景
     */
    public static final String URL_CHANGE_SCENE = SERVER_ADDRESS_DOMAIN + "/scenes/%s/active?access_token=%s";
    /**
     * 忘记密码--发送验证码
     */
    public static final String URL_SEND_VERIFICATION_CODE = SERVER_ADDRESS_DOMAIN + "/users/sendverificationcode";
    /**
     * 重置密码
     */
    public static final String URL_RESET_PWD = SERVER_ADDRESS_DOMAIN + "/users/updatepassword";
    /**
     * 创建规则
     */
    public static final String URL_CREATE_RULES = SERVER_ADDRESS_DOMAIN + "/rules?access_token=";
    /**
     * 修改个人信息
     */
    public static final String URL_MODIFY_USER_INFO = SERVER_ADDRESS_DOMAIN + "/users/";
    /**
     * 上传照片
     */
    public static final String URL_UPLOAD_IMG = SERVER_ADDRESS_DOMAIN + "/users/";
    /**
     * 注册协议帮助的url
     */
    public static final String URL_GET_REGISTER_URL = SERVER_ADDRESS_DOMAIN + "/users/regagreement";
    /**
     * 获取帮助的url
     */
    public static final String URL_GET_HELP_URL = SERVER_ADDRESS_DOMAIN + "/users/help";
    /**
     * 报表url
     */
    public static final String URL_GET_REPORT_FORM = SERVER_ADDRESS_DOMAIN + "/users/report?access_token=";
    /**
     * 灯开关接口
     */
    public static final String URL_LAMP_SWITCH = SERVER_ADDRESS_DOMAIN + "/devices/";
    /**
     * 启动条件列表及详细信息
     */
    public static final String URL_RULES_RULECONDITIONS = SERVER_ADDRESS_DOMAIN + "/rules/ruleconditions";
    /**
     * 编辑场景
     */
    public static final String URL_EDIT_SCENE = SERVER_ADDRESS_DOMAIN + "/scenes/%s?access_token=%s";
    /**
     * 编辑设备信息
     */
    public static final String URL_EDIT_DEVICE_INFO = SERVER_ADDRESS_DOMAIN + "/devicenodes/%s?access_token=%s";
    /**
     * 配置摇一摇信息
     */
    public static final String URL_CONFIG_SHAKE_INFO = SERVER_ADDRESS_DOMAIN + "/shakes?access_token=";
    /**
     * 触发摇一摇动作
     */
    public static final String URL_ACTION_SHAKE = SERVER_ADDRESS_DOMAIN + "/shakes/shakestart?access_token=";

    /**
     * 获取摇一摇配置信息
     */
    public static final String URL_GET_CONFIG_SHAKE_INFO = SERVER_ADDRESS_DOMAIN + "/shakes?access_token=";
    /**
     * 编辑规则
     */
    public static final String URL_EDIT_RULE = SERVER_ADDRESS_DOMAIN + "/rules/";

    /**
     * 获取传感器信息
     */
    public static final String URL_GET_SENSOR = SERVER_ADDRESS_DOMAIN + "/devices/state?access_token=%s";

    /**
     * 添加共享
     */
    public static final String URL_ADD_SHARE = SERVER_ADDRESS_DOMAIN + "/shares?access_token=%s";

    /**
     * 获取共享
     */
    public static final String URL_GET_SHARE = SERVER_ADDRESS_DOMAIN + "/shares?access_token=%s&userId=%s&send=%s";

    /**
     * 更新共享
     */
    public static final String URL_UPDATE_SHARE = SERVER_ADDRESS_DOMAIN + "/shares/%s?access_token=%s";
}
