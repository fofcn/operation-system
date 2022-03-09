package distributed.network.netty;


/**
 *
 */
public class Manager {

    private  Integer managerId; //辅导老师(助教)id
    private  String code; //基站编号
    private  Integer lessonId; //课次id


    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }
}
