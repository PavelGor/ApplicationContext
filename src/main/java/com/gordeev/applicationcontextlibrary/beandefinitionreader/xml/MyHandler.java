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
    private HashMap<String, String> mapVar;
    private HashMap<String, String> mapRef;
    private List<String> importPaths;

    @Override
    public void startElement(String uri, String localName, String tagName, Attributes attributes) {

        if (tagName.equalsIgnoreCase("bean")) {
            String attributeId = attributes.getValue("id");
            String attributeClass = attributes.getValue("class");

            beanDefinition = new BeanDefinition();
            beanDefinition.setId(attributeId);
            beanDefinition.setBeanClassName(attributeClass);

            if (beanDefinitions == null) {
                beanDefinitions = new ArrayList<>();
            }
            mapVar = new HashMap();
            mapRef = new HashMap();

        } else if (tagName.equalsIgnoreCase("property")) {
            String attributeName = attributes.getValue("name");
            String attributeValue = attributes.getValue("value");
            String attributeRef = attributes.getValue("ref");

            if (attributeValue != null) {
                mapVar.put(attributeName, attributeValue);
            } else if (attributeRef != null) {
                mapRef.put(attributeName, attributeRef);
            }

        } else if (tagName.equalsIgnoreCase("import")) {
            if (importPaths == null) {
                importPaths = new ArrayList<>();
            }
            importPaths.add(attributes.getValue("resource"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String tagName) {
        if (tagName.equalsIgnoreCase("bean")) {
            beanDefinition.setDependencies(mapVar);
            beanDefinition.setRefDependencies(mapRef);
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
