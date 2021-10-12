package com.panoramagl.hotspots

import com.panoramagl.*
import com.panoramagl.computation.PLVector3
import com.panoramagl.enumerations.PLSceneElementTouchStatus
import com.panoramagl.interpreters.PLCommandInterpreter
import com.panoramagl.interpreters.PLIInterpreter
import com.panoramagl.structs.PLPosition
import com.panoramagl.structs.PLRect
import com.panoramagl.utils.PLUtils
import java.nio.FloatBuffer
import java.util.*
import javax.microedition.khronos.opengles.GL10

open class PLHotspot : PLSceneElementBase, PLIHotspot {
    private var mWidth = PLConstants.kDefaultHotspotSize
    private var mHeight = PLConstants.kDefaultHotspotSize
    private var mAtv = 0f
    private var mAth = 0f
    private var mVertexs: FloatArray = FloatArray(12)
    private var mVertexsBuffer: FloatBuffer? = null
    private var mTextureCoordsBuffer: FloatBuffer? = null
    private var mOnClick: String? = null
    private var mOverAlpha = 0f
    private var mDefaultOverAlpha = 0f
    private var hasChangedCoordProperty = false
    private var hasManuallySetPosition = false

    constructor(identifier: Long, atv: Float, ath: Float, width: Float, height: Float) : super(identifier) {
        mAtv = atv
        mAth = ath
        mWidth = width
        mHeight = height
    }

    constructor(identifier: Long, texture: PLITexture?, atv: Float, ath: Float, width: Float, height: Float) : super(identifier, texture) {
        mAtv = atv
        mAth = ath
        mWidth = width
        mHeight = height
    }

    constructor(identifier: Long, image: PLIImage, atv: Float, ath: Float, width: Float, height: Float) : this(
        identifier,
        PLTexture(image),
        atv,
        ath,
        width,
        height
    ) {
    }

    override fun initializeValues() {
        super.initializeValues()
        this.width = PLConstants.kDefaultHotspotSize
        this.height = PLConstants.kDefaultHotspotSize
        mAth = 0.0f
        mAtv = mAth
        isYZAxisInverseRotation = true
        z = PLConstants.kDefaultHotspotZPosition
        mOnClick = null
        alpha = PLConstants.kDefaultHotspotAlpha
        defaultAlpha = PLConstants.kDefaultHotspotAlpha
        mDefaultOverAlpha = PLConstants.kDefaultHotspotOverAlpha
        mOverAlpha = mDefaultOverAlpha
        hasChangedCoordProperty = true
    }

    override fun reset() {
        super.reset()
        this.overAlpha = mDefaultOverAlpha
    }

    override fun getAtv(): Float {
        return mAtv
    }

    override fun setAtv(atv: Float) {
        if (mAtv != atv) {
            mAtv = atv
            hasChangedCoordProperty = true
        }
    }

    override fun getAth(): Float {
        return mAth
    }

    override fun setAth(ath: Float) {
        if (mAth != ath) {
            mAth = ath
            hasChangedCoordProperty = true
        }
    }

    override fun getWidth(): Float {
        return mWidth / (PLConstants.kPanoramaRadius * 2.0f)
    }

    override fun setWidth(width: Float) {
        if (width in 0.0f..1.0f && width != width) {
            mWidth = width * PLConstants.kPanoramaRadius * 2.0f
            hasChangedCoordProperty = true
        }
    }

    override fun getHeight(): Float {
        return mHeight / (PLConstants.kPanoramaRadius * 2.0f)
    }

    override fun setHeight(height: Float) {
        if (height in 0.0f..1.0f && height != height) {
            mHeight = height * PLConstants.kPanoramaRadius * 2.0f
            hasChangedCoordProperty = true
        }
    }

    override fun getOnClick(): String {
        return mOnClick!!
    }

    override fun setOnClick(onClick: String) {
        mOnClick = onClick.trim { it <= ' ' }
    }

    override var alpha: Float = PLConstants.kDefaultHotspotAlpha
        get() = super.alpha
        set(alpha) {
            field = Math.min(alpha, defaultAlpha)
        }

    override fun getOverAlpha(): Float {
        return mOverAlpha
    }

    override fun setOverAlpha(overAlpha: Float) {
        mOverAlpha = overAlpha
    }

    override fun getDefaultOverAlpha(): Float {
        return mDefaultOverAlpha
    }

    override fun setDefaultOverAlpha(defaultOverAlpha: Float) {
        mDefaultOverAlpha = defaultOverAlpha
    }

