package com.hx.steven.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.hx.steven.BuildConfig;
import com.hx.steven.app.BaseApplication;
import com.hx.steven.util.BitmapUtil;
import com.hx.steven.util.JsonHelp;
import com.hx.steven.web.WebManager;
import com.hx.steven.wxShare.AuthCallback;
import com.hx.steven.wxShare.ShareCallback;
import com.hx.steven.wxShare.WxAuthDto;
import com.hx.steven.wxShare.WxShareBean;
import com.hx.steven.wx_ali_Pay.wx.WXPay;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WxManager {
    private static final String TAG = "WxManager";
    private static WxManager instance;
    private String wxWeChatId = BuildConfig.wxWeChatId;
    private String wxWeChatSec = BuildConfig.wxWeChatSec;
    private WXPay wxPay;

    private WxManager() {
    }

    public static WxManager getInstance() {
        if (instance == null) {
            synchronized (WxManager.class) {
                if (instance == null) {
                    instance = new WxManager();
                }
            }
        }
        return instance;
    }

    private AuthCallback authCallback;
    private ShareCallback shareCallback;
    private String loginSate = "wechat_sdk_login";
    private final String TRANSACTION_TEXT = "text";
    private final String TRANSACTION_IMAGE = "image";
    private final String TRANSACTION_WEBPAGE = "webpage";
    private final String TRANSACTION_MINI_PROGRAM = "mini_program";

    private final int IMAGE_URL = 0;
    private final int IMAGE_BASE64 = 1;
    private static IWXAPI api;

    public void regToWx() {
        // ??????WXAPIFactory???????????????IWXAPI?????????
        api = WXAPIFactory.createWXAPI(Objects.requireNonNull(BaseApplication.Companion.getAppContext()), wxWeChatId);
        //?????????????????????????????????????????????????????????
        BaseApplication.Companion.getAppContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // ??????app???????????????
                api.registerApp(wxWeChatId);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    /**
     * ????????????????????????
     *
     * @param authCallback
     */
    public void sendAuth(AuthCallback authCallback) {
        this.authCallback = authCallback;
        if (getApi().isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = loginSate;
            getApi().sendReq(req);

        } else {
            authCallback.onFailure(100, "???????????????");
        }
    }

    /**
     * ????????????
     *
     * @param resp
     */
    public void authReturn(SendAuth.Resp resp) {
        //????????????
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = resp.code;
                //??????????????????
                // TODO: 2020/12/31 ??????openId??????????????????????????????????????????
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://??????????????????
                authCallback.onFailure(resp.errCode, "??????????????????");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://????????????
                authCallback.onFailure(resp.errCode, "????????????");
                break;
            default:
                authCallback.onFailure(resp.errCode, "????????????");
                break;
        }
    }

    /**
     * ????????????
     *
     * @param resp
     */
    public void shareReturn(SendMessageToWX.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                shareCallback.onSuccess("1");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://????????????
                shareCallback.onFailure("0", "????????????");
                break;
            default:
                shareCallback.onFailure("0", "????????????");
                break;
        }
    }

    /**
     * ??????code??????AccessToken
     *
     * @param code
     */
    private void getAccessToken(String code) {
        //????????????
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(wxWeChatId)
                .append("&secret=")
                .append(wxWeChatSec)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        SimpleNetManager.getInstance().doGetRequestAsync(loginUrl.toString(), (isSuccess, result) -> {
            if (isSuccess) {
                String access = null;
                String openId = null;

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");

                } catch (Exception e) {
                    authCallback.onFailure(102, result);
                    e.printStackTrace();
                }

                getUserInfo(access, openId);
            } else {
                authCallback.onFailure(101, result);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param access
     * @param openId }
     */
    private void getUserInfo(String access, String openId) {
        String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openId;
        SimpleNetManager.getInstance().doGetRequestAsync(getUserInfo, (isSuccess, result) -> {
            if (isSuccess) {
                WxAuthDto wxAuthDto = JsonHelp.json2Bean(result, WxAuthDto.class);
                wxAuthDto.setFrom("android");
                authCallback.onSuccess(JsonHelp.toJson(wxAuthDto));
            } else {
                authCallback.onFailure(103, result);
            }
        });
    }

    public String getLoginSate() {
        return loginSate;
    }

    public IWXAPI getApi() {
        return api;
    }


    private String buildTransaction(String type) {
        return TextUtils.isEmpty(type) ? String.valueOf(System.currentTimeMillis()) : (type + System.currentTimeMillis());
    }

    /**
     * ????????????
     *
     * @param data
     */
    public void shareText(String data, ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
        //??????????????? WXTextObject ????????????????????????????????????
        WXTextObject textObj = new WXTextObject();
        textObj.text = data;
        //??? WXTextObject ????????????????????? WXMediaMessage ??????
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TRANSACTION_TEXT);
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        //??????api??????????????????????????????
        getApi().sendReq(req);
    }

    /**
     * ????????????
     */
    public void shareImage(int imageType, final int scene, WxShareBean bean, ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
        if (imageType == IMAGE_BASE64) {
            Bitmap bmp = base64ToBitmap(bean.getImgUrl());
            executeShareImag(bmp, scene);
        } else if (imageType == IMAGE_URL) {
            SimpleNetManager.getInstance().doGetRequestAsyncByte(bean.getImgUrl(), ((isSuccess, result) -> {
                if (isSuccess) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
                    executeShareImag(bmp, scene);
                } else {
                    Toast.makeText(BaseApplication.Companion.getAppContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    private void executeShareImag(Bitmap bmp, int scene) {

        //????????? WXImageObject ??? WXMediaMessage ??????
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //???????????????
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        msg.thumbData = bmpToByteArray(BitmapUtil.imageZoom(thumbBmp, 30 * 1024), true);
        bmp.recycle();

        //????????????Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TRANSACTION_IMAGE);
        req.message = msg;
        req.scene = scene;
        //??????api??????????????????????????????
        getApi().sendReq(req);
    }

    private byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    public static Bitmap base64ToBitmap(String base64String) {
        String base64 = base64String.split(",")[1];
        byte[] decode = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    /**
     * ????????????
     */
    public void shareWeb(final int scene, final WxShareBean bean, ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
        SimpleNetManager.getInstance().doGetRequestAsyncByte(bean.getImgUrl(), (isSuccess, result) -> {
            if (isSuccess) {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = bean.getLink();
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = scene == SendMessageToWX.Req.WXSceneTimeline ? bean.getGroupTitle() : bean.getTitle();
                msg.description = bean.getDesc();
                msg.thumbData = result;
                //????????????Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction(TRANSACTION_WEBPAGE);
                req.message = msg;
                req.scene = scene;
                //??????api??????????????????????????????
                getApi().sendReq(req);
            } else {
                Toast.makeText(BaseApplication.Companion.getAppContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ???????????????
     */
    public void shareMiniProgram(final WxShareBean bean, ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            SimpleNetManager.getInstance().doGetRequestAsyncByte(bean.getImgUrl(), (isSuccess, result) -> {
                if (isSuccess) {
                    executeShareMiniProgram(result, bean);
                } else {
                    Toast.makeText(BaseApplication.Companion.getAppContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Bitmap bitmap = BitmapUtil.imageZoom(BitmapUtil.ImageCrop(
                    BitmapUtil.captureByView(WebManager.getInstance().getWebView())
                    , 5, 4, true), 100 * 1024);
            executeShareMiniProgram(bmpToByteArray(bitmap, true), bean);
        }
    }

    public void executeShareMiniProgram(byte[] bytes, WxShareBean bean) {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = bean.getLink(); // ??????????????????????????????
        miniProgramObj.miniprogramType = bean.getmType();// ?????????:0????????????:1????????????:2
        miniProgramObj.userName = bean.getUserName();     // ???????????????id
        miniProgramObj.path = bean.getPath();            //?????????????????????
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = bean.getTitle();                    // ???????????????title
        msg.description = bean.getDesc();               // ???????????????desc
        msg.thumbData = bytes; // ????????????????????????????????????128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TRANSACTION_MINI_PROGRAM);
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // ?????????????????????
        getApi().sendReq(req);
    }

    /**
     * ????????????
     */
    public void wxPay(String payInfo) {
        wxPay = new WXPay(BaseApplication.Companion.getAppContext());
        Map<String, Object> maps = new HashMap<>();
        maps.put("data", payInfo);
        wxPay.setDataMap(maps);
        wxPay.setWeChatId(wxWeChatId);
        wxPay.toPay();
    }

    public void destroyWx() {
        wxPay.DestroyWx();
    }
}
