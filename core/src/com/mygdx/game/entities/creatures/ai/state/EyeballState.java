package com.mygdx.game.entities.creatures.ai.state;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.Apple;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Item;
import com.mygdx.game.entities.creatures.Creature;
import com.mygdx.game.entities.creatures.Eyeball;
import com.mygdx.game.entities.creatures.Player;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.actions.*;
import com.mygdx.game.entities.utils.MessageTypes;

import java.util.List;

public enum EyeballState implements State<Eyeball, EyeballState> {

    WANDER() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            addAppleObservationListener(mob, ai);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new WanderAction(mob);
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            mob.removeObservationListener(Apple.class);
        }

        private void addAppleObservationListener(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            mob.addObservationListener(Apple.class, (apple) -> {
                ai.changeState(PURSUE_ITEM, 1, List.of(apple));
            });
        }
    },

    WATCH_PLAYER() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            Entity target = targets.get(0);
            if (target instanceof Creature) {
                ((Creature) target).addWatcher(mob);
            }
            var watchAction = (WatchAction)((StateTransitionActionWrapper<?>)action).getWrapped();
            watchAction.setTarget(target);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateTransitionActionWrapper<>(mob, ai, ai.getDefaultState(), 1, new WatchAction(mob));
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            var wrapper = (StateTransitionActionWrapper<?>)action;
            wrapper.reset();

            var watchAction = (WatchAction)wrapper.getWrapped();
            Entity target = watchAction.getTarget();
            if (target instanceof Creature) {
                ((Creature) target).removeWatcher(mob);
            }
            watchAction.setTimeToLive(2);
        }

        @Override
        public void handleMessage(Telegram telegram, Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            switch (telegram.message) {
                case (MessageTypes.ROOM_CHANGE):
                    ai.changeState(ai.getDefaultState(), 1);
                    break;
                case (MessageTypes.ITEM_DROPPED):
                    ai.changeState(PURSUE_ITEM_FROM_PLAYER, 4, List.of((Entity)telegram.extraInfo, (Player)telegram.sender));
                    break;
                default:
            }
        }
    },

    PURSUE_ITEM() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            var pursueAction = (PursueAction)((StateTransitionActionWrapper<?>)action).getWrapped();
            pursueAction.setTarget(targets.get(0));
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateTransitionActionWrapper<>(mob, ai, WANDER, 1, new PursueAction(mob, (Entity target) -> {
                ((Item)target).pickUp();
            }));
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            ((StateTransitionActionWrapper<?>)action).reset();
        }
    },

    PURSUE_ITEM_FROM_PLAYER() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            var actionSequence = (ActionSequence)((StateTransitionActionWrapper<?>) action).getWrapped();
            actionSequence.setTargets(targets);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateTransitionActionWrapper<>(mob, ai, ai.getDefaultState(), 1, new ActionSequence(mob,
                    new PursueAction(mob, (Entity target) -> ((Item)target).pickUp()),
                    new WatchAction(mob, 2)
            ));
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            ((StateTransitionActionWrapper<?>)action).reset();
        }
    };
}
