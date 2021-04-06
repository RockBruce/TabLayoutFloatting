package cn.edsmall.tablayoutfloatting.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;

import cn.edsmall.tablayoutfloatting.FixFragmentNavigator;
import cn.edsmall.tablayoutfloatting.model.Destination;

public class NavGraphBuilder {
    public static void build(FragmentActivity activity, FragmentManager childFragmentManager, NavController controller, int containerId) {
        NavigatorProvider navigatorProvider = controller.getNavigatorProvider();
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,childFragmentManager,containerId);
        navigatorProvider.addNavigator(fragmentNavigator);
        ActivityNavigator activityNavigator = navigatorProvider.getNavigator(ActivityNavigator.class);
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(navigatorProvider));
        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Destination value : destConfig.values()) {
            if (value.isIsFragment()) {
                FixFragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClassName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                destination.setClassName(value.getClassName());
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), value.getClassName()));
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                navGraph.addDestination(destination);
            }
            if (value.isAsStarter()) {
                navGraph.setStartDestination(value.getId());
            }
        }
        controller.setGraph(navGraph);
    }
}
