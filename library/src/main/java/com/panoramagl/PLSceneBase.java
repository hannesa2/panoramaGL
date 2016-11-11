/*
 * PanoramaGL library
 * Version 0.2 beta
 * Copyright (c) 2010 Javier Baez <javbaezga@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.panoramagl;

import android.os.Handler;

import com.panoramagl.computation.PLIntersection;
import com.panoramagl.computation.PLVector3;
import com.panoramagl.enumerations.PLSceneElementTouchStatus;
import com.panoramagl.hotspots.PLIHotspot;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.opengl.GLUES;
import com.panoramagl.opengl.matrix.MatrixGrabber;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.utils.PLOpenGLSupport;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public abstract class PLSceneBase extends PLRenderableElementBase implements PLIScene
{
	/**member variables*/
	
	private PLICamera mCamera;
	private PLCameraListener mInternalCameraListener;
	
	private List<PLISceneElement> mElements;
	
	private PLIView mView;
	
	private PLCollisionData mCollisionData;
	
	private MatrixGrabber mMatrixGrabber;
	private float[] mModelMatrix, mProjectionMatrix;
	private int[] mViewport;
	private float[] mPosition;
	
	private boolean mIsLocked, mIsWaitingForClick;
	
	/**init methods*/
	
	@Override
	protected void initializeValues()
	{
		mCamera = new PLCamera();
		mInternalCameraListener = null;
		mElements = new ArrayList<PLISceneElement>();
		mView = null;
		mCollisionData = PLCollisionData.PLCollisionDataMake();
		mMatrixGrabber = new MatrixGrabber();
		mModelMatrix = mMatrixGrabber.mModelView;
		mProjectionMatrix = mMatrixGrabber.mProjection;
		mViewport = new int[4];
		mPosition = new float[3];
		mIsLocked = mIsWaitingForClick = false;
		super.initializeValues();
	}
	
	/**reset methods*/
	
	@Override
	public void reset()
	{
		if(!mIsLocked)
		{
			super.reset();
			for(int i = 0; i < mElements.size(); i++)
				mElements.get(i).reset();
			mCamera.reset();
		}
	}
	
	@Override
	public void resetAlpha()
	{
		super.setAlpha(this.getDefaultAlpha());
		this.resetObjectsAlpha(mElements);
	}
	
	/**property methods*/
	
	@Override
	public PLICamera getCamera()
	{
		return mCamera;
	}
	
	@Override
	public void setCamera(PLICamera camera)
	{
		if(!mIsLocked && camera != null)
		{
			mCamera.setInternalListener(null);
			camera.setInternalListener(mInternalCameraListener);
			mCamera = camera;
		}
	}
	
	@Override
	public PLCameraListener getInternalCameraListener()
	{
		return mInternalCameraListener;
	}
	
	@Override
	public void setInternalCameraListener(PLCameraListener listener)
	{
		mInternalCameraListener = listener;
		mCamera.setInternalListener(listener);
	}
	
	protected List<PLISceneElement> getElements()
	{
		return mElements;
	}
	
	@Override
	public PLIView getInternalView()
	{
		return mView;
	}
	
	@Override
	public void setInternalView(PLIView view)
	{
		mView = view;
	}
	
	@Override
	public boolean isLocked()
	{
		return mIsLocked;
	}
	
	@Override
	public void setLocked(boolean isLocked)
	{
		mIsLocked = isLocked;
		mCamera.setLocked(isLocked);
	}
	
	@Override
	public boolean getWaitingForClick()
	{
		return mIsWaitingForClick;
	}
	
	@Override
	public void setWaitingForClick(boolean isWaitingForClick)
	{
		mIsWaitingForClick = isWaitingForClick;
	}
	
	@Override
	public void setAlpha(float alpha)
	{
		super.setAlpha(alpha);
		this.setObjectsAlpha(mElements, alpha);
	}
	
	/**alpha methods*/
	
	protected void setObjectsAlpha(List<? extends PLIObject> objects, float alpha)
	{
		for(int i = 0; i < objects.size(); i++)
			objects.get(i).setAlpha(alpha);
	}
	
	protected void resetObjectsAlpha(List<? extends PLIObject> objects)
	{
		for(int i = 0; i < objects.size(); i++)
		{
			PLIObject object = objects.get(i);
			object.setAlpha(object.getDefaultAlpha());
		}
	}
	
	/**elements methods*/
	
	@Override
	public int elementsLength()
	{
		return mElements.size();
	}
	
	@Override
	public List<PLISceneElement> getElements(List<PLISceneElement> elements)
	{
		if(elements != null && mElements.size() > 0)
		{
			synchronized(mElements)
			{
				elements.clear();
				elements.addAll(mElements);
			}
		}
		return elements;
	}
	
	@Override
	public PLISceneElement getElement(int index)
	{
		return (index >= 0 && index < mElements.size() ? mElements.get(index) : null);
	}
	
	@Override
	public boolean addElement(PLISceneElement element)
	{
		if(element != null)
		{
			synchronized(mElements)
			{
				mElements.add(element);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean insertElement(PLISceneElement element, int index)
	{
		if(element != null && index >= 0 && index <= mElements.size())
		{
			synchronized(mElements)
			{
				mElements.add(index, element);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeElement(PLISceneElement element)
	{
		if(element != null && mElements.contains(element))
		{
			synchronized(mElements)
			{
				mElements.remove(element);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public PLISceneElement removeElementAtIndex(int index)
	{
		if(index >= 0 && index < mElements.size())
		{
			synchronized(mElements)
			{
				return mElements.remove(index);
			}
		}
		return null;
	}
	
	@Override
	public boolean removeAllElements()
	{
		if(mElements.size() > 0)
		{
			synchronized(mElements)
			{
				mElements.clear();
				return true;
			}
		}
		return false;
	}
	
	protected boolean removeAllElements(boolean recycleElementsByParent)
	{
		int elementsLength = mElements.size();
		if(elementsLength > 0)
		{
			synchronized(mElements)
			{
				if(recycleElementsByParent)
				{
					for(int i = 0; i < elementsLength; i++)
					{
						PLISceneElement element = mElements.get(i);
						if(element.isRecycledByParent())
							element.clear();
					}
				}
				mElements.clear();
				return true;
			}
		}
		return false;
	}
	
	/**clear methods*/
	
	@Override
	protected void internalClear()
	{
		this.removeAllElements(true);
	}
	
	/**render methods*/
	
	@Override
	protected void beginRender(GL10 gl, PLIRenderer renderer)
	{
		super.beginRender(gl, renderer);
		mCamera.render(gl, renderer);
	}
	
	protected void renderElements(GL10 gl, PLIRenderer renderer)
	{
		this.renderRenderableElements(gl, renderer, mElements);
	}
	
	protected void renderRenderableElements(GL10 gl, PLIRenderer renderer, List<? extends PLIRenderableElement> elements)
	{
		for(int i = 0; i < elements.size(); i++)
			elements.get(i).render(gl, renderer);
	}
	
	@Override
	protected void endRender(GL10 gl, PLIRenderer renderer)
	{
		this.renderElements(gl, renderer);
		if(mView != null && !mView.isValidForScrolling() && !mView.isValidForFov() && !mView.isValidForTransition())
		{
			this.updateMatrixes(gl);
			CGPoint screenPoint = mView.getEndPoint();
			this.createRayWithPoint(gl, renderer, screenPoint, mCollisionData.ray);
			this.checkCollisionsWithRay(gl, mCollisionData.ray, screenPoint, !mIsWaitingForClick);
			if(mIsWaitingForClick)
				mIsWaitingForClick = false;
		}
		super.endRender(gl, renderer);
	}
	
	/**matrix methods*/
	
	protected void updateMatrixes(GL10 gl)
	{
		if(PLOpenGLSupport.isHigherThanOpenGL1(gl))
		{
			GL11 gl11 = (GL11)gl;
			gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, mProjectionMatrix, 0);
			gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mModelMatrix, 0);
		}
		else
		{
			mMatrixGrabber.getCurrentProjection(gl);
			mMatrixGrabber.getCurrentModelView(gl);
		}
	}
	
	/**conversion methods*/
	
	@Override
	public PLPosition convertPointTo3DPoint(GL10 gl, CGPoint point, float z)
	{
		PLPosition result = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f);
		this.convertPointTo3DPoint(gl, point, z, result);
		return result;
	}
	
	@Override
	public void convertPointTo3DPoint(GL10 gl, CGPoint point, float z, PLPosition result)
	{
		if(result != null)
		{
			if(mView != null && gl != null && point != null)
			{
				this.updateMatrixes(gl);
				CGRect viewport = mView.getRenderingViewport();
				mViewport[0] = viewport.x;
				mViewport[1] = viewport.y;
				mViewport[2] = viewport.width;
				mViewport[3] = viewport.height;
				float y = (float)mView.getRenderingSize().height - point.y;
				GLUES.gluUnProject(point.x, y, z, mModelMatrix, 0, mProjectionMatrix, 0, mViewport, 0, mPosition, 0);
				result.setValues(mPosition);
			}
			else
				result.reset();
		}
	}
	
	/**collision methods*/
	
	protected void createRayWithPoint(GL10 gl, PLIRenderer renderer, CGPoint point, PLVector3[] ray)
	{
		CGRect viewport = renderer.getViewport();
		mViewport[0] = viewport.x;
		mViewport[1] = viewport.y;
		mViewport[2] = viewport.width;
		mViewport[3] = viewport.height;
		float y = (float)renderer.getSize().height - point.y;
		GLUES.gluUnProject(point.x, y, 0.0f, mModelMatrix, 0, mProjectionMatrix, 0, mViewport, 0, mPosition, 0);
		ray[0].setValues(mPosition);
		GLUES.gluUnProject(point.x, y, 1.0f, mModelMatrix, 0, mProjectionMatrix, 0, mViewport, 0, mPosition, 0);
		ray[1].setValues(mPosition);
	}
	
	protected int checkCollisionsWithRay(GL10 gl, PLVector3[] ray, CGPoint screenPoint, boolean isMoving)
	{
		return this.checkSceneElementsCollisionWithRay(gl, mElements, ray, screenPoint, isMoving);
	}
	
	protected int checkSceneElementsCollisionWithRay(GL10 gl, List<? extends PLISceneElement> elements, PLVector3[] ray, CGPoint screenPoint, boolean isMoving)
	{
		int hits = 0;
		for(int i = 0; i < elements.size(); i++)
		{
			PLISceneElement element = elements.get(i);
			if(element.isCollisionEnabled())
			{
				float vertexs[] = element.getVertexs();
				if(vertexs == null || vertexs.length != 12)
					continue;
				
				mCollisionData.points[0].setValues(vertexs[0], vertexs[1],  vertexs[2]);
				mCollisionData.points[1].setValues(vertexs[3], vertexs[4],  vertexs[5]);
				mCollisionData.points[2].setValues(vertexs[6], vertexs[7],  vertexs[8]);
				mCollisionData.points[3].setValues(vertexs[9], vertexs[10], vertexs[11]);
				
				if(PLIntersection.checkLineBox(ray, mCollisionData.points[0], mCollisionData.points[1], mCollisionData.points[2], mCollisionData.points[3], mCollisionData.hitPoint))
				{
					if(isMoving)
					{
						if(element.getTouchStatus() == PLSceneElementTouchStatus.PLSceneElementTouchStatusOut)
						{
							if(element.touchOver(this))
								this.performSceneElementOverEvent(mView, element, screenPoint, mCollisionData.hitPoint[0].getPosition(new PLPosition()));
						}
						else
							element.touchMove(this);
					}
					else
					{
						if(element.touchDown(this))
							this.performSceneElementClickEvent(mView, element, screenPoint, mCollisionData.hitPoint[0].getPosition(new PLPosition()));
						break;
					}
					hits++;
				}
				else
				{
					if(element.getTouchStatus() != PLSceneElementTouchStatus.PLSceneElementTouchStatusOut)
					{
						if(element.touchOut(this));
							this.performSceneElementOutEvent(mView, element, screenPoint, mCollisionData.hitPoint[0].getPosition(new PLPosition()));
					}
				}
			}
		}
		return hits;
	}
	
	/**scene element event methods*/
	
	protected void performSceneElementOverEvent(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{
		new Handler(view.getContext().getMainLooper()).post(new PLSceneElementEventRunnable(view, element, screenPoint, scene3DPoint, PLSceneElementTouchStatus.PLSceneElementTouchStatusOver));
	}
	
	protected void performSceneElementClickEvent(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{
		new Handler(view.getContext().getMainLooper()).post(new PLSceneElementEventRunnable(view, element, screenPoint, scene3DPoint, PLSceneElementTouchStatus.PLSceneElementTouchStatusDown));
	}
	
	protected void performSceneElementOutEvent(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{
		new Handler(view.getContext().getMainLooper()).post(new PLSceneElementEventRunnable(view, element, screenPoint, scene3DPoint, PLSceneElementTouchStatus.PLSceneElementTouchStatusOut));
	}
	
	/**PLIReleaseView methods*/
	
	@Override
	public void releaseView()
	{
		mView = null;
		mInternalCameraListener = null;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			this.clear();
		}
		catch(Throwable e)
		{
		}
		mCamera = null;
		mInternalCameraListener = null;
		mElements = null;
		mView = null;
		mCollisionData = null;
		mMatrixGrabber = null;
		mModelMatrix = mProjectionMatrix = null;
		mViewport = null;
		mPosition = null;
		super.finalize();
	}
	
	/**internal classes declaration*/
	
	protected static class PLCollisionData
	{
		/**member variables*/
		
		public PLVector3[] ray, hitPoint, points;
		
		/**init methods*/
		
		public PLCollisionData()
		{
			super();
			ray = new PLVector3[]{ new PLVector3(), new PLVector3() };
			hitPoint = new PLVector3[]{ new PLVector3() };
			points = new PLVector3[]{ new PLVector3(), new PLVector3(), new PLVector3(), new PLVector3() };
		}
		
		public static PLCollisionData PLCollisionDataMake()
		{
			return new PLCollisionData();
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			ray = hitPoint = points = null;
			super.finalize();
		}
	}
	
	protected class PLSceneElementEventRunnable implements Runnable
	{
		/**member variables*/
		
		private PLIView mView;
		private PLISceneElement mElement;
		private CGPoint mScreenPoint;
		private PLPosition mScene3DPoint;
		private PLSceneElementTouchStatus mTouchStatus;
		
		/**init methods*/
		
		public PLSceneElementEventRunnable(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint, PLSceneElementTouchStatus touchStatus)
		{
			super();
			mView = view;
			mElement = element;
			mScreenPoint = screenPoint;
			mScene3DPoint = scene3DPoint;
			mTouchStatus = touchStatus;
		}
		
		/**runnable methods*/
		
		@Override
		public void run()
		{
			PLViewListener listener = mView.getListener();
			if(listener != null)
			{
				switch(mTouchStatus)
				{
					case PLSceneElementTouchStatusOver:
						listener.onDidOverElement(mView, mElement, mScreenPoint, mScene3DPoint);
						if(mElement instanceof PLIHotspot)
							listener.onDidOverHotspot(mView, (PLIHotspot)mElement, mScreenPoint, mScene3DPoint);
						break;
					case PLSceneElementTouchStatusDown:
						listener.onDidClickElement(mView, mElement, mScreenPoint, mScene3DPoint);
						if(mElement instanceof PLIHotspot)
							listener.onDidClickHotspot(mView, (PLIHotspot)mElement, mScreenPoint, mScene3DPoint);
						break;
					case PLSceneElementTouchStatusOut:
						listener.onDidOutElement(mView, mElement, mScreenPoint, mScene3DPoint);
						if(mElement instanceof PLIHotspot)
							listener.onDidOutHotspot(mView, (PLIHotspot)mElement, mScreenPoint, mScene3DPoint);
						break;
					default:
						break;
				}
			}
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mView = null;
			mElement = null;
			mScreenPoint = null;
			mScene3DPoint = null;
			mTouchStatus = null;
			super.finalize();
		}
	}
}