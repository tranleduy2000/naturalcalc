package com.mkulesh.micromath.formula.io;

import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.widget.Toast;

import com.mkulesh.micromath.formula.FormulaListView;
import com.mkulesh.micromath.formula.views.FormulaView;
import com.mkulesh.micromath.properties.DocumentProperties;
import com.nstudio.calc.casio.R;

import org.xmlpull.v1.XmlSerializer;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Duy on 1/14/2018.
 */

public class FormulaWritter implements IFormulaWritter {
    private final FormulaListView mFormulaListView;
    private final DocumentProperties mDocumentSettings;
    private final AppCompatActivity mActivity;

    public FormulaWritter(FormulaListView formulaListView, DocumentProperties mDocumentSettings, AppCompatActivity mActivity) {
        this.mFormulaListView = formulaListView;
        this.mDocumentSettings = mDocumentSettings;
        this.mActivity = mActivity;
    }

    @Override
    public boolean write(OutputStream stream, String name) {
        try {
            final StringWriter writer = new StringWriter();
            final XmlSerializer serializer = Xml.newSerializer();

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            serializer.setPrefix(Constants.XML_PROP_MMT, Constants.XML_HTTP);
            serializer.startTag(Constants.XML_NS, Constants.XML_MAIN_TAG);
            {
                serializer.startTag(Constants.XML_NS, Constants.XML_LIST_TAG);
                {
                    mDocumentSettings.writeToXml(serializer);
                    final ArrayList<FormulaView> formulas = mFormulaListView.getFormulas(FormulaView.class);
                    for (FormulaView formula : formulas) {
                        final String term = formula.getBaseType().toString().toLowerCase(Locale.ENGLISH);
                        serializer.startTag(Constants.XML_NS, term);
                        {
                            formula.writeToXml(serializer, String.valueOf(formula.getId()));
                        }
                        serializer.endTag(Constants.XML_NS, term);
                    }
                }
                serializer.endTag(Constants.XML_NS, Constants.XML_LIST_TAG);
            }
            serializer.endTag(Constants.XML_NS, Constants.XML_MAIN_TAG);
            serializer.endDocument();

            System.out.println(writer.toString());
            stream.write(writer.toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            final String error = String.format(mActivity.getResources().getString(R.string.error_file_write), name);
            Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
