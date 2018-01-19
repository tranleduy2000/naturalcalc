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
package com.mkulesh.micromath.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.duy.natural.calc.calculator.utils.FontManager;
import com.nstudio.calc.casio.R;

public class FormulaTextView extends AppCompatTextView implements OnLongClickListener, OnClickListener {

    protected final Paint mPaint = new Paint();
    protected final Path mPath = new Path();
    private final RectF rect = new RectF();
    private final RectF oval = new RectF();
    protected AppCompatActivity activity = null;
    protected int mStrokeWidth = 0;
    private SymbolType symbolType = SymbolType.TEXT;
    private boolean useExternalPaint = false;
    // context menu handling
    private ContextMenuHandler mMenuHandler = null;
    private OnFormulaChangeListener onFormulaChangeListener = null;

    /*********************************************************
     * Creating
     *********************************************************/

    public FormulaTextView(Context context) {
        super(context);
    }

    public FormulaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public FormulaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {
        FontManager.setDefaultFont(context, this);
        mMenuHandler = new ContextMenuHandler(getContext());
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FormulaEditText, 0, 0);
            String s = a.getString(R.styleable.FormulaEditText_symbol);
            if (s != null) {
                for (SymbolType f : SymbolType.values()) {
                    if (s.equals(f.toString())) {
                        symbolType = f;
                        break;
                    }
                }
            }
            mMenuHandler.initialize(a);
            a.recycle();
        }
    }

    public void prepare(SymbolType symbolType, AppCompatActivity activity, OnFormulaChangeListener onFormulaChangeListener) {
        this.symbolType = symbolType;
        this.activity = activity;
        this.onFormulaChangeListener = onFormulaChangeListener;
        setOnLongClickListener(this);
        setOnClickListener(this);

        mPaint.set(getPaint());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        setSaveEnabled(false);
    }

    public void updateTextSize(ScaledDimensions dimen, int termDepth) {
        mStrokeWidth = dimen.get(ScaledDimensions.Type.STROKE_WIDTH);

        if (symbolType == SymbolType.SUMMATION || symbolType == SymbolType.PRODUCT || symbolType == SymbolType.INTEGRAL) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen.getTextSize(ScaledDimensions.Type.BIG_SYMBOL_SIZE, termDepth));
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen.getTextSize(termDepth));
        }

        // Here is a bug on the old android versions:
        // http://stackoverflow.com/questions/9541196
        // TextView height doesn't change after shrinking the font size
        // Trick: reset text buffer
        setText(getText(), AppCompatTextView.BufferType.SPANNABLE);

        if (symbolType != null) {
            switch (symbolType) {
                case EMPTY:
                    // nothing to do
                    break;
                case HOR_LINE:
                    setPadding(0, 0, 0, 0);
                    setHeight(mStrokeWidth * 5);
                    break;
                case LEFT_BRACKET:
                case LEFT_SQR_BRACKET:
                case RIGHT_BRACKET:
                case RIGHT_SQR_BRACKET:
                    final int p1 = dimen.get(ScaledDimensions.Type.HOR_BRAKET_PADDING);
                    setPadding(p1, 0, p1, 0);
                    break;
                case SLASH:
                    setPadding(0, 0, 0, 0);
                    break;
                case INTEGRAL:
                    final int p2 = dimen.get(ScaledDimensions.Type.HOR_SYMBOL_PADDING);
                    setWidth(20 * mStrokeWidth);
                    setHeight(40 * mStrokeWidth);
                    setPadding(p2, 0, p2, 0);
                default:
                    final int p3 = dimen.get(ScaledDimensions.Type.HOR_SYMBOL_PADDING);
                    setPadding(p3, 0, p3, 0);
                    break;
            }
        }
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public void setExternalPaint(Paint p) {
        if (p != null) {
            useExternalPaint = true;
            mPaint.set(p);
        } else {
            useExternalPaint = false;
            mPaint.reset();
        }

    }

    /*********************************************************
     * Painting
     *********************************************************/

    @Override
    public int getBaseline() {
        return (this.getMeasuredHeight() - getPaddingBottom() + getPaddingTop()) / 2;
    }

    @Override
    protected void onDraw(Canvas c) {
        if (symbolType == null) {
            return;
        }

        rect.set(getPaddingLeft(), getPaddingTop(), this.getRight() - this.getLeft() - getPaddingRight(),
                this.getBottom() - this.getTop() - getPaddingBottom());

        if (!useExternalPaint) {
            mPaint.setColor(getCurrentTextColor());
            mPaint.setStrokeWidth(mStrokeWidth);
        }

        switch (symbolType) {
            case EMPTY:
                // nothing to to
                break;
            case TEXT:
                super.onDraw(c);
                break;
            case LEFT_BRACKET:
                drawLeftBracket(c);
                break;
            case LEFT_SQR_BRACKET:
                drawLeftSqrBracket(c);
                break;
            case RIGHT_BRACKET:
                drawRightBracket(c);
                break;
            case RIGHT_SQR_BRACKET:
                drawRightSqrBracket(c);
                break;
            case PLUS:
                drawPlus(c);
                break;
            case MINUS:
                drawMinus(c);
                break;
            case MULT:
                drawMult(c);
                break;
            case HOR_LINE:
                drawHorLine(c);
                break;
            case VERT_LINE:
                drawVertLine(c);
                break;
            case SLASH:
                drawSlash(c);
                break;
            case SUMMATION:
                drawSummation(c);
                break;
            case PRODUCT:
                drawProduct(c);
                break;
            case INTEGRAL:
                drawIntegral(c);
                break;
        }

        //Test code to trace paddings:
        /*mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.STROKE);
        c.drawRect(0, 0, this.getWidth(), this.getHeight(), mPaint);
        mPaint.setColor(Color.GREEN);
        c.drawRect(rect, mPaint);*/
    }

    private void drawLeftBracket(Canvas c) {
        mPaint.setStrokeWidth(1);
        rect.right += rect.width() * 1.5;
        for (int i = 1; i < mStrokeWidth + 1; i++) {
            rect.left += i;
            rect.right -= i;
            c.drawArc(rect, 0, 360, false, mPaint);
            rect.left -= i;
            rect.right += i;
        }
    }

    private void drawRightSqrBracket(Canvas c) {
        c.drawLine(rect.centerX(), rect.top, rect.centerX(), rect.bottom, mPaint);
        c.drawLine(rect.centerX(), rect.top, rect.left, rect.top, mPaint);
        c.drawLine(rect.centerX(), rect.bottom, rect.left, rect.bottom, mPaint);
    }

    private void drawRightBracket(Canvas c) {
        mPaint.setStrokeWidth(1);
        rect.left -= rect.width() * 1.5;
        for (int i = 1; i < mStrokeWidth + 1; i++) {
            rect.left += i;
            rect.right -= i;
            c.drawArc(rect, 0, 360, false, mPaint);
            rect.left -= i;
            rect.right += i;
        }
    }

    private void drawLeftSqrBracket(Canvas c) {
        c.drawLine(rect.centerX(), rect.top, rect.centerX(), rect.bottom, mPaint);
        c.drawLine(rect.centerX(), rect.top, rect.right, rect.top, mPaint);
        c.drawLine(rect.centerX(), rect.bottom, rect.right, rect.bottom, mPaint);
    }

    private void drawPlus(Canvas c) {
        float s = rect.width() / 2.0f;
        c.drawLine(rect.centerX() - s, rect.centerY(), rect.centerX() + s, rect.centerY(), mPaint);
        c.drawLine(rect.centerX(), rect.centerY() - s, rect.centerX(), rect.centerY() + s, mPaint);
    }

    private void drawMinus(Canvas c) {
        c.drawLine(rect.left, rect.centerY(), rect.right, rect.centerY(), mPaint);
    }

    private void drawMult(Canvas c) {
        mPaint.setStrokeWidth(1);
        c.drawPoint(rect.centerX(), rect.centerY(), mPaint);
        for (int i = 1; i < mStrokeWidth; i++) {
            c.drawCircle(rect.centerX(), rect.centerY(), i, mPaint);
        }
    }

    private void drawHorLine(Canvas c) {
        mPath.reset();
        mPath.moveTo(rect.left, rect.centerY());
        mPath.lineTo(rect.right, rect.centerY());
        c.drawPath(mPath, mPaint);
    }

    private void drawVertLine(Canvas c) {
        mPath.reset();
        mPath.moveTo(rect.centerX(), rect.top);
        mPath.lineTo(rect.centerX(), rect.bottom);
        c.drawPath(mPath, mPaint);
    }

    private void drawSlash(Canvas c) {
        c.drawLine(rect.left + mStrokeWidth, rect.bottom - mStrokeWidth, rect.right - mStrokeWidth,
                rect.top + mStrokeWidth, mPaint);
    }

    private void drawSummation(Canvas c) {
        final int sw1 = mStrokeWidth;
        final int sw2 = 2 * mStrokeWidth;
        final int sw3 = 3 * mStrokeWidth;
        final int sw4 = 4 * mStrokeWidth;
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPath.reset();
        mPath.moveTo(rect.left, rect.top);
        mPath.lineTo(rect.left, rect.top + sw1);
        mPath.lineTo(rect.centerX(), rect.centerY());
        mPath.lineTo(rect.left, rect.bottom - sw1);
        mPath.lineTo(rect.left, rect.bottom);
        mPath.lineTo(rect.right - sw1, rect.bottom);
        mPath.lineTo(rect.right, rect.bottom - 2 * sw3);
        mPath.lineTo(rect.right - sw1 / 2, rect.bottom - 2 * sw3 - sw1 / 2);
        mPath.lineTo(rect.right - sw3, rect.bottom - sw3);
        mPath.lineTo(rect.left + sw3 + sw1 / 2, rect.bottom - sw3);
        mPath.lineTo(rect.centerX() + sw3, rect.centerY() - sw1);
        mPath.lineTo(rect.left + sw4, rect.top + sw2);
        mPath.lineTo(rect.right - sw4, rect.top + sw2);
        mPath.lineTo(rect.right - sw1 / 2, rect.top + 2 * sw3 + sw1 / 2);
        mPath.lineTo(rect.right, rect.top + 2 * sw3);
        mPath.lineTo(rect.right - sw1, rect.top);
        mPath.close();
        c.drawPath(mPath, mPaint);
    }

    private void drawProduct(Canvas canvas) {
        final int sw2 = 2 * mStrokeWidth;
        final int sw4 = 4 * mStrokeWidth;
        final int sw6 = 6 * mStrokeWidth;
        final int sw8 = 8 * mStrokeWidth;
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPath.reset();
        mPath.moveTo(rect.left + sw2, rect.top + sw2);

        mPath.lineTo(rect.left + sw4, rect.top + sw4);
        mPath.lineTo(rect.left + sw4, rect.bottom - sw4);

        mPath.lineTo(rect.left + sw2, rect.bottom - sw2);

        mPath.lineTo(rect.left + sw8, rect.bottom - sw2);
        mPath.lineTo(rect.left + sw6, rect.bottom - sw4);
        mPath.lineTo(rect.left + sw6, rect.top + sw4);
        mPath.lineTo(rect.right - sw6, rect.top + sw4);
        mPath.lineTo(rect.right - sw6, rect.bottom - sw4);
        mPath.lineTo(rect.right - sw8, rect.bottom - sw2);
        mPath.lineTo(rect.right - sw2, rect.bottom - sw2);
        mPath.lineTo(rect.right - sw4, rect.bottom - sw4);
        mPath.lineTo(rect.right - sw4, rect.top + sw4);
        mPath.lineTo(rect.right - sw2, rect.top + sw2);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private void drawIntegral(Canvas c) {
        final float sw = mStrokeWidth;
        final float rad = rect.width() / 10f;

        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.reset();

        // top line
        final float r2 = rect.centerX() + 5f * rad;
        mPath.moveTo(rect.centerX() - 1.5f * sw, rect.centerY());
        mPath.lineTo(rect.centerX(), rect.top + 4 * rad);
        oval.set(rect.centerX() - 0.05f * sw, rect.top, r2 + 1.2f * sw, rect.top + 10 * rad);
        mPath.arcTo(oval, 200, 115);
        oval.set(r2 - 2 * rad, rect.top + 0.9f * rad, r2, rect.top + 3 * rad);
        mPath.arcTo(oval, 0, 359);
        oval.set(rect.centerX() + 3 * sw, rect.top + sw, r2 - 0.0f * sw, rect.top + 5 * rad);
        mPath.arcTo(oval, -30, -140);
        mPath.lineTo(rect.centerX() + 1.5f * sw, rect.centerY());

        // bottom line
        final float l2 = rect.centerX() - 5f * rad;
        mPath.lineTo(rect.centerX(), rect.bottom - 4 * rad);
        oval.set(l2 - 1.2f * sw, rect.bottom - 10 * rad, rect.centerX() + 0.05f * sw, rect.bottom);
        mPath.arcTo(oval, 20, 115);
        oval.set(l2, rect.bottom - 3 * rad, l2 + 2 * rad, rect.bottom - 0.9f * rad);
        mPath.arcTo(oval, 180, 359);
        oval.set(l2 + 0.0f * sw, rect.bottom - 5 * rad, rect.centerX() - 3 * sw, rect.bottom - sw);
        mPath.arcTo(oval, 150, -140);

        mPath.close();
        c.drawPath(mPath, mPaint);
    }

    /*********************************************************
     * Implementation for methods for OnClickListener interface
     *********************************************************/

    @Override
    public void onClick(View v) {
        if (onFormulaChangeListener != null) {
            onFormulaChangeListener.onFocus(v, true);
        }
    }

    /**
     * Procedure returns the parent action mode or null if there are no related mode
     */
    public android.support.v7.view.ActionMode getActionMode() {
        return mMenuHandler.getActionMode();
    }

    /*********************************************************
     * Context menu handling
     *********************************************************/

    @Override
    public boolean onLongClick(View view) {
        if (onFormulaChangeListener == null) {
            return false;
        }
        if (mMenuHandler.getActionMode() != null) {
            return true;
        }
        mMenuHandler.startActionMode(activity, this, onFormulaChangeListener);
        return true;
    }


    public enum SymbolType {
        EMPTY,
        TEXT,
        LEFT_BRACKET,
        LEFT_SQR_BRACKET,
        RIGHT_BRACKET,
        RIGHT_SQR_BRACKET,
        PLUS,
        MINUS,
        MULT,
        HOR_LINE,
        VERT_LINE,
        SLASH,
        SUMMATION,
        PRODUCT,
        INTEGRAL
    }

}
