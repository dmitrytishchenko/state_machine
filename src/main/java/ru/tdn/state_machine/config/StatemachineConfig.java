package ru.tdn.state_machine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import ru.tdn.state_machine.statemachine.Events;
import ru.tdn.state_machine.statemachine.States;

import javax.swing.text.html.Option;
import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachine
public class StatemachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .listener(listener())
                .autoStartup(true);
    }

    private StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void transition(Transition<States, Events> transition) {
                log.info("MOVE from {} to {}",
                        ofNullableState(transition.getSource().getId()),
                        ofNullableState(transition.getTarget().getId()));
            }

            public void stateContext(StateContext<States, Events> stateContext) {
                stateContext.getEvent();
            }

            private Object ofNullableState(States s) {
                return Optional.ofNullable(s)
                        .map(States::getId)
                        .orElse(null);
            }
        };
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .source(States.BACKLOG)
                .target(States.IN_PROGRESS)
                .event(Events.IN_PROGRESS)
                .and()
                .withExternal()
                .source(States.IN_PROGRESS)
                .target(States.TESTING)
                .event(Events.TESTING)
                .and()
                .withExternal()
                .source(States.TESTING)
                .target(States.DONE)
                .event(Events.DONE);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(States.BACKLOG)
                .state(States.IN_PROGRESS, timeToWork(), timeToSleep())
                .state(States.TESTING, deployAction())
                .state(States.DONE);
    }

    private Action<States, Events> timeToSleep() {
        return stateContext -> log.warn("БЕГОМ СПАТЬ");
    }

    private Action<States, Events> timeToWork() {
        return stateContext -> log.warn("ПОЕХАЛИ РАБОТАТЬ");
    }

    private Action<States, Events> deployAction() {
        return stateContext -> log.warn("ВЫКАТЫВАЕМСЯ!");
    }
}
