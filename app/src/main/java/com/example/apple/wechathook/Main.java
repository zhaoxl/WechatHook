package com.example.apple.wechathook;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import android.os.Bundle;

public class Main implements IXposedHookLoadPackage {
    final String apiBaseUrl = "http://192.168.2.116:3000";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(loadPackageParam.packageName.equals("com.tencent.mm"))
        {
            Log.d("CallbackMethod", "进入微信");
//			final Class<?> pmClass = XposedHelpers.findClass("com.tencent.mm.protocal.c.pm", loadPackageParam.classLoader);
//			Log.d("CallbackMethod", "pmClass:"+pmClass.toString());
//			final Class<?> sClass = XposedHelpers.findClass("com.tencent.mm.plugin.messenger.foundation.a.s", loadPackageParam.classLoader);
//			Log.d("CallbackMethod", "sClass:"+sClass.toString());
//            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.messenger.foundation.c", loadPackageParam.classLoader, "a", pmClass, byte[].class, boolean.class, sClass, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                    try {
//                        Log.d("CallbackMethod", "com.tencent.mm.plugin.messenger.foundation.c.a_1");
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//                        Log.d("CallbackMethod", "params[1]:" + param.args[1]);
//                        Log.d("CallbackMethod", "params[2]:" + param.args[2]);
//                        Log.d("CallbackMethod", "params[3]:" + param.args[3]);
//                        int rtM = XposedHelpers.getIntField(param.args[0], "rtM");
//                        Log.d("CallbackMethod", "pmVar:" + rtM);
//                        byte[] bArr = (byte[])param.args[1];
//                        for (int i = 0; i < bArr.length; i++) {
//                            Log.d("CallbackMethod", "bArr["+i+"]:" + bArr[i]);
//                        }
//
//
//                    } catch (Throwable t) {
//                        XposedBridge.log(t);
//                    }
//                }
//            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.account.ui.LoginUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        Log.d("CallbackMethod", "com.tencent.mm.plugin.account.ui.LoginUI.onCreate");
                        //账号框
                        Field eRXField = XposedHelpers.findField(param.thisObject.getClass(), "eRX");
                        final android.widget.EditText eRX = (android.widget.EditText) eRXField.get(param.thisObject);
                        //密码框
                        Field eRYField = XposedHelpers.findField(param.thisObject.getClass(), "eRY");
                        final android.widget.EditText eRY = (android.widget.EditText) eRYField.get(param.thisObject);
                        //向服务获取此设备绑定的微信账号密码
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,String> map=new HashMap<>();
//                                map.put("unique_id", Utlis.getUniqueID());

                                HttpUtlis.getRequest(apiBaseUrl+"/api/v1/devices/get_account.json?unique_id="+Utlis.getUniqueID(), map, "utf-8", new OnResponseListner() {
                                    @Override
                                    public void onSucess(String response) {
                                        try
                                        {
                                            JSONObject responseJson = new JSONObject(response);
                                            if(responseJson.getInt("code") == 200)
                                            {
                                                final JSONObject dataJson = responseJson.getJSONObject("data");
                                                if(dataJson.getString("result").equals("OK"))
                                                {
                                                    ((Activity)param.thisObject).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try{
                                                                eRX.setText(dataJson.getString("wx_account"));
                                                                eRY.setText(dataJson.getString("wx_pwd"));
                                                            }
                                                            catch (org.json.JSONException error)
                                                            {
                                                                //
                                                            }
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    Log.d("Hook::Handle", "devices_get_account: WARNING: result:"+dataJson.getString("result"));
                                                    //返回错误原因，退出微信
                                                }
                                            }
                                            else
                                            {
                                                Log.d("Hook::Handle", "devices_get_account: WARNING: code:"+responseJson.getString("code"));
                                                //服务器错误，退出微信
                                            }
                                        }
                                        catch (org.json.JSONException error)
                                        {
                                            Log.d("HookCommonException", error.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.d("CallApiError", "devices_get_account: ERROR: "+error);
                                    }
                                });

                            }
                        }).start();


                    } catch (Throwable t) {
                        XposedBridge.log(t);
                    }
                }
            });


            final Class<?> aClass = XposedHelpers.findClass("com.tencent.mm.ab.d.a", loadPackageParam.classLoader);
