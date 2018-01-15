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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;

import com.mkulesh.micromath.fman.AdapterIf;
import com.mkulesh.micromath.fman.FileUtils;
import com.mkulesh.micromath.formula.FormulaListView;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.formula.type.LoopType;
import com.mkulesh.micromath.formula.type.OperatorType;
import com.mkulesh.micromath.formula.views.EquationView;
import com.mkulesh.micromath.formula.views.FormulaResultView;
import com.mkulesh.micromath.formula.views.FormulaTermComparatorView;
import com.mkulesh.micromath.formula.views.FormulaTermFunctionView;
import com.mkulesh.micromath.formula.views.FormulaTermIntervalView;
import com.mkulesh.micromath.formula.views.FormulaTermLoopView;
import com.mkulesh.micromath.formula.views.FormulaTermOperatorView;
import com.mkulesh.micromath.formula.views.FormulaTermView;
import com.mkulesh.micromath.formula.views.FormulaView;
import com.mkulesh.micromath.formula.views.TermField;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Export interface: export to LaTeX
 */
public class ExportToLatex {
    protected final Context context;
    protected final OutputStream stream;
    protected final StringWriter writer = new StringWriter();
    protected final AdapterIf adapter;
    protected final Exporter.Parameters exportParameters;
    protected final String[][] greekTable = new String[][]{

            {"Α", "A"}, {"α", "\\alpha"},

            {"Β", "B"}, {"β", "\\beta"},

            {"Γ", "\\Gamma"}, {"γ", "\\gamma"},

            {"Δ", "\\Delta"}, {"δ", "\\delta"},

            {"Ε", "E"}, {"ε", "\\varepsilon"},

            {"Ζ", "Z"}, {"ζ", "\\zeta"},

            {"Η", "H"}, {"η", "\\eta"},

            {"Θ", "\\Theta"}, {"θ", "\\theta"},

            {"Ι", "I"}, {"ι", "\\iota"},

            {"Κ", "K"}, {"κ", "\\kappa"},

            {"Λ", "\\Lambda"}, {"λ", "\\lambda"},

            {"Μ", "M"}, {"μ", "\\mu"},

            {"Ν", "N"}, {"ν", "\\nu"},

            {"Ξ", "\\Xi"}, {"ξ", "\\xi"},

            {"Ο", "O"}, {"ο", "\\omicron"},

            {"Π", "\\Pi"}, {"π", "\\pi"},

            {"Ρ", "P"}, {"ρ", "\\rho"},

            {"Σ", "\\Sigma"}, {"σ", "\\sigma"}, {"ς", "\\varsigma"},

            {"Τ", "T"}, {"τ", "\\tau"},

            {"Υ", "\\Upsilon"}, {"υ", "\\upsilon"},

            {"Φ", "\\Phi"}, {"φ", "\\varphi"},

            {"Χ", "X"}, {"χ", "\\chi"},

            {"Ψ", "\\Psi"}, {"ψ", "\\psi"},

            {"Ω", "\\Omega"}, {"ω", "\\omega"}};
    protected String fileName = null;
    protected int figNumber = 1;
    protected boolean currTextNumber = false;

    public ExportToLatex(Context context, OutputStream stream, final Uri uri, final AdapterIf adapter,
                         final Exporter.Parameters exportParameters) throws Exception {
        this.context = context;
        this.stream = stream;
        this.adapter = adapter;
        this.exportParameters = exportParameters;
        fileName = FileUtils.getFileName(context, uri);
        if (fileName == null) {
            throw new Exception("file name is empty");
        }
        final int dotPos = fileName.indexOf(".");
        if (dotPos >= 0 && dotPos < fileName.length()) {
            fileName = fileName.substring(0, dotPos);
        }
        if (skipImageLocale() && fileName.length() > 3) {
            if (fileName.endsWith("_en") || fileName.endsWith("_ru") ||
                    fileName.endsWith("_de") || fileName.endsWith("_br")) {
                fileName = fileName.substring(0, fileName.length() - 3);
            }
        }
    }

    private boolean skipDocumentHeader() {
        return exportParameters != null && exportParameters.skipDocumentHeader;
    }

