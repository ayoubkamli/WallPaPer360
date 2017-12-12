package wp.a360.point.com.myapplication.ui.entity;

import java.io.Serializable;

/**
 * 图片分类Bean
 * Created by DN on 2017/12/6.
 */

public class ImageType implements Serializable{
    private int imageTypeID;
    private String imageTypeName;//类型名称
    private String imageTypeBackground;//类型背景图
    private int imageTypeNum;//类型张数

    public int getImageTypeID() {
        return imageTypeID;
    }

    public void setImageTypeID(int imageTypeID) {
        this.imageTypeID = imageTypeID;
    }

    public String getImageTypeName() {
        return imageTypeName;
    }

    public void setImageTypeName(String imageTypeName) {
        this.imageTypeName = imageTypeName;
    }

    public String getImageTypeBackground() {
        return imageTypeBackground;
    }

    public void setImageTypeBackground(String imageTypeBackground) {
        this.imageTypeBackground = imageTypeBackground;
    }

    public int getImageTypeNum() {
        return imageTypeNum;
    }

    public void setImageTypeNum(int imageTypeNum) {
        this.imageTypeNum = imageTypeNum;
    }
}
