package org.dborm.core.schema;

import org.dborm.core.domain.ColumnBean;
import org.dborm.core.domain.TableBean;
import org.dborm.core.framework.Cache;
import org.dborm.core.utils.StringUtilsDborm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 操作Schema
 *
 * @author COCHO
 * @time 2013-5-23下午3:25:42
 */
public class DbormSchemaInit {

    StringUtilsDborm stringUtils = new StringUtilsDborm();


    /**
     * 初始化描述文件
     *
     * @param schemaBasePath 描述文件所在的基础路径（将会扫描该路径下的所有子文件）
     * @throws Exception
     */
    public void initSchema(String schemaBasePath) throws Exception {
        List<String> schemaFiles = getSchemaFiles(schemaBasePath);
        for (String schemaFile : schemaFiles) {
            String schemaFilePath = schemaBasePath + File.separator + schemaFile;
            InputStream schemaInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaFilePath);
            Cache.getCache().putAllTablesCache(getSchemaByFile(schemaInputStream));
        }
    }

    /**
     * 通过描述文件初始化（该函数用于Android端等环境使用）
     *
     * @param schemasInputStream 描述文件的输入流
     * @throws Exception
     */
    public void initSchema(List<InputStream> schemasInputStream) throws Exception {
        for (InputStream schemaInputStream : schemasInputStream) {
            Cache.getCache().putAllTablesCache(getSchemaByFile(schemaInputStream));
        }
    }

    private List<String> getSchemaFiles(String schemaBasePath) throws Exception {
        List<String> schemaFiles = new ArrayList<String>();
        URL url = Thread.currentThread().getContextClassLoader().getResource(schemaBasePath);
        if (url != null) {
            File file = new File(url.toURI());
            String[] files = file.list();
            if (files != null) {
                for (String fileName : files) {
                    if (fileName.toLowerCase().endsWith(".xml")) {// 避免不必要的文件产生干扰，如.svn文件
                        schemaFiles.add(fileName);
                    }
                }
            }
        }
        return schemaFiles;
    }

    private Map<String, TableBean> getSchemaByFile(InputStream schemaInputStream) throws Exception {
        Map<String, TableBean> tables = new HashMap<String, TableBean>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            Document document = builder.parse(schemaInputStream);
            if (document != null) {
                Element root = document.getDocumentElement();// 获得根元素
                NodeList methodList = root.getElementsByTagName(SchemaConstants.TABLE);// 获得名称为method的元素集合
                for (int i = 0; i < methodList.getLength(); i++) {// 遍历节点 DocumentBuilder builder=null;  DocumentBuilder builder=null; 
                    Element table = (Element) methodList.item(i);
                    String classPath = getStringMustAttributeValue(table, SchemaConstants.TABLE_CLASS_PATH);
                    String name = getStringAttributeValue(table, SchemaConstants.TABLE_NAME);
                    if (name == null) {
                        String className = classPath.substring(classPath.lastIndexOf("\\."));
                        stringUtils.humpToUnderlineName(className);
                    }
                    TableBean tableDomain = new TableBean();
                    tableDomain.setClassPath(classPath);
                    tableDomain.setTableName(name);
                    tableDomain.setColumns(getColumnDomains(tableDomain, table));
                    tableDomain.setRelation(getRelations(tableDomain, table));
                    tables.put(classPath, tableDomain);
                }
            }
        } finally {
            schemaInputStream.close();
        }
        return tables;
    }

    /**
     * 获得列属性集合
     *
     * @param tableDomain 表结构对象
     * @param table       表标签信息
     * @return 列对象集合
     * @author COCHO
     * @time 2013-5-23下午3:27:02
     */
    private Map<String, ColumnBean> getColumnDomains(TableBean tableDomain, Element table) {
        Map<String, ColumnBean> fieldList = tableDomain.getColumns();
        NodeList columnList = table.getElementsByTagName(SchemaConstants.COLUMN);
        for (int i = 0; i < columnList.getLength(); i++) {
            ColumnBean columnDomain = new ColumnBean();
            Element column = (Element) columnList.item(i);
            String fieldName = getStringMustAttributeValue(column, SchemaConstants.COLUMN_FIELD_NAME);
            columnDomain.setFieldName(fieldName);
            columnDomain.setPrimaryKey(getBooleanAttributeValue(column, SchemaConstants.COLUMN_IS_PRIMARY_KEY));
            columnDomain.setDefaultValue(getDefaultValue(column));
            String columnName = stringUtils.humpToUnderlineName(fieldName);
            fieldList.put(columnName, columnDomain);
        }
        return fieldList;
    }

    private Set<String> getRelations(TableBean tableDomain, Element table) {
        Set<String> relations = tableDomain.getRelation();
        NodeList relationList = table.getElementsByTagName(SchemaConstants.RELATION);
        for (int j = 0; j < relationList.getLength(); j++) {
            Element relation = (Element) relationList.item(j);
            String fieldName = getStringMustAttributeValue(relation, SchemaConstants.RELATION_FIELD_NAME);
            relations.add(fieldName);
        }
        return relations;
    }

    /**
     * 获得必填属性的值
     *
     * @param column        列标签信息
     * @param attributeName 属性名称
     * @return 属性值，如果没有值则抛出异常
     * @author COCHO
     * @time 2013-5-24下午4:25:23
     */
    private String getStringMustAttributeValue(Element column, String attributeName) {
        Node node = column.getAttributes().getNamedItem(attributeName);
        if (node != null) {
            return node.getNodeValue();
        } else {
            throw new IllegalArgumentException("The attribute[" + attributeName + "] in " + column.getNamespaceURI() + " can't be null !");
        }
    }

    /**
     * 获得字符串类型属性的值
     *
     * @param column        列标签信息
     * @param attributeName 属性名称
     * @return 对应的值或null
     * @author COCHO
     * @time 2013-5-24下午4:25:37
     */
    private String getStringAttributeValue(Element column, String attributeName) {
        Node node = column.getAttributes().getNamedItem(attributeName);
        if (node != null) {
            return node.getNodeValue();
        } else {
            return "";
        }
    }

    /**
     * 获得布尔类型的值
     *
     * @param column        列标签信息
     * @param attributeName 属性名称
     * @return 如果有值则取值，默认为false
     * @author COCHO
     * @time 2013-5-24下午4:26:13
     */
    private boolean getBooleanAttributeValue(Element column, String attributeName) {
        Node node = column.getAttributes().getNamedItem(attributeName);
        return node != null && Boolean.parseBoolean(node.getNodeValue());
    }

    /**
     * 获得设置的默认值
     *
     * @param column 列标签信息
     * @return 默认值，如果为设置则为null
     * @author COCHO
     */
    private Object getDefaultValue(Element column) {
        Node node = column.getAttributes().getNamedItem(SchemaConstants.COLUMN_DEFAULT_VALUE);
        if (node != null) {
            return node.getNodeValue();
        }
        return null;
    }

}