    private boolean skipImageLocale() {
        return exportParameters != null && exportParameters.skipImageLocale;
    }

    private String getImageDirectory() {
        return exportParameters != null ? exportParameters.imageDirectory : "";
    }

    public void write(FormulaListView formulaListView) throws Exception {
        writer.append("% This is auto-generated file: do not edit!\n");
        try {
            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            writer.append("% Exported from ").append(context.getResources().getString(pi.applicationInfo.labelRes)).append(", version ").append(pi.versionName).append("\n");
        } catch (NameNotFoundException e) {
            ViewUtils.debug(context, "can not write package info: " + e.getLocalizedMessage());
        }

        if (!skipDocumentHeader()) {
            writer.append("\\documentclass[a4paper,10pt]{article}\n");
            writer.append("\\usepackage[utf8]{inputenc}\n");
            writer.append("\\usepackage{graphicx}\n");
            writer.append("\\usepackage{amssymb}\n");
            writer.append("\\usepackage{amsmath}\n");
            writer.append("% If you use russian, please uncomment the line below\n");
            writer.append("% \\usepackage[russian]{babel}\n\n");
            writer.append("\\voffset=-20mm \\textwidth= 170mm \\textheight=255mm \\oddsidemargin=0mm\n");
            writer.append("\\begin{document}");
        }

        final int n = formulaListView.getList().getChildCount();
        for (int i = 0; i < n; i++) {
            View v = formulaListView.getList().getChildAt(i);
            if (v instanceof FormulaListView.ListRow) {
                ArrayList<FormulaView> row = new ArrayList<>();
                ((FormulaListView.ListRow) v).getFormulas(FormulaView.class, row);
                if (row.size() == 0) {
                    // nothing to do
                } else {
                    writer.append("\n\\begin{center}\\begin{tabular}{");
                    for (int k = 0; k < row.size(); k++) {
                        writer.append("c");
                    }
                    writer.append("}");
                    for (int k = 0; k < row.size(); k++) {
                        writer.append("\n  ");
                        writeFormulaBase(row.get(k), true);
                        if (k < row.size() - 1) {
                            writer.append(" &");
                        } else {
                            writer.append(" \\cr");
                        }
                    }
                    writer.append("\n\\end{tabular}\\end{center}");
                }
            } else if (v instanceof FormulaView) {
                writeFormulaBase((FormulaView) v, false);
            }
        }

        if (!skipDocumentHeader()) {
            writer.append("\n\n\\end{document}\n");
        }
        stream.write(writer.toString().getBytes());
    }

    protected void writeFormulaBase(FormulaView f, boolean inLine) {
        if (f instanceof EquationView) {
            writeEquation((EquationView) f, inLine);
        } else if (f instanceof FormulaResultView) {
            writeFormulaResult((FormulaResultView) f, inLine);
        }
    }

    protected void writeEquation(EquationView f, boolean inLine) {
        writer.append(inLine ? "$" : "\n\\begin{center}\\begin{tabular}{c}\n  $");
        writeTermField(f.findTermWithKey(R.string.formula_left_term_key));
        writer.append(" := ");
        writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
        writer.append(inLine ? "$" : "$\n\\end{tabular}\\end{center}");
    }

    protected void appendFormulaResult(FormulaResultView f) {
        writeTermField(f.findTermWithKey(R.string.formula_left_term_key));
        if (f.isResultVisible()) {
            writer.append(" = ");
            if (f.isArrayResult()) {
                final ArrayList<ArrayList<String>> res = f.fillResultMatrixArray();
                if (res != null) {
                    writer.append("\\begin{bmatrix}");
                    for (ArrayList<String> row : res) {
                        for (int i = 0; i < row.size(); i++) {
                            writer.append(FormulaResultView.CELL_DOTS.equals(row.get(i)) ? "\\dots" : row.get(i));
                            writer.append(i + 1 < row.size() ? "&" : "\\\\");
                        }
                    }
                    writer.append("\\end{bmatrix}");
                } else {
                    writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
                }
            } else {
                writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
            }
        }
    }

