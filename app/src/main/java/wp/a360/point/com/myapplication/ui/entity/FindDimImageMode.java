package wp.a360.point.com.myapplication.ui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DN on 2017/12/9.
 */

public class FindDimImageMode implements Serializable{

    private List<DailySelect> image;
    private Integer imageNum;

    public List<DailySelect> getImage() {
        return image;
    }
    public void setImage(List<DailySelect> image) {
        this.image = image;
    }
    public Integer getImageNum() {
        return imageNum;
    }
    public void setImageNum(Integer imageNum) {
        this.imageNum = imageNum;
    }
}
