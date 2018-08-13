package cwj.androidfilemanage.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.IProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cwj.androidfilemanage.R;
import cwj.androidfilemanage.adapter.MultipleItem;
import cwj.androidfilemanage.adapter.MultipleItemQuickAdapter;
import cwj.androidfilemanage.base.BaseActivity;
import cwj.androidfilemanage.bean.EventCenter;
import cwj.androidfilemanage.bean.FileInfo;
import cwj.androidfilemanage.constant.ComParamContact;
import cwj.androidfilemanage.utils.FileMimeUtil;
import cwj.androidfilemanage.view.DividerItemDecoration;

public class PCActivity extends BaseActivity {
    @BindView(R.id.rlv_sd_card)
    RecyclerView rlv_sd_card;
    @BindView(R.id.tv_path)
    TextView tv_path;

    private List<FileInfo> fileInfos = new ArrayList<>();
    private List<MultipleItem> mMultipleItems = new ArrayList<>();
    private MultipleItemQuickAdapter mAdapter;
    private String currentPath;
    private String path;

    @Override
    public void onEventComming(EventCenter var1) {

    }

    @Override
    public boolean isBindEventBusHere() {
        return false;
    }

    @Override
    public void initViewAndEvent() {
        path = getIntent().getStringExtra("path");
        rlv_sd_card.setLayoutManager(new LinearLayoutManager(this));
        rlv_sd_card.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.drawable.divide_line));
        mAdapter = new MultipleItemQuickAdapter(mMultipleItems);
        rlv_sd_card.setAdapter(mAdapter);
        showFiles(path);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (adapter.getItemViewType(position) == MultipleItem.FILE) {
                    if (fileInfos.get(position).getMime().contains("video") ||
                            fileInfos.get(position).getMime().contains("audio")) {
                        FileMimeUtil.openNetVideo(PCActivity.this, fileInfos.get(position).getFilePath());
                    } else {
//                        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
//                        intent.putExtra("FileInfo", (ArrayList) mListphoto);
//                        startActivity(intent);
                    }
//                    FileMimeUtil.openFile(PCActivity.this,fileInfos.get(position).getFilePath());
                } else {
                    showFiles(fileInfos.get(position).getFilePath());
                }
            }
        });
    }

    private void showFiles(String folder) {
        mMultipleItems.clear();
        tv_path.setText(folder);
        currentPath = folder;
        //获取文件信息
//        fileInfos = getFileInfosFromFileArray(files);
        IProgressDialog mProgressDialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(PCActivity.this);
                dialog.setMessage("登录中...");
                return dialog;
            }
        };
        EasyHttp.post(ComParamContact.Login.PATH)
                .params(ComParamContact.Login.ACCOUNT, folder)
                .sign(true)
                .timeStamp(true)
                .execute(new ProgressDialogCallBack<List<FileInfo>>(mProgressDialog, true, true) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        Toast.makeText(PCActivity.this, "失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(List<FileInfo> infos) {
                        fileInfos.addAll(infos);
                        if (fileInfos.size() == 0) {
                            mAdapter.setEmptyView(getEmptyView());
                            Log.e("files", "files::为空啦");
                        } else {

                            for (int i = 0; i < fileInfos.size(); i++) {
                                if (fileInfos.get(i).isDirectory) {
                                    mMultipleItems.add(new MultipleItem(MultipleItem.FOLD, fileInfos.get(i)));
                                } else {
                                    mMultipleItems.add(new MultipleItem(MultipleItem.FILE, fileInfos.get(i)));
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private View getEmptyView() {
        return getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rlv_sd_card.getParent(), false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pc;
    }

    @Override
    public void onBackPressed() {
        if (path.equalsIgnoreCase(currentPath)) {
            finish();
        } else {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
            showFiles(currentPath);
        }
    }

    @OnClick(R.id.iv_title_back)
    void iv_title_back() {
        onBackPressed();
    }
}
