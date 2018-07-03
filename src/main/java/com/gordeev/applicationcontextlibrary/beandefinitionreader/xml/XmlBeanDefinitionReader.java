package com.gordeev.applicationcontextlibrary.beandefinitionreader.xml;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private String[] paths;

    public XmlBeanDefinitionReader(String[] paths) {
        this.paths = paths;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        return readBeanDefinitions(paths);
    }

    private List<BeanDefinition> readBeanDefinitions(String[] paths) {
        for (String path : paths) {
            try {
                SAXParser saxParser = saxParserFactory.newSAXParser();
                MyHandler handler = new MyHandler();
                InputStream inputStream = new FileInputStream(new File(path));
                saxParser.parse(inputStream, handler);


                beanDefinitions.addAll(handler.getBeanDefinitions());

                String[] importPaths = handler.getImportPaths();
                if (importPaths != null) {
                    //TODO: Check for take(дубль) between importPaths and paths before method call
                    readBeanDefinitions(importPaths);
                }

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        return beanDefinitions;
    }
}
