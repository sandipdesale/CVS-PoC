package com.redhat.gpte.policyquote.rules;

import org.drools.core.common.InternalAgenda;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;

public class BaseRulesTest {

    public BaseRulesTest() {
        super();
    }

    protected void activateRuleFlowGroup(KieSession session, String ruleFlowGroup) {
        InternalAgenda agenda = (InternalAgenda)session.getAgenda();
        agenda.activateRuleFlowGroup(ruleFlowGroup); 
    }

    protected void buildAll(KieServices ks, KieFileSystem kfs) {
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build errors\n" + kb.getResults().toString());
        }
    }

}