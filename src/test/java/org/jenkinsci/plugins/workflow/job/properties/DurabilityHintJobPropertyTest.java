package org.jenkinsci.plugins.workflow.job.properties;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDurabilityHint;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DurabilityHintJobPropertyTest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void configRoundTripAndRun() throws Exception{
        WorkflowJob defaultCase = r.jenkins.createProject(WorkflowJob.class, "testCase");
        defaultCase.setDefinition(new CpsFlowDefinition("echo 'cheese is delicious'", true));

        assertNull(defaultCase.getProperty(DurabilityHintJobProperty.class));

        for (FlowDurabilityHint hint : FlowDurabilityHint.values()) {
            try {
                defaultCase.addProperty(new DurabilityHintJobProperty(hint));
                assertEquals(hint, defaultCase.getProperty(DurabilityHintJobProperty.class).getHint());
                r.configRoundtrip(defaultCase);
                assertEquals(hint, defaultCase.getProperty(DurabilityHintJobProperty.class).getHint());

                r.buildAndAssertSuccess(defaultCase);
                Assert.assertEquals(hint, defaultCase.getLastBuild().getExecution().getDurabilityHint());

                defaultCase.removeProperty(DurabilityHintJobProperty.class);
                assertNull(defaultCase.getProperty(DurabilityHintJobProperty.class));
            } catch (Exception ex) {
                throw new Exception("Error with FlowDurabilityHint "+hint, ex);
            }
        }
    }
}
