package io.cresco.library.messaging;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@XmlRootElement
public class MsgEvent {
    public enum Type {
        CONFIG, DISCOVER, ERROR, EXEC, GC, INFO, KPI, LOG, WATCHDOG
    }

    private Type msgType;
    private String msgRegion;
    private String msgAgent;
    private String msgPlugin;
    private Map<String, String> params;

    public MsgEvent() {

    }

    public MsgEvent(Type msgType, String msgRegion, String msgAgent, String msgPlugin, String msgBody) {
        this.msgType = msgType;
        this.msgRegion = msgRegion;
        this.msgAgent = msgAgent;
        this.msgPlugin = msgPlugin;
        this.params = new HashMap<String, String>();
        params.put("msg", msgBody);
    }

    public MsgEvent(Type msgType, String msgRegion, String msgAgent, String msgPlugin, Map<String, String> params) {
        this.msgType = msgType;
        this.msgRegion = msgRegion;
        this.msgAgent = msgAgent;
        this.msgPlugin = msgPlugin;
        this.params = params;
        this.params = new HashMap<String, String>(params);
    }

    public void setSrc(String region, String agent, String plugin) {
        setParam("src_region", region);
        setParam("src_agent", agent);
        setParam("src_plugin", plugin);
    }

    public void setDst(String region, String agent, String plugin) {
        setParam("dst_region", region);
        setParam("dst_agent", agent);
        setParam("dst_plugin", plugin);
    }

    public void setReturn() {
        String src_region = getParam("src_region");
        String src_agent = getParam("src_agent");
        String src_plugin = getParam("src_plugin");

        removeParam("src_region");
        removeParam("src_agent");
        removeParam("src_plugin");

        if (getParam("dst_region") != null) {
            setParam("src_region", getParam("dst_region"));
        }
        if (getParam("dst_agent") != null) {
            setParam("src_agent", getParam("dst_agent"));
        }
        if (getParam("dst_plugin") != null) {
            setParam("src_plugin", getParam("dst_plugin"));
        }

        if (src_region != null) {
            setParam("dst_region", src_region);
        } else {
            removeParam("dst_region");
        }
        setMsgRegion(src_region);
        if (src_agent != null) {
            setParam("dst_agent", src_agent);
        } else {
            removeParam("dst_agent");
        }
        setMsgAgent(src_agent);
        if (src_plugin != null) {
            setParam("dst_plugin", src_plugin);
        } else {
            removeParam("dst_plugin");
        }
        setMsgPlugin(src_plugin);
    }

    public String getMsgBody() {
        return params.get("msg");
    }

    public void setMsgBody(String msgBody) {
        params.put("msg", msgBody);
    }

    @XmlJavaTypeAdapter(MsgEventTypesAdapter.class)
    public Type getMsgType() {
        return msgType;
    }

    public void setMsgType(Type msgType) {
        this.msgType = msgType;
    }

    public String getMsgRegion() {
        return msgRegion;
    }

    public void setMsgRegion(String msgRegion) {
        this.msgRegion = msgRegion;
    }

    public String getMsgAgent() {
        return msgAgent;
    }

    public void setMsgAgent(String msgAgent) {
        this.msgAgent = msgAgent;
    }

    public String getMsgPlugin() {
        return msgPlugin;
    }

    public void setMsgPlugin(String msgPlugin) {
        this.msgPlugin = msgPlugin;
    }

    @XmlJavaTypeAdapter(MsgEventParamsAdapter.class)
    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void removeParam(String key) {
        params.remove(key);
    }

    public void setCompressedParam(String key, String value) {
        params.put(key, DatatypeConverter.printBase64Binary(stringCompress(value)));
    }

    public String getCompressedParam(String key) {
        String value = params.get(key);
        if (value == null)
            return null;
        try {
            byte[] exportDataRawCompressed = DatatypeConverter.parseBase64Binary(value);
            InputStream iss = new ByteArrayInputStream(exportDataRawCompressed);
            InputStream is = new GZIPInputStream(iss);
            return new Scanner(is,"UTF-8").useDelimiter("\\A").next();
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] stringCompress(String str) {
        byte[] dataToCompress = str.getBytes(StandardCharsets.UTF_8);
        byte[] compressedData;
        try {
            ByteArrayOutputStream byteStream =
                    new ByteArrayOutputStream(dataToCompress.length);
            try {
                GZIPOutputStream zipStream =
                        new GZIPOutputStream(byteStream);
                try {
                    zipStream.write(dataToCompress);
                }
                finally {
                    zipStream.close();
                }
            } finally {
                byteStream.close();
            }
            compressedData = byteStream.toByteArray();
        } catch(Exception e) {
            return null;
        }
        return compressedData;
    }
}