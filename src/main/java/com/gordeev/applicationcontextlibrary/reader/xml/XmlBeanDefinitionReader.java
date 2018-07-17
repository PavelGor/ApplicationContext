package com.gordeev.applicationcontextlibrary.reader.xml;

import com.gordeev.applicationcontextlibrary.exception.BeanInstantiationException;
import com.gordeev.applicationcontextlibrary.reader.BeanDefinitionReader;
import com.gordeev.applicationcontextlibrary.entity.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.*;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {
    private static final Logger LOG = LoggerFactory.getLogger(XmlBeanDefinitionReader.class);
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    private List<String> paths = new ArrayList<>();


    public XmlBeanDefinitionReader(String[] paths) {
        this.paths.addAll(Arrays.asList(paths));
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        Queue<String> resourceFilesQueue = new LinkedList<>(paths);

        while (!resourceFilesQueue.isEmpty()){

            String pathToFile = resourceFilesQueue.remove();

            try {
                SAXParser saxParser = saxParserFactory.newSAXParser();
                XmlParserHandler xmlParserHandler = new XmlParserHandler();

                saxParser.parse(pathToFile, xmlParserHandler);

                beanDefinitions.addAll(xmlParserHandler.getBeanDefinitions());

                resourceFilesQueue.addAll(xmlParserHandler.getImportPaths());

            } catch (SAXException | ParserConfigurationException | IOException e) {
                LOG.error("Cannot parse file: {}, {}", pathToFile, e);
                throw new BeanInstantiationException("Cannot parse file: "+ pathToFile, e);
            }
        }

        return beanDefinitions;
    }
}
