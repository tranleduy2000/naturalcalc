package com.duy.natural.calc.calculator.keyboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.duy.natural.calc.calculator.keyboard.adapters.FunctionAdapter;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/14/2018.
 */

public class FunctionPadFragment2 extends Fragment implements View.OnLongClickListener, View.OnClickListener {

    private OnCalcButtonClickListener listener;
    private RecyclerView mRecyclerView;
    private FunctionAdapter mFunctionAdapter;

    public static FunctionPadFragment2 newInstance(OnCalcButtonClickListener listener) {
        FunctionPadFragment2 fragment = new FunctionPadFragment2();
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
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mFunctionAdapter);

        mFunctionAdapter.setOnClickListener(this);
        mFunctionAdapter.setOnLongClickListener(this);
    }

    public void setListener(OnCalcButtonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
