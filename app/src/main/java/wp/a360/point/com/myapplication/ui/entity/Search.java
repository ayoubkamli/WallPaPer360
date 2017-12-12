package wp.a360.point.com.myapplication.ui.entity;

/**
 * 搜索关键字Bean
 * Created by DN on 2017/12/6.
 */

public class Search {
    private String keyWordName; //关键字名称
    private int keyWordType;//关键字类型 :用于字体背景，颜色的区分 0：默认  1：字体颜色改变  2：背景改变，字体为白色

    public String getKeyWordName() {
        return keyWordName;
    }

    public void setKeyWordName(String keyWordName) {
        this.keyWordName = keyWordName;
    }

    public int getKeyWordType() {
        return keyWordType;
    }

    public void setKeyWordType(int keyWordType) {
        this.keyWordType = keyWordType;
    }
}
