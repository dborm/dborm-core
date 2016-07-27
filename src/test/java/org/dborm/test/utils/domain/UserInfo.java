package org.dborm.test.utils.domain;


import org.dborm.core.annotation.Column;
import org.dborm.core.annotation.Relation;
import org.dborm.core.annotation.Table;
import org.dborm.core.domain.BaseDomain;

import java.util.Date;
import java.util.List;

@Table
public class UserInfo extends BaseDomain {

    @Column(isPrimaryKey = true)
    private String id;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column(defaultValue = "18")
    private Integer age;

    @Column
    private Date createTime;

    @Relation
    BookInfo bookInfo;

    @Relation
    List<BookInfo> bookInfos;

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

    public List<BookInfo> getBookInfos() {
        return bookInfos;
    }

    public void setBookInfos(List<BookInfo> bookInfos) {
        this.bookInfos = bookInfos;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }
}
