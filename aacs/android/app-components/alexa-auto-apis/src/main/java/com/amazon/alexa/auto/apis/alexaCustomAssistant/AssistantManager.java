package com.amazon.alexa.auto.apis.alexaCustomAssistant;

import com.amazon.alexa.auto.apis.app.ScopedComponent;

import org.json.JSONObject;

/**
 * An interface that allows components to have access to assistant related information.
 */
public interface AssistantManager extends ScopedComponent {
    /**
     * Provide the current state of the assistant manager.
     * @return The state of the assistant manager.
     */
    String getAssistantsState();

    /**
     * Provide the assistants settings.
     * @return The settings of the assistants.
     */
    JSONObject getAssistantsSettings();

    /**
     * Provide the default assistant for PTT.
     * @return The assistant that is assigned to PTT.
     */
    String getDefaultAssistantForPTT();
}
