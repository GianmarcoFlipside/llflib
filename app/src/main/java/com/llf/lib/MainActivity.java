package com.llf.lib;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.llflib.cm.adapter.RecycleBaseAdapter;
import com.llflib.cm.ui.ToolbarActivity;
import com.llflib.cm.ui.view.RecycleDecoration;
import com.llflib.cm.util.Views;

import java.util.List;

public class MainActivity extends ToolbarActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override protected void setupViews() {
        RecyclerView listview = (RecyclerView) findViewById(R.id.listview);
        listview.addItemDecoration(new RecycleDecoration(getResources().getDimensionPixelOffset(R.dimen.margin)));
        listview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Intent intent = new Intent("com.llf.lib.EXAMPLE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        ExampleAdapter adapter = new ExampleAdapter(getPackageManager().queryIntentActivities(intent, 0));
        listview.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecycleBaseAdapter.OnItemClickListener() {
            @Override public void onItemClick(View v, Object obj) {
                if (obj == null || !(obj instanceof ResolveInfo))
                    return;
                ResolveInfo info = (ResolveInfo) obj;
                ComponentName cn = new ComponentName(info.activityInfo.packageName,info.activityInfo.name);
                Intent it = new Intent();
                it.setComponent(cn);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });

    }


    class ExampleAdapter extends RecycleBaseAdapter<ResolveInfo, ViewHolder> {
        public ExampleAdapter(List<ResolveInfo> l) {
            super(l);
        }

        @Override protected ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext(), null, android.R.attr.textAppearanceMedium);
            tv.setMinHeight(Views.getDimenPx(parent.getContext(), 48));
            return new ViewHolder(tv);
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            ResolveInfo info = mList.get(position);
            holder.tv.setTag(info);
            holder.tv.setText(info.loadLabel(holder.tv.getContext().getPackageManager()));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }

}
