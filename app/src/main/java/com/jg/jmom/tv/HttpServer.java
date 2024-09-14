package com.gecko.webview.tv;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private OnDataListener listener;

    public void setListener(OnDataListener listener) {
        this.listener = listener;
    }

    public HttpServer() throws IOException {
        super(8080);  // 监听局域网内的8080端口
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (Method.POST.equals(session.getMethod())) {
            try {
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);  // 解析 body 数据，文件和表单都在这里处理
                // 获取 POST 请求中的参数
                Map<String, String> postData = session.getParms();

                // 提取表单中的参数 "user_input"
                String userInput = postData.get("user_input");

                // 检查 userInput 是否为空
                if (userInput != null) {
                    if (listener != null) {
                        listener.onData(userInput);
                    }
                    return newFixedLengthResponse("配置完成");
                } else {
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "No user input found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal Error");
            }
        } else {
            String html = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background-color: #f4f4f9; }" +
                    ".container { background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1); max-width: 400px; width: 100%; text-align: center; }" +
                    "h1 { color: #333; margin-bottom: 20px; }" +
                    "form { display: flex; flex-direction: column; align-items: center; }" +
                    "input[type='search'] { padding: 18px 10px; margin-bottom: 20px; width: 100%; border: 1px solid #ccc; border-radius: 5px; font-size: 16px; }" +
                    "input[type='submit'] { padding: 18px; width: 100%; border: none; border-radius: 25px; background-color: #007BFF; color: white; font-size: 16px; cursor: pointer; transition: background-color 0.3s ease; }" +
                    "input[type='submit']:hover { background-color: #0056b3; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>配置URL</h1>" +
                    "<form action='/' method='POST'>" +
                    "<input type='search' name='user_input' placeholder='请输入地址' />" +
                    "<input type='submit' value='提交' />" +
                    "</form>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            return newFixedLengthResponse(html);
        }
    }

    public interface OnDataListener {
        void onData(String input);
    }
}