    override fun getRect(): PLRect {
        val rect = PLRect.PLRectMake()
        this.getRect(rect)
        return rect
    }

    override fun getRect(rect: PLRect) {
        if (mVertexsBuffer != null) rect.setValues(
            mVertexs[0],
            mVertexs[1],
            mVertexs[2],
            mVertexs[9],
            mVertexs[10],
            mVertexs[11]
        ) else rect.reset()
    }

    override fun getVertexs(): FloatArray {
        return mVertexs
    }

    override var x: Float
        get() = super.x
        set(x) {
            super.x = x
            hasManuallySetPosition = true
            hasChangedCoordProperty = true
        }
    override var y: Float
        get() = super.y
        set(y) {
            super.y = y
            hasManuallySetPosition = true
            hasChangedCoordProperty = true
        }
    override var z: Float
        get() = super.z
        set(z) {
            super.z = z
            hasManuallySetPosition = true
            hasChangedCoordProperty = true
        }

    /**
     * layout methods
     */
    override fun setSize(valeuWidth: Float, valueHeight: Float) {
        width = valeuWidth
        height = valueHeight
    }

    override fun setLayout(pitch: Float, yaw: Float, valeuWidth: Float, valueHeight: Float) {
        this.pitch = pitch
        this.yaw = yaw
        width = valeuWidth
        height = valueHeight
    }

    protected fun array(result: FloatArray, size: Int, vararg args: Float) {
        if (size >= 0) System.arraycopy(args, 0, result, 0, size)
    }

    /**
     * calculate methods
     */
    protected fun convertPitchAndYawToPosition(pitch: Float, yaw: Float): PLPosition {
        val r = z
        val pr = (90.0f - pitch) * PLConstants.kToRadians
        val yr = -yaw * PLConstants.kToRadians
        val x = r * Math.sin(pr.toDouble()).toFloat() * Math.cos(yr.toDouble()).toFloat()
        val y = r * Math.sin(pr.toDouble()).toFloat() * Math.sin(yr.toDouble()).toFloat()
        val z = r * Math.cos(pr.toDouble()).toFloat()
        return PLPosition.PLPositionMake(y, z, x)
    }

    protected fun calculatePoints(gl: GL10?): List<PLPosition> {
        val result: MutableList<PLPosition> = ArrayList(4)
        //1
        val pos = convertPitchAndYawToPosition(mAtv, mAth)
        val pos1 = convertPitchAndYawToPosition(mAtv + 0.0001f, mAth)

        //2 and 3
        val x: Float
        val y: Float
        val z: Float
        if (!hasManuallySetPosition) {
            x = pos.x
            y = pos.y
            z = pos.z
        } else {
            x = this.x
            y = this.y
            z = this.z
        }
        val p1 = PLVector3(x, y, z)
        val p2p1 = PLVector3(0.0f, 0.0f, 0.0f).sub(p1)
        val r: PLVector3
        r = if (hasManuallySetPosition) {
            p2p1.crossProduct(PLVector3(x, y + 1f, z).sub(p1))
        } else {
            p2p1.crossProduct(PLVector3(pos1.x, pos1.y, pos1.z).sub(p1))
        }
        val s = p2p1.crossProduct(r)
        r.normalize()
        s.normalize()
        //5.1
        val w = mWidth * PLConstants.kPanoramaRadius
        val h = mHeight * PLConstants.kPanoramaRadius
        val radius = Math.sqrt((w * w + h * h).toDouble()).toFloat()
        //5.2
        val angle = Math.asin((h / radius).toDouble()).toFloat()
        //5.3
        val n = PLVector3(0.0f, 0.0f, 0.0f)
        for (theta in floatArrayOf(PLConstants.kPI - angle, angle, PLConstants.kPI + angle, 2 * PLConstants.kPI - angle)) {
            n.x = p1.x + radius * Math.cos(theta.toDouble()).toFloat() * r.x + radius * Math.sin(theta.toDouble()).toFloat() * s.x
            n.y = p1.y + radius * Math.cos(theta.toDouble()).toFloat() * r.y + radius * Math.sin(theta.toDouble()).toFloat() * s.y
            n.z = p1.z + radius * Math.cos(theta.toDouble()).toFloat() * r.z + radius * Math.sin(theta.toDouble()).toFloat() * s.z
            n.normalize()
            result.add(PLPosition.PLPositionMake(n.x, n.y, n.z))
        }
        return result
    }

