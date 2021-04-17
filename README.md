# GlideLoadImg
Glide加载图片和高斯模糊

使用了三方库Glide

高斯模糊

    public static final float DEFAULT_RADIUS = 25.0f;
    public static final float MAX_RADIUS = 25.0f;
    private static final float DEFAULT_SAMPLING = 1.0f;

    private Context mContext;
    private float mSampling = DEFAULT_SAMPLING;
    private float mRadius;
    private int mColor;

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

    public static class Builder {

        private Context mContext;
        private float mRadius = DEFAULT_RADIUS;
        private int mColor = Color.TRANSPARENT;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public float getRadius() {
            return mRadius;
        }

        public Builder setRadius(float radius) {
            mRadius = radius;
            return this;
        }

        public int getColor() {
            return mColor;
        }

        public Builder setColor(int color) {
            mColor = color;
            return this;
        }

        public BlurUtil build() {
            return new BlurUtil(mContext, mRadius, mColor);
        }

    }

    /**
     * @param context
     * @param radius
     * @param color
     */
    public BlurUtil(Context context, @FloatRange(from = 0.0f) float radius, int color) {
        super();
        mContext = context;
        if (radius > MAX_RADIUS) {
            mSampling = radius / 25.0f;
            mRadius = MAX_RADIUS;
        } else {
            mRadius = radius;
        }
        mColor = color;
    }

    /**
     * @param context
     * @param radius
     */
    public BlurUtil(Context context, @FloatRange(from = 0.0f) float radius) {
        this(context, radius, Color.TRANSPARENT);
    }

    public BlurUtil(Context context) {
        this(context, DEFAULT_RADIUS);
    }


    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        boolean needScaled = mSampling == DEFAULT_SAMPLING;
        int originWidth = toTransform.getWidth();
        int originHeight = toTransform.getHeight();
        int width, height;
        if (needScaled) {
            width = originWidth;
            height = originHeight;
        } else {
            width = (int) (originWidth / mSampling);
            height = (int) (originHeight / mSampling);
        }

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        if (mSampling != DEFAULT_SAMPLING) {
            canvas.scale(1 / mSampling, 1 / mSampling);
        }
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        PorterDuffColorFilter filter =
                new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        RenderScript rs = RenderScript.create(mContext);
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        blur.setInput(input);
        blur.setRadius(mRadius);
        blur.forEach(output);
        output.copyTo(bitmap);

        rs.destroy();

        if (needScaled) {
            return bitmap;
        } else {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, originWidth, originHeight, true);
            bitmap.recycle();
            return scaled;
        }
    }
