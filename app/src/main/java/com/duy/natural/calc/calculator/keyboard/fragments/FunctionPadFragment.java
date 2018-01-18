package com.duy.natural.calc.calculator.keyboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.calcbutton.ICalcButton;
import com.duy.natural.calc.calculator.dialogs.DialogDocument;
import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.duy.natural.calc.calculator.keyboard.adapters.FunctionAdapter;
import com.duy.natural.calc.calculator.keyboard.models.FunctionItem;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/14/2018.
 */

public class FunctionPadFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {

    private OnCalcButtonClickListener mListener;
    private RecyclerView mRecyclerView;
    private FunctionAdapter mFunctionAdapter;

    public static FunctionPadFragment newInstance(OnCalcButtonClickListener listener) {
        FunctionPadFragment fragment = new FunctionPadFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad_function2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFunctionAdapter = new FunctionAdapter(getContext());
        mRecyclerView = view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mFunctionAdapter.getItemViewType(position) == FunctionItem.TYPE_CATEGORY) {
                    return 4;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setAdapter(mFunctionAdapter);

        mFunctionAdapter.setOnClickListener(this);
        mFunctionAdapter.setOnLongClickListener(this);
    }

    public void setListener(OnCalcButtonClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (view instanceof ICalcButton) {
            String documentPath = ((ICalcButton) view).getDocumentPath();
            if (documentPath != null){
                DialogDocument dialogDocument = DialogDocument.newInstance(documentPath);
                dialogDocument.show(getChildFragmentManager(), dialogDocument.getClass().getName());
            }else {
                return ViewUtils.showButtonDescription(getContext(), view);
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ICalcButton && mListener != null) {
            final ICalcButton calcButton = (ICalcButton) view;
            String categoryCode = calcButton.getCategoryCode();
            if (categoryCode != null) {
                mListener.onButtonPressed(view, categoryCode);
            }
        }
    }
}
