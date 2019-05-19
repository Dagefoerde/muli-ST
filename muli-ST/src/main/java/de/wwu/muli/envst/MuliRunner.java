package de.wwu.muli.envst;

import de.wwu.muggl.common.TimeSupport;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.support.Logging;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class MuliRunner {
    private Application app;

    private static final String MAIN_METHOD_NAME = "main";
    private static final String MAIN_METHOD_DESCRIPTOR = "([Ljava/lang/String;)V";

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
            return;
        }

        // The following is inspired by de.wwu.muggl.ui.gui.support.ExecutionRunner.run()
        // Initialize the Application.

        MuliRunner runner = null;
        try {
            runner = new MuliRunner(args);
        } catch (ClassFileException | InitializationException e) {
            throw new RuntimeException(e);
        }

        // Enter the main execution loop.
        final long timeStarted = System.currentTimeMillis();
        runner.app.execute();


        // Finished the execution.
        final long milliSecondsRun = System.currentTimeMillis() - timeStarted;
        Logging.general.info("Total running time: " + TimeSupport.computeRunningTime(milliSecondsRun, true));

    }

    private MuliRunner(String[] args) throws ClassFileException, InitializationException {
        assert (args != null);
        assert (args.length > 0);

//        Globals.getInst().changeLogLevel(Level.INFO);// DEV: DEBUG // PROD: INFO
//        Globals.getInst().execLogger.setLevel(Level.ERROR); // DEV: comment this line // PROD: remove comment
//        Globals.getInst().parserLogger.setLevel(Level.WARN); // DEV: INFO // PROD: WARN


        // Accept class
        final String className = args[0];

        // Extract arguments
        String[] newArgs;
        if (args.length > 1) {
            newArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            newArgs = new String[0];
        }
        // TODO: remove args that control (Muli/Muggl) VM instead of program.

        // Instantiate class loader
        final MugglClassLoader classLoader = new MugglClassLoader(new String[]{ "./system-classes/", "../examples/"});
        // TODO: Remove fake cp; Enable more classpaths from -cp arg

        // Find main method
        final ClassFile classFile = classLoader.getClassAsClassFile(className);
        final Method mainMethod = classFile.getMethodByNameAndDescriptor(MAIN_METHOD_NAME, MAIN_METHOD_DESCRIPTOR);


        // Pass newArgs to invoked main method.
        mainMethod.setPredefinedParameters(new Object[] { newArgs });
        this.app = new Application(classLoader, className, mainMethod);
    }

    private static void printUsage() {
        System.out.println("USAGE: muli " + MuliRunner.class.getName() + " CLASS [ARG1 [ARG 2 ...]]");

    }

}
