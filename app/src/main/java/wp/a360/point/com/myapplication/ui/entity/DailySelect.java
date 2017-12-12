package wp.a360.point.com.myapplication.ui.entity;

import java.io.Serializable;

/**
 * 每日精选bean
 * Created by DN on 2017/12/6.
 */

public class DailySelect implements Serializable {

    private int imageID;//ID
    private String imageUrl;//图片地址
    private int imageTypeID;//图片类型
    private String imageDate;//图片日期
    private int collectionNumber;//图片收藏数
    private String imageLabel;//图片标签

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageTypeID() {
        return imageTypeID;
    }

    public void setImageTypeID(int imageTypeID) {
        this.imageTypeID = imageTypeID;
    }

    public String getImageDate() {
        return imageDate;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public String getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
    }
}
