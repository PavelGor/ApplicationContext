package com.gordeev.applicationcontextlibrary.reader.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParserHandler extends DefaultHandler {

    private List<BeanDefinition> beanDefinitions;

    private List<String> importPaths;

    @Override
    public void startElement(String uri, String localName, String tagName, Attributes attributes) {

        if (tagName.equalsIgnoreCase("bean")) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setDependencies(new HashMap<>());
            beanDefinition.setRefDependencies(new HashMap<>());
            if (beanDefinitions == null) {
                beanDefinitions = new ArrayList<>();
            }
            beanDefinitions.add(beanDefinition);

            String id = attributes.getValue("id");
            String clazz = attributes.getValue("class");

            beanDefinition.setId(id);
            beanDefinition.setBeanClassName(clazz);

        } else if (tagName.equalsIgnoreCase("property")) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            String reference = attributes.getValue("ref");
            BeanDefinition beanDefinition = beanDefinitions.get(beanDefinitions.size()-1);
            if (value != null) {
                beanDefinition.getDependencies().put(name, value);
            } else if (reference != null) {
                beanDefinition.getRefDependencies().put(name, reference);
            }

        } else if (tagName.equalsIgnoreCase("import")) {
            if (importPaths == null) {
                importPaths = new ArrayList<>();
            }
            importPaths.add(attributes.getValue("resource"));
        }
    }

    public List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public String[] getImportPaths() {
        return importPaths != null ? importPaths.toArray(new String[importPaths.size()]) : null;
    }
}
