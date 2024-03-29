package pl.gocards.ui.cards.xml.study.zoom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * C_U_36 Pinch-to-zoom the term/definition
 * @author Grzegorz Ziemski
 */
@SuppressLint("AppCompatCustomView")
public class ZoomTextView extends TextView {

    @NonNull
    private final ScaleGestureDetector scaleDetector;
    @NonNull
    private final ZoomTextView view;
    private boolean modeZoom;
    private TextSizeListener textSizeListener;
    private final SimpleOnScaleGestureListener onScaleGestureListener =
            new SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(@NonNull ScaleGestureDetector detector) {
                    float newSize = view.getTextSize() * detector.getScaleFactor();
                    if (newSize > 30 && newSize < 80) {
                        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                        if (textSizeListener != null) {
                            textSizeListener.onUpdate(getScaledTextSize());
                        }
                    }
                    return true;
                }
            };

    public ZoomTextView(@NonNull Context context) {
        super(context);
        view = this;
        scaleDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    public ZoomTextView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        view = this;
        scaleDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    public ZoomTextView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = this;
        scaleDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getActionMasked()) {
            if (modeZoom) {
                modeZoom = false;
                return true;
            }
            return super.onTouchEvent(event);
        } else {
            scaleDetector.onTouchEvent(event);// always returns true
            if (scaleDetector.isInProgress()) {
                modeZoom = true;
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private float getScaledTextSize() {
        return super.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
    }

    public void setTextSize(float size) {
        super.setTextSize(size);
    }

    public void setTextSizeListener(TextSizeListener textSizeListener) {
        this.textSizeListener = textSizeListener;
    }

    interface TextSizeListener {
        void onUpdate(float textSize);
    }
}
