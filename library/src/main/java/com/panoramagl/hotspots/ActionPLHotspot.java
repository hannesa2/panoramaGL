package com.panoramagl.hotspots;


import com.panoramagl.PLIImage;

public class ActionPLHotspot extends PLHotspot {

    private HotSpotListener hotSpotListener;

    public ActionPLHotspot(HotSpotListener hotSpotListener, long identifier, PLIImage image, float atv, float ath, float width, float height) {
        super(identifier, image, atv, ath, width, height);
        this.hotSpotListener = hotSpotListener;
    }

    public boolean touchDown(Object obj) {
        boolean touchDown = super.touchDown(obj);
        hotSpotListener.onClick(getIdentifier());
        return touchDown;
    }

    public interface HotSpotListener{
        void onClick(long identifier);
    }
}
