package org.dborm.test.utils.domain;

import org.dborm.core.domain.BaseDomain;

import java.util.Date;
import java.util.List;

/**
 * Created by shk
 * 16/5/16 13:53
 */
public class User extends BaseDomain{

    private String id;

    private String name;

    private String nickname;

    private Integer age;

    private Date createTime;

    BookInfo bookInfo;

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

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public List<BookInfo> getBookInfos() {
        return bookInfos;
    }

    public void setBookInfos(List<BookInfo> bookInfos) {
        this.bookInfos = bookInfos;
    }
}
