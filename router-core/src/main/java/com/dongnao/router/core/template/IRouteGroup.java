package com.dongnao.router.core.template;


import java.util.Map;

import cn.edsmall.router_annotation.model.RouteMeta;

/**
 * @author Lance
 * @date 2018/2/22
 */

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
