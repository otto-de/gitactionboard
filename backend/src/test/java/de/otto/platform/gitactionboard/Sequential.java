package de.otto.platform.gitactionboard;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.parallel.Execution;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Execution(SAME_THREAD)
public @interface Sequential {}
