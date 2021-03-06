package fs.trivial.boot;

import helper.annotation.FixedByteSerializer;
import helper.annotation.SerializerOrder;

/**
 * Boot block.
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class BootBlock implements FixedByteSerializer {

    /**
     * 是否初始化
     */
    @SerializerOrder
    private int isInit;

    public int getIsInit() {
        return isInit;
    }

    public void setIsInit(int isInit) {
        this.isInit = isInit;
    }
}
