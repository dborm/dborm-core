package org.dborm.test.utils.domain;


import org.dborm.core.annotation.Column;
import org.dborm.core.annotation.Table;
import org.dborm.core.domain.BaseDomain;

/**
 * Created by shk
 * 16/4/15 下午3:58
 */
@Table
public class BookInfo extends BaseDomain {

    @Column(isPrimaryKey = true)
    private String id;

    @Column
    private String name;//书名

    @Column(isPrimaryKey = true)//一个表可以有多个主键字段,形成联合主键
    private String userId;//归属的用户ID

    @Column
    private Double price;//价格

    @Column
    private Boolean looked;//是否看过

    @Column
    private Long readTime;//阅读时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getLooked() {
        return looked;
    }

    public void setLooked(Boolean looked) {
        this.looked = looked;
    }

    public Long getReadTime() {
        return readTime;
    }

    public void setReadTime(Long readTime) {
        this.readTime = readTime;
    }
}
