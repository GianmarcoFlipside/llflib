package com.bean;

/**
 * Created by 908397 on 2015/1/13.
 * 描述页的实体类
 */
public class Page {
    public final  static String POLICY_KEY = "@";
    private final static String POLICY_KEY_APPEND = "@a";

    /** 默认加载方式，先读缓存，若无则网络加载 **/
    public final static String POLICY_DEFAUL = "";
    /** 忽略缓存，只接网络加载 **/
    public final static String POLICY_NETWORK = POLICY_KEY_APPEND;
    /** 先加载缓存，再请求网络 **/
    public final static String POLICY_CACHE_NET = POLICY_NETWORK+POLICY_KEY_APPEND;
    /** 禁止缓存 **/
    public final static String POLICY_NO_CACHE = POLICY_CACHE_NET+POLICY_KEY_APPEND;

    final static int PAGE_DEFAULT = 20;
    /** 当前显示页 **/
    public int page;
    /** 每次显示条数 **/
    public int pageCount;
    /** 请求地址 **/
    public String url;
    /** 追加参数 **/
    public String appendArgs;
    /** 数据读取策略 **/
    public String policyKey;

    public Page(String url){
        this(url,-1);
    }

    public Page(String url,int page){
        this(url,page,PAGE_DEFAULT);
    }
    public Page(String url,int page,String policy){
        this(url,page,PAGE_DEFAULT,policy);
    }

    public Page(String url,int page,int pageCount){
        this(url,page,pageCount,"");
    }

    public Page(String url,int page,int pageCount,String policy) {
        this.url = url;
        this.page = page;
        this.pageCount = pageCount;
        this.policyKey = policy;
    }

    public void setPolicy(String key){
        policyKey = key;
    }

    public String getPolicy(){
        if(policyKey == null)
            policyKey = "";
        return policyKey;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(url);
        boolean hasArgs = (appendArgs != null && appendArgs.length() > 0);
        if (page >= 0 || hasArgs) {
            if (url.indexOf("?") < 0) {
                sb.append("?");
            } else {
                if (!url.endsWith("&"))
                    sb.append("&");
            }
            if(page >= 0) {
                sb.append("page=").append(page);
                sb.append("&pagesize=").append(pageCount);
            }
            if(hasArgs){
                if(page >= 0) sb.append("&");
                sb.append(appendArgs);
            }
        }
        return sb.toString();
    }


}