//            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.messenger.foundation.c", loadPackageParam.classLoader, "a", aClass, sClass, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                    try {
//                        Log.d("CallbackMethod", "com.tencent.mm.plugin.messenger.foundation.c.a_2");
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//                        Log.d("CallbackMethod", "params[1]:" + param.args[1]);
//                        Object aVar2 = XposedHelpers.getObjectField(param.args[0], "dIN");
//                        Log.d("CallbackMethod", "aVar2:" + aVar2.toString());
//                        Log.d("CallbackMethod", "aVar2.rcq:" + XposedHelpers.getObjectField(aVar2, "rcq"));
//                        Log.d("CallbackMethod", "aVar2.rci:" + XposedHelpers.getObjectField(aVar2, "rci"));
//                        Log.d("CallbackMethod", "aVar2.rcr:" + XposedHelpers.getObjectField(aVar2, "rcr"));
//                        Log.d("CallbackMethod", "aVar2.hcd:" + XposedHelpers.getObjectField(aVar2, "hcd"));
//                        Log.d("CallbackMethod", "aVar2.jQd:" + XposedHelpers.getObjectField(aVar2, "jQd"));
//                        Log.d("CallbackMethod", "aVar2.lOH:" + XposedHelpers.getObjectField(aVar2, "lOH"));
//                        Log.d("CallbackMethod", "aVar2.jQd:" + XposedHelpers.getObjectField(aVar2, "jQd"));
//                        Log.d("CallbackMethod", "aVar2.rcm:" + XposedHelpers.getObjectField(aVar2, "rcm"));
//                        Object aVar2_rcj = XposedHelpers.getObjectField(aVar2, "rcj");
//                        Object aVar2_rck = XposedHelpers.getObjectField(aVar2, "rck");
//                        final Class<?> abClass = XposedHelpers.findClass("com.tencent.mm.platformtools.ab", loadPackageParam.classLoader);
//                        String a = (String)XposedHelpers.callStaticMethod(abClass, "a", aVar2_rcj);
//                        String a2 = (String)XposedHelpers.callStaticMethod(abClass, "a", aVar2_rck);
//                        Log.d("CallbackMethod", "rcj:" + aVar2_rcj);
//                        Log.d("CallbackMethod", "rck:" + aVar2_rck);
//                        Log.d("CallbackMethod", "发送微信号：a:" + a);
//                        Log.d("CallbackMethod", "接收微信号：a2:" + a2);
//
//                        final Class<?> iClass = XposedHelpers.findClass("com.tencent.mm.plugin.messenger.foundation.a.i", loadPackageParam.classLoader);
//                        Log.d("CallbackMethod", "i.class:" + iClass.getClass());
//                        final Class<?> gClass = XposedHelpers.findClass("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
//                        Object gl_result = XposedHelpers.callStaticMethod(gClass, "l", iClass);
//                        Log.d("CallbackMethod", "gl_result:" + gl_result);
//                        Object bcY_result = XposedHelpers.callMethod(gl_result, "bcY");
//                        Log.d("CallbackMethod", "bcY_result:" + bcY_result );
//                        Object byVar = XposedHelpers.getObjectField(param.args[0], "dIN");
//                        Object byVar_rcj = XposedHelpers.getObjectField(byVar, "rcj");
//                        Object byVar_rcq = XposedHelpers.getObjectField(byVar, "rcq");
//                        Object ab_a_result = XposedHelpers.callStaticMethod(abClass, "a", byVar_rcj);
//                        Object I = XposedHelpers.callMethod(bcY_result, "I", ab_a_result, byVar_rcq);
//                        Log.d("CallbackMethod", "I:" + I);
//                        Object field_msgId = XposedHelpers.getObjectField(I, "field_msgId");
//                        Log.d("CallbackMethod", "I.field_msgId:" + field_msgId);
//                        String field_content = (String)XposedHelpers.getObjectField(I, "field_content");
//                        Log.d("CallbackMethod", "I.field_content:" + field_content);
//                        String field_talker = (String)XposedHelpers.getObjectField(I, "field_talker");
//                        Log.d("CallbackMethod", "I.field_talker:" + field_talker);
//
//                        Map z = (Map)XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.bl", loadPackageParam.classLoader), "z", field_content, "msg");
//
//
//                        if(field_content.indexOf("<img ") > 0)
//                        {
//                            final Class<?> oClass = XposedHelpers.findClass("com.tencent.mm.ak.o", loadPackageParam.classLoader);
//                            Object o_pf_result = XposedHelpers.callStaticMethod(oClass, "Pf");
//                            Object b = XposedHelpers.callMethod(o_pf_result, "b", Long.valueOf("1".toString()));
//                            Long dTR = XposedHelpers.getLongField(b, "dTR");
//                            Object iVar = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mm.modelcdntran.i", loadPackageParam.classLoader));
//                            Class <?> dClass = XposedHelpers.findClass("com.tencent.mm.modelcdntran.d", loadPackageParam.classLoader);
//                            Object dVk = XposedHelpers.callStaticMethod(dClass, "a", "downimg", dTR, field_talker, field_msgId.toString());
//
//                            XposedHelpers.setObjectField(iVar, "field_mediaId", dVk);
////                            iVar.field_mediaId = this.dVk;
//                            Object b_dTl = XposedHelpers.getObjectField(b, "dTL");
//                            Object dVn = XposedHelpers.callMethod(o_pf_result, "o", b_dTl, null, ""); // o.Pf().o(this.dVm, null, "");
//                            XposedHelpers.setObjectField(iVar, "field_fullpath", dVn);
////                            iVar.field_fullpath = this.dVn;
//                            Object dVl = 2;
//                            XposedHelpers.setObjectField(iVar, "field_fileType", dVl);
////                            iVar.field_fileType = this.dVl;
//                            Class<?> bi = XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.bi", loadPackageParam.classLoader);
//                            int dHI = (int)XposedHelpers.callStaticMethod(bi, "getInt", (String) z.get(".msg.img.$length"), 0);
//                            XposedHelpers.setObjectField(iVar, "field_totalLen", dHI);
////                            iVar.field_totalLen = this.dHI;
//                            String aesk = (String) z.get(".msg.img.$aeskey");
//                            XposedHelpers.setObjectField(iVar, "field_aesKey", aesk);
////                            iVar.field_aesKey = str;
//                            String str2 = (String) z.get(".msg.img.$cdnmidimgurl");
//                            XposedHelpers.setObjectField(iVar, "field_fileId", str2);
////                            iVar.field_fileId = str2;
//                            XposedHelpers.setObjectField(iVar, "field_priority", 2);
////                            iVar.field_priority = com.tencent.mm.modelcdntran.b.dOk;
//                            Object dVu = null;
//                            XposedHelpers.setObjectField(iVar, "dPV", dVu);
////                            iVar.dPV = this.dVu;
//                            Boolean fq = (Boolean)XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.model.s", loadPackageParam.classLoader), "fq", field_talker);
//                            XposedHelpers.setObjectField(iVar, "field_chattype", fq ? 1 : 0); // s.fq(this.bGS.field_talker) ? 1 : 0;
////                            if (!bi.oW(str3)) {
////                                str = (String) z.get(".msg.img.$tpauthkey");
////                                iVar.field_fileType = 19;
////                                iVar.field_authKey = str;
////                                iVar.dPW = str3;
////                                iVar.field_fileId = "";
////                            }
////                            x.i(this.TAG, "cdnautostart %s %b", "image_" + this.bGS.field_msgId, Boolean.valueOf(com.tencent.mm.modelcdntran.g.ND().dPa.contains("image_" + this.bGS.field_msgId)));
////                            if (com.tencent.mm.modelcdntran.g.ND().dPa.contains("image_" + this.bGS.field_msgId)) {
////                                com.tencent.mm.modelcdntran.g.ND().dPa.remove("image_" + this.bGS.field_msgId);
//
//                            XposedHelpers.setBooleanField(iVar, "field_autostart", true);
////                                iVar.field_autostart = true;
////                            } else {
////                                iVar.field_autostart = false;
////                            }
//
//                            Log.d("CallbackMethod", "iVar.field_mediaId:" + XposedHelpers.getObjectField(iVar, "field_mediaId"));
//                            Log.d("CallbackMethod", "iVar.field_fullpath:" + XposedHelpers.getObjectField(iVar, "field_fullpath"));
//                            Log.d("CallbackMethod", "iVar.field_fileType:" + XposedHelpers.getObjectField(iVar, "field_fileType"));
//                            Log.d("CallbackMethod", "iVar.field_totalLen:" + XposedHelpers.getObjectField(iVar, "field_totalLen"));
//                            Log.d("CallbackMethod", "iVar.field_aesKey:" + XposedHelpers.getObjectField(iVar, "field_aesKey"));
//                            Log.d("CallbackMethod", "iVar.field_fileId:" + XposedHelpers.getObjectField(iVar, "field_fileId"));
//                            Log.d("CallbackMethod", "iVar.field_priority:" + XposedHelpers.getObjectField(iVar, "field_priority"));
//                            Log.d("CallbackMethod", "iVar.dPV:" + XposedHelpers.getObjectField(iVar, "dPV"));
//                            Log.d("CallbackMethod", "iVar.field_chattype:" + XposedHelpers.getObjectField(iVar, "field_chattype"));
//                            Log.d("CallbackMethod", "iVar.field_autostart:" + XposedHelpers.getObjectField(iVar, "field_autostart"));
//                            Log.d("CallbackMethod", "iVar.field_mediaId:" + XposedHelpers.getObjectField(iVar, "field_mediaId"));
//                            Object ND = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.modelcdntran.g", loadPackageParam.classLoader), "ND");
//                            Boolean b_result = (Boolean)XposedHelpers.callMethod(ND, "b", iVar, -1);
//                            Log.d("CallbackMethod", "b_result:" + b_result);
//                            if(b_result)
//                            {
//                                Log.d("CallbackMethod", "原图下载成功，图片地址:" + dVn);
//
//                            }
//                            else
//                            {
//                                Log.d("CallbackMethod", "原图下载失败");
//                            }
////                            com.tencent.mm.modelcdntran.g.ND().b(iVar, -1)
//                        }
//
//
//
//                    } catch (Throwable t) {
//                        XposedBridge.log(t);
//                    }
//                }
//            });


