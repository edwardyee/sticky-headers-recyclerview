package com.timehop.stickyheadersrecyclerview.rendering;

import android.content.res.Resources;
import android.graphics.*;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import com.timehop.stickyheadersrecyclerview.R;
import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * Responsible for drawing headers to the canvas provided by the item decoration
 */
public class HeaderRenderer {

  private final DimensionCalculator mDimensionCalculator;
  private final OrientationProvider mOrientationProvider;

  /**
   * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
   * allocating new Rect every time we need one.
   */
  private final Rect mTempRect = new Rect();

  public HeaderRenderer(OrientationProvider orientationProvider) {
    this(orientationProvider, new DimensionCalculator());
  }

  private HeaderRenderer(OrientationProvider orientationProvider,
      DimensionCalculator dimensionCalculator) {
    mOrientationProvider = orientationProvider;
    mDimensionCalculator = dimensionCalculator;
  }

  /**
   * Draws a header to a canvas, offsetting by some x and y amount
   *
   * @param recyclerView the parent recycler view for drawing the header into
   * @param canvas       the canvas on which to draw the header
   * @param header       the view to draw as the header
   * @param offset       a Rect used to define the x/y offset of the header. Specify x/y offset by setting
   *                     the {@link Rect#left} and {@link Rect#top} properties, respectively.
   */
  public void drawHeader(RecyclerView recyclerView, Canvas canvas, View header, Rect offset) {
    canvas.save();

    if (recyclerView.getLayoutManager().getClipToPadding()) {
      // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
      initClipRectForHeader(mTempRect, recyclerView, header);
      canvas.clipRect(mTempRect);
    }
    canvas.translate(offset.left, offset.top);
    header.draw(canvas);
    canvas.restore();
  }

  public void drawShadowedHeader(RecyclerView recyclerView, Canvas canvas, View header, Rect offset, int shadowSize) {
    canvas.save();

    if (recyclerView.getLayoutManager().getClipToPadding()) {
      // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
      initClipRectForHeader(mTempRect, recyclerView, header);
      canvas.clipRect(mTempRect);
    }
    canvas.translate(offset.left, offset.top);

    if ( canvas.getClipBounds().top == 0 && canvas.getClipBounds().left == 0 )
    {
      Resources resources = recyclerView.getResources();
      int start = resources.getColor( R.color.header_shadow_start_color );
      int end = resources.getColor( R.color.header_shadow_end_color );
      int inset = resources.getDimensionPixelSize( R.dimen.shadow_inset );

      Paint paint = new Paint();
      //paint.setShader( new LinearGradient( 0, header.getBottom() - inset, 0, header.getBottom() + shadowSize, new int[]{start, start, end }, new float[]{0f, .5f, 1f}, Shader.TileMode.CLAMP ) );
      paint.setShader( new LinearGradient( 0, header.getBottom() - inset, 0, header.getBottom() + shadowSize, start, end, Shader.TileMode.CLAMP ) );
      paint.setAntiAlias( false );
      paint.setStyle( Paint.Style.FILL );
      RectF rectF = new RectF( header.getLeft(), header.getBottom() - inset, header.getRight(), header.getBottom() + shadowSize );
      canvas.drawRect( rectF, paint );
    }
    header.draw(canvas);
    canvas.restore();
  }

  /**
   * Initializes a clipping rect for the header based on the margins of the header and the padding of the
   * recycler.
   * FIXME: Currently right margin in VERTICAL orientation and bottom margin in HORIZONTAL
   * orientation are clipped so they look accurate, but the headers are not being drawn at the
   * correctly smaller width and height respectively.
   *
   * @param clipRect {@link Rect} for clipping a provided header to the padding of a recycler view
   * @param recyclerView for which to provide a header
   * @param header       for clipping
   */
  private void initClipRectForHeader(Rect clipRect, RecyclerView recyclerView, View header) {
    mDimensionCalculator.initMargins(clipRect, header);
    if (mOrientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
      clipRect.set(
          recyclerView.getPaddingLeft(),
          recyclerView.getPaddingTop(),
          recyclerView.getWidth() - recyclerView.getPaddingRight() - clipRect.right,
          recyclerView.getHeight() - recyclerView.getPaddingBottom());
    } else {
        clipRect.set(
          recyclerView.getPaddingLeft(),
          recyclerView.getPaddingTop(),
          recyclerView.getWidth() - recyclerView.getPaddingRight(),
          recyclerView.getHeight() - recyclerView.getPaddingBottom() - clipRect.bottom);
    }
  }

}
