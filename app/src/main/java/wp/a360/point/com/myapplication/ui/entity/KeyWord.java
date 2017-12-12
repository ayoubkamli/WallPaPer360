package wp.a360.point.com.myapplication.ui.entity;

import java.io.Serializable;

/**
 * Created by DN on 2017/12/9.
 */

public class KeyWord implements Serializable {
    //关键词搜索id
    private Integer keyWordID;
    //搜索的关键词名字
    private String keyWordName;
    //关键字类型 :用于区分字体背景，颜色的区分 0：默认  1：字体颜色改变  2：背景改变，字体为白色
    private Integer keyWordType;

    public Integer getKeyWordID() {
        return keyWordID;
    }
    public void setKeyWordID(Integer keyWordID) {
        this.keyWordID = keyWordID;
    }
    public String getKeyWordName() {
        return keyWordName;
    }
    public void setKeyWordName(String keyWordName) {
        this.keyWordName = keyWordName;
    }
    public Integer getKeyWordType() {
        return keyWordType;
    }
    public void setKeyWordType(Integer keyWordType) {
        this.keyWordType = keyWordType;
    }
}
