/*******************************************************************************
 * microMathematics Plus - Extended visual calculator
 * *****************************************************************************
 * Copyright (C) 2014-2017 Mikhail Kulesh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.mkulesh.micromath.export;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;

import com.mkulesh.micromath.fman.AdapterIf;
import com.mkulesh.micromath.fman.FileUtils;
import com.mkulesh.micromath.formula.views.EquationView;
import com.mkulesh.micromath.formula.views.FormulaView;
import com.mkulesh.micromath.formula.FormulaListView;
import com.mkulesh.micromath.formula.views.FormulaResultView;
import com.nstudio.calc.casio.R;
import com.mkulesh.micromath.utils.ViewUtils;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Export interface: export to LaTeX
 */
public class ExportToMathJax extends ExportToLatex {
    private final static int NEW_LINE_CODE = 10;

    public ExportToMathJax(Context context, OutputStream stream, final Uri uri, final AdapterIf adapter)
            throws Exception {
        super(context, stream, uri, adapter, null);
    }

    public void write(FormulaListView formulaListView) throws Exception {
        writer.append("<!DOCTYPE html>\n");
        writer.append("<html><head>\n");
        writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        writer.append("<title>");
        writer.append(fileName);
        writer.append("</title>\n");

        writer.append("<script type=\"text/x-mathjax-config\">\n");
        writer.append("  MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$']]}});\n");
        writer.append("</script>\n");
        writer.append("<script type=\"text/javascript\"\n");
        writer.append("  src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">\n");
        writer.append("</script>\n");
        writer.append("<style>\n");
        writer.append("  td { margin: 0px; padding: 0px 10px 0px 10px; }\n");
        writer.append("</style>\n");
        writer.append("<style>\n");
        writer.append("  img { padding: 10px; display: block; max-width: 90%; }\n");
        writer.append("</style>\n");
        writer.append("</head><body>");

        final int n = formulaListView.getList().getChildCount();
        for (int i = 0; i < n; i++) {
            View v = formulaListView.getList().getChildAt(i);
            if (v instanceof FormulaListView.ListRow) {
                ArrayList<FormulaView> row = new ArrayList<>();
                ((FormulaListView.ListRow) v).getFormulas(FormulaView.class, row);
                if (row.size() == 0) {
                    // nothing to do
                } else {
                    writer.append("\n\n<table align=\"center\" border = \"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
                    for (int k = 0; k < row.size(); k++) {
                        writer.append("\n  <td>");
                        writeFormulaBase(row.get(k), true);
                        writer.append("</td>");
                    }
                    writer.append("\n</tr></table>");
                }
            } else if (v instanceof FormulaView) {
                writeFormulaBase((FormulaView) v, false);
            }
        }
        writer.append("\n\n</body></html>\n");
        stream.write(writer.toString().getBytes());
    }

    protected void writeEquation(EquationView f, boolean inLine) {
        writer.append(inLine ? "$$" : "\n\n$$");
        writeTermField(f.findTermWithKey(R.string.formula_left_term_key));
        writer.append(" := ");
        writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
        writer.append(inLine ? "$$" : "$$");
    }

    protected void writeFormulaResult(FormulaResultView f, boolean inLine) {
        writer.append(inLine ? "$$" : "\n\n$$");
        appendFormulaResult(f);
        writer.append(inLine ? "$$" : "$$");
    }

    protected void writePlotFunction(FormulaView f, boolean inLine) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(f.getMeasuredWidth(), f.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            f.draw(new Canvas(bitmap));
        } catch (OutOfMemoryError e) {
            ViewUtils.debug(this, "cannot save picture: " + e);
            return;
        }

        final String figName = fileName + "_fig" + String.valueOf(figNumber) + ".png";
        Uri figUri = adapter.getItemUri(figName);
        if (figUri == null) {
            figUri = adapter.newFile(figName);
        }
        try {
            FileUtils.ensureScheme(figUri);
            OutputStream fos = FileUtils.getOutputStream(context, figUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            FileUtils.closeStream(fos);
            figNumber++;
        } catch (Exception e) {
            ViewUtils.debug(this, "cannot save picture: " + e);
            return;
        }
        if (!inLine) {
            writer.append("\n\n<center>");
        }
        writer.append("<img src=\"").append(figName).append("\"alt=\"Image\">");
        if (!inLine) {
            writer.append("</center>");
        }
    }

    private void writeHtmlText(CharSequence text, boolean inEquation) {
        StringBuilder outStr = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            boolean processed = false;

            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.GREEK) {
                for (String[] aGreekTable : greekTable) {
                    if (aGreekTable[0].charAt(0) == c) {
                        outStr.append(inEquation ? "{" : "$").append(aGreekTable[1]).append(inEquation ? "}" : "$");
                        processed = true;
                        break;
                    }
                }
            } else if (c == NEW_LINE_CODE) {
                final int offset = getParagraphOffset(text, i);
                if (offset != ViewUtils.INVALID_INDEX) {
                    outStr.append("</p>\n\n<p>");
                    i += offset;
                    processed = true;
                }
            }
            if (!processed) {
                outStr.append(c);
            }
        }
        writer.append(outStr.toString());
    }

    private int getParagraphOffset(CharSequence text, int currIdx) {
        int i = currIdx, newLineNumber = 0;
        for (; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == NEW_LINE_CODE) {
                newLineNumber++;
            } else if (!Character.isWhitespace(c)) {
                break;
            }
        }
        return (newLineNumber > 1 && i > currIdx) ? i - currIdx - 1 : ViewUtils.INVALID_INDEX;
    }
}
