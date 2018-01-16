package com.duy.natural.calc.calculator.keyboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duy.natural.calc.calculator.calcbutton.CalcTextButton;
import com.duy.natural.calc.calculator.keyboard.models.FunctionCategory;
import com.duy.natural.calc.calculator.keyboard.models.FunctionCategoryHelper;
import com.duy.natural.calc.calculator.keyboard.models.FunctionItem;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

/**
 * Created by Duy on 1/16/2018.
 */

public class FunctionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<FunctionItem> mItems = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public FunctionAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initData();
    }


    private void initData() {
        ArrayList<FunctionCategory> categories = FunctionCategoryHelper.getAllCategories();
        for (FunctionCategory category : categories) {
            mItems.add(new FunctionItem(FunctionItem.TYPE_CATEGORY, category));
            for (FunctionType type : category.getFunctionTypes()) {
                mItems.add(new FunctionItem(FunctionItem.TYPE_FUNCTION, type));
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FunctionItem.TYPE_CATEGORY:
                return new CategoryHolder(mInflater.inflate(R.layout.list_item_category_function, parent, false));
            case FunctionItem.TYPE_FUNCTION:
                return new FunctionHolder(mInflater.inflate(R.layout.list_item_function, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FunctionItem functionItem = mItems.get(position);

        if (holder instanceof CategoryHolder) {
            FunctionCategory category = (FunctionCategory) functionItem.data;
            ((CategoryHolder) holder).txtTitle.setText(category.getTitleId());
        } else if (holder instanceof FunctionHolder) {
            FunctionType type = (FunctionType) functionItem.data;
            CalcTextButton button = ((FunctionHolder) holder).mCalcButton;
            button.setText(type.getFunctionName());
            button.initWithParameter(-1, type.getDescriptionId(), type.getCode());
            button.setDocumentPath(type.getDocumentPath());
            button.setOnClickListener(onClickListener);
            button.setOnLongClickListener(onLongClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }


    static final class CategoryHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;

        CategoryHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
        }
    }

    static final class FunctionHolder extends RecyclerView.ViewHolder {
        private CalcTextButton mCalcButton;

        FunctionHolder(View itemView) {
            super(itemView);
            mCalcButton = itemView.findViewById(R.id.calc_button);
        }
    }


}
