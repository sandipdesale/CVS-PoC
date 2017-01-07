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

public class NewerVehicleSurchargeTest extends BaseRulesTest {
    
    @Test
    public void testNewerVehicleSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("NewerVehicleSurcharge.drl");
        
        kfs.write("src/main/resources/NewerVehicleSurcharge.drl", resource);
        
        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        kSession.addEventListener(ael);
        
        Driver driver = new Driver();
        driver.setId("1");
        driver.setAge(24);
        driver.setNumberOfAccidents(3);
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
        
        Assert.assertEquals(200, policy.getPrice().intValue());
        
        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        Mockito.verify( ael,Mockito.times(1) ).afterMatchFired(cap.capture());
        List<AfterMatchFiredEvent> values = cap.getAllValues();
        Assert.assertThat( values.get(0).getMatch().getRule().getName(),IsEqual.equalTo("NewerVehicleSurcharge"));
        
    }

}
