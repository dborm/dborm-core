package org.dborm.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询结果集
 * Created by shk
 * 16/7/25 17:22
 */
public class QueryResult {

    /**
     * 用集合形势存储每一列的值
     */
    List<Object> resultList = new ArrayList<Object>();


    /**
     * 用键值对方式存储每一列的值
     * 键:列的别名
     * 值:列对应的值
     */
    Map<String, Object> resultMap = new HashMap<String, Object>();


    public void addResult(Object value) {
        resultList.add(value);
    }

    public void putResult(String columnLabel, Object value) {
        resultMap.put(columnLabel, value);
    }

    public Object getObject(String columnName) {
        return resultMap.get(columnName);
    }

    public Object getObject(int columnIndex) {
        return resultList.get(columnIndex);
    }

    public List<Object> getResultList() {
        return resultList;
    }

    public void setResultList(List<Object> resultList) {
        this.resultList = resultList;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
