package cn.cocho.dborm.test.utils.domain;

import cn.cocho.dborm.annotation.Column;
import cn.cocho.dborm.annotation.Table;
import cn.cocho.dborm.domain.BaseDomain;

import java.util.Date;

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
}
