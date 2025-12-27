/* (C)2025 */
package com.dyingday.dronecraft.api.behavior.action;

/** Represents a category an action is associated with */
public enum ActionCategory {
  /** For any actions that require moving (i.e.e move to block, or teleport action) */
  MOVEMENT("movement"),

  /** For any actions that require interaction with an item in the inventory */
  INVENTORY("inventory"),

  /** For any actions that require interacting with the world */
  INTERACTION("interaction"),

  /** For any actions that do any computation */
  LOGIC("logic"),

  /** For any helper actions (i.e. finding trees in an area) */
  UTILITY("utility"),

  /** For any custom actions */
  CUSTOM("custom");

  private final String name;

  ActionCategory(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
