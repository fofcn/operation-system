package fs.trivial.boot;

import helper.annotation.SerializerOrder;

/**
 * Boot block.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class BootBlock {

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
