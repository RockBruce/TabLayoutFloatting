package cn.edsmall.tablayoutfloatting.model;

import java.util.List;

public class BottomBar {

    /**
     * activeColor : #333333
     * tabs : [{"size":24,"enable":true,"index":0,"pageUrl":"main/tabs/home","title":"首页"},{"size":24,"enable":true,"index":1,"pageUrl":"main/tabs/dashboard","title":"沙发"},{"size":40,"enable":true,"index":2,"pageUrl":"main/tabs/home","title":"我的"}]
     * inActiveColor : #66666
     */
    private String activeColor;
    private List<Tabs> tabs;
    private String inActiveColor;

    public void setActiveColor(String activeColor) {
        this.activeColor = activeColor;
    }

    public void setTabs(List<Tabs> tabs) {
        this.tabs = tabs;
    }

    public void setInActiveColor(String inActiveColor) {
        this.inActiveColor = inActiveColor;
    }

    public String getActiveColor() {
        return activeColor;
    }

    public List<Tabs> getTabs() {
        return tabs;
    }

    public String getInActiveColor() {
        return inActiveColor;
    }

    public class Tabs {
        /**
         * size : 24
         * enable : true
         * index : 0
         * pageUrl : main/tabs/home
         * title : 首页
         */
        private int size;
        private boolean enable;
        private int index;
        private String pageUrl;
        private String title;

        public void setSize(int size) {
            this.size = size;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSize() {
            return size;
        }

        public boolean isEnable() {
            return enable;
        }

        public int getIndex() {
            return index;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public String getTitle() {
            return title;
        }
    }
}
