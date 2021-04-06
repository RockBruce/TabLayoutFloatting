package cn.edsmall.tablayoutfloatting.model;

public class Destination {

    /**
     * isFragment : true
     * asStarter : false
     * needLogin : false
     * pageUrl : main/tabs/dashboard
     * className : cn.edsmall.tablayoutfloatting.ui.dashboard.DashboardFragment
     * id : 283061654
     */
    private boolean isFragment;
    private boolean asStarter;
    private boolean needLogin;
    private String pageUrl;
    private String className;
    private int id;

    public void setIsFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsFragment() {
        return isFragment;
    }

    public boolean isAsStarter() {
        return asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getClassName() {
        return className;
    }

    public int getId() {
        return id;
    }
}
