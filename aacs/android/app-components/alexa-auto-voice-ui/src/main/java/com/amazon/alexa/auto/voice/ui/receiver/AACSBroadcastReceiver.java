package com.amazon.alexa.auto.voice.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazon.aacsconstants.Action;
import com.amazon.aacsconstants.Topic;
import com.amazon.alexa.auto.aacs.common.AACSMessage;
import com.amazon.alexa.auto.aacs.common.AACSMessageBuilder;
import com.amazon.alexa.auto.aacs.common.DialogStateChangedMessages;
import com.amazon.alexa.auto.aacs.common.WakewordDetectedMessages;
import com.amazon.alexa.auto.voice.ui.common.AutoVoiceUIMessage;
import com.amazon.alexa.auto.voice.ui.common.Constants;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Broadcast receiver which get AACS directives related to voice UI changes.
 */
public class AACSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AACSBroadcastReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceived");
        if (intent == null || intent.getAction() == null) {
            return;
        }

        AACSMessageBuilder.parseEmbeddedIntent(intent).ifPresent(message -> {
            Log.d(TAG, message.messageId + " | " + message.topic + " | " + message.action + " | " + message.payload);

            switch (message.action) {
                case Action.AlexaClient.DIALOG_STATE_CHANGED:
                    handleDialogStateChanged(message);
                    break;
                case Action.SpeechRecognizer.WAKEWORD_DETECTED:
                    handleWakewordDetected(message);
                    break;
                case Action.SpeechRecognizer.END_OF_SPEECH_DETECTED:
                    sendAutoVoiceUIMessage(
                            Topic.SPEECH_RECOGNIZER, Action.SpeechRecognizer.END_OF_SPEECH_DETECTED, "");
                    break;
            }
        });
    }

    @VisibleForTesting
    void handleDialogStateChanged(@NonNull AACSMessage aacsMessage) {
        if (aacsMessage.payload != null) {
            DialogStateChangedMessages.parseDialogState(aacsMessage.payload).ifPresent(payload -> {
                sendAutoVoiceUIMessage(
                        Constants.TOPIC_VOICE_ANIMATION, Action.AlexaClient.DIALOG_STATE_CHANGED, payload);
            });
        }
    }

    @VisibleForTesting
    void handleWakewordDetected(@NonNull AACSMessage aacsMessage) {
        if (aacsMessage.payload != null) {
            WakewordDetectedMessages.parseWakeword(aacsMessage.payload).ifPresent(payload -> {
                sendAutoVoiceUIMessage(
                        Constants.TOPIC_VOICE_ANIMATION, Action.SpeechRecognizer.WAKEWORD_DETECTED, payload);
            });
        }
    }

    @VisibleForTesting
    void sendAutoVoiceUIMessage(String topic, String action, String payload) {
        Log.d(TAG, "Sending Alexa Auto voice interaction message, topic: " + topic + ", action: " + action);
        EventBus.getDefault().post(new AutoVoiceUIMessage(topic, action, payload));
    }
}
