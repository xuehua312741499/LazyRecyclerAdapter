package com.shuyu.bind;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 通用适配管理
 * Created by guoshuyu on 2017/8/28.
 */

public class NormalBindAdapterManager {

    private final String TAG = NormalBindAdapterManager.class.getName();

    //根据model类名绑定layoutId
    private HashMap<String, List<Integer>> modelToId = new HashMap<>();

    //根据id类名绑定holder
    private HashMap<Integer, Class<? extends NormalBindRecyclerBaseHolder>> idToHolder = new HashMap<>();

    //没有数据的布局id
    private int noDataLayoutId = -1;

    //加载更多的布局id
    private int loadmoreId = -1;

    //没有数据的object
    private Object noDataObject;

    //加载更多的object
    private Object loadmoreObject;

    //加载更多的holder
    private Class<? extends NormalBindLoadMoreHolder> loadmoreHolder;

    //没有数据的holder
    private Class<? extends NormalBindRecyclerBaseHolder> noDataHolder;

    //一个model对应多种Holder的数据的筛选，不设置默认使用最后一个
    private NormalBindDataChooseListener normalBindDataChooseListener;

    /**
     * 绑定加载更多模块显示
     *
     * @param modelClass  数据实体类名，一个model可以对用多个布局和Holder
     * @param layoutId    布局id，一个manager中，一个id只能对应一个holder。
     * @param holderClass holder类名，一个manager中，一个holder只能对应一个id。
     */
    public NormalBindAdapterManager bind(Class modelClass, int layoutId, Class<? extends NormalBindRecyclerBaseHolder> holderClass) {

        if (!modelToId.containsKey(modelClass.getName())) {
            List<Integer> list = new ArrayList<>();
            list.add(layoutId);
            modelToId.put(modelClass.getName(), list);
        } else {
            List<Integer> list = modelToId.get(modelClass.getName());
            if (!queryIdIn(list, layoutId)) {
                list.add(layoutId);
                modelToId.put(modelClass.getName(), list);
            }
        }
        if (!idToHolder.containsKey(layoutId)) {
            idToHolder.put(layoutId, holderClass);
        } else {
            Log.e(TAG, "*******************layoutId had bind Holder******************* \n" + holderClass.getName());
        }
        return this;
    }

    /**
     * 绑定空数据显示
     *
     * @param noDataModel 数据实体
     * @param layoutId    布局id
     * @param holderClass holder类名
     */
    public NormalBindAdapterManager bindEmpty(Object noDataModel, int layoutId, Class<? extends NormalBindRecyclerBaseHolder> holderClass) {
        noDataHolder = holderClass;
        noDataObject = noDataModel;
        noDataLayoutId = layoutId;
        return this;
    }

    /**
     * 绑定加载更多模块显示
     *
     * @param loadMoreModel 数据实体
     * @param layoutId      布局id
     * @param holderClass   holder类名
     */
    public NormalBindAdapterManager bindLoadMore(Object loadMoreModel, int layoutId, Class<? extends NormalBindLoadMoreHolder> holderClass) {
        loadmoreId = layoutId;
        loadmoreHolder = holderClass;
        loadmoreObject = loadMoreModel;
        return this;
    }

    /**
     * 一个model对应多种Holder的数据的筛选回调
     * 不设置默认使用最后一个
     */
    public NormalBindAdapterManager bingChooseListener(NormalBindDataChooseListener listener) {
        normalBindDataChooseListener = listener;
        return this;
    }

    HashMap<String, List<Integer>> getModelToId() {
        return modelToId;
    }

    HashMap<Integer, Class<? extends NormalBindRecyclerBaseHolder>> getIdToHolder() {
        return idToHolder;
    }

    Object getNoDataObject() {
        return noDataObject;
    }

    Object getLoadMoreObject() {
        return loadmoreObject;
    }

    Class<? extends NormalBindRecyclerBaseHolder> getNoDataHolder() {
        return noDataHolder;
    }

    Class<? extends NormalBindLoadMoreHolder> getLoadMoreHolder() {
        return loadmoreHolder;
    }

    int getNoDataLayoutId() {
        return noDataLayoutId;
    }

    int getLoadMoreId() {
        return loadmoreId;
    }

    /**
     * 是否需要显示空数据
     */
    boolean isShowNoData() {
        return noDataLayoutId != -1 && noDataHolder != null && noDataObject != null;
    }

    /**
     * 是否支持加载更多
     */
    boolean isSupportLoadMore() {
        return loadmoreId != -1 && loadmoreHolder != null && loadmoreObject != null;
    }

    NormalBindDataChooseListener getNormalBindDataChooseListener() {
        return normalBindDataChooseListener;
    }

    /**
     * 创建正常数据的holder
     */
    NormalBindRecyclerBaseHolder getViewTypeHolder(Context context, ViewGroup parent, int viewType) {
        Class<? extends NormalBindRecyclerBaseHolder> idToHolder = getIdToHolder().get(viewType);

        Constructor object = null;
        try {
            View v = LayoutInflater.from(context).inflate(viewType, parent, false);
            object = idToHolder.getDeclaredConstructor(new Class[]{Context.class, View.class});
            object.setAccessible(true);
            return (NormalBindRecyclerBaseHolder) object.newInstance(context, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建空数据的holder
     */
    NormalBindRecyclerBaseHolder getNoDataViewTypeHolder(Context context, ViewGroup parent) {
        Constructor object;
        try {
            View v = LayoutInflater.from(context).inflate(noDataLayoutId, parent, false);
            object = getNoDataHolder().getDeclaredConstructor(new Class[]{Context.class, View.class});
            object.setAccessible(true);
            return (NormalBindRecyclerBaseHolder) object.newInstance(context, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建加载更多的holder
     */
    NormalBindLoadMoreHolder getLoadMoreViewTypeHolder(Context context, ViewGroup parent) {
        Constructor object;
        try {
            View v = LayoutInflater.from(context).inflate(loadmoreId, parent, false);
            object = getLoadMoreHolder().getDeclaredConstructor(new Class[]{Context.class, View.class});
            object.setAccessible(true);
            return (NormalBindLoadMoreHolder) object.newInstance(context, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean queryIdIn(List<Integer> list, int id) {
        for (int i : list) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

}
