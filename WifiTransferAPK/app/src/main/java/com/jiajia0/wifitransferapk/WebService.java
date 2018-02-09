package com.jiajia0.wifitransferapk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.body.UrlEncodedFormBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;

/**
 * Created by Leafage on 2018/2/9 13:40.
 * DESCRIPTION :
 */

public class WebService extends Service {

    static final String ACTION_START_WEB_SERVICE = "com.jiajia0.wifitransferapk.action.START_WEB_SERVICE";
    static final String ACTION_STOP_WEB_SERVICE = "com.jiajia0.wifitransferapk.action.STOP_WEB_SERVICE";

    private static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    private static final String JS_CONTENT_TYPE = "application/javascript";
    private static final String PNG_CONTENT_TYPE = "application/x-png";
    private static final String JPG_CONTENT_TYPE = "application/jpeg";
    private static final String SWF_CONTENT_TYPE = "application/x-shockwave-flash";
    private static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
    private static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
    private static final String MP3_CONTENT_TYPE = "audio/mp3";
    private static final String MP4_CONTENT_TYPE = "video/mpeg4";

    private AsyncHttpServer mAsyncHttpServer = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    private FileUploadHolder mFileUploadHolder = new FileUploadHolder();

    /**
     * @param context
     * 开启Service
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, WebService.class);
        intent.setAction(ACTION_START_WEB_SERVICE);
        context.startService(intent);
    }

    /**
     * @param context
     * 关闭Service
     */
    public static void stop(Context context) {
        Intent intent = new Intent(context, WebService.class);
        intent.setAction(ACTION_STOP_WEB_SERVICE);
        context.startService(intent);
    }

    /**
     * @param intent
     * @param flags
     * @param startId
     * Service运行
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START_WEB_SERVICE.equals(action)) {
                startService();
            } else if (ACTION_STOP_WEB_SERVICE.equals(action)){
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startService() {
        // 加载css、js、images资源
        mAsyncHttpServer.get("/images/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                sendResources(request, response);
            }
        });
        mAsyncHttpServer.get("/scripts/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                sendResources(request, response);
            }
        });
        mAsyncHttpServer.get("/css/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                sendResources(request, response);
            }
        });
        // 主界面
        mAsyncHttpServer.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    response.send(getIndexContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(500).end();
                }
            }
        });

        //查询文件列表
        mAsyncHttpServer.get("/files", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONArray array = new JSONArray();
                File dir = Constants.DIR;
                if (dir.exists() && dir.isDirectory()) {
                    String[] fileNames = dir.list();
                    if (fileNames != null) {
                        for (String fileName : fileNames) {
                            File file = new File(dir, fileName);
                            if (file.exists() && file.isFile()) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("name", fileName);
                                    long fileLen = file.length();
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    if (fileLen > 1024 * 1024) {
                                        jsonObject.put("size", df.format(fileLen * 1f / 1024 / 1024) + "MB");
                                    } else if (fileLen > 1024) {
                                        jsonObject.put("size", df.format(fileLen * 1f / 1024) + "KB");
                                    } else {
                                        jsonObject.put("size", fileLen + "B");
                                    }
                                    array.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                response.send(array.toString());
            }
        });

        // 删除文件
        mAsyncHttpServer.post("/files/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                final UrlEncodedFormBody body = (UrlEncodedFormBody) request.getBody();
                if ("delete".equalsIgnoreCase(body.get().getString("_method"))) {
                    String path = request.getPath().replace("/files/", "");
                    try {
                        path = URLDecoder.decode(path, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    File file = new File(Constants.DIR, path);
                    if (file.exists() && file.isFile()) {
                        file.delete();
                        RxBus.get().post(Constants.RxBusEventType.LOAD_APK_LIST, 0);
                    }
                }
                response.end();
            }
        });

        // 下载文件
        mAsyncHttpServer.get("/files/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String path = request.getPath().replace("/files/", "");
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                File file = new File(Constants.DIR, path);
                if (file.exists() && file.isFile()) {
                    try {
                        response.getHeaders().add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    response.sendFile(file);
                    return;
                }
                response.code(404).send("Not found!");
            }
        });

        // 上传文件
        mAsyncHttpServer.post("/files", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                body.setMultipartCallback(new MultipartFormDataBody.MultipartCallback() {
                    @Override
                    public void onPart(Part part) {
                        if (part.isFile()) {
                            body.setDataCallback(new DataCallback() {
                                @Override
                                public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                    mFileUploadHolder.write(bb.getAllByteArray());
                                    bb.recycle();
                                }
                            });
                        } else {
                            if (body.getDataCallback() == null) {
                                body.setDataCallback(new DataCallback() {
                                    @Override
                                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                        try {
                                            String fileName = URLDecoder.decode(new String(bb.getAllByteArray()), "UTF-8");
                                            mFileUploadHolder.setFileName(fileName);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        bb.recycle();
                                    }
                                });
                            }
                        }
                    }
                });
                request.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        mFileUploadHolder.reset();
                        response.end();
                        RxBus.get().post(Constants.RxBusEventType.LOAD_APK_LIST, 0);
                    }
                });
            }
        });

        mAsyncHttpServer.get("/progress/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONObject res = new JSONObject();

                String path = request.getPath().replace("/progress/", "");

                if (path.equals(mFileUploadHolder.fileName)) {
                    try {
                        res.put("fileName", mFileUploadHolder.fileName);
                        res.put("size", mFileUploadHolder.totalSize);
                        res.put("progress", mFileUploadHolder.fileOutPutStream == null ? 1 : 0.1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                response.send(res);
            }
        });

        mAsyncHttpServer.listen(mAsyncServer, Constants.HTTP_PORT);
    }

    /**
     * @return
     * 加载主页界面
     * @throws IOException
     */
    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open("wifi/index.html"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendResources(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
        try {
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20", " ");
            String resourceName = fullPath;
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"));
            }
            if (!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                response.setContentType(getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bInputStream = new BufferedInputStream(getAssets().open("wifi/" + resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(404).end();
            return;
        }
    }

    private String getContentTypeByResourceName(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".js")) {
            return JS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".swf")) {
            return SWF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".png")) {
            return PNG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return JPG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".woff")) {
            return WOFF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".ttf")) {
            return TTF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".svg")) {
            return SVG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE;
        }
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAsyncHttpServer != null) {
            mAsyncServer.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
    }
}
