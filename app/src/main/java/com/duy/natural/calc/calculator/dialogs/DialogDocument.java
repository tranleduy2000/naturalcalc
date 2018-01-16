package com.duy.natural.calc.calculator.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mukesh.MarkdownView;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/16/2018.
 */

public class DialogDocument extends BottomSheetDialogFragment {
    private static final String EXTRA_ASSET_PATH = "EXTRA_ASSET_PATH";

    public static DialogDocument newInstance(String assetPath) {

        Bundle args = new Bundle();
        args.putString(EXTRA_ASSET_PATH, assetPath);
        DialogDocument fragment = new DialogDocument();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String path = getArguments().getString(EXTRA_ASSET_PATH);
        if (path == null) {
            dismiss();
            return;
        }
        MarkdownView markdownView = view.findViewById(R.id.markdown_view);
        markdownView.loadMarkdownFromAssets(path); //Loads the markdown file from the assets folder
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_document, container, false);
    }
}