    protected void writeFormulaResult(FormulaResultView f, boolean inLine) {
        writer.append(inLine ? "$" : "\n\\begin{center}\\begin{tabular}{c}\n  $");
        appendFormulaResult(f);
        writer.append(inLine ? "$" : "$\n\\end{tabular}\\end{center}");
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
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int densityDpi = (int) (metrics.density * 160f);
        if (!inLine) {
            writer.append("\n\\begin{center}");
        }
        writer.append("\\begin{tabular}{c} \\includegraphics[resolution=").append(String.valueOf(densityDpi)).append("]{");
        if (!getImageDirectory().isEmpty()) {
            writer.append(getImageDirectory()).append("/");
        }
        writer.append(figName).append("} \\end{tabular}");
        if (!inLine) {
            writer.append("\\end{center}");
        }
    }

    public void writeTermField(TermField t) {
        if (t == null) {
            return;
        }
        if (!t.isTerm()) {
            if (t.isEmpty()) {
                writer.append("{\\Box}");
            } else {
                writeText(t.getText(), true);
            }
        } else {
            FormulaTermView ft = t.getTerm();
            if (ft instanceof FormulaTermOperatorView) {
                writeTermOperator((FormulaTermOperatorView) ft);
            } else if (ft instanceof FormulaTermComparatorView) {
                writeTermComparator((FormulaTermComparatorView) ft);
            } else if (ft instanceof FormulaTermFunctionView) {
                writeTermFunction((FormulaTermFunctionView) ft);
            } else if (ft instanceof FormulaTermIntervalView) {
                writeTermInterval((FormulaTermIntervalView) ft);
            } else if (ft instanceof FormulaTermLoopView) {
                writeTermLoop((FormulaTermLoopView) ft);
            }
        }
    }

    private void writeTermOperator(FormulaTermOperatorView f) {
        OperatorType operatorType = f.getOperatorType();
        if (f.isUseBrackets()) {
            writer.append("\\left( ");
        }
        if (operatorType == OperatorType.FRACTION) {
            writer.append("\\frac{");
        }
        writeTermField(f.findTermWithKey(R.string.formula_left_term_key));
        switch (operatorType) {
            case FRACTION:
                writer.append("}{");
                break;
            case DIVIDE_SLASH:
                writer.append(" / ");
                break;
            case MINUS:
                writer.append(" - ");
                break;
            case MULTIPLY:
                writer.append(" \\cdot ");
                break;
            case PLUS:
                writer.append(" + ");
                break;
        }
        writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
        if (operatorType == OperatorType.FRACTION) {
            writer.append("}");
        }
        if (f.isUseBrackets()) {
            writer.append(" \\right)");
        }
    }

    private void writeTermComparator(FormulaTermComparatorView f) {
        ComparatorType comparatorType = f.getComparatorType();
        if (f.isUseBrackets()) {
            writer.append("\\left( ");
        }
        writeTermField(f.findTermWithKey(R.string.formula_left_term_key));
        switch (comparatorType) {
            case COMPARATOR_AND:
                writer.append(" and ");
                break;
            case COMPARATOR_OR:
                writer.append(" or ");
                break;
            case EQUAL:
                writer.append(" = ");
                break;
            case GREATER:
                writer.append(" > ");
                break;
            case GREATER_EQUAL:
                writer.append(" \\ge ");
                break;
            case LESS:
                writer.append(" < ");
                break;
            case LESS_EQUAL:
                writer.append(" \\le ");
                break;
            case NOT_EQUAL:
                writer.append(" \\neq ");
                break;
        }
        writeTermField(f.findTermWithKey(R.string.formula_right_term_key));
        if (f.isUseBrackets()) {
            writer.append(" \\right)");
        }
    }

