package com.gordeev.applicationcontextlibrary.beandefinitionreader.xml;

import com.gordeev.applicationcontextlibrary.beandefinitionreader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(XmlBeanDefinitionReader.class);
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
                    importPaths = deleteDoublesWithPaths(importPaths);
                    readBeanDefinitions(importPaths);
                }

            } catch (IOException e) {
                LOG.error("Cannot open file: {}", path, e);
            } catch (ParserConfigurationException | SAXException e) {
                LOG.error("Error in {}", SAXParser.class, e);
            }
        }
        return beanDefinitions;
    }

    //public - only for tests
    public String[] deleteDoublesWithPaths(String[] importPaths) {
        String[] newPaths;
        int countEmpties = 0;

        for (int i = 0; i < importPaths.length; i++) {
            for (String pathI : paths) {
                if(importPaths[i].equals(pathI)){
                    importPaths[i] = "";
                    countEmpties++;
                }
            }
        }
        newPaths = new String[importPaths.length - countEmpties];
        for (int i = 0, j = 0; i < importPaths.length; i++) {
            if (!importPaths[i].equals("")){
                newPaths[j] = importPaths[i];
                j++;
            }
        }
        return newPaths;
    }
}