    protected fun calculateCoords(gl: GL10?) {
        if (!hasChangedCoordProperty) return
        hasChangedCoordProperty = false
        val textureCoords = FloatArray(8)
        val positions = calculatePoints(gl)
        val pos1 = positions[0]
        val pos2 = positions[1]
        val pos3 = positions[2]
        val pos4 = positions[3]
        array(
            mVertexs, 12,
            pos1.x, pos1.y, pos1.z,
            pos2.x, pos2.y, pos2.z,
            pos3.x, pos3.y, pos3.z,
            pos4.x, pos4.y, pos4.z
        )
        array(
            textureCoords, 8,
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
        )
        mVertexsBuffer = PLUtils.makeFloatBuffer(mVertexs)
        mTextureCoordsBuffer = PLUtils.makeFloatBuffer(textureCoords)
    }

    /**
     * translate methods
     */
    override fun translate(gl: GL10) {}

    /**
     * render methods
     */
    override fun internalRender(gl: GL10, renderer: PLIRenderer) {
        calculateCoords(gl)
        val textures = this.textures
        val textureId = if (textures.size > 0) textures[0].getTextureId(gl) else 0
        if (textureId == 0 || mVertexsBuffer == null || mTextureCoordsBuffer == null) return
        gl.glEnable(GL10.GL_TEXTURE_2D)
        val view = renderer.internalView
        gl.glColor4f(
            1.0f,
            1.0f,
            1.0f,
            if (view != null && view.isValidForTransition || this.touchStatus == PLSceneElementTouchStatus.PLSceneElementTouchStatusOut) alpha else mOverAlpha
        )
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexsBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoordsBuffer)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glEnable(GL10.GL_CULL_FACE)
        gl.glCullFace(GL10.GL_FRONT)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)
        gl.glDisable(GL10.GL_TEXTURE_2D)
        gl.glDisable(GL10.GL_BLEND)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
    }

    /**
     * touch methods
     */
    override fun touchDown(sender: Any): Boolean {
        if (super.touchDown(sender)) {
            if (mOnClick != null && mOnClick!!.length > 0) {
                val commandInterpreter: PLIInterpreter = PLCommandInterpreter()
                if (sender is PLIScene) commandInterpreter.interpret(
                    sender.internalView,
                    mOnClick
                ) else if (sender is PLIRenderer) commandInterpreter.interpret(
                    sender.internalView, mOnClick
                )
            }
            return true
        }
        return false
    }

    /**
     * clone methods
     */
    override fun clonePropertiesOf(`object`: PLIObject): Boolean {
        if (super.clonePropertiesOf(`object`)) {
            if (`object` is PLIHotspot) {
                val hotspot = `object`
                this.atv = hotspot.atv
                this.ath = hotspot.ath
                this.width = hotspot.width
                this.height = hotspot.height
                this.overAlpha = hotspot.overAlpha
                this.defaultOverAlpha = hotspot.defaultOverAlpha
            }
            return true
        }
        return false
    }

    /**
     * dealloc methods
     */
    @Throws(Throwable::class)
    override fun finalize() {
        mTextureCoordsBuffer = null
        mVertexsBuffer = mTextureCoordsBuffer
        super.finalize()
    }

    private fun convertXYtoYawPitch(normalizedX: Float, normalizedY: Float): FloatArray {
        val rot = FloatArray(2)
        rot[0] = (2 * Math.PI * -normalizedX).toFloat()
        rot[1] = (Math.PI * (2.0f - normalizedY)).toFloat()
        return rot
    }

    private fun convertYawPitchTo3D(angles: FloatArray): PLPosition {
        val result = PLPosition.PLPositionMake()
        result.x = (Math.sin(angles[1].toDouble()) * Math.sin(angles[0].toDouble())).toFloat()
        result.z = (Math.sin(angles[1].toDouble()) * Math.cos(angles[0].toDouble())).toFloat()
        result.y = Math.cos(angles[1].toDouble()).toFloat()
        return result
    }

    /**
     * Set position of a point given the normalized 2D coordinates of an hotspot
     * To normalize a coordinate just do coordinateX / imageWidth and coordinateY / imageHeight
     *
     * @param normalizedX the normalized X coordinate
     * @param normalizedY the normalized Y coordinate
     */
    fun setPosition(normalizedX: Float, normalizedY: Float) {
        val position = convertYawPitchTo3D(convertXYtoYawPitch(normalizedX, normalizedY))
        super.position = position
    }
}