    private void writeTermFunction(FormulaTermFunctionView f) {
        FunctionType functionType = f.getFunctionType();
        final ArrayList<TermField> terms = f.getTerms();
        switch (functionType) {
            case SQRT_LAYOUT:
                writer.append("\\sqrt{");
                writeTermField(terms.get(0));
                writer.append("} ");
                break;
            case SURD_LAYOUT:
                if (terms.size() == 2) {
                    writer.append("\\sqrt[\\leftroot{-3}\\uproot{3}");
                    writeTermField(terms.get(0));
                    writer.append("]{");
                    writeTermField(terms.get(1));
                    writer.append("} ");
                }
                break;
            case CONJUGATE_LAYOUT:
                writer.append("\\overline{");
                writeTermField(terms.get(0));
                writer.append("} ");
                break;
            case RE:
                writer.append("\\Re\\left( ");
                writeTermField(terms.get(0));
                writer.append(" \\right) ");
                break;
            case IM:
                writer.append("\\Im\\left( ");
                writeTermField(terms.get(0));
                writer.append(" \\right) ");
                break;
            case ABS_LAYOUT:
                writer.append(" \\left| ");
                writeTermField(terms.get(0));
                writer.append(" \\right| ");
                break;
            case FACTORIAL:
                writeTermField(terms.get(0));
                writer.append("! ");
                break;
            case POWER:
                writer.append("{");
                writeTermField(terms.get(0));
                writer.append("}^{");
                writeTermField(terms.get(1));
                writer.append("}");
                break;
            default:
                if (f.getFunctionTerm() != null) {
                    writeText(f.getFunctionTerm().getText(), true);
                }
                if (functionType == FunctionType.FUNCTION_INDEX) {
                    writer.append("_{");
                } else {
                    writer.append(" \\left( ");
                }
                for (int i = 0; i < terms.size(); i++) {
                    if (i > 0) {
                        writer.append(",\\, ");
                    }
                    writeTermField(terms.get(i));
                }
                if (functionType == FunctionType.FUNCTION_INDEX) {
                    writer.append("} ");
                } else {
                    writer.append("\\right) ");
                }
                break;
        }
    }

    private void writeTermInterval(FormulaTermIntervalView f) {
        writer.append("\\left[ ");
        writeTermField(f.findTermWithKey(R.string.formula_min_value_key));
        writer.append(",\\, ");
        writeTermField(f.findTermWithKey(R.string.formula_next_value_key));
        writer.append(" \\,..\\, ");
        writeTermField(f.findTermWithKey(R.string.formula_max_value_key));
        writer.append(" \\right]");
    }

    private void writeTermLoop(FormulaTermLoopView f) {
        if (f.isUseBrackets()) {
            writer.append("\\left( ");
        }
        LoopType loopType = f.getLoopType();
        final TermField minValueTerm = f.findTermWithKey(R.string.formula_min_value_key);
        final TermField maxValueTerm = f.findTermWithKey(R.string.formula_max_value_key);
        switch (loopType) {
            case DERIVATIVE:
                writer.append("\\frac{d}{d");
                writeText(f.getIndexName(), true);
                writer.append("} ");
                writeTermField(f.getArgumentTerm());
                break;
            case INTEGRAL:
                writer.append("\\displaystyle\\int_{");
                writeTermField(minValueTerm);
                writer.append("}^{");
                writeTermField(maxValueTerm);
                writer.append("}");
                writeTermField(f.getArgumentTerm());
                writer.append("\\, d");
                writeText(f.getIndexName(), true);
                break;
            case PRODUCT:
                writer.append("\\displaystyle\\prod_{");
                writeText(f.getIndexName(), true);
                writer.append("=");
                writeTermField(minValueTerm);
                writer.append("}^{");
                writeTermField(maxValueTerm);
                writer.append("} ");
                writeTermField(f.getArgumentTerm());
                break;
            case SUMMATION:
                writer.append("\\displaystyle\\sum_{");
                writeText(f.getIndexName(), true);
                writer.append("=");
                writeTermField(minValueTerm);
                writer.append("}^{");
                writeTermField(maxValueTerm);
                writer.append("} ");
                writeTermField(f.getArgumentTerm());
                break;
        }
        if (f.isUseBrackets()) {
            writer.append(" \\right)");
        }
    }

    private void writeText(CharSequence text, boolean inEquation) {
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
            } else if (c == '_') {
                outStr.append("\\_");
                processed = true;
            } else if (c == '"') {
                outStr.append("''");
                processed = true;
            }
            if (!processed) {
                outStr.append(c);
            }
        }
        writer.append(outStr.toString());
    }
}
