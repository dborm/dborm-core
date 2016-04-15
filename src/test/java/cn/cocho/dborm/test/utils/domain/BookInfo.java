package cn.cocho.dborm.test.utils.domain;

/**
 * Created by shk
 * 16/4/15 下午3:58
 */
public class BookInfo {

    private String bookId;//图书ID

    private String userId;//归属的用户ID

    private String bookName;//书名

    private Double price;//价格

    private Boolean looked;//是否看过

    private Long readTime;//阅读时间

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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
