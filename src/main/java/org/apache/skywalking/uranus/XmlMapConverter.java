package org.apache.skywalking.uranus;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML-MAP converter
 * <p>
 * Created by S.Violet on 2016/3/29.
 */
public class XmlMapConverter {

    private static final String DEFAULT_XML_ROOT_NAME = "Document";
    private static final String DEFAULT_XML_ENCODING = "GBK";
    private static final OutputFormat DEFAULT_OUTPUT_FORMAT;
    private static final String DEFAULT_LIST_NAME = "List";

    static {
        DEFAULT_OUTPUT_FORMAT = new OutputFormat("  ", true);
        DEFAULT_OUTPUT_FORMAT.setEncoding(DEFAULT_XML_ENCODING);
    }

    /**
     * MAP->XML
     *
     * @param map      map
     * @param rootName Root node name
     * @return XML(String)
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> map, String rootName) throws Exception {
        return mapToXml(map, rootName, DEFAULT_OUTPUT_FORMAT);
    }

    /**
     * MAP->XML
     *
     * @param map          map
     * @param rootName     Root node name
     * @param outputFormat Output format
     * @return XML(String)
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> map, String rootName, OutputFormat outputFormat) throws Exception {
        if (map == null) {
            throw new RuntimeException("[XmlMapConverter]can't convert null map into xml");
        }
        if (rootName == null || rootName.length() <= 0) {
            rootName = DEFAULT_XML_ROOT_NAME;
        }
        Element root = DocumentHelper.createElement(rootName);
        Document document = DocumentHelper.createDocument(root);

        convert(map, root, rootName);

        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        xmlWriter.write(document);
        xmlWriter.close();
        return stringWriter.toString();
    }

    private static void convert(Object obj, Element element, String elementName) {
        if (obj == null || element == null) {
            return;
        }
        if (obj instanceof Map) {
            if (((Map) obj).size() <= 0) {
                return;
            }
            for (Object entry : ((Map) obj).entrySet()) {
                String name = String.valueOf(((Map.Entry) entry).getKey());
                Object data = ((Map.Entry) entry).getValue();
                if (data instanceof Map) {
                    convert(data, element.addElement(name), name);
                } else if (data instanceof List) {
                    convert(data, element, name);
                } else {
                    Element subElement = element.addElement(name);
                    if (data != null) {
                        subElement.addText(String.valueOf(data));
                    }
                }
            }
        } else if (obj instanceof List) {
            if (((List) obj).size() <= 0) {
                return;
            }
            for (Object data : ((List) obj)) {
                if (data instanceof Map) {
                    convert(data, element.addElement(elementName), elementName);
                } else if (data instanceof List) {
                    convert(data, element.addElement(elementName), DEFAULT_LIST_NAME);
                } else {
                    Element subElement = element.addElement(elementName);
                    if (data != null) {
                        subElement.addText(String.valueOf(data));
                    }
                }
            }
        }
    }

    public static Map<String, Object> xmlToMap(String xml) throws Exception {
        if (xml == null || xml.length() <= 0) {
            throw new Exception("[XmlMapConverter]can't convert null xml into map");
        }
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        Map<String, Object> map = new HashMap<String, Object>();
        convert(root, map);
        return map;
    }

    private static void convert(Element element, Map<String, Object> map) {
        if (element == null || map == null) {
            return;
        }
        List subElements = element.elements();
        if (subElements == null || subElements.size() <= 0) {
            return;
        }
        for (Object subElement : subElements) {
            if (!(subElement instanceof Element)) {
                continue;
            }
            String name = ((Element) subElement).getName();
            Object data;
            if (((Element) subElement).isTextOnly()) {
                data = ((Element) subElement).getData();
            } else {
                data = new HashMap<String, Object>();
                convert((Element) subElement, (Map<String, Object>) data);
            }
            Object preData = map.remove(name);
            if (preData != null) {
                if (preData instanceof List) {
                    ((List) preData).add(data);
                    map.put(name, preData);
                } else {
                    List list = new ArrayList();
                    list.add(preData);
                    list.add(data);
                    map.put(name, list);
                }
            } else if (data != null) {
                map.put(name, data);
            }
        }
    }

}
