package com.dachen.commons.file.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载的文件实体
 *
 * @author zhanghui
 * @date 2013-3-8
 */
public class DownloadFileData {

    private String filename;
    private String mimeType;
    private String charset;
    private InputStream input;
    private Map<String, Object> params = new HashMap<String, Object>();

    public DownloadFileData(InputStream input) throws IOException {
        this.input = input;
    }

    public InputStream getInputStream() throws IOException {
        return input;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setParam(String key, Object value) {
        params.put(key, value);
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
