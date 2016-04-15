package cn.cocho.dborm.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shk
 * 16/1/22 下午3:03
 */
public class BaseDomain {

    private Map<String, Object> params = new HashMap<String, Object>();

    public void putParam(String key, Object obj) {
        params.put(key, obj);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }




}
