package com.gordeev.applicationcontextlibrary.beandefinitionreader.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {

    private List<BeanDefinition> beanDefinitions;
    private BeanDefinition beanDefinition;
    private HashMap<String, String> map;
    private List<String> importPaths;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("bean")) {
            String idAtr = attributes.getValue("id");
            String classAtr = attributes.getValue("class");

            beanDefinition = new BeanDefinition();
            beanDefinition.setId(idAtr);
            beanDefinition.setBeanClassName(classAtr);
            map = new HashMap();

            if (beanDefinitions == null) {
                beanDefinitions = new ArrayList<>();
            }

        } else if (qName.equalsIgnoreCase("property")) {
            String nameAtr = attributes.getValue("name");
            String valueAtr = attributes.getValue("value");
            String refAtr = attributes.getValue("ref");

            if (valueAtr != null) {
                map.put(nameAtr, valueAtr);
            } else if (refAtr != null) {
                map.put(nameAtr, refAtr);
            }

        } else if (qName.equalsIgnoreCase("import")) {
            if (importPaths == null) {
                importPaths = new ArrayList<>();
            }
            importPaths.add(attributes.getValue("resource"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("bean")) {
            beanDefinition.setDependencies(map);
            beanDefinition.setRefDependencies(map);
            beanDefinitions.add(beanDefinition);
        }
    }

    public List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public String[] getImportPaths() {
        return importPaths != null ? importPaths.toArray(new String[importPaths.size()]) : null;
    }
}
