package de.wwu.muli.envst;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.support.Logging;

/**
 * An Application is the top level element of any execution. it instantiates and holds the reference to the
 * Muli logic virtual machine and the class loader. It will start the execution in a new thread and
 * provide access to the results of the execution.
 *
 * @author Jan C. Dageförde
 * @version 1.0.0, 2016-09-09
 */
public class Application {
    protected MugglClassLoader classLoader;
    protected LogicVM virtualMachine;
    /**
     * Basic constructor.
     * @param classLoader The main classLoader to use.
     * @param initialClassName The class that is to be executed initially.
     * @param method The method that is to be executed initially. It must be a method of the class initialClassName.
     * @throws ClassFileException Thrown on fatal errors loading or parsing a class file.
     * @throws InitializationException If initialization of auxiliary classes fails.
     */
    public Application(
            MugglClassLoader classLoader,
            String initialClassName,
            Method method
    ) throws ClassFileException {
        this.classLoader = classLoader;
        ClassFile classFile = this.classLoader.getClassAsClassFile(initialClassName);
        this.virtualMachine = new LogicVM(this, this.classLoader, classFile, method);
        Logging.general.debug("Application set up for logic execution.");
    }

    public void execute() {
        // TODO implement
    }
}
