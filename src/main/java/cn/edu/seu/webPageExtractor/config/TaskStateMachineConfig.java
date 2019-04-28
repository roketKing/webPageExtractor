package cn.edu.seu.webPageExtractor.config;

import cn.edu.seu.webPageExtractor.constants.TaskEventEnum;
import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class TaskStateMachineConfig  extends EnumStateMachineConfigurerAdapter<TaskStateEnum, TaskEventEnum> {
    @Override
    public void configure(StateMachineStateConfigurer<TaskStateEnum, TaskEventEnum> states) throws Exception {
        states
                .withStates().
                initial(TaskStateEnum.NEWTASK)
                .states(EnumSet.allOf(TaskStateEnum.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TaskStateEnum, TaskEventEnum> transitions) throws Exception {
        transitions
                .withExternal()
                .source(TaskStateEnum.NEWTASK).target(TaskStateEnum.PROCESTASK)
                .event(TaskEventEnum.START_TASK)
                .and()
                .withExternal()
                .source(TaskStateEnum.PROCESTASK).target(TaskStateEnum.FINISHTASK)
                .event(TaskEventEnum.NORMAL_EXECUTE)
                .and()
                .withExternal()
                .source(TaskStateEnum.PROCESTASK).target(TaskStateEnum.INNORMALTASK)
                .event(TaskEventEnum.EXCEPECTION_EXECUTE)
                .and()
                .withExternal()
                .source(TaskStateEnum.FINISHTASK).target(TaskStateEnum.NEWTASK)
                .event(TaskEventEnum.RESET_TASK)
                .and()
                .withExternal()
                .source(TaskStateEnum.INNORMALTASK).target(TaskStateEnum.NEWTASK)
                .event(TaskEventEnum.RESET_TASK);
    }


}
