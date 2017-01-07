package com.redhat.gpte.policyquote.rules;

import java.util.List;

import org.drools.core.io.impl.ClassPathResource;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.redhat.gpte.policyquote.model.Driver;
import com.redhat.gpte.policyquote.model.Policy;

public class NoviceDriverSurchargeTest extends BaseRulesTest {
    
    @Test
    public void testNoviceDriverSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("NoviceDriverSurcharge.drl");
        
        kfs.write("src/main/resources/NoviceDriverSurcharge.drl", resource);
        
        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        kSession.addEventListener(ael);
        
        Driver driver = new Driver("1");
        driver.setAge(19);
        driver.setNumberOfAccidents(0);
        driver.setNumberOfTickets(1);
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(100);
        policy.setVehicleYear(new Integer(2004));
        
        kSession.insert(driver);
        kSession.insert(policy);
        
        activateRuleFlowGroup(kSession, "surcharge");
        
        kSession.fireAllRules();
        
        kSession.dispose();
        
        Assert.assertEquals(350, policy.getPrice().intValue());
        
        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        Mockito.verify( ael,Mockito.times(1) ).afterMatchFired(cap.capture());
        List<AfterMatchFiredEvent> values = cap.getAllValues();
        Assert.assertThat( values.get(0).getMatch().getRule().getName(),IsEqual.equalTo("NoviceDriverSurcharge"));
        
    }

}
