package org.dborm.test.utils.domain;

import org.dborm.core.domain.BaseDomain;

import java.util.Date;

public class SelectModel extends BaseDomain {

    private String id;

    private String name;

    private String nickname;

    private Integer age;

    private Date createTime;

    private String bookId;

    private String bookName;//书名

    private Double price;//价格

    private Boolean looked;//是否看过

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
}
