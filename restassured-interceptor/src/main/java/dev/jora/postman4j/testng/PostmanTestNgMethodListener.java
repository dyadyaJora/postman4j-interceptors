package dev.jora.postman4j.testng;

import dev.jora.postman4j.PostmanRestassuredFilter;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * @author dyadyaJora on 17.02.2025
 */
public class PostmanTestNgMethodListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        if (!isConfigMethod(iInvokedMethod.getTestMethod())) {
            PostmanRestassuredFilter.addFolder(iInvokedMethod.getTestMethod().getMethodName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        if (!isConfigMethod(iInvokedMethod.getTestMethod())) {
            PostmanRestassuredFilter.removeFolder();
        }
    }

    private boolean isConfigMethod(final ITestNGMethod testMethod) {
        return testMethod.isBeforeMethodConfiguration() || testMethod.isAfterMethodConfiguration()
                || testMethod.isBeforeTestConfiguration() || testMethod.isAfterTestConfiguration()
                || testMethod.isBeforeClassConfiguration() || testMethod.isAfterClassConfiguration()
                || testMethod.isBeforeSuiteConfiguration() || testMethod.isAfterSuiteConfiguration();
    }
}
