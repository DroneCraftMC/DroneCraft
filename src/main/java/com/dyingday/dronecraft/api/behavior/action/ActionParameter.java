/* (C)2025 */
package com.dyingday.dronecraft.api.behavior.action;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

/**
 * Defines a configurable parameter for an action in the visual programming interface. Each
 * parameter specifies its identifier, display information, type constraints, and validation
 * requirements. The visual programming system uses this metadata to generate appropriate UI
 * controls (text inputs, number spinners, dropdowns, etc.) and validate user input before action
 * execution.
 *
 * @param <T> The Java type of this parameter's value
 * @param id The unique identifier for this parameter within the action
 * @param displayName The localized name shown to users in the UI
 * @param type The Java class representing the parameter's type (used for validation and UI
 *     generation)
 * @param defaultValue The default value used when the parameter is not specified, or null if no
 *     default
 * @param required Whether this parameter must be provided by the user (true) or is optional (false)
 */
public record ActionParameter<T>(
    String id, Component displayName, Class<T> type, @Nullable T defaultValue, boolean required) {}
