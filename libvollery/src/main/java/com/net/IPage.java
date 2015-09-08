package com.net;

import com.bean.Page;

/**
 * Created by 908397 on 2015/1/21.
 * 分页信息接口，若返回结果中包含分页信息，请实现此接口
 */
public interface IPage {
    void setP(Page p);

    Page getP();

    boolean isEmpty();
}
