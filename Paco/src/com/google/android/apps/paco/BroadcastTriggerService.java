package com.google.android.apps.paco;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class BroadcastTriggerService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    if (intent == null) {
      Log.e(PacoConstants.TAG, "Null intent on broadcast trigger!");
      return;
    }
    final Bundle extras = intent.getExtras();

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                                    "Paco BroadcastTriggerService wakelock");
    wl.acquire();

    Runnable runnable = new Runnable() {
      public void run() {
        try {
          notifyExperimentsThatCare(extras);
        } finally {
          wl.release();
          stopSelf();
        }
      }
    };
    (new Thread(runnable)).start();
  }

  // TODO:phil branch this code, allowing passiveLogging and/or notification creation
  // TODO:phil refactor the backgroundListen thing into its own DAO?
  // TODO:phil refactor this backgroundListen thing to have a better name? like 'appListen'?
  protected void notifyExperimentsThatCare(Bundle extras) {
    
    final int triggerEvent = extras.getInt(Experiment.TRIGGER_EVENT);
    final String sourceIdentifier = extras.getString(Experiment.TRIGGER_SOURCE_IDENTIFIER);
    final String timeStr = extras.getString(Experiment.TRIGGERED_TIME);
    DateTime time = null;
    if (timeStr != null) {
      time = DateTimeFormat.forPattern(TimeUtil.DATETIME_FORMAT).parseDateTime(timeStr);
    }
    
    ExperimentProviderUtil eu = new ExperimentProviderUtil(this);
    DateTime now = new DateTime();
    NotificationCreator notificationCreator = NotificationCreator.create(this);
    List<Experiment> joined = eu.getJoinedExperiments();
    
    for (Experiment experiment : joined) {
      
      // see if this experiment wants background logging for the current event
      if (experiment.isRunning(now)
          && experiment.isBackgroundListen()
          && experiment.getBackgroundListenSourceIdentifier().equals(sourceIdentifier)) {
        
        // add PACO outputs and the original intent's extras as Output responses,
        // and insert event into ExperimentExecutor
        Event event = ExperimentExecutor.createEvent(experiment, now.getMillis());
        Bundle payload = extras.getBundle(BroadcastTriggerReceiver.PACO_ACTION_PAYLOAD);
        for (String key : payload.keySet()) {
          Output output = new Output();
          output.setEventId(event.getId());
          output.setName(key);
          output.setAnswer(payload.get(key).toString());
          event.addResponse(output);
        }
        eu.insertEvent(event);
        notifySyncService();
      }
      
      // see if this experiment wants a notification given the current event
      SignalingMechanism signalingMechanism = experiment.getSignalingMechanisms().get(0);
      if (experiment.isRunning(now)
          && experiment.shouldTriggerBy(triggerEvent, sourceIdentifier)
          && !recentlyTriggered(experiment.getServerId(),
                                signalingMechanism.getMinimumBuffer())) {
        setRecentlyTriggered(now, experiment.getServerId());
        notificationCreator.createNotificationsForTrigger(experiment, time, triggerEvent, sourceIdentifier);
      }
    }
  }

  private void setRecentlyTriggered(DateTime now, long experimentId) {
    UserPreferences prefs = new UserPreferences(getApplicationContext());
    prefs.setRecentlyTriggeredTime(experimentId, now);

  }

  private boolean recentlyTriggered(long experimentId, Integer minimumBufferInMinutes) {
    UserPreferences prefs = new UserPreferences(getApplicationContext());
    DateTime recentlyTriggered = prefs.getRecentlyTriggeredTime(experimentId);
    return recentlyTriggered != null && recentlyTriggered.plusMinutes(minimumBufferInMinutes).isAfterNow();
  }

  private void notifySyncService() {
    startService(new Intent(this, SyncService.class));
  }

}
