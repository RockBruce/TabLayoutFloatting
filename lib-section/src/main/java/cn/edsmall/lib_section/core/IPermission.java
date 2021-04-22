package cn.edsmall.lib_section.core;

public interface IPermission {
    /**
     * 已授权
     */
    void authorized();

    /**
     * 取消授权
     */
    void prohibit();

    /**
     * 被拒绝了，点击了不在提示
     */
    void denied(String... permissions);
}
