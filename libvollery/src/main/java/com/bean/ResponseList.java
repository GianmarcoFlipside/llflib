package com.bean;

import com.net.IPage;

import java.util.List;
/**
 * @author llfer 2015/3/18
 */
public class ResponseList<T> extends Response implements IPage {
    public int page;
    public int pagesize;
    public int total;

    public List<T> list;
    public Page p;

    public ResponseList() {
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    @Override public void setP(Page p) {
        this.p = p;
    }

    @Override public Page getP() {
        return p;
    }

    @Override public boolean isEmpty() {
        return list == null || list.isEmpty();
    }
}
