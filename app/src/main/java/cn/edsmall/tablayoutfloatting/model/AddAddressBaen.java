package cn.edsmall.tablayoutfloatting.model;

import java.util.ArrayList;
import java.util.List;

public class AddAddressBaen {
    private All all;

    public All getAll() {
        return all;
    }

    public void setAll(All all) {
        this.all = all;
    }

    public List<Areas> getAreas() {
        return areas;
    }

    public void setAreas(List<Areas> areas) {
        this.areas = areas;
    }

    private List<Areas> areas=new ArrayList<>();
    class All{
        private String code;
        private String name;

    }
    class Areas{
        private String code;
        private String name;
        private List<Areas> areas=new ArrayList<>();
    }
}
