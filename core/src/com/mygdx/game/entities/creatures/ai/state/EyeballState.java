package com.mygdx.game.entities.creatures.ai.state;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.Apple;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Item;
import com.mygdx.game.entities.creatures.Creature;
import com.mygdx.game.entities.creatures.Eyeball;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.actions.Action;
import com.mygdx.game.entities.creatures.ai.actions.PursueAction;
import com.mygdx.game.entities.creatures.ai.actions.WanderAction;
import com.mygdx.game.entities.creatures.ai.actions.WatchAction;
import com.mygdx.game.entities.utils.MessageTypes;

public enum EyeballState implements State<Eyeball, EyeballState> {

    WANDER() {

        @Override
        public void enter(Eyeball mob) {
        }

        //TODO: debug this shit. for whatever reason, mob will only see the apple the first time. and never again.
        @Override
        public void reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
            addAppleObservationListener(mob, ai);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            addAppleObservationListener(mob, ai);
            return new WanderAction(mob);
        }

        @Override
        public void exit(Eyeball mob) {
            mob.removeObservationListener(Apple.class);
        }

        private void addAppleObservationListener(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            mob.addObservationListener(Apple.class, (apple) -> {
                mob.setTarget(apple);
                PursueAction pursueAction = (PursueAction) ai.changeState(PURSUE_ITEM);
                pursueAction.appendToEndedFunction(() -> {
                    ai.changeState(WANDER);
                });
            });
        }

//        @Override
//        public void handleMessage(Telegram telegram, Eyeball Mob, MobAI<Eyeball, EyeballState> ai) {
//            switch(telegram.message) {
//                case ()
//            }
//        }
    },

    WATCH() {
        @Override
        public void enter(Eyeball mob) {
            Entity target = mob.getTarget();
            if (target instanceof Creature) {
                ((Creature) target).addWatcher(mob);
            }
        }

        @Override
        public void reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
            ((WatchAction) action).reset();
            ((WatchAction) action).setTimeToLive(2);
            enter(mob);
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new WatchAction(mob, () -> ai.changeState(WANDER));
        }

        @Override
        public void exit(Eyeball mob) {
            Entity target = mob.getTarget();
            if (target instanceof Creature) {
                ((Creature) target).removeWatcher(mob);
            }
        }

        @Override
        public void handleMessage(Telegram telegram, Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            switch (telegram.message) {
                case (MessageTypes.ROOM_CHANGE):
                    ai.revertState();
                    break;
                case (MessageTypes.ITEM_DROPPED):
                    Entity whoDropped = mob.getTarget();
                    // setting the target before changing state lets the createInstance function in
                    // PURSUE set the action's target for us, using the mob's target.
                    mob.setTarget((Entity) telegram.extraInfo);
                    PursueAction pursueAction = (PursueAction)ai.changeState(PURSUE_ITEM);
                    pursueAction.appendToEndedFunction(() -> {
                        mob.setTarget(whoDropped);
                        ai.changeState(WATCH);
                    });
                    break;
                default:
            }
        }
    },

    PURSUE_ITEM() {
        @Override
        public void enter(Eyeball mob) {

        }

        @Override
        public void reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai, Action action) {
            ((PursueAction) action).setTarget(mob.getTarget());
        }

        @Override
        public Action newInstance(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {
            return new PursueAction(mob, () -> {
                ((Item)mob.getTarget()).pickUp();
                // this line will have to be prepended by the state that switches into this, in order to allow for customizeable state transitions
                //ai.changeState(WANDER);
            });
        }

        @Override
        public void exit(Eyeball mob) {

        }
    };

    public void reEnter(Eyeball mob, MobAI<Eyeball, EyeballState> ai) {

    }
}
