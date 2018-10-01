# Activiti 

Quick Start Guide: https://www.activiti.org/quick-start

![](https://i.imgur.com/KJWE6di.jpg)

## log

```
11:33:40,908 [main] INFO  org.activiti.engine.compatibility.DefaultActiviti5CompatibilityHandlerFactory  - Activiti 5 compatibility handler implementation not found or error during instantiation : org.activiti.compatibility.DefaultActiviti5CompatibilityHandler. Activiti 5 backwards compatibility disabled.
11:33:40,981 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on engine with resource org/activiti/db/create/activiti.h2.create.engine.sql
11:33:41,123 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on history with resource org/activiti/db/create/activiti.h2.create.history.sql
11:33:41,144 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on identity with resource org/activiti/db/create/activiti.h2.create.identity.sql
11:33:41,151 [main] INFO  org.activiti.engine.impl.ProcessEngineImpl  - ProcessEngine default created
11:33:41,192 [main] INFO  com.corning.activiti.start.OnboardingRequest  - ProcessEngine [default] Version: [6.0.0.4]
11:33:43,671 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Found process definition [Onboarding] with id [onboarding:1:4]
11:33:43,710 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Onboarding process started with process instance id [5] key [onboarding]
11:33:43,734 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Active outstanding tasks: [1]
11:33:43,734 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Processing Task [Enter Data]
11:33:43,742 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Full Name?
A
11:33:46,694 [main] INFO  com.corning.activiti.start.OnboardingRequest  - Years of Experience? (Must be a whole number)
1
11:33:47,611 [main] INFO  com.corning.activiti.start.AutomatedDataDelegate  - Faux call to backend for [A]
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - BEGIN Onboarding [onboarding] Mon Oct 01 11:33:43 CST 2018
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - -- Start [startOnboarding] 1 ms
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - -- Enter Data [enterOnboardingData] 3921 ms
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - -- Years of Experience [decision] 10 ms
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - -- Generic and Automated Data Entry [automatedIntro] 1 ms
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - -- End [endOnboarding] 0 ms
11:33:47,640 [main] INFO  com.corning.activiti.start.OnboardingRequest  - COMPLETE Onboarding [onboarding] Mon Oct 01 11:33:47 CST 2018
```

