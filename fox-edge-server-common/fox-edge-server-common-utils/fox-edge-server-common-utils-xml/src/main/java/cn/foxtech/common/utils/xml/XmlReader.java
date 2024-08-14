/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://blog.csdn.net/qq_42721085/article/details/117711508
 */
public class XmlReader {
    /**
     * xml 转换为 map
     *
     * @param xml xml文本
     * @return HashMap
     */
    public static Map<String, Object> parse(String xml) {
        return parse(xml, "xml", "");
    }

    /**
     * xml 转换为 map
     *
     * @param xml
     * @return
     */
    /**
     * xml 转换为 map
     *
     * @param xml              xml文本串
     * @param rootName         根节点名称
     * @param defaultNullValue 缺省的空数值
     * @return HashMap
     */
    public static Map<String, Object> parse(String xml, String rootName, String defaultNullValue) {
        Map<String, AtomicInteger> contextMap = new HashMap();

        Map<String, Object> map = null;
        StringReader stringReader = null;
        try {
            stringReader = new StringReader(xml);
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stringReader);
            map = doParse(reader, contextMap, rootName, defaultNullValue);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            if (null != stringReader) {
                try {
                    stringReader.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return map;
    }

    /**
     * 真正开始解析转换
     *
     * @param reader
     * @return
     * @throws Throwable
     */
    private static Map<String, Object> doParse(XMLStreamReader reader, Map<String, AtomicInteger> contextMap, String rootName, String defaultNullValue) throws Throwable {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> currentMap = map;
        int event = reader.getEventType();
        List<String> names = new ArrayList<String>();
        int taglength = 0;
        String tagName = null;
        String tagValue = defaultNullValue;
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    tagValue = defaultNullValue;
                    tagName = reader.getLocalName();
                    if (rootName.equals(tagName)) {
                        break;
                    }
                    names.add(tagName);
                    taglength++;

                    currentMap = map;
                    if (taglength > 1) {
                        for (int i = 0; i < taglength - 1; i++) {
                            Object object = currentMap.get(names.get(i));
                            if (null == object) {
                                object = new HashMap<String, Object>();
                                currentMap.put(names.get(i), object);
                                currentMap = (Map<String, Object>) object;
                            } else {
                                int currentTagNameSize = getSize(contextMap, i + 1 + "" + names.get(i));
                                if (currentTagNameSize > 1) {
                                    if (object instanceof Map) {
                                        List parentList = new ArrayList();
                                        parentList.add(object);
                                        Map tempMap = new HashMap();
                                        parentList.add(tempMap);
                                        currentMap.put(names.get(i), parentList);
                                        currentMap = tempMap;
                                    } else if (object instanceof List) {
                                        List parentList = (List) object;
                                        int parentListSize = parentList.size();
                                        if (parentListSize != currentTagNameSize) {
                                            Map tempMap = new HashMap();
                                            parentList.add(tempMap);
                                            currentMap = tempMap;
                                        } else {
                                            Map tempMap = (Map) parentList.get(parentList.size() - 1);
                                            currentMap = tempMap;
                                        }
                                    }
                                } else {
                                    currentMap = (Map<String, Object>) object;
                                }
                            }
                        }
                    }
                    add(contextMap, names.size() + tagName);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    tagValue = reader.getText();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    tagName = reader.getLocalName();
                    if (rootName.equals(tagName)) {
                        break;
                    }

                    currentMap = map;
                    if (taglength > 1) {
                        for (int i = 0; i < taglength - 1; i++) {
                            Object object = currentMap.get(names.get(i));
                            if (null == object) {
                                //nothing to do
                            } else {
                                if (object instanceof List) {
                                    List list = (List) object;
                                    currentMap = (Map) list.get(list.size() - 1);
                                } else if (object instanceof Map) {
                                    currentMap = (Map) object;
                                }
                            }
                        }
                    }

                    Object oldValue = currentMap.get(tagName);
                    if (!currentMap.containsKey(tagName)) {
                        currentMap.put(tagName, tagValue);
                        remove(contextMap, names.size() + tagName);
                    } else {
                        if (oldValue instanceof List) {
                            List list = (List) oldValue;
                            if (list.size() > 0) {
                                Object obj = list.get(0);
                                if (obj instanceof String) {
                                    ((List) oldValue).add(tagValue);
                                    remove(contextMap, names.size() + tagName);
                                }
                            }
                        } else if (oldValue instanceof Map) {

                        } else {
                            List tmpList = new ArrayList();
                            currentMap.put(tagName, tmpList);
                            tmpList.add(oldValue);
                            tmpList.add(tagValue);
                            remove(contextMap, names.size() + tagName);
                        }
                    }

                    tagValue = defaultNullValue;
                    names.remove(names.size() - 1);
                    taglength--;
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    break;
            }

            if (!reader.hasNext()) {
                break;
            }
            event = reader.next();
        }
        return map;
    }

    private static void add(Map<String, AtomicInteger> contextMap, String nodeName) {
        AtomicInteger integer = contextMap.get(nodeName);
        if (null == integer) {
            integer = new AtomicInteger(0);
            contextMap.put(nodeName, integer);
        }
        integer.incrementAndGet();
    }

    private static void remove(Map<String, AtomicInteger> contextMap, String nodeName) {
        AtomicInteger integer = contextMap.get(nodeName);
        if (null != integer) {
            integer.decrementAndGet();
        }
    }

    private static int getSize(Map<String, AtomicInteger> contextMap, String nodeName) {
        AtomicInteger integer = contextMap.get(nodeName);
        if (null == integer) {
            integer = new AtomicInteger(0);
            contextMap.put(nodeName, integer);
        }
        return integer.intValue();
    }

    public static void main(String[] args) {
        String str = "<xml>\n" + "<auth>\n" + "<randomStr>suiji</randomStr>\n" + "<token>3ed6b709d31e87af73216149dc510c6fc94975af</token>\n" + "</auth>\n" + "<datas>\n" + "<dataStatus>\n" + "<errorCode>0</errorCode>\n" + "<errormsg>发送成功</errormsg>\n" + "<seqId>1</seqId>\n" + "<lastModifyTime>2018-07-26 11:01:38</lastModifyTime>\n" + "</dataStatus>\n" + "<dataStatus>\n" + "<errorCode>1</errorCode>\n" + "<errormsg>发送失败，手机号为空</errormsg>\n" + "<seqId>2</seqId>\n" + "<lastModifyTime>2018-07-26 11:02:38</lastModifyTime>\n" + "</dataStatus>\n" + "</datas>\n" + "</xml>";
        Map<String, Object> parse = XmlReader.parse(str);
        System.out.println(parse);
    }
}
