package com.redhat.gpte.policyquote.rules;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.redhat.gpte.policyquote.model.Driver;
import com.redhat.gpte.policyquote.model.Policy;

public class SurchargeTest extends BaseRulesTest {
    
    @Test
    public void testAccidentSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("AccidentSurcharge.drl");
        kfs.write("src/main/resources/AccidentSurcharge.drl", resource);
        
        resource = new ClassPathResource("NoviceDriverSurcharge.drl");
        kfs.write("src/main/resources/NoviceDriverSurcharge.drl", resource);

        resource = new ClassPathResource("NewerVehicleSurcharge.drl");
        kfs.write("src/main/resources/NewerVehhicleSurcharge.drl", resource);

        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        
        Driver driver = new Driver("1");
        driver.setAge(24);
        driver.setNumberOfAccidents(3);
        driver.setNumberOfTickets(1);
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(100);
        policy.setVehicleYear(new Integer(2003));
        
        kSession.insert(driver);
        kSession.insert(policy);
        
        activateRuleFlowGroup(kSession, "surcharge");
        
        kSession.fireAllRules();
        
        kSession.dispose();
        
        Assert.assertEquals(300, policy.getPrice().intValue());        
    }
    
    @Test
    public void testNewerVehicleSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("AccidentSurcharge.drl");
        kfs.write("src/main/resources/AccidentSurcharge.drl", resource);
        
        resource = new ClassPathResource("NoviceDriverSurcharge.drl");
        kfs.write("src/main/resources/NoviceDriverSurcharge.drl", resource);

        resource = new ClassPathResource("NewerVehicleSurcharge.drl");
        kfs.write("src/main/resources/NewerVehhicleSurcharge.drl", resource);

        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        
        Driver driver = new Driver("1");
        driver.setAge(24);
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
        
        Assert.assertEquals(200, policy.getPrice().intValue());
        
    }
    
    @Test
    public void testNoviceDriverSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("AccidentSurcharge.drl");
        kfs.write("src/main/resources/AccidentSurcharge.drl", resource);
        
        resource = new ClassPathResource("NoviceDriverSurcharge.drl");
        kfs.write("src/main/resources/NoviceDriverSurcharge.drl", resource);

        resource = new ClassPathResource("NewerVehicleSurcharge.drl");
        kfs.write("src/main/resources/NewerVehhicleSurcharge.drl", resource);
        
        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        
        Driver driver = new Driver("1");
        driver.setAge(19);
        driver.setNumberOfAccidents(0);
        driver.setNumberOfTickets(1);
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(100);
        policy.setVehicleYear(new Integer(2003));
        
        kSession.insert(driver);
        kSession.insert(policy);
        
        activateRuleFlowGroup(kSession, "surcharge");
        
        kSession.fireAllRules();
        
        kSession.dispose();
        
        Assert.assertEquals(350, policy.getPrice().intValue());
        
    }
    
    @Test
    public void testAccidentNoviceDriverSurcharge() throws Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieRepository kr = ks.getRepository();
        
        Resource resource = new ClassPathResource("AccidentSurcharge.drl");
        kfs.write("src/main/resources/AccidentSurcharge.drl", resource);
        
        resource = new ClassPathResource("NoviceDriverSurcharge.drl");
        kfs.write("src/main/resources/NoviceDriverSurcharge.drl", resource);

        resource = new ClassPathResource("NewerVehicleSurcharge.drl");
        kfs.write("src/main/resources/NewerVehhicleSurcharge.drl", resource);
        
        buildAll(ks, kfs);
        
        KieContainer kc = ks.newKieContainer(kr.getDefaultReleaseId());
        
        KieSession kSession = kc.newKieSession();
        
        Driver driver = new Driver("1");
        driver.setAge(19);
        driver.setNumberOfAccidents(3);
        driver.setNumberOfTickets(1);
        Policy policy = new Policy();
        policy.setDriver(driver.getId());
        policy.setPolicyType("AUTO");
        policy.setPrice(100);
        policy.setVehicleYear(new Integer(2003));
        
        kSession.insert(driver);
        kSession.insert(policy);
        
        activateRuleFlowGroup(kSession, "surcharge");
        
        kSession.fireAllRules(100);
        
        kSession.dispose();
        
        Assert.assertEquals(550, policy.getPrice().intValue());        
    }
    


}
