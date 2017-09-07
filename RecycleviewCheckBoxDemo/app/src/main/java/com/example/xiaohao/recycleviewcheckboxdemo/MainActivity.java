package com.example.xiaohao.recycleviewcheckboxdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by guohao on 2017/9/6.
 */
public class MainActivity extends Activity implements View.OnClickListener, MineRadioAdapter.OnItemClickListener {

    private static final int MYLIVE_MODE_CHECK = 0;
    private static final int MYLIVE_MODE_EDIT = 1;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @InjectView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @InjectView(R.id.btn_delete)
    Button mBtnDelete;
    @InjectView(R.id.select_all)
    TextView mSelectAll;
    @InjectView(R.id.ll_mycollection_bottom_dialog)
    LinearLayout mLlMycollectionBottomDialog;
    @InjectView(R.id.btn_editor)
    TextView mBtnEditor;
    private MineRadioAdapter mRadioAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private List<MyLiveList> mList = new ArrayList<>();
    private int mEditMode = MYLIVE_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
    }

    private void initData() {
        mRadioAdapter = new MineRadioAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration itemDecorationHeader = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        itemDecorationHeader.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_main_bg_height_1));
        mRecyclerview.addItemDecoration(itemDecorationHeader);
        mRecyclerview.setAdapter(mRadioAdapter);
        for (int i = 0; i < 30; i++) {
            MyLiveList myLiveList = new MyLiveList();
            myLiveList.setTitle("这是第" + i + "个条目");
            myLiveList.setSource("来源" + i);
            mList.add(myLiveList);
            mRadioAdapter.notifyAdapter(mList, false);
        }
    }


    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            mBtnDelete.setBackgroundResource(R.drawable.button_shape);
            mBtnDelete.setEnabled(true);
            mBtnDelete.setTextColor(Color.WHITE);
        } else {
            mBtnDelete.setBackgroundResource(R.drawable.button_noclickable_shape);
            mBtnDelete.setEnabled(false);
            mBtnDelete.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd));
        }
    }

    private void initListener() {
        mRadioAdapter.setOnItemClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        mBtnEditor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                deleteVideo();
                break;
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.btn_editor:
                updataEditMode();
                break;
            default:
                break;
        }
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mRadioAdapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mRadioAdapter.getMyLiveList().size(); i < j; i++) {
                mRadioAdapter.getMyLiveList().get(i).setSelect(true);
            }
            index = mRadioAdapter.getMyLiveList().size();
            mBtnDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mRadioAdapter.getMyLiveList().size(); i < j; i++) {
                mRadioAdapter.getMyLiveList().get(i).setSelect(false);
            }
            index = 0;
            mBtnDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mRadioAdapter.notifyDataSetChanged();
        setBtnBackground(index);
        mTvSelectNum.setText(String.valueOf(index));
    }

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0){
            mBtnDelete.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = mRadioAdapter.getMyLiveList().size(), j =0 ; i > j; i--) {
                    MyLiveList myLive = mRadioAdapter.getMyLiveList().get(i-1);
                    if (myLive.isSelect()) {

                        mRadioAdapter.getMyLiveList().remove(myLive);
                        index--;
                    }
                }
                index = 0;
                mTvSelectNum.setText(String.valueOf(0));
                setBtnBackground(index);
                if (mRadioAdapter.getMyLiveList().size() == 0){
                    mLlMycollectionBottomDialog.setVisibility(View.GONE);
                }
                mRadioAdapter.notifyDataSetChanged();
                builder.dismiss();
            }
        });
    }
    private void updataEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            mBtnEditor.setText("取消");
            mLlMycollectionBottomDialog.setVisibility(View.VISIBLE);
            editorStatus = true;
        } else {
            mBtnEditor.setText("编辑");
            mLlMycollectionBottomDialog.setVisibility(View.GONE);
            editorStatus = false;
            clearAll();
        }
        mRadioAdapter.setEditMode(mEditMode);
    }


    private void clearAll() {
        mTvSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        mSelectAll.setText("全选");
        setBtnBackground(0);
    }

    @Override
    public void onItemClickListener(int pos, List<MyLiveList> myLiveList) {
        if (editorStatus) {
            MyLiveList myLive = myLiveList.get(pos);
            boolean isSelect = myLive.isSelect();
            if (!isSelect) {
                index++;
                myLive.setSelect(true);
                if (index == myLiveList.size()) {
                    isSelectAll = true;
                    mSelectAll.setText("取消全选");
                }

            } else {
                myLive.setSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            setBtnBackground(index);
            mTvSelectNum.setText(String.valueOf(index));
            mRadioAdapter.notifyDataSetChanged();
        }
    }
}