//            XposedHelpers.findAndHookMethod("com.tencent.mm.modelcdntran.d", loadPackageParam.classLoader, "a", String.class, long.class, String.class, String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                    try {
//                        Log.d("CallbackMethod", "com.tencent.mm.modelcdntran.d.a");
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//                        Log.d("CallbackMethod", "params[1]:" + param.args[1]);
//                        Log.d("CallbackMethod", "params[2]:" + param.args[2]);
//                        Log.d("CallbackMethod", "params[3]:" + param.args[3]);
//                        Log.d("CallbackMethod", "result:"+param.getResult());
//
//                    } catch (Throwable t) {
//                        XposedBridge.log(t);
//                    }
//                }
//            });
//
//            final Class<?> networkEClass = XposedHelpers.findClass("com.tencent.mm.network.e", loadPackageParam.classLoader);
//            final Class<?> qClass = XposedHelpers.findClass("com.tencent.mm.network.q", loadPackageParam.classLoader);
//            final Class<?> eClass = XposedHelpers.findClass("com.tencent.mm.ab.e", loadPackageParam.classLoader);
//            final Class<?> kClass = XposedHelpers.findClass("com.tencent.mm.ak.k", loadPackageParam.classLoader);
//
//            Log.d("CallbackMethod", "networkEClass:"+networkEClass+"-----  eClass:"+eClass+"-----  kClass:"+kClass);
//			XposedHelpers.findAndHookMethod("com.tencent.mm.ak.k", loadPackageParam.classLoader, "a", networkEClass, eClass, new XC_MethodHook() {
//				@Override
//				protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//					try {
//						Log.d("CallbackMethod", "com.tencent.mm.ak.k.a:"+param.toString());
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//                        Log.d("CallbackMethod", "params[1]:" + param.args[1]);
//
//                        Object dlN = XposedHelpers.getObjectField(param.thisObject, "dlN");
//                        final Class<?> oClass = XposedHelpers.findClass("com.tencent.mm.ak.o", loadPackageParam.classLoader);
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.oClass:" + oClass);
//                        Object o_pf_result = XposedHelpers.callStaticMethod(oClass, "Pf");
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.o_pf_result:" + o_pf_result);
//                        Object b = XposedHelpers.callMethod(o_pf_result, "b", Long.valueOf(dlN.toString()));
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.b:" + b);
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.dlN:" + dlN);
//                        Object dVn = XposedHelpers.getObjectField(param.thisObject, "dVn");
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.dVn:" + dVn);
//                        Object dVl = XposedHelpers.getObjectField(param.thisObject, "dVl");
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.dVl:" + dVl);
//                        Object dHI = XposedHelpers.getObjectField(param.thisObject, "dHI");
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.dHI:" + dHI);
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.b.dTL:" + XposedHelpers.getObjectField(b, "dTL"));
//                        Log.d("CallbackMethod", "com.tencent.mm.ak.k.a.b.dTO:" + XposedHelpers.getObjectField(b, "dTO"));
//
//
//
//                        Log.d("CallbackMethod", "result:"+param.getResult());
//					} catch (Throwable t) {
//						XposedBridge.log(t);
//					}
//				}
//			});
//
//            XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                    try {
//                        Log.d("CallbackMethod", "com.tencent.mm.sdk.platformtools.x.d:"+param.toString());
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//                        Log.d("CallbackMethod", "params[1]:" + param.args[1]);
//                        Log.d("CallbackMethod", "params[2]:" + param.args[2]);
//                        Object[] objArr = (Object[])param.args[2];
//                        if(objArr != null)
//                        {
//                            for (int i = 0; i < objArr.length; i++) {
//                                Log.d("CallbackMethod", "objArr["+i+"]:" + objArr[i]);
//                            }
//                        }
//
//                    } catch (Throwable t) {
//                        XposedBridge.log(t);
//                    }
//                }
//            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", loadPackageParam.classLoader, "aYS", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        Log.d("CallbackMethod", "profile.ui.ContactInfoUI.aYS");
                        Object guS = XposedHelpers.getObjectField(param.thisObject, "guS");
                        Log.d("CallbackMethod", "profile.ui.ContactInfoUI.aYS.guS:" + guS);
                        Log.d("CallbackMethod", "profile.ui.ContactInfoUI.aYS.guS.field_alias:" + XposedHelpers.getObjectField(guS, "field_alias"));
                        XposedHelpers.setObjectField(guS, "field_alias", "****");

                    } catch (Throwable t) {
                        XposedBridge.log(t);
                    }
                }
            });

            // hook微信插入数据的方法
            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", loadPackageParam.classLoader, "insertWithOnConflict", String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable { // 打印插入数据信息
                    Log.d("SQLiteDatabase", "------------------------insert start---------------------" + "\n\n");
                    Log.d("SQLiteDatabase", "param args1:" + param.args[0]);
                    Log.d("SQLiteDatabase", "param args1:" + param.args[1]);
                    ContentValues contentValues = (ContentValues) param.args[2];
                    Log.d("SQLiteDatabase", "param args3 contentValues:");
                    for (Map.Entry<String, Object> item : contentValues.valueSet()) {
                        if (item.getValue() != null) {
                            Log.d("SQLiteDatabase", item.getKey() + "---------" + item.getValue().toString());
                        } else {
                            Log.d("SQLiteDatabase", item.getKey() + "---------" + "null");
                        }
                    }
                    Log.d("SQLiteDatabase", "------------------------insert over---------------------" + "\n\n");

                    //截获消息
                    switch (param.args[0].toString())
                    {
                        //保存消息
                        case "message":
                            String msgId = contentValues.getAsString("msgId");
                            String msgType = contentValues.getAsString("type");
                            String isSend = contentValues.getAsString("isSend");
                            String talker = contentValues.getAsString("talker");
                            String md5 = contentValues.getAsString("md5");
                            String createTime = contentValues.getAsString("createTime");
                            String content = contentValues.getAsString("content");

                            Map<String,String> map=new HashMap<>();
                            map.put("msg_id", msgId);
                            map.put("unique_id", Utlis.getUniqueID());
                            map.put("talker", talker);
                            map.put("msg_type", msgType);
                            map.put("raw", param.args[2].toString());
                            map.put("content", content);
                            map.put("create_time", createTime);
                            map.put("is_send", isSend);
                            map.put("map_json", new JSONObject(map).toString());
                            Log.d("SQLiteDatabase", "message_save_result: OK");
                            HttpUtlis.postJsonRequest(apiBaseUrl+"/api/v1/messages/save.json", new JSONObject(map).toString(), "utf-8", new OnResponseListner() {
                                @Override
                                public void onSucess(String response) {
                                    Log.d("SQLiteDatabase", "message_save_result: OK");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("SQLiteDatabase", "message_save_result: ERROR");
                                }
                            });
                            break;
                        //上传图片
                        case "MediaDuplication":
                            md5 = contentValues.getAsString("md5");
                            String path = contentValues.getAsString("path");
                            map=new HashMap<>();
                            map.put("unique_id", Utlis.getUniqueID());
                            map.put("md5", md5);
                            HttpUtlis.postFileRequest(apiBaseUrl+"/api/v1/messages/upload_image", map, path, "image/jpeg", new OnResponseListner() {
                                @Override
                                public void onSucess(String response) {
                                    Log.d("SQLiteDatabase", "upload_image_result: OK");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("SQLiteDatabase", "upload_image_result: ERROR");
                                }
                            });
                            break;
                        //上传语音文件
                        case "WxFileIndex2":
                            path = "/storage/emulated/0/tencent/MicroMsg/" + contentValues.getAsString("path");
                            if(path.contains(".amr"))
                            {
                                msgId = contentValues.getAsString("msgId");
                                map=new HashMap<>();
                                map.put("msg_id", msgId);
                                map.put("unique_id", Utlis.getUniqueID());
                                HttpUtlis.postFileRequest(apiBaseUrl+"/api/v1/messages/upload_voice", map, path, "audio/amr", new OnResponseListner() {
                                    @Override
                                    public void onSucess(String response) {
                                        Log.d("SQLiteDatabase", "upload_voice_result: OK");
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.d("SQLiteDatabase", "upload_voice_result: ERROR");
                                    }
                                });
                            }
                            break;
                        //更新转账记录
                        case "RemittanceRecord":
                            msgId = contentValues.getAsString("locaMsgId");
                            String state = contentValues.getAsString("receiveStatus");
                            isSend = contentValues.getAsString("isSend");
                            map=new HashMap<>();
                            map.put("msg_id", msgId);
                            map.put("unique_id", Utlis.getUniqueID());
                            map.put("state", state);
                            map.put("is_send", isSend);
                            HttpUtlis.postRequest(apiBaseUrl+"/api/v1/messages/add_remittance_record", map, "utf-8", new OnResponseListner() {
                                @Override
                                public void onSucess(String response) {
                                    Log.d("SQLiteDatabase", "add_remittance_result: OK");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("SQLiteDatabase", "add_remittance_result: ERROR");
                                }
                            });
                            break;
                        //更新红包记录
                        case "WalletLuckyMoney":
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String,String> map=new HashMap<>();
                                    ContentValues contentValues = (ContentValues) param.args[2];
                                    map.put("unique_id", Utlis.getUniqueID());
                                    map.put("hb_status", contentValues.getAsString("hbStatus"));
                                    map.put("hb_type", contentValues.getAsString("hbType"));
                                    map.put("receive_status", contentValues.getAsString("receiveStatus"));
                                    map.put("receive_time", contentValues.getAsString("receiveTime"));
                                    map.put("receive_amount", contentValues.getAsString("receiveAmount"));
                                    map.put("content", contentValues.getAsString("mNativeUrl"));

                                    Log.d("SQLiteDatabase", "add_wallet_lucky_money_record_params: "+new JSONObject(map));
                                    HttpUtlis.postRequest(apiBaseUrl+"/api/v1/messages/add_wallet_lucky_money_record", map, "utf-8", new OnResponseListner() {
                                        @Override
                                        public void onSucess(String response) {
                                            Log.d("SQLiteDatabase", "add_wallet_lucky_money_record_result: OK:"+response);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.d("SQLiteDatabase", "add_wallet_lucky_money_record_result: ERROR:"+error);
                                        }
                                    });
                                }
                            }).start();
                            break;
                    }
                }
            });


//            //显示消息Hook
//            final Class<?> messageClass = XposedHelpers.findClass("android.os.Message", loadPackageParam.classLoader);
//            XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ai", loadPackageParam.classLoader, "dispatchMessage", messageClass, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                    try {
//                        Log.d("CallbackMethod", "com.tencent.mm.sdk.platformtools.ai.dispatchMessage:"+param.toString());
//                        Log.d("CallbackMethod", "params[0]:" + param.args[0]);
//
//                    } catch (Throwable t) {
//                        XposedBridge.log(t);
//                    }
//                }
//            });

        }


    }
}


