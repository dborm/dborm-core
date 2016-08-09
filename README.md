Dborm框架简介
=====

## Dborm-core 介绍

Dborm-core 是由科巧「研发团队」开发的数据库操作框架Dborm的核心框架，具有轻量级，简洁易用，功能强大等特点，开源基于MIT协议，允许自由使用和修改代码。

## 1 入门部署和体验

### 1.1 下载框架源码

到官网下载 Dborm 最新版：[[官网地址]](http://www.dborm.org "官网下载地址")   
或者直接通过Github[[下载Dborm-core框架源码]](https://github.com/dborm/dborm-core "下载Dborm-core框架源码")

### 1.2 将项目导入开发工具
使用Intellij IDEA导入之后项目结构如下:

![部署成功](doc/images/dborm-core-fram.png)

### 1.3 将如下脚本导入MySQL数据库
将dborm-core/src/test/resources/dborm/dborm-test.sql脚本导入MySQL数据库
脚本内容如下:
```sql
CREATE TABLE `user_info` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `is_boy` int(11) DEFAULT NULL,
  `user_name` varchar(64) DEFAULT NULL,
  `birthday` bigint(20) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `login_num` int(11) DEFAULT '0',
  `user_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `qsm_info` (
  `content` varchar(64) DEFAULT NULL,
  `user_id` varchar(64) DEFAULT NULL,
  `question_id` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `qsm_option` (
  `content` varchar(64) DEFAULT NULL,
  `show_order` float DEFAULT NULL,
  `attachment_count` int(11) DEFAULT NULL,
  `user_id` varchar(64) DEFAULT NULL,
  `option_id` varchar(64) NOT NULL DEFAULT '',
  `question_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8


```

### 1.4 修改数据源
修改dborm-core/src/test/java/org/dborm/test/utils/db/DataBaseManager.java文件中的数据源为自己的数据源
修改如下内容:

```java

 private Connection createConnection() {
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/dborm-test?useUnicode=true&characterEncoding=utf8";
    String username = "root";
    String password = "";


    Connection conn = null;
    try {
        Class.forName(driver);// 加载驱动程序
        conn = DriverManager.getConnection(url, username, password);// 连续数据库
    } catch (Exception e) {
        e.printStackTrace();
    }

    return conn;
}
    
```

### 1.5 运行测试用例
dborm-core/src/test/java/org/dborm/test/目录下有一系列的测试用例,几乎覆盖了框架的所有功能,可以单个运行,通过测试用例来学习框架的使用


## 2 详细文档

Dborm 官网：[http://www.dborm.org](http://www.dborm.org "Dborm 官网")

Dborm-core Github地址：[https://github.com/dborm/dborm-core](https://github.com/dborm/dborm-core "Dborm-core源码地址")

Dborm-web Github地址：[https://github.com/dborm/dborm-web](https://github.com/dborm/dborm-web "Dborm-web源码地址")

Dborm-android Github地址：[https://github.com/dborm/dborm-android](https://github.com/dborm/dborm-android "Dborm-android源码地址")

## 3 联系我们

Email：[dborm@cocho.cn](mailto://email:dborm@cocho.cn "发邮件给 Dborm 开发组")

Issue：[github issue](https://github.com/dborm/dborm-core/issues "Dborm 论坛")
