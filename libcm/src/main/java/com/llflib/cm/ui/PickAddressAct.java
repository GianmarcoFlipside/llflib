package com.llflib.cm.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.llflib.cm.R;
import com.llflib.cm.db.AddressHelper;
import com.llflib.cm.ui.view.RecycleDecoration;
import com.llflib.cm.util.Views;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by llf on 2015/9/18.
 */
public class PickAddressAct extends ToolbarActivity {
    private AddressHelper mAddressHelper;
    private AddressAdapter mAdapter;
    /**
     * 0:省,1:市，2:区域
     */
    int mAddressLevel;
    private HandlerThread mThreads;
    Handler mWorkHandler, mUIHandler;

    private TextView mAddressTv;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm_act_address);
        Views.setApplyWindowInserts(this);
        setResult(RESULT_CANCELED);
        mUIHandler = new Handler();
    }

    @Override protected void onStart() {
        super.onStart();
        mThreads = new HandlerThread("getAddressThread");
        mThreads.start();

        mWorkHandler = new WorkHandler(this, mThreads.getLooper());
        if (mAdapter.getItemCount() == 0)
            mWorkHandler.sendEmptyMessage(mAddressLevel);
    }

    @Override protected void onStop() {
        super.onStop();
        mThreads.quit();
    }

    @Override protected void setupViews() {
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.cm_ic_left);
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
        ab.setBackgroundDrawable(new ColorDrawable(outValue.data));

        mAddressTv = (TextView) findViewById(R.id.text);
        final RecyclerView listView = (RecyclerView) findViewById(R.id.listview);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.addItemDecoration(new RecycleDecoration(this));
        //data;
        mAdapter = new AddressAdapter(new ArrayList<AddressHelper.Bean>());
        listView.setAdapter(mAdapter);

        mAddressLevel = 0;
        mAddressTv.setText("");
        mAddressTv.setVisibility(View.GONE);
        mAdapter.setClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AddressHelper.Bean bean = (AddressHelper.Bean) v.getTag();
                if (bean.id == null)
                    return;
                mAddressTv.setVisibility(View.VISIBLE);
                mAddressTv.append(bean.name);
                mAddressLevel++;
                if (mAddressLevel == 3) {    //finish
                    Intent data = getIntent();
                    data.putExtra(Intent.EXTRA_TEXT, mAddressTv.getText().toString());
                    setResult(RESULT_OK, data);
                    finish();
                    return;
                }
                mWorkHandler.obtainMessage(mAddressLevel, bean.id).sendToTarget();
            }
        });
    }

    AddressHelper getHelper() {
        if (mAddressHelper == null)
            mAddressHelper = new AddressHelper(this);
        return mAddressHelper;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class WorkHandler extends Handler {
        private SoftReference<PickAddressAct> mCtx;

        public WorkHandler(PickAddressAct act, Looper looper) {
            super(looper);
            mCtx = new SoftReference<>(act);
        }

        @Override public void handleMessage(Message msg) {
            final PickAddressAct ctx = mCtx.get();
            if (ctx == null)
                return;
            List<AddressHelper.Bean> list;
            switch (msg.what) {
                case 0:
                    list = ctx.getHelper().getProvinces();
                    break;
                case 1:
                    list = ctx.getHelper().getCitysByProvince((String) msg.obj);
                    break;
                default:
                    list = ctx.getHelper().getZonesByCity((String) msg.obj);
                    break;
            }
            final List<AddressHelper.Bean> l = list;
            ctx.mUIHandler.post(new Runnable() {
                @Override public void run() {
                    ctx.mAdapter.setNewList(l);
                }
            });
        }
    }

    class AddressAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<AddressHelper.Bean> mList;
        private View.OnClickListener mClickListener;
        public AddressAdapter(List<AddressHelper.Bean> l) {
            mList = l;
        }

        public void setClickListener(View.OnClickListener l){
            mClickListener = l;
        }

        public void setNewList(List<AddressHelper.Bean> l) {
            mList.clear();
            if (l != null) {
                mList.addAll(l);
                notifyDataSetChanged();
            }
        }

        @Override public int getItemViewType(int position) {
            return mList.get(position).id == null ? 1 : 0;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            TextView tv = (TextView) inflater.inflate(
                    viewType == 0 ? R.layout.cm_layout_address : R.layout.cm_layout_add_sel, parent, false);
            tv.setOnClickListener(mClickListener);
            return new ViewHolder(tv);
        }

        @Override public int getItemCount() {
            return mList == null ?0:mList.size();
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            AddressHelper.Bean bean = mList.get(position);
            holder.tv.setText(bean.name);
            holder.tv.setTag(bean);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public ViewHolder(TextView itemView) {
            super(itemView);
            tv = itemView;
        }
    }
}
