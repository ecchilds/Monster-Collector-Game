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
    },

    WATCH_PLAYER() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            Entity target = targets.get(0);
            if (target instanceof Creature) {
                ((Creature) target).addWatcher(mob);
            }
            var wrapper = (ActionWrapper)action;
            wrapper.start();
            ((WatchAction)wrapper.getWrapped()).setTarget(target);
        }

        @Override
        public EyeballState reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
            return reEnterWrappedTargetedAction(mob, ai, action);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateReversionActionWrapper(mob, new WatchAction(mob), ai, 1);
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            var wrapper = (ActionWrapper)action;
            wrapper.resetAndSleep();

            var watchAction = (WatchAction)wrapper.getWrapped();
            Entity target = watchAction.getTarget();
            if (target instanceof Creature) {
                ((Creature) target).removeWatcher(mob);
            }
            watchAction.setTimeToLive(2);
        }

        @Override
        public boolean handleMessage(Telegram telegram, Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            switch (telegram.message) {
                case (MessageTypes.ROOM_CHANGE):
                    ai.changeState(ai.getDefaultState(), 1);
                    break;
                case (MessageTypes.ITEM_DROPPED):
                    ai.changeState(PURSUE_ITEM_FROM_PLAYER, 4, List.of((Entity)telegram.extraInfo, (Player)telegram.sender));
                    break;
                default:
                    return false;
            }
            return true;
        }
    },

    PURSUE_ITEM() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            var wrapper = (ActionWrapper)action;
            wrapper.start();
            ((PursueAction)wrapper.getWrapped()).setTarget(targets.get(0));
        }

        @Override
        public EyeballState reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
            return reEnterWrappedTargetedAction(mob, ai, action);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateReversionActionWrapper(mob, new PursueAction(mob, (Entity target) -> {
                ((Item)target).pickUp();
            }), ai, 1);
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            ((ActionWrapper)action).resetAndSleep();
        }
    },

    PURSUE_ITEM_FROM_PLAYER() {

        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            var wrapper = (ActionWrapper)action;
            wrapper.start();
            ((ActionSequence)wrapper.getWrapped()).setTargets(targets);
        }

        @Override
        public boolean isReEnterable() {
            return false;
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
            ((ActionWrapper)action).resetAndSleep();
        }
    },

    CONFUSION() {
        @Override
        public void enter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action, List<Entity> targets) {
            ((ActionWrapper)action).start();
            addAppleObservationListener(mob, ai);
        }

        @Override
        public boolean isReEnterable() {
            return false;
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new StateTransitionActionWrapper<>(mob, ai, ai.getDefaultState(), 1, new ConfusionAction(mob, 2.4f));
        }

        @Override
        public void exit(Eyeball mob, Action action) {
            ((ActionWrapper)action).resetAndSleep();
            mob.removeObservationListener(Apple.class);
        }
    };

    private static void addAppleObservationListener(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
        mob.addObservationListener(Apple.class, (apple) -> {
            ai.changeState(PURSUE_ITEM, 3, List.of(apple));
        });
    }

    private static EyeballState reEnterWrappedTargetedAction(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
        var wrapper = (ActionWrapper)action;
        if (((PursueAction)wrapper.getWrapped()).getTarget().isExisting()) {
            wrapper.start();
            return null;
        } else {
            return CONFUSION;
        }
    }
}
