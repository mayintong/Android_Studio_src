package com.cogent.Communications;

import java.util.Map;

/**
 * Created by shawn on 3/30/15.
 */

public interface BLIObserver {
    public void onBLUpdate(int notificationType, String args);
}
