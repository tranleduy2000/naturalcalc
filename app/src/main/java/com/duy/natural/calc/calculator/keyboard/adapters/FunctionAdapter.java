package com.duy.natural.calc.calculator.keyboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.calcbutton.ICalcButton;
import com.duy.natural.calc.calculator.keyboard.models.FunctionCategory;

import java.util.ArrayList;

/**
 * Created by Duy on 1/16/2018.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {
    private final ArrayList<FunctionCategory> functionCategories = new ArrayList<>();
    private Context mContext;

    public FunctionAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ICalcButton mCalcButton;

        public ViewHolder(View itemView) {
            super(itemView);
//            mCalcButton = itemView.findViewById(R.id.calc_button);
        }
    }


}
