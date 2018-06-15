package com.tbs.tbssms.receiver;

/**
 * 
 * @author任雪涛 pubDate:2011-12-28
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tbs.tbssms.R;
import com.tbs.tbssms.client.ServiceManager;

public class launchPhone extends BroadcastReceiver
{

    private static final String TAG = "launchPhone";

    @Override
    public void onReceive(Context arg0, Intent arg1)
    {
        if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d(TAG,"onReceive......................");
            ServiceManager serviceManager = new ServiceManager(arg0);
            serviceManager.setNotificationIcon(R.drawable.notification);
            serviceManager.startService();
        }
    }

}
