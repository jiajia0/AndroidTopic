package com.jiajia0.wifitransferapk;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    Unbinder mUnbinder;//解除视图绑定
    AppShelfAdapter mAppShelfAdapter;//适配器

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;//开启Wifi
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;//展示APP的信息
    @BindView(R.id.content_main)
    SwipeRefreshLayout mSwipeRefreshLayout;//刷新

    List<InfoModel> mApps = new ArrayList<>();// 用来保存App的信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
        mUnbinder = ButterKnife.bind(this);
        initView();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    // 设定点击事件
    private void initView() {
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        if (!mApps.isEmpty()) {

                        } else {
                            Toast.makeText(MainActivity.this, "暂无可删内容", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initRecyclerView() {
        mAppShelfAdapter = new AppShelfAdapter(mApps);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAppShelfAdapter);
    }

}
