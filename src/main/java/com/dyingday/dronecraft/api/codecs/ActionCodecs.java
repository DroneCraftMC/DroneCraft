/* (C)2025 */
package com.dyingday.dronecraft.api.codecs;

/** Codec for parsing Actions */
public class ActionCodecs {

  private ActionCodecs() {}

  //    public static final Codec<IAction> ACTION_CODEC = Identifier.CODEC.dispatch("action_type",
  // action -> ActionRegistry.getActionId(action), ActionRegistry::getActionCodc);

  //    public static final Codec<Behavior> BEHAVIOR_CODEC = RecordCodecBuilder.create(instance ->
  //            instance.group(
  //                    Identifier.CODEC.fieldOf("id").forGetter(Behavior::getId),
  //                    BehaviorNode.CODEC.listOf().filedOf("nodes").forGetter(Behavior::getNodes),
  //                    Codec.unboundedMap(Codec.STRING,
  // ExtraCodecs.JAVA.fieldOf("variables")).optionalFieldOf("variables",
  // Map.of()).forGetter(Behavior::getDefaultVariables)
  //            ).apply(instance, Behavior::new)
  //    );
}
