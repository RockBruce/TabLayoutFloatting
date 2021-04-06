package cn.edsmall.tablayoutfloatting.service;

import cn.edsmall.network.bean.RespMsg;
import cn.edsmall.tablayoutfloatting.model.AddAddressBaen;
import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface UserService {
    @GET("/api/area/addrSource")
    Flowable<RespMsg<AddAddressBaen>> queryArea();
}